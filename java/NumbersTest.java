/*
  # NumbersTest.java
  
  Perform unit testing for `Numbers.java`.
  
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc., see LICENSE.
  
  For brevity, we abbreviate expressions thus:
      _3_a_7 --> 3 + 7
      _3_s_7 --> 3 - 7
      _3_m_7 --> 3 * 7
      _3_d_7 --> 3 / 7
      _3_a_7_mm_2_s_1 --> (3 + 7) * (2 - 1)
  
  Basically:
  1. Begin the variable name with a single underscore
  2. Separate operands and operators with a single underscore
  3. Denote operators with letters:
     - a for ADD
     - s for SUBTRACT
     - m for MULTIPLY
     - d for DIVIDE
  4. Repeat operator letters to denote lower precedence
*/

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Arrays;

public class NumbersTest
{
  private static final float FLOAT_ASSERTION_DELTA = 0f;
  
  @Test
  public void isPositiveInteger_isCorrect()
  {
    assertTrue(Numbers.isPositiveInteger(1));
    assertTrue(Numbers.isPositiveInteger(2));
    assertTrue(Numbers.isPositiveInteger(1000));
    assertTrue(Numbers.isPositiveInteger(7.0f));
    
    assertFalse(Numbers.isPositiveInteger(0));
    assertFalse(Numbers.isPositiveInteger(-1));
    assertFalse(Numbers.isPositiveInteger(2/3f));
    assertFalse(Numbers.isPositiveInteger(7.77f));
  }
  
  @Test
  public void expression_isCorrect()
  {
    final Expression _2 = new Expression(2);
    final Expression _3 = new Expression(3);
    final Expression _4 = new Expression(4);
    final Expression _5 = new Expression(5);
    final Expression _9 = new Expression(9);
    
    final Expression _2_a_2 = new Expression(_2, _2, Expression.ADD);
    final Expression _9_s_4 = new Expression(_9, _4, Expression.SUBTRACT);
    final Expression _3_m_2 = new Expression(_3, _2, Expression.MULTIPLY);
    final Expression _5_d_2 = new Expression(_5, _2, Expression.DIVIDE);
  
    final Expression _2_a_2_mm_9_s_4 = new Expression(_2_a_2, _9_s_4, Expression.MULTIPLY);
    final Expression _3_m_2_s_5_d_2 = new Expression(_3_m_2, _5_d_2, Expression.SUBTRACT);
    
    final Expression _2_a_2_mm_9_s_4_dd_3_m_2_s_5_d_2 =
            new Expression(_2_a_2_mm_9_s_4, _3_m_2_s_5_d_2, Expression.DIVIDE);
    
    assertEquals(_2_a_2.getValue(), 2 + 2, FLOAT_ASSERTION_DELTA);
    assertEquals(_9_s_4.getValue(), 9 - 4, FLOAT_ASSERTION_DELTA);
    assertEquals(_3_m_2.getValue(), 3 * 2, FLOAT_ASSERTION_DELTA);
    assertEquals(_5_d_2.getValue(), 5 / 2f, FLOAT_ASSERTION_DELTA);
    assertEquals(
      _2_a_2_mm_9_s_4_dd_3_m_2_s_5_d_2.getValue(),
      (9 - 4) * (2 + 2) / (3 * 2 - 5 / 2f),
      FLOAT_ASSERTION_DELTA
    );
    
    assertEquals(_2_a_2.getConstantsList(), Arrays.asList(2, 2));
    assertEquals(_9_s_4.getConstantsList(), Arrays.asList(9, 4));
    assertEquals(_3_m_2.getConstantsList(), Arrays.asList(3, 2));
    assertEquals(_5_d_2.getConstantsList(), Arrays.asList(5, 2));
    assertEquals(_2_a_2_mm_9_s_4_dd_3_m_2_s_5_d_2.getConstantsList(), Arrays.asList(2, 2, 9, 4, 3, 2, 5, 2));
    
    assertEquals(_2_a_2.getPartsList(), Arrays.asList(_2, _2));
    assertEquals(_9_s_4.getPartsList(), Arrays.asList(_9, _4));
    assertEquals(_3_m_2.getPartsList(), Arrays.asList(_3, _2));
    assertEquals(_5_d_2.getPartsList(), Arrays.asList(_5, _2));
    assertEquals(_2_a_2_mm_9_s_4_dd_3_m_2_s_5_d_2.getPartsList(), Arrays.asList(_9_s_4, _2_a_2, _3_m_2_s_5_d_2));
  
    assertEquals(_2_a_2.getSignsList(), Arrays.asList(1, 1));
    assertEquals(_9_s_4.getSignsList(), Arrays.asList(1, -1));
    assertEquals(_3_m_2.getSignsList(), Arrays.asList(1, 1));
    assertEquals(_5_d_2.getSignsList(), Arrays.asList(1, -1));
    assertEquals(_2_a_2_mm_9_s_4_dd_3_m_2_s_5_d_2.getSignsList(), Arrays.asList(1, 1, -1));
  }
  
  /*
    Alias Numbers.Expression as Expression
  */
  private static class Expression extends Numbers.Expression
  {
    public Expression(int integer) {
      super(integer);
    }
    
    public Expression(Expression expression1, Expression expression2, String operator) {
      super(expression1, expression2, operator);
    }
  }
}
