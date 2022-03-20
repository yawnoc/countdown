/*
  # ArgumentParser.java
  
  A custom parser for command line arguments.
  
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
  public static final Function<String, Object> TO_INTEGER = (final String string) -> Integer.valueOf(string);
  public static final Function<String, Object> TO_POSITIVE_INTEGER = (final String string) -> Integer.valueOf(string);
  
  private static final int NORMAL_EXIT_CODE = 0;
  private static final int ERROR_EXIT_CODE = 2;
  private static final String FLAG_START_REGEX = "[-]{1,2}[a-z]";
  private static final String FLAG_REGEX = FLAG_START_REGEX + "[a-z0-9-]*";
  private static final Pattern FLAG_START_PATTERN = Pattern.compile(FLAG_START_REGEX, Pattern.CASE_INSENSITIVE);
  private static final Pattern FLAG_PATTERN = Pattern.compile(FLAG_REGEX, Pattern.CASE_INSENSITIVE);
  private static final String END_OF_OPTIONAL_ARGUMENTS_STRING = "--";
  
  private final Set<String> recognisedNameSet = new HashSet<>();
  private final Set<String> recognisedFlagSet = new HashSet<>();
  private final LinkedList<PositionalArgument> recognisedPositionalArgumentList = new LinkedList<>();
  private final Map<String, OptionalArgument> recognisedOptionalArgumentFromFlag = new LinkedHashMap<>();
  
  private final String commandName;
  private final String displayHelp;
  
  public ArgumentParser(final String commandName, final String displayHelp)
  {
    this.commandName = commandName;
    this.displayHelp = displayHelp;
    addOptionalArgument(
      HELP_ARGUMENT_NAME, new String[]{"-h", "--help"}, "",
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
  
  /*
    Boolean flags are represented by optional arguments with nil argumentCount.
  */
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
    
    argumentConsumption:
    while (!argumentStringList.isEmpty())
    {
      final String firstArgumentString = argumentStringList.getFirst();
      
      if (denotesEndOfOptionalArguments(firstArgumentString))
      {
        allowOptionalArguments = false;
        argumentStringList.removeFirst();
        continue argumentConsumption;
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
        continue argumentConsumption;
      }
      else // positional argument
      {
        if (positionalArgumentList.isEmpty())
        {
          System.err.println(usageLine());
          System.err.println(unrecognisedArgumentsMessage(firstArgumentString));
          System.exit(ERROR_EXIT_CODE);
        }
        
        final PositionalArgument positionalArgument = positionalArgumentList.getFirst();
        positionalArgument.consume(argumentStringList, positionalArgumentList);
        continue argumentConsumption;
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
  
  private boolean isValidFlag(final String string)
  {
    return FLAG_PATTERN.matcher(string).matches();
  }
  
  private boolean denotesEndOfOptionalArguments(final String argumentString)
  {
    return argumentString.equals(END_OF_OPTIONAL_ARGUMENTS_STRING);
  }
  
  private boolean denotesFlag(final String argumentString)
  {
    return FLAG_START_PATTERN.matcher(argumentString).lookingAt();
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
    
    System.err.println(usageLine());
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
        System.err.println(usageLine());
        System.err.println(String.format("error: argument %s: not positive: %s", displayNameOrFlag, argumentString));
        System.exit(ERROR_EXIT_CODE);
      }
      return value;
    }
    catch (NumberFormatException exception)
    {
      System.err.println(usageLine());
      System.err.println(String.format("error: argument %s: not integer: %s", displayNameOrFlag, argumentString));
      System.exit(ERROR_EXIT_CODE);
      return null; // so that compiler doesn't complain
    }
  }
  
  private String usageLine()
  {
    final List<String> usageStringList = new ArrayList<>();
    
    usageStringList.add("usage:");
    usageStringList.add(String.format("java %s", commandName));
    
    usageStringList.add(String.format("[%s]", HELP_SHORT_FLAG));
    for (final OptionalArgument optionalArgument : recognisedOptionalArgumentFromFlag.values())
    {
      if (optionalArgument.name.equals(HELP_ARGUMENT_NAME))
      {
        continue;
      }
      final String shortFlag = optionalArgument.flags[0];
      final String repeatedDisplayName = repeatDisplayName(optionalArgument.argumentCount, optionalArgument.displayName);
      usageStringList.add(String.format("[%s %s]", shortFlag, repeatedDisplayName));
    }
    
    for (final PositionalArgument positionalArgument : recognisedPositionalArgumentList)
    {
      final String repeatedDisplayName = repeatDisplayName(positionalArgument.argumentCount, positionalArgument.displayName);
      usageStringList.add(repeatedDisplayName);
    }
    
    return String.join(" ", usageStringList);
  }
  
  private String repeatDisplayName(final int count, final String displayName)
  {
    if (count == Integer.MAX_VALUE)
    {
      return String.format("%s [%s ...]", displayName);
    }
    else
    {
      return String.join(" ", Collections.nCopies(count, displayName));
    }
  }
  
  private class PositionalArgument
  {
    private final String name;
    private final String displayName;
    private final String displayHelp;
    private final int argumentCount;
    private final Function<String, Object> parsingFunction;
    
    private List<Object> valueList;
    
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
      if (areValuesFilled())
      {
        positionalArgumentList.removeFirst();
      }
    }
    
    private boolean areValuesFilled()
    {
      return valueList.size() >= argumentCount;
    }
    
    private void checkValuesFilled()
    {
      if (!areValuesFilled())
      {
        System.err.println(usageLine());
        System.err.println(insufficientArgumentsMessage(displayName, argumentCount));
        System.exit(ERROR_EXIT_CODE);
      }
    }
  }
  
  private class OptionalArgument
  {
    private final String name;
    private final String[] flags;
    private final String displayName;
    private final String displayHelp;
    private final int argumentCount;
    private final Function<String, Object> parsingFunction;
    
    private Object[] values;
    
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
      for (int index = 0; index < Math.min(argumentCount, defaultValuesCount); index++)
      {
        values[index] = defaultValues[index];
      }
    }
    
    private void consume(final LinkedList<String> argumentStringList, final String flag)
    {
      if (argumentCount == 0) // boolean flag
      {
        values[0] = true;
        
        if (name.equals(HELP_ARGUMENT_NAME))
        {
          System.out.println(ArgumentParser.this.displayHelp);
          System.exit(NORMAL_EXIT_CODE);
        }
        
        return;
      }
      
      if (argumentStringList.size() < argumentCount)
      {
        System.err.println(usageLine());
        System.err.println(insufficientArgumentsMessage(flag, argumentCount));
        System.exit(ERROR_EXIT_CODE);
      }
      
      for (int index = 0; index < argumentCount; index++)
      {
        final String firstArgumentString = argumentStringList.getFirst();
        
        if (denotesEndOfOptionalArguments(firstArgumentString) || denotesFlag(firstArgumentString))
        {
          System.err.println(usageLine());
          System.err.println(insufficientArgumentsMessage(flag, argumentCount));
          System.exit(ERROR_EXIT_CODE);
        }
        
        values[index] = parseValue(parsingFunction, firstArgumentString, flag);
        argumentStringList.removeFirst();
      }
    }
  }
  
  private String unrecognisedArgumentsMessage(final String argumentString)
  {
    return String.format("error: unrecognised arguments: %s", argumentString);
  }
  
  private String insufficientArgumentsMessage(final String displayNameOrFlag, final int argumentCount)
  {
    final String argumentNoun =
            (argumentCount == 1)
              ? "argument"
              : "arguments";
    return String.format("error: argument %s: expected %d %s", displayNameOrFlag, argumentCount, argumentNoun);
  }
}
