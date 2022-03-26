/*
  # Letters.java
  
  Solve a Countdown letters game.
  
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc., see LICENSE.
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Letters
{
  private static final int MAX_RESULTS_DEFAULT = 30;
  private static final String WORD_LIST_FILE_NAME_DEFAULT = "../yawl.txt";
  
  public static String normaliseLetters(final String string)
  {
    return string.strip().toUpperCase();
  }
  
  public static List<String> toCharacterList(final String string)
  {
    return
      string
        .codePoints()
        .mapToObj(codePoint -> new String(new int[] {codePoint}, 0, 1))
        .collect(Collectors.toList());
  }
  
  public static boolean isValid(final String word, final String inputLetters)
  {
    final List<String> wordLetterList = toCharacterList(word);
    final List<String> inputLetterList = toCharacterList(inputLetters);
    
    for (final String letter : wordLetterList)
    {
      if (Collections.frequency(wordLetterList, letter) > Collections.frequency(inputLetterList, letter))
      {
        return false;
      }
    }
    
    return true;
  }
  
  public static List<String> computeValidWordList(final List<String> wordList, final String inputLetters)
  {
    final List<String> validWordList = new ArrayList<>();
    for (final String word : wordList)
    {
      if (isValid(word, inputLetters))
      {
        validWordList.add(word);
      }
    }
    
    return validWordList;
  }
  
  private static Map<String, Object[]> parseCommandLineArguments(final String[] arguments)
  {
    final ArgumentParser argumentParser = new ArgumentParser("Letters", "Solve a Countdown letters game.");
    
    argumentParser.addPositionalArgument(
      "inputLetters", "LETTERS",
      "string containing the letters that can be used to form words",
      1, ArgumentParser.TO_STRING
    );
    
    argumentParser.addOptionalArgument(
      "maxResultsCount", new String[]{"-m"}, "MAX_RESULTS",
      String.format("maximum number of output results (default %d)", MAX_RESULTS_DEFAULT),
      1, new Object[]{MAX_RESULTS_DEFAULT}, ArgumentParser.TO_INTEGER
    );
    
    argumentParser.addOptionalArgument(
      "wordListFile", new String[]{"-w"}, "WORD_LIST",
      String.format("word list file name (default %s)", WORD_LIST_FILE_NAME_DEFAULT),
      1, new Object[]{WORD_LIST_FILE_NAME_DEFAULT}, ArgumentParser.TO_STRING
    );
    
    return argumentParser.parseCommandLineArguments(arguments);
  }
  
  private static void printResults(final List<String> validWordList, final int maxResultsCount)
  {
    final int resultsCount = Math.min(maxResultsCount, validWordList.size());
    for (final String word : validWordList.subList(0, resultsCount))
    {
      final int score = word.codePointCount(0, word.length());
      System.out.printf("%d\t%s%n", score, word);
    }
  }
  
  public static void main(final String[] arguments)
  {
    final Map<String, Object[]> valuesFromName = parseCommandLineArguments(arguments);
    
    final int maxResultsCount = (int) valuesFromName.get("maxResultsCount")[0];
    final String wordListFile = (String) valuesFromName.get("wordListFile")[0];
    String inputLetters = (String) valuesFromName.get("inputLetters")[0];
    
    inputLetters = normaliseLetters(inputLetters);
    
    final List<String> wordList = new ArrayList<>();
    try
    {
      final FileReader fileReader = new FileReader(wordListFile);
      final BufferedReader bufferedReader = new BufferedReader(fileReader);
      
      String line;
      while ((line = bufferedReader.readLine()) != null)
      {
        wordList.add(normaliseLetters(line));
      }
    }
    catch (IOException exception)
    {
      exception.printStackTrace();
    }
    
    final List<String> validWordList = computeValidWordList(wordList, inputLetters);
    validWordList.sort(Comparator.comparingInt(string -> -string.length()));
    
    printResults(validWordList, maxResultsCount);
  }
}
