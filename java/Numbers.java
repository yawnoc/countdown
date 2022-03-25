/*
  # numbers.py
  
  Solve a Countdown numbers game.
  
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc., see LICENSE.
*/

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Numbers
{
  private static final int MAX_RESULTS_DEFAULT = 30;
  
  private static Map<String, Object[]> parseCommandLineArguments(final String[] arguments)
  {
    final ArgumentParser argumentParser = new ArgumentParser("Letters", "Solve a Countdown numbers game.");
    
    argumentParser.addPositionalArgument(
      "target", "TARGET",
      "target number (positive integer)",
      1, ArgumentParser.TO_POSITIVE_INTEGER
    );
  
    argumentParser.addPositionalArgument(
      "inputNumbers", "NUMBER",
      "number (positive integer) that can be used to obtain the target",
      Integer.MAX_VALUE, ArgumentParser.TO_POSITIVE_INTEGER
    );
    
    argumentParser.addOptionalArgument(
      "maxResultsCount", new String[]{"-m"}, "MAX_RESULTS",
      String.format("maximum number of output results (default %d)", MAX_RESULTS_DEFAULT),
      1, new Object[]{MAX_RESULTS_DEFAULT}, ArgumentParser.TO_INTEGER
    );
    
    return argumentParser.parseCommandLineArguments(arguments);
  }
  
  public static void main(final String[] arguments)
  {
    final Map<String, Object[]> valuesFromName = parseCommandLineArguments(arguments);
    
    final int target = (int) valuesFromName.get("target")[0];
    final List<Integer> inputNumberList =
            Arrays.stream(valuesFromName.get("inputNumbers"))
              .mapToInt(i -> (int) i)
              .boxed()
              .collect(Collectors.toList());
    final int maxResultsCount = (int) valuesFromName.get("maxResultsCount")[0];
    
    System.out.println("target: " + target);
    System.out.println("inputNumberList: " + inputNumberList);
    System.out.println("maxResultsCount: " + maxResultsCount);
  }
}
