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
  
  private final List<PositionalArgument> positionalArgumentList;
  
  public ArgumentParser()
  {
    positionalArgumentList = new ArrayList<>();
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
  
  private class Argument
  {
  }
  
  private class PositionalArgument<T> extends Argument
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
}
