/*
  # ArgumentParser.java
  
  A custom parser for command line arguments.
  
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc., see LICENSE.
*/

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ArgumentParser
{
  public static final Function<String, String> PARSE_UNTO_STRING =
          (final String argumentString) -> argumentString;
  
  public static final Function<String, Integer> PARSE_UNTO_INTEGER =
          (final String argumentString) -> Integer.valueOf(argumentString);
  
  private final List<PositionalArgument> positionalArgumentList;
  private final List<OptionalArgument> optionalArgumentList;
  
  public ArgumentParser()
  {
    positionalArgumentList = new ArrayList<>();
    optionalArgumentList = new ArrayList<>();
  }
  
  public <T> void addPositionalArgument(
    final String internalName,
    final String displayName,
    final String displayHelp,
    final int argumentCount,
    final Function<String, T> parsingFunction
  )
  {
    positionalArgumentList.add(
      new PositionalArgument<T>(
        internalName,
        displayName,
        displayHelp,
        argumentCount,
        parsingFunction
      )
    );
  }
  
  public <T> void addOptionalArgument(
    final String internalName,
    final String[] commandLineFlags,
    final String displayName,
    final String displayHelp,
    final int argumentCount,
    final T[] defaultValues,
    final Function<String, T> parsingFunction
  )
  {
    optionalArgumentList.add(
      new OptionalArgument<T>(
        internalName,
        commandLineFlags,
        displayName,
        displayHelp,
        argumentCount,
        defaultValues,
        parsingFunction
      )
    );
  }
  
  private class PositionalArgument<T>
  {
    private final String internalName;
    private final String displayName;
    private final String displayHelp;
    private final int argumentCount;
    private final Function<String, T> parsingFunction;
    
    private PositionalArgument(
      final String internalName,
      final String displayName,
      final String displayHelp,
      final int argumentCount,
      final Function<String, T> parsingFunction
    )
    {
      this.internalName = internalName;
      this.displayName = displayName;
      this.displayHelp = displayHelp;
      this.argumentCount = argumentCount;
      this.parsingFunction = parsingFunction;
    }
  }
  
  private class OptionalArgument<T>
  {
    private final String internalName;
    private final String[] commandLineFlags;
    private final String displayName;
    private final String displayHelp;
    private final int argumentCount;
    private final T[] defaultValues;
    private final Function<String, T> parsingFunction;
    
    private OptionalArgument(
      final String internalName,
      final String[] commandLineFlags,
      final String displayName,
      final String displayHelp,
      final int argumentCount,
      final T[] defaultValues,
      final Function<String, T> parsingFunction
    )
    {
      this.internalName = internalName;
      this.commandLineFlags = commandLineFlags;
      this.displayName = displayName;
      this.displayHelp = displayHelp;
      this.argumentCount = argumentCount;
      this.defaultValues = defaultValues;
      this.parsingFunction = parsingFunction;
    }
  }
}
