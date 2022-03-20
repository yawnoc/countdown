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
  public static final Function<String, Object> PARSE_UNTO_STRING = (final String string) -> string;
  public static final Function<String, Object> PARSE_UNTO_INTEGER = (final String string) -> Integer.valueOf(string);
  
  private static final String FLAG_REGEX = "[-]{1,2}[a-z0-9][a-z0-9-]*";
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
    if (recognisedNameSet.contains(name))
    {
      throw new IllegalArgumentException(
        String.format("name `%s` has already been used for a positional argument", name)
      );
    }
    recognisedNameSet.add(name);
    
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
          String.format("command line flag `%s` not of the form `%s`", flag, FLAG_REGEX)
        );
      }
      
      if (recognisedFlagSet.contains(flag))
      {
        throw new IllegalArgumentException(
          String.format("flag `%s` has already been used for an optional argument", flag)
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
          final String conjoinedArgument = firstArgumentString.replaceFirst("^" + Pattern.quote(flag), "");
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
  
  private boolean isValidFlag(final String string)
  {
    return FLAG_PATTERN.matcher(string).matches();
  }
  
  private boolean denotesFlag(final String argumentString)
  {
    return argumentString.startsWith("-");
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
    
    throw new UnrecognisedArgumentsException(unrecognisedOptionalArgumentsMessage(argumentString));
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
        throw new InsufficientArgumentsException(insufficientOptionalArgumentsMessage(flag, argumentCount));
      }
      
      for (int index = 0; index < argumentCount; index++)
      {
        final String firstArgumentString = argumentStringList.getFirst();
        
        if (denotesFlag(firstArgumentString))
        {
          throw new InsufficientArgumentsException(insufficientOptionalArgumentsMessage(flag, argumentCount));
        }
        
        values[index] = parsingFunction.apply(firstArgumentString);
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
  
  private String unrecognisedOptionalArgumentsMessage(final String flag)
  {
    return String.format("unrecognised arguments: %s", flag);
  }
  
  private class InsufficientArgumentsException extends IndexOutOfBoundsException
  {
    InsufficientArgumentsException(final String message)
    {
      super(message);
    }
  }
  
  private class UnrecognisedArgumentsException extends IllegalArgumentException
  {
    UnrecognisedArgumentsException(final String message)
    {
      super(message);
    }
  }
}
