/*
  # numbers.py
  
  Solve a Countdown numbers game.
  
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc., see LICENSE.
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    
    // TODO: sorting
    // TODO: print results
  }
  
  /*
  A God-class for expressions.
  
  Basically the goal is to have a canonical representation,
  so that e.g. a + (b + c) is the same as (a + b) + c.
  
  An expression can be either:
  0. TYPE_CONSTANT
          Just a constant, of the form a.
  1. TYPE_ADDITIVE
          Of the form x_1 + x_2 + ... - y_1 - y_2 - ...
          where the terms are non-TYPE_ADDITIVE expressions.
          The parts are (x_1, x_2, ..., y_1, y_2, ...)
          and the signs ( +1,  +1, ...,  -1,  -1, ...),
          with canonical order x_1 >= x_2 >= ...
          and y_1 >= y_2 >= ....
  2. TYPE_MULTIPLICATIVE
          Of the form x_1 * x_2 * ... / y_1 / y_2 / ...
          where the factors are non-TYPE_MULTIPLICATIVE expressions.
          The parts are (x_1, x_2, ..., y_1, y_2, ...)
          and the signs ( +1,  +1, ...,  -1,  -1, ...),
          with canonical order x_1 >= x_2 >= ...
          and y_1 >= y_2 >= ....
  The imposed canonical order ensures preference for
  positive integer results as required by the rules
  of the Countdown numbers game.
  */
  private class Expression
  {
    private static final int TYPE_CONSTANT = 0;
    private static final int TYPE_ADDITIVE = 1;
    private static final int TYPE_MULTIPLICATIVE = 2;
    
    private static final String ADD = "+";
    private static final String SUBTRACT = "-";
    private static final String MULTIPLY = "*";
    private static final String DIVIDE = "/";
    private static final String BINARY_OPERATOR_EXCEPTION_MESSAGE =
            "\n" + String.format("binary operator must be one of %s, %s, %s, %s", ADD, SUBTRACT, MULTIPLY, DIVIDE);
    
    private final int type;
    private final List<Integer> constantsList = new ArrayList<>();
    private final List<Expression> partsList = new ArrayList<>();
    private final List<Integer> signsList = new ArrayList<>();
    private final float value;
    private final int mass;
    private final int depth;
    private final Complexity complexity;
    private final int hash;
    
    private Expression(final int integer)
    {
      type = TYPE_CONSTANT;
      constantsList.add(integer);
      value = integer;
      mass = computeMass();
      depth = computeDepth();
      complexity = new Complexity(this);
      hash = computeHash();
    }
    
    private Expression(final Expression expression1, final Expression expression2, final String operator)
    {
      if (isAdditiveOperator(operator))
      {
        type = TYPE_ADDITIVE;
      }
      else if (isMultiplicativeOperator(operator))
      {
        type = TYPE_MULTIPLICATIVE;
      }
      else
      {
        throw new IllegalArgumentException(BINARY_OPERATOR_EXCEPTION_MESSAGE);
      }
      
      constantsList.addAll(expression1.constantsList);
      constantsList.addAll(expression2.constantsList);
      
      partsList.addAll(getPartsListFor(expression1));
      partsList.addAll(getPartsListFor(expression2));
      signsList.addAll(getSignsListFor(expression1, operator, true));
      signsList.addAll(getSignsListFor(expression2, operator, false));
      sortPartsAndSigns(partsList, signsList);
      
      value = computeValue(expression1.value, expression2.value, operator);
      
      mass = computeMass();
      depth = computeDepth();
      complexity = new Complexity(this);
      hash = computeHash();
    }
    
    private static boolean isAdditiveOperator(final String operator)
    {
      return operator.equals(ADD) || operator.equals(SUBTRACT);
    }
    
    private static boolean isMultiplicativeOperator(final String operator)
    {
      return operator.equals(MULTIPLY) || operator.equals(DIVIDE);
    }
    
    private List<Expression> getPartsListFor(final Expression child)
    {
      if (type == child.type)
      {
        return child.partsList; // so as to flatten it out
      }
      else
      {
        final List<Expression> partsList = new ArrayList<>();
        partsList.add(child);
        return partsList; // keep it as is
      }
    }
    
    private List<Integer> getSignsListFor(final Expression child, final String operator, final boolean isFirstChild)
    {
      final int operatorSign =
              (isFirstChild || operator.equals(ADD) || operator.equals(MULTIPLY))
                ?  1
                : -1;
      
      final List<Integer> signsList = new ArrayList<>();
      if (type == child.type)
      {
        for (final int sign : child.signsList)
        {
          signsList.add(operatorSign * sign);
        }
      }
      else
      {
        signsList.add(operatorSign);
      }
      
      return signsList;
    }
    
    private void sortPartsAndSigns(final List<Expression> partsList, final List<Integer> signsList)
    {
      final int count = partsList.size();
      final List<Integer> sortedIndexList =
              IntStream.rangeClosed(0, count - 1)
                .boxed()
                .sorted(
                  Comparator
                    .comparing(index -> -signsList.get((Integer) index))
                    .thenComparing(index -> -partsList.get((Integer) index).value)
                    // TODO: compare by part itself
                )
                .collect(Collectors.toList());
      
      for (int index = 0; index < count; index++)
      {
        final int sortedIndex = sortedIndexList.get(index);
        partsList.set(index, partsList.get(sortedIndex));
        signsList.set(index, signsList.get(sortedIndex));
      }
    }
    
    private int computeMass()
    {
      return constantsList.size();
    }
    
    private int computeDepth()
    {
      int depth = 0;
      for (Expression part : partsList)
      {
        depth = Math.max(depth, part.depth);
      }
      return depth;
    }
    
    private static float computeValue(final float value1, final float value2, final String operator)
    {
      return switch (operator)
      {
        case ADD -> value1 + value2;
        case SUBTRACT -> value1 - value2;
        case MULTIPLY -> value1 * value2;
        case DIVIDE -> (float) value1 / value2;
        default -> throw new IllegalArgumentException(BINARY_OPERATOR_EXCEPTION_MESSAGE);
      };
    }
    
    private int computeHash()
    {
      return Objects.hash(value, type, partsList, signsList);
    }
    
    private class Complexity
    {
      private final int mass;
      private final int depth;
      private final int firstPartDepth;
      
      Complexity(final Expression expression)
      {
        mass = expression.mass;
        depth = expression.depth;
        
        final List<Expression> partsList = expression.partsList;
        firstPartDepth =
                (partsList.size() == 0)
                  ? 0
                  : partsList.get(0).depth;
      }
    }
  }
}
