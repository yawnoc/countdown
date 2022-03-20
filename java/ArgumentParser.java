/*
  # ArgumentParser.java
  
  A custom parser for command line arguments.
  
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc., see LICENSE.
*/

import java.util.ArrayList;
import java.util.Arrays;
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
  public static final int ERROR_EXIT_CODE = -1;
  public static final Function<String, Object> TO_STRING = (final String string) -> string;
  public static final Function<String, Object> TO_INTEGER = (final String string) -> Integer.valueOf(string);
  
  private static final String FLAG_START_REGEX = "[-]{1,2}[a-z]";
  private static final String FLAG_REGEX = FLAG_START_REGEX + "[a-z0-9-]*";
  private static final Pattern FLAG_START_PATTERN = Pattern.compile(FLAG_START_REGEX, Pattern.CASE_INSENSITIVE);
  private static final Pattern FLAG_PATTERN = Pattern.compile(FLAG_REGEX, Pattern.CASE_INSENSITIVE);
  
  private final Set<String> recognisedNameSet = new HashSet<>();
  private final Set<String> recognisedFlagSet = new HashSet<>();
  private final List<PositionalArgument> positionalArgumentList = new ArrayList<>();
  private final Map<String, OptionalArgument> optionalArgumentFromFlag = new LinkedHashMap<>();
  
  private final String displayHelp;
  
  public ArgumentParser(final String displayHelp)
  {
    this.displayHelp = displayHelp;
  }
  
  public void addPositionalArgument(
    final String name, final String displayName,
    final String displayHelp,
    final int argumentCount, final Function<String, Object> parsingFunction
  )
  {
    checkForDuplicateName(name);
    
    positionalArgumentList.add(
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
      
      optionalArgumentFromFlag.put(flag, optionalArgument);
    }
  }
  
  public Map<String, Object[]> parseCommandLineArguments(final String[] arguments)
  {
    final LinkedList<String> argumentStringList = new LinkedList<>(Arrays.asList(arguments));
    
    argumentConsumption:
    while (!argumentStringList.isEmpty())
    {
      final String firstArgumentString = argumentStringList.getFirst();
      
      if (denotesFlag(firstArgumentString)) // optional argument
      {
        final String flag = extractRecognisedFlag(firstArgumentString);
        
        argumentStringList.removeFirst();
        if (!firstArgumentString.equals(flag))
        {
          final String conjoinedArgument = firstArgumentString.replaceFirst("^" + Pattern.quote(flag) + "[=]?", "");
          argumentStringList.addFirst(conjoinedArgument);
        }
        
        final OptionalArgument optionalArgument = optionalArgumentFromFlag.get(flag);
        optionalArgument.consume(argumentStringList, flag);
        continue argumentConsumption;
      }
      else // positional argument
      {
        // TODO: implement this
        break argumentConsumption;
      }
    }
    
    final Map<String, Object[]> valuesFromName = new HashMap<>();
    for (OptionalArgument optionalArgument : optionalArgumentFromFlag.values())
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
  
  private boolean denotesFlag(final String argumentString)
  {
    return FLAG_START_PATTERN.matcher(argumentString).matches();
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
    
    System.err.println(String.format("unrecognised arguments: %s", argumentString));
    System.exit(ERROR_EXIT_CODE);
    return null; // so that compiler doesn't complain
  }
  
  private Object parseValue(
    final Function<String, Object> parsingFunction,
    final String argumentString,
    final String displayNameOrFlag
  )
  {
    final Object value;
    try
    {
      return parsingFunction.apply(argumentString);
    }
    catch (NumberFormatException exception)
    {
      System.err.println(String.format("argument %s: not integer: %s", displayNameOrFlag, argumentString));
      System.exit(ERROR_EXIT_CODE);
      return null; // so that the compiler doesn't complain
    }
  }
  
  private class PositionalArgument
  {
    private final String name;
    private final String displayName;
    private final String displayHelp;
    private final int argumentCount;
    private final Function<String, Object> parsingFunction;
    
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
      
      final int defaultValuesCount = defaultValues.length;
      values = new Object[argumentCount];
      for (int index = 0; index < Math.min(argumentCount, defaultValuesCount); index++)
      {
        values[index] = defaultValues[index];
      }
    }
    
    private void consume(final LinkedList<String> argumentStringList, final String flag)
    {
      if (argumentStringList.size() < argumentCount)
      {
        System.err.println(insufficientOptionalArgumentsMessage(flag, argumentCount));
        System.exit(ERROR_EXIT_CODE);
      }
      
      for (int index = 0; index < argumentCount; index++)
      {
        final String firstArgumentString = argumentStringList.getFirst();
        
        if (denotesFlag(firstArgumentString))
        {
          System.err.println(insufficientOptionalArgumentsMessage(flag, argumentCount));
          System.exit(ERROR_EXIT_CODE);
        }
        
        values[index] = parseValue(parsingFunction, firstArgumentString, flag);
        argumentStringList.removeFirst();
      }
    }
  }
  
  private String insufficientOptionalArgumentsMessage(final String flag, final int argumentCount)
  {
    final String argumentNoun =
            (argumentCount == 1)
              ? "argument"
              : "arguments";
    return String.format("argument %s: expected %d %s", flag, argumentCount, argumentNoun);
  }
}
