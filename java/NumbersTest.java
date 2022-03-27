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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
  
  @Test
  public void expressionEquals_isCorrect()
  {
    final Expression _2 = new Expression(2);
    final Expression _3 = new Expression(3);
    final Expression _4 = new Expression(4);
    final Expression _5 = new Expression(5);
    final Expression _9 = new Expression(9);
    
    final Expression _5_a_2 = new Expression(_5, _2, Expression.ADD);
    final Expression _2_a_5 = new Expression(_2, _5, Expression.ADD);
    final Expression _3_a_4 = new Expression(_3, _4, Expression.ADD);
    final Expression _3_m_3 = new Expression(_3, _3, Expression.MULTIPLY);
    final Expression _9_d_3 = new Expression(_9, _3, Expression.DIVIDE);
    final Expression _3_d_9 = new Expression(_3, _9, Expression.DIVIDE);
    assertEquals(_2_a_5, _2_a_5);
    assertEquals(_2_a_5, _5_a_2);
    assertNotEquals(_2_a_5, _3_a_4);
    assertNotEquals(_3_m_3, _9);
    assertNotEquals(_9_d_3, _3_d_9);
    
    final Expression _3_s_4_aaa_9_s_2_aa_5 =
            new Expression(
              new Expression(_3, _4, Expression.SUBTRACT),
              new Expression(new Expression(_9, _2, Expression.SUBTRACT), _5, Expression.ADD),
              Expression.ADD
            );
    final Expression _9_a_5_a_3_s_4_s_2 =
            new Expression(
              new Expression(
                new Expression(
                  new Expression(
                    _9,
                    _5,
                    Expression.ADD
                  ),
                  _3,
                  Expression.ADD
                ),
                _4,
                Expression.SUBTRACT
              ),
              _2,
              Expression.SUBTRACT
            );
    assertEquals(_3_s_4_aaa_9_s_2_aa_5, _9_a_5_a_3_s_4_s_2);
  
    final Expression _5_a_3_m_4 = new Expression(_5, new Expression(_3, _4, Expression.MULTIPLY), Expression.ADD);
    final Expression _4_m_3_a_5 = new Expression(new Expression(_4, _3, Expression.MULTIPLY), _5, Expression.ADD);
    assertEquals(_5_a_3_m_4, _4_m_3_a_5);
  }
  
  @Test
  public void expressionLessThan_isCorrect()
  {
    /*
      Value descending
    */
    final Expression _6 = new Expression(6);
    final Expression _4 = new Expression(4);
    final Expression _3 = new Expression(3);
    final Expression _2 = new Expression(2);
    assertEquals(-1, _6.compareTo(_4));
    assertEquals(-1, _4.compareTo(_3));
    assertEquals(-1, _3.compareTo(_2));
    
    /*
      First-part value descending
    */
    final Expression _4_a_2 = new Expression(_4, _2, Expression.ADD);
    final Expression _3_a_3 = new Expression(_3, _3, Expression.ADD);
    assertEquals(-1, _4_a_2.compareTo(_3_a_3));
    final Expression _6_m_2 = new Expression(_6, _2, Expression.MULTIPLY);
    final Expression _4_m_3 = new Expression(_4, _3, Expression.MULTIPLY);
    assertEquals(-1, _6_m_2.compareTo(_4_m_3));
    
    /*
      Type (additive then multiplicative)
    */
    final Expression _6_a_4_a_2 = new Expression(_6, _4_a_2, Expression.ADD);
    final Expression _6_m_4_d_2 = new Expression(new Expression(_6, _4, Expression.MULTIPLY), _2, Expression.DIVIDE);
    assertEquals(-1, _6_a_4_a_2.compareTo(_6_m_4_d_2));
    
    /*
      Mass ascending
    */
    final Expression _6_a_4_m_3 = new Expression(_6, _4_m_3, Expression.ADD);
    final Expression _4_a_2_aa_3_a_3 = new Expression(_4_a_2, _3_a_3, Expression.ADD);
    assertEquals(-1, _6_a_4_m_3.compareTo(_4_a_2_aa_3_a_3));
    
    /*
      Depth ascending
    */
    final Expression _6_a_4_m_3_a_2 = new Expression(_6_a_4_m_3, _2, Expression.ADD);
    assertEquals(-1, _4_a_2_aa_3_a_3.compareTo(_6_a_4_m_3_a_2));
    
    /*
      Parts count ascending
    */
    final Expression _4_m_3_m_2 = new Expression(_4_m_3, _2, Expression.MULTIPLY);
    final Expression _6_a_4_m_3_m_2 = new Expression(_6, _4_m_3_m_2, Expression.ADD);
    assertEquals(-1, _6_a_4_m_3_m_2.compareTo(_6_a_4_m_3_a_2));
  }
  
  @Test
  public void computeExpressionSet_isCorrect()
  {
    assertEquals(
      Numbers.computeExpressionSet(Collections.singletonList(70)),
      Collections.singleton(new Expression(70))
    );
    
    {
      final Expression _10 = new Expression(10);
      final Expression _7 = new Expression(7);
      
      assertEquals(
        Numbers.computeExpressionSet(Arrays.asList(7, 10)),
        arraysAsSet(
          /*
            ----------------------------------------------------------------
            Size 1
            ----------------------------------------------------------------
          */
            _10,
            _7,
          /*
            ----------------------------------------------------------------
            Size 2
            ----------------------------------------------------------------
          */
            new Expression(_10, _7, Expression.ADD),
            new Expression(_10, _7, Expression.SUBTRACT),
            new Expression(_10, _7, Expression.MULTIPLY)
            // 10 / 7, not integer
        )
      );
    }
    
    {
      final Expression _10000 = new Expression(10000);
      final Expression _20 = new Expression(20);
      final Expression _3 = new Expression(3);
      
      final Expression _10000_a_20 = new Expression(_10000, _20, Expression.ADD);
      final Expression _10000_s_20 = new Expression(_10000, _20, Expression.SUBTRACT);
      final Expression _10000_m_20 = new Expression(_10000, _20, Expression.MULTIPLY);
      final Expression _10000_d_20 = new Expression(_10000, _20, Expression.DIVIDE);
      
      final Expression _10000_a_3 = new Expression(_10000, _3, Expression.ADD);
      final Expression _10000_s_3 = new Expression(_10000, _3, Expression.SUBTRACT);
      final Expression _10000_m_3 = new Expression(_10000, _3, Expression.MULTIPLY);
      
      final Expression _20_a_3 = new Expression(_20, _3, Expression.ADD);
      final Expression _20_s_3 = new Expression(_20, _3, Expression.SUBTRACT);
      final Expression _20_m_3 = new Expression(_20, _3, Expression.MULTIPLY);
      
      final Expression _10000_a_20_a_3 = new Expression(_10000_a_20, _3, Expression.ADD);
      final Expression _10000_a_20_s_3 = new Expression(_10000_a_20, _3, Expression.SUBTRACT);
      final Expression _10000_a_20_mm_3 = new Expression(_10000_a_20, _3, Expression.MULTIPLY);
      final Expression _10000_a_20_dd_3 = new Expression(_10000_a_20, _3, Expression.DIVIDE);
      
      final Expression _10000_s_20_s_3 = new Expression(_10000_s_20, _3, Expression.SUBTRACT);
      final Expression _10000_s_20_mm_3 = new Expression(_10000_s_20, _3, Expression.MULTIPLY);
      
      final Expression _10000_m_20_a_3 = new Expression(_10000_m_20, _3, Expression.ADD);
      final Expression _10000_m_20_s_3 = new Expression(_10000_m_20, _3, Expression.SUBTRACT);
      final Expression _10000_m_20_m_3 = new Expression(_10000_m_20, _3, Expression.MULTIPLY);
      
      final Expression _10000_d_20_a_3 = new Expression(_10000_d_20, _3, Expression.ADD);
      final Expression _10000_d_20_s_3 = new Expression(_10000_d_20, _3, Expression.SUBTRACT);
      final Expression _10000_d_20_m_3 = new Expression(_10000_d_20, _3, Expression.MULTIPLY);
      
      final Expression _10000_a_3_s_20 = new Expression(_10000_a_3, _20, Expression.SUBTRACT);
      final Expression _10000_a_3_mm_20 = new Expression(_10000_a_3, _20, Expression.MULTIPLY);
      
      final Expression _10000_s_3_mm_20 = new Expression(_10000_s_3, _20, Expression.MULTIPLY);
      
      final Expression _10000_m_3_a_20 = new Expression(_10000_m_3, _20, Expression.ADD);
      final Expression _10000_m_3_s_20 = new Expression(_10000_m_3, _20, Expression.SUBTRACT);
      
      final Expression _10000_mm_20_a_3 = new Expression(_10000, _20_a_3, Expression.MULTIPLY);
      
      final Expression _10000_mm_20_s_3 = new Expression(_10000, _20_s_3, Expression.MULTIPLY);
      
      final Expression _10000_aa_20_m_3 = new Expression(_10000, _20_m_3, Expression.ADD);
      final Expression _10000_ss_20_m_3 = new Expression(_10000, _20_m_3, Expression.SUBTRACT);
      
      assertEquals(
        Numbers.computeExpressionSet(Arrays.asList(3, 20, 10000)),
        arraysAsSet(
          /*
            ----------------------------------------------------------------
            Size 1
            ----------------------------------------------------------------
          */
            _10000,
            _20,
            _3,
          /*
            ----------------------------------------------------------------
            Size 2
            ----------------------------------------------------------------
          */
          /* (10000, 20) */
            _10000_a_20,
            _10000_s_20,
            _10000_m_20,
            _10000_d_20,
          /* (10000, 3) */
            _10000_a_3,
            _10000_s_3,
            _10000_m_3,
            // 10000 / 3, not integer
          /* (20, 3) */
            _20_a_3,
            _20_s_3,
            _20_m_3,
            // 20 / 3, not integer
          /*
            ----------------------------------------------------------------
            Size 3
            ----------------------------------------------------------------
          */
          /* (10000 + 20, 3) */
            _10000_a_20_a_3,
            _10000_a_20_s_3,
            _10000_a_20_mm_3,
            _10000_a_20_dd_3,
          /* (10000 - 20, 3) */
            // 10000 - 20 + 3, equivalent to (10000 + 3) - 20
            _10000_s_20_s_3,
            _10000_s_20_mm_3,
            // (10000 - 20) / 3, not integer
          /* (10000 * 20, 3) */
            _10000_m_20_a_3,
            _10000_m_20_s_3,
            _10000_m_20_m_3,
            // (10000 * 20) / 3, not integer
          /* (10000 / 20, 3) */
            _10000_d_20_a_3,
            _10000_d_20_s_3,
            _10000_d_20_m_3,
            // (10000 / 20) / 3, not integer
          /* (10000 + 3, 20) */
            // (10000 + 3) + 20, equivalent to (10000 + 20) + 3
            _10000_a_3_s_20,
            _10000_a_3_mm_20,
            // (10000 + 3) / 20, not integer
          /* (10000 - 3, 20) */
            // (10000 - 3) + 20, equivalent to (10000 + 20) - 3
            // (10000 - 3) - 20, equivalent to (10000 - 20) - 3
            _10000_s_3_mm_20,
            // (10000 - 3) / 20, not integer
          /* (10000 * 3, 20) */
            _10000_m_3_a_20,
            _10000_m_3_s_20,
            // (10000 * 3) * 20, equivalent to (10000 * 20) * 3
            // (10000 * 3) / 20, not integer
          /* (10000, 20 + 3) */
            // 10000 + (20 + 3), equivalent to (10000 + 20) + 3
            // 10000 - (20 + 3), equivalent to (10000 - 20) - 3
            _10000_mm_20_a_3,
            // 10000 / (20 + 3), not integer
          /* (10000, 20 - 3) */
            // 10000 + (20 - 3), equivalent to (10000 + 20) - 3
            // 10000 - (20 - 3), equivalent to (10000 + 3) - 20
            _10000_mm_20_s_3,
            // 10000 / (20 - 3), not integer
          /* (10000, 20 * 3) */
            _10000_aa_20_m_3,
            _10000_ss_20_m_3
            // 10000 * (20 * 3), equivalent to (10000 * 20) * 3
            // 10000 / (20 * 3), not integer
        )
      );
    }
  }
  
  /*
    Abbreviation for new HashSet<>(Arrays.asList(...))
  */
  private static Set<Expression> arraysAsSet(Expression... expressions)
  {
    return new HashSet<>(Arrays.asList(expressions));
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
