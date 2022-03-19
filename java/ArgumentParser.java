/*
  # ArgumentParser.java
  
  A custom parser for command line arguments.
  
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc., see LICENSE.
*/

import java.util.function.Function;

public class ArgumentParser
{
  public static final Function<String, String> PARSE_UNTO_STRING =
          (final String argumentString) -> argumentString;
  
  public ArgumentParser()
  {
  }
  
  public <T> void addPositionalArgument(
    final String internalName,
    final String displayName,
    final String displayHelp,
    final int argumentCount,
    final Function<String, T> parsingFunction
  )
  {
  }
}
