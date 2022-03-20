/*
  # ArgumentParser.java
  
  A custom parser for command line arguments.
  
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc., see LICENSE.
*/

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
  
  private final Set<String> nameSet = new HashSet<>();
  private final Set<String> flagSet = new HashSet<>();
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
    if (nameSet.contains(name))
    {
      throw new IllegalArgumentException(
        String.format("name `%s` has already been used for a positional argument", name)
      );
    }
    nameSet.add(name);
    
    positionalArgumentList.add(
      new PositionalArgument(
        name, displayName,
        displayHelp,
        argumentCount, parsingFunction
      )
    );
  }
  
  public void addOptionalArgument(
    final String name, final String[] commandLineFlags, final String displayName,
    final String displayHelp,
    final int argumentCount, final Object[] defaultValues, final Function<String, Object> parsingFunction
  )
  {
    final OptionalArgument optionalArgument =
            new OptionalArgument(
              name, commandLineFlags, displayName,
              displayHelp,
              argumentCount, defaultValues, parsingFunction
            );
    
    for (final String flag : commandLineFlags)
    {
      if (!isValidFlag(flag))
      {
        throw new IllegalArgumentException(
          String.format("command line flag `%s` not of the form `%s`", flag, FLAG_REGEX)
        );
      }
      
      if (flagSet.contains(flag))
      {
        throw new IllegalArgumentException(
          String.format("flag `%s` has already been used for an optional argument", flag)
        );
      }
      flagSet.add(flag);
      
      optionalArgumentFromFlag.put(flag, optionalArgument);
    }
  }
  
  private boolean isValidFlag(final String string)
  {
    return FLAG_PATTERN.matcher(string).matches();
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
    private final String[] commandLineFlags;
    private final String displayName;
    private final String displayHelp;
    private final int argumentCount;
    private final Object[] defaultValues;
    private final Function<String, Object> parsingFunction;
    
    private OptionalArgument(
      final String name, final String[] commandLineFlags, final String displayName,
      final String displayHelp,
      final int argumentCount, final Object[] defaultValues, final Function<String, Object> parsingFunction
    )
    {
      this.name = name;
      this.commandLineFlags = commandLineFlags;
      this.displayName = displayName;
      this.displayHelp = displayHelp;
      this.argumentCount = argumentCount;
      this.defaultValues = defaultValues;
      this.parsingFunction = parsingFunction;
    }
  }
}
