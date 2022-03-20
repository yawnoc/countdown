/*
  # Letters.java
  
  Solve a Countdown letters game.
  
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc., see LICENSE.
*/

import java.util.Map;

public class Letters
{
  private static final int MAX_RESULTS_DEFAULT = 30;
  private static final String WORD_LIST_FILE_NAME_DEFAULT = "../yawl.txt";
  
  private static Map<String, Object[]> parseCommandLineArguments(final String[] arguments)
  {
    final ArgumentParser argumentParser = new ArgumentParser("Solve a Countdown letters game.");
    
    argumentParser.addPositionalArgument(
      "inputLetters", "LETTERS",
      "string containing the letters that can be used to form words",
      1, ArgumentParser.TO_STRING
    );
    
    argumentParser.addOptionalArgument(
      "maxResultsCount", new String[]{"-m"}, "MAX_RESULTS",
      String.format("maximum number of output results (default %d)", MAX_RESULTS_DEFAULT),
      1, new Object[]{MAX_RESULTS_DEFAULT}, ArgumentParser.TO_POSITIVE_INTEGER
    );
    
    argumentParser.addOptionalArgument(
      "wordListFile", new String[]{"-w"}, "WORD_LIST",
      String.format("word list file name (default %s)", WORD_LIST_FILE_NAME_DEFAULT),
      1, new Object[]{WORD_LIST_FILE_NAME_DEFAULT}, ArgumentParser.TO_STRING
    );
    
    return argumentParser.parseCommandLineArguments(arguments);
  }
  
  public static void main(final String[] arguments)
  {
    final Map<String, Object[]> valuesFromName = parseCommandLineArguments(arguments);
    
    final int maxResultsCount = (int) valuesFromName.get("maxResultsCount")[0];
    final String wordListFile = (String) valuesFromName.get("wordListFile")[0];
    final String inputLetters = (String) valuesFromName.get("inputLetters")[0];
    final boolean needHelp = (boolean) valuesFromName.get("needHelp")[0];
    
    System.out.println(String.format("maxResultsCount: %d", maxResultsCount));
    System.out.println(String.format("wordListFile: %s", wordListFile));
    System.out.println(String.format("inputLetters: %s", inputLetters));
    System.out.println(String.format("needHelp: %s", needHelp));
  }
}
