/*
  # Letters.java
  
  Solve a Countdown letters game.
  
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc., see LICENSE.
*/

public class Letters
{
  public static void main(final String[] arguments)
  {
    final ArgumentParser argumentParser = new ArgumentParser();
    argumentParser.addPositionalArgument(
      "inputLetters",
      "LETTERS",
      "string containing the letters that can be used to form words",
      1,
      ArgumentParser.PARSE_UNTO_STRING
    );
  }
}
