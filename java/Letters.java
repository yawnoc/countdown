/*
  # Letters.java
  
  Solve a Countdown letters game.
  
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc., see LICENSE.
*/

public class Letters
{
  private static final int MAX_RESULTS_DEFAULT = 30;
  private static final String WORD_LIST_FILE_NAME_DEFAULT = "../yawl.txt";
  
  private static void parseCommandLineArguments(final String[] arguments)
  {
    final ArgumentParser argumentParser = new ArgumentParser();
    
    argumentParser.addPositionalArgument(
      "inputLetters", "LETTERS",
      "string containing the letters that can be used to form words",
      1, ArgumentParser.PARSE_UNTO_STRING
    );
    
    argumentParser.addOptionalArgument(
      "maxResultsCount", new String[]{"-m"}, "MAX_RESULTS",
      String.format("maximum number of output results (default %d)", MAX_RESULTS_DEFAULT),
      1, new Integer[]{MAX_RESULTS_DEFAULT}, ArgumentParser.PARSE_UNTO_INTEGER
    );
    
    argumentParser.addOptionalArgument(
      "wordListFile", new String[]{"-w"}, "WORD_LIST",
      String.format("word list file name (default %s)", WORD_LIST_FILE_NAME_DEFAULT),
      1, new String[]{WORD_LIST_FILE_NAME_DEFAULT}, ArgumentParser.PARSE_UNTO_STRING
    );
  }
  
  public static void main(final String[] arguments)
  {
    parseCommandLineArguments(arguments);
  }
}
