/*
  # ArgumentParser.java
  
  A custom parser for command line arguments.
  
  Hacks for number of arguments:
  - Binary flag: use optional argument with argumentCount == 0.
  - One or more: use positional argument with argumentCount == Integer.MAX_VALUE.
  
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc., see LICENSE.
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ArgumentParser
{
  public static final String HELP_ARGUMENT_NAME = "HELP";
  public static final String HELP_SHORT_FLAG = "-h";
  public static final String HELP_LONG_FLAG = "--help";
  public static final Function<String, Object> TO_STRING = (final String string) -> string;
  public static final Function<String, Object> TO_INTEGER = Integer::valueOf;
  public static final Function<String, Object> TO_POSITIVE_INTEGER = Integer::valueOf;
  
  private static final int NORMAL_EXIT_CODE = 0;
  private static final int ERROR_EXIT_CODE = 2;
  private static final int TERMINAL_WIDTH = 80;
  private static final int HELP_COLUMNS_GAP_WIDTH = 2;
  private static final int HELP_ARGUMENTS_COLUMN_WIDTH = 20;
  private static final String FLAG_START_REGEX = "[-]{1,2}[a-z]";
  private static final String FLAG_REGEX = FLAG_START_REGEX + "[a-z0-9-]*";
  private static final Pattern FLAG_START_PATTERN = Pattern.compile(FLAG_START_REGEX, Pattern.CASE_INSENSITIVE);
  private static final Pattern FLAG_PATTERN = Pattern.compile(FLAG_REGEX, Pattern.CASE_INSENSITIVE);
  private static final String END_OF_OPTIONAL_ARGUMENTS_STRING = "--";
  
  private final Set<String> recognisedNameSet = new HashSet<>();
  private final Set<String> recognisedFlagSet = new HashSet<>();
  private final List<PositionalArgument> recognisedPositionalArgumentList = new ArrayList<>();
  private final Map<String, OptionalArgument> recognisedOptionalArgumentFromFlag = new LinkedHashMap<>();
  
  private final String commandName;
  private final String displayHelp;
  
  public ArgumentParser(final String commandName, final String displayHelp)
  {
    this.commandName = commandName;
    this.displayHelp = displayHelp;
    addOptionalArgument(
      HELP_ARGUMENT_NAME, new String[]{HELP_SHORT_FLAG, HELP_LONG_FLAG}, "",
      "show this help message and exit"
    );
  }
  
  public void addPositionalArgument(
    final String name, final String displayName,
    final String displayHelp,
    final int argumentCount, final Function<String, Object> parsingFunction
  )
  {
    checkForDuplicateName(name);
    
    if (argumentCount <= 0)
    {
      throw new IllegalArgumentException(
        "\n" + String.format("argumentCount %d for `%s` must be positive", argumentCount, name)
      );
    }
    
    recognisedPositionalArgumentList.add(
      new PositionalArgument(
        name, displayName,
        displayHelp,
        argumentCount, parsingFunction
      )
    );
  }
  
  public void addOptionalArgument(
    final String name, final String[] flags, final String displayName,
    final String displayHelp,
    final int argumentCount, final Object[] defaultValues, final Function<String, Object> parsingFunction
  )
  {
    checkForDuplicateName(name);
    
    final OptionalArgument optionalArgument =
            new OptionalArgument(
              name, flags, displayName,
              displayHelp,
              argumentCount, defaultValues, parsingFunction
            );
    
    for (final String flag : flags)
    {
      if (!isValidFlag(flag))
      {
        throw new IllegalArgumentException(
          "\n" + String.format("command line flag `%s` not of the form `%s`", flag, FLAG_REGEX)
        );
      }
      
      if (recognisedFlagSet.contains(flag))
      {
        throw new IllegalArgumentException(
          "\n" + String.format("flag `%s` has already been used", flag)
        );
      }
      recognisedFlagSet.add(flag);
      
      recognisedOptionalArgumentFromFlag.put(flag, optionalArgument);
    }
  }
  
  public void addOptionalArgument(
    final String name, final String[] flags, final String displayName,
    final String displayHelp
  )
  {
    addOptionalArgument(
      name, flags, displayName,
      displayHelp,
      0, null, null
    );
  }
  
  public Map<String, Object[]> parseCommandLineArguments(final String[] arguments)
  {
    final LinkedList<String> argumentStringList = new LinkedList<>(Arrays.asList(arguments));
    final LinkedList<PositionalArgument> positionalArgumentList = new LinkedList<>(recognisedPositionalArgumentList);
    boolean allowOptionalArguments = true;
    
    while (!argumentStringList.isEmpty())
    {
      final String firstArgumentString = argumentStringList.getFirst();
      
      if (denotesEndOfOptionalArguments(firstArgumentString))
      {
        allowOptionalArguments = false;
        argumentStringList.removeFirst();
        continue;
      }
      
      if (allowOptionalArguments && denotesFlag(firstArgumentString)) // optional argument
      {
        final String flag = extractRecognisedFlag(firstArgumentString);
        
        argumentStringList.removeFirst();
        if (!firstArgumentString.equals(flag))
        {
          final String conjoinedArgument = firstArgumentString.replaceFirst("^" + Pattern.quote(flag) + "[=]?", "");
          argumentStringList.addFirst(conjoinedArgument);
        }
        
        final OptionalArgument optionalArgument = recognisedOptionalArgumentFromFlag.get(flag);
        optionalArgument.consume(argumentStringList, flag);
      }
      else // positional argument
      {
        if (positionalArgumentList.isEmpty())
        {
          System.err.println(usageMessage());
          System.err.println(unrecognisedArgumentsMessage(firstArgumentString));
          System.exit(ERROR_EXIT_CODE);
        }
        
        final PositionalArgument positionalArgument = positionalArgumentList.getFirst();
        positionalArgument.consume(argumentStringList, positionalArgumentList);
      }
    }
    
    final Map<String, Object[]> valuesFromName = new HashMap<>();
    for (PositionalArgument positionArgument : recognisedPositionalArgumentList)
    {
      positionArgument.checkValuesFilled();
      valuesFromName.put(positionArgument.name, positionArgument.getValues());
    }
    for (OptionalArgument optionalArgument : recognisedOptionalArgumentFromFlag.values())
    {
      valuesFromName.put(optionalArgument.name, optionalArgument.values);
    }
    
    return valuesFromName;
  }
  
  private void checkForDuplicateName(final String name)
  {
    if (recognisedNameSet.contains(name))
    {
      throw new IllegalArgumentException(
        "\n" + String.format("name `%s` has already been used", name)
      );
    }
    recognisedNameSet.add(name);
  }
  
  private static boolean isValidFlag(final String string)
  {
    return FLAG_PATTERN.matcher(string).matches();
  }
  
  private static boolean denotesEndOfOptionalArguments(final String argumentString)
  {
    return argumentString.equals(END_OF_OPTIONAL_ARGUMENTS_STRING);
  }
  
  private static boolean denotesFlag(final String argumentString)
  {
    return FLAG_START_PATTERN.matcher(argumentString).lookingAt();
  }
  
  private static String repeatSpaces(final int count)
  {
    return " ".repeat(count);
  }
  
  private String extractRecognisedFlag(final String argumentString)
  {
    for (final String flag : recognisedFlagSet)
    {
      if (argumentString.startsWith(flag))
      {
        return flag;
      }
    }
    
    System.err.println(usageMessage());
    System.err.println(unrecognisedArgumentsMessage(argumentString));
    System.exit(ERROR_EXIT_CODE);
    return null; // so that compiler doesn't complain
  }
  
  private Object parseValue(
    final Function<String, Object> parsingFunction,
    final String argumentString,
    final String displayNameOrFlag
  )
  {
    try
    {
      final Object value = parsingFunction.apply(argumentString);
      if (parsingFunction == TO_POSITIVE_INTEGER && (int) value <= 0)
      {
        System.err.println(usageMessage());
        System.err.printf("error: argument %s: not positive: %s%n", displayNameOrFlag, argumentString);
        System.exit(ERROR_EXIT_CODE);
      }
      return value;
    }
    catch (NumberFormatException exception)
    {
      System.err.println(usageMessage());
      System.err.printf("error: argument %s: not integer: %s%n", displayNameOrFlag, argumentString);
      System.exit(ERROR_EXIT_CODE);
      return null; // so that compiler doesn't complain
    }
  }
  
  private String fullHelpMessage()
  {
    final List<String> fullHelpStringList = new ArrayList<>();
    
    fullHelpStringList.add(usageMessage());
    fullHelpStringList.add(displayHelp);
    
    final String positionalArgumentsHelp = positionalArgumentsHelpMessage();
    if (!positionalArgumentsHelp.isEmpty())
    {
      fullHelpStringList.add(positionalArgumentsHelp);
    }
    
    final String optionalArgumentsHelp = optionalArgumentsHelpMessage();
    if (!positionalArgumentsHelp.isEmpty())
    {
      fullHelpStringList.add(optionalArgumentsHelp);
    }
    
    return String.join("\n\n", fullHelpStringList);
  }
  
  private String usageMessage()
  {
    final List<String> usageStringList = new ArrayList<>();
    
    usageStringList.add("usage:");
    usageStringList.add(String.format("java %s", commandName));
    
    final OptionalArgument helpArgument = getHelpArgument();
    usageStringList.add(helpArgument.flagUsageString());
    for (final OptionalArgument optionalArgument : recognisedOptionalArgumentFromFlag.values())
    {
      if (optionalArgument == helpArgument)
      {
        continue;
      }
      usageStringList.add(String.format("[%s]", optionalArgument.flagUsageString()));
    }
    
    for (final PositionalArgument positionalArgument : recognisedPositionalArgumentList)
    {
      usageStringList.add(positionalArgument.argumentUsageString());
    }
    
    return String.join(" ", usageStringList);
  }
  
  private static String repeatDisplayName(final int count, final String displayName)
  {
    if (count == Integer.MAX_VALUE)
    {
      return String.format("%s [%s ...]", displayName, displayName);
    }
    else
    {
      return String.join(" ", Collections.nCopies(count, displayName));
    }
  }
  
  private static String wrapDisplayHelp(final String string)
  {
    final int availableWidth = TERMINAL_WIDTH - HELP_ARGUMENTS_COLUMN_WIDTH - 3 * HELP_COLUMNS_GAP_WIDTH;
    final int wrapIndentWidth = HELP_ARGUMENTS_COLUMN_WIDTH + 2 * HELP_COLUMNS_GAP_WIDTH;
    
    if (string.length() > availableWidth)
    {
      final int spaceIndex = string.substring(0, availableWidth).lastIndexOf(" ");
      final int breakBeforeIndex;
      final int breakAfterIndex;
      if (spaceIndex < 0)
      {
        breakBeforeIndex = availableWidth;
        breakAfterIndex = availableWidth;
      }
      else
      {
        breakBeforeIndex = spaceIndex;
        breakAfterIndex = spaceIndex + 1;
      }
      
      return
        string.substring(0, breakBeforeIndex)
          + "\n"
          + repeatSpaces(wrapIndentWidth)
          + wrapDisplayHelp(string.substring(breakAfterIndex));
    }
    
    return string;
  }
  
  private static String formatHelpLine(final String helpArgumentsString, final String displayHelp)
  {
    String helpLine = "";
    helpLine += repeatSpaces(HELP_COLUMNS_GAP_WIDTH);
    helpLine += helpArgumentsString;
    
    final int argumentsStringLength = helpArgumentsString.length();
    if (argumentsStringLength <= HELP_ARGUMENTS_COLUMN_WIDTH)
    {
      helpLine += repeatSpaces(HELP_ARGUMENTS_COLUMN_WIDTH - argumentsStringLength);
    }
    else
    {
      helpLine += "\n";
      helpLine += repeatSpaces(HELP_COLUMNS_GAP_WIDTH);
      helpLine += repeatSpaces(HELP_ARGUMENTS_COLUMN_WIDTH);
    }
    helpLine += repeatSpaces(HELP_COLUMNS_GAP_WIDTH);
    helpLine += wrapDisplayHelp(displayHelp);
    
    return helpLine;
  }
  
  private String formatHelpLine(final PositionalArgument positionalArgument)
  {
    return formatHelpLine(positionalArgument.displayName, positionalArgument.displayHelp);
  }
  
  private String formatHelpLine(final OptionalArgument optionalArgument)
  {
    return formatHelpLine(optionalArgument.helpArgumentsString(), optionalArgument.displayHelp);
  }
  
  private String positionalArgumentsHelpMessage()
  {
    if (recognisedPositionalArgumentList.size() == 0)
    {
      return "";
    }
    
    final List<String> helpLineList = new ArrayList<>();
    for (final PositionalArgument positionalArgument : recognisedPositionalArgumentList)
    {
      helpLineList.add(formatHelpLine(positionalArgument));
    }
    return "positional arguments:" + "\n" + String.join("\n", helpLineList);
  }
  
  private String optionalArgumentsHelpMessage()
  {
    final List<String> helpLineList = new ArrayList<>();
    final OptionalArgument helpArgument = getHelpArgument();
    helpLineList.add(formatHelpLine(helpArgument));
    for (final OptionalArgument optionalArgument : recognisedOptionalArgumentFromFlag.values())
    {
      if (optionalArgument == helpArgument)
      {
        continue;
      }
      helpLineList.add(formatHelpLine(optionalArgument));
    }
    return "optional arguments:" + "\n" + String.join("\n", helpLineList);
  }
  
  public class PositionalArgument
  {
    private final String name;
    private final String displayName;
    private final String displayHelp;
    private final int argumentCount;
    private final Function<String, Object> parsingFunction;
    
    private final List<Object> valueList;
    
    private PositionalArgument(
      final String name, final String displayName,
      final String displayHelp,
      final int argumentCount, final Function<String, Object> parsingFunction
    )
    {
      this.name = name;
      this.displayName = displayName;
      this.displayHelp = displayHelp;
      this.argumentCount = argumentCount;
      this.parsingFunction = parsingFunction;
      
      valueList = new ArrayList<>();
    }
    
    private Object[] getValues()
    {
      return valueList.toArray(new Object[0]);
    }
    
    private void consume(
      final LinkedList<String> argumentStringList,
      final LinkedList<PositionalArgument> positionalArgumentList
    )
    {
      final String firstArgumentString = argumentStringList.getFirst();
      final Object value = parseValue(parsingFunction, firstArgumentString, displayName);
      valueList.add(value);
      
      argumentStringList.removeFirst();
      if (valueList.size() >= argumentCount)
      {
        positionalArgumentList.removeFirst();
      }
    }
    
    private void checkValuesFilled()
    {
      final boolean infiniteCountUnfilled = argumentCount == Integer.MAX_VALUE && valueList.size() == 0;
      final boolean finiteCountUnfilled = argumentCount != Integer.MAX_VALUE && valueList.size() < argumentCount;
      
      if (infiniteCountUnfilled || finiteCountUnfilled)
      {
        System.err.println(usageMessage());
        System.err.println(insufficientArgumentsMessage(displayName, argumentCount));
        System.exit(ERROR_EXIT_CODE);
      }
    }
    
    private String argumentUsageString()
    {
      return repeatDisplayName(argumentCount, displayName);
    }
  }
  
  public class OptionalArgument
  {
    private final String name;
    private final String[] flags;
    private final String displayName;
    private final String displayHelp;
    private final int argumentCount;
    private final Function<String, Object> parsingFunction;
    
    private final Object[] values;
    
    private OptionalArgument(
      final String name, final String[] flags, final String displayName,
      final String displayHelp,
      final int argumentCount, final Object[] defaultValues, final Function<String, Object> parsingFunction
    )
    {
      this.name = name;
      this.flags = flags;
      this.displayName = displayName;
      this.displayHelp = displayHelp;
      this.argumentCount = argumentCount;
      this.parsingFunction = parsingFunction;
      
      if (argumentCount == 0) // boolean flag
      {
        values = new Object[]{false};
        return;
      }
      
      final int defaultValuesCount = defaultValues.length;
      values = new Object[argumentCount];
      System.arraycopy(defaultValues, 0, values, 0, Math.min(argumentCount, defaultValuesCount));
    }
    
    private void consume(final LinkedList<String> argumentStringList, final String flag)
    {
      if (argumentCount == 0) // boolean flag
      {
        values[0] = true;
        
        if (name.equals(HELP_ARGUMENT_NAME))
        {
          System.out.println(fullHelpMessage());
          System.exit(NORMAL_EXIT_CODE);
        }
        
        return;
      }
      
      if (argumentStringList.size() < argumentCount)
      {
        System.err.println(usageMessage());
        System.err.println(insufficientArgumentsMessage(flag, argumentCount));
        System.exit(ERROR_EXIT_CODE);
      }
      
      for (int index = 0; index < argumentCount; index++)
      {
        final String firstArgumentString = argumentStringList.getFirst();
        
        if (denotesEndOfOptionalArguments(firstArgumentString) || denotesFlag(firstArgumentString))
        {
          System.err.println(usageMessage());
          System.err.println(insufficientArgumentsMessage(flag, argumentCount));
          System.exit(ERROR_EXIT_CODE);
        }
        
        values[index] = parseValue(parsingFunction, firstArgumentString, flag);
        argumentStringList.removeFirst();
      }
    }
    
    private String flagUsageString()
    {
      final String firstFlag = flags[0];
      if (argumentCount == 0)
      {
        return firstFlag;
      }
      
      final String repeatedDisplayName = repeatDisplayName(argumentCount, displayName);
      return String.join(" ", firstFlag, repeatedDisplayName);
    }
    
    private String helpArgumentsString()
    {
      final List<String> flagUsageStringList = new ArrayList<>();
      for (final String flag : flags)
      {
        final String repeatedDisplayName = repeatDisplayName(argumentCount, displayName);
        final String flagUsageString =
                String.join(
                  repeatSpaces(Math.min(argumentCount, 1)),
                  flag,
                  repeatedDisplayName
                );
        flagUsageStringList.add(flagUsageString);
      }
      return String.join(", ", flagUsageStringList);
    }
  }
  
  private OptionalArgument getHelpArgument()
  {
    return recognisedOptionalArgumentFromFlag.get(HELP_LONG_FLAG);
  }
  
  private static String unrecognisedArgumentsMessage(final String argumentString)
  {
    return String.format("error: unrecognised arguments: %s", argumentString);
  }
  
  private static String insufficientArgumentsMessage(final String displayNameOrFlag, final int argumentCount)
  {
    final String argumentCountString =
            (argumentCount == Integer.MAX_VALUE)
              ? "one or more"
              : String.valueOf(argumentCount);
    
    final String argumentNoun =
            (argumentCount == 1)
              ? "argument"
              : "arguments";
    
    return String.format("error: argument %s: expected %s %s", displayNameOrFlag, argumentCountString, argumentNoun);
  }
}
