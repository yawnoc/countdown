#!/usr/bin/env python3

"""
# test_numbers.py

Perform unit testing on `numbers.py`.

Copyright 2022 Conway
Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
This is free software with NO WARRANTY etc. etc., see LICENSE.

For brevity, we make use of the walrus operator
and abbreviate expressions thus:

    _3_a_7 --> 3 + 7
    _3_s_7 --> 3 - 7
    _3_m_7 --> 3 * 7
    _3_d_7 --> 3 / 7
    _3_a_7_mm_2_s_1 --> (3 + 7) * (2 - 1)

Basically:

1. Begin the variable name with a single underscore
2. Separate operands and operators with a single underscore
3. Denote operators with letters:
   - a for add
   - s for sub
   - m for mul
   - d for (true)div
4. Repeat operator letters to denote lower precedence
"""


import numbers as n
import operator
import unittest


class TestNumbers(unittest.TestCase):
  
  def test_expression(self):
    
    _2 = n.Expression(2)
    _3 = n.Expression(3)
    _4 = n.Expression(4)
    _5 = n.Expression(5)
    _9 = n.Expression(9)
    
    _2_a_2 = n.Expression(_2, _2, operator.add)
    _9_s_4 = n.Expression(_9, _4, operator.sub)
    _3_m_2 = n.Expression(_3, _2, operator.mul)
    _5_d_2 = n.Expression(_5, _2, operator.truediv)
    
    _2_a_2_mm_9_s_4 = n.Expression(_2_a_2, _9_s_4, operator.mul)
    _3_m_2_s_5_d_2 = n.Expression(_3_m_2, _5_d_2, operator.sub)
    
    _2_a_2_mm_9_s_4_dd_3_m_2_s_5_d_2 = \
            n.Expression(_2_a_2_mm_9_s_4, _3_m_2_s_5_d_2, operator.truediv)
    
    self.assertEqual(_2_a_2.value, 2 + 2)
    self.assertEqual(_9_s_4.value, 9 - 4)
    self.assertEqual(_3_m_2.value, 3 * 2)
    self.assertEqual(_5_d_2.value, 5 / 2)
    self.assertEqual(
      _2_a_2_mm_9_s_4_dd_3_m_2_s_5_d_2.value,
      (9 - 4) * (2 + 2) / (3 * 2 - 5 / 2)
    )
    
    self.assertEqual(_2_a_2.constants, [2, 2])
    self.assertEqual(_9_s_4.constants, [9, 4])
    self.assertEqual(_3_m_2.constants, [3, 2])
    self.assertEqual(_5_d_2.constants, [5, 2])
    self.assertEqual(
      _2_a_2_mm_9_s_4_dd_3_m_2_s_5_d_2.constants,
      [2, 2, 9, 4, 3, 2, 5, 2]
    )
    
    self.assertEqual(_2_a_2.parts, (_2, _2))
    self.assertEqual(_9_s_4.parts, (_9, _4))
    self.assertEqual(_3_m_2.parts, (_3, _2))
    self.assertEqual(_5_d_2.parts, (_5, _2))
    self.assertEqual(
      _2_a_2_mm_9_s_4_dd_3_m_2_s_5_d_2.parts,
      (_9_s_4, _2_a_2, _3_m_2_s_5_d_2)
    )
    
    self.assertEqual(_2_a_2.signs, (1, 1))
    self.assertEqual(_9_s_4.signs, (1, -1))
    self.assertEqual(_3_m_2.signs, (1, 1))
    self.assertEqual(_5_d_2.signs, (1, -1))
    self.assertEqual(_2_a_2_mm_9_s_4_dd_3_m_2_s_5_d_2.signs, (1, 1, -1))
  
  def test_is_positive_integer(self):
    
    self.assertTrue(n.is_positive_integer(1))
    self.assertTrue(n.is_positive_integer(2))
    self.assertTrue(n.is_positive_integer(1000))
    self.assertTrue(n.is_positive_integer(7.0))
    
    self.assertFalse(n.is_positive_integer(0))
    self.assertFalse(n.is_positive_integer(-1))
    self.assertFalse(n.is_positive_integer(2/3))
    self.assertFalse(n.is_positive_integer(7.77))
  
  def test_compute_expression_set(self):
    
    self.assertCountEqual(
      n.compute_expression_set([70]),
      [n.Expression(70)]
    )
    
    self.assertCountEqual(
      n.compute_expression_set([7, 10]),
      [
        # ----------------------------------------------------------------
        # Size 1
        # ----------------------------------------------------------------
          n.Expression(10),
          n.Expression(7),
        # ----------------------------------------------------------------
        # Size 2
        # ----------------------------------------------------------------
          n.Expression(n.Expression(10), n.Expression(7), operator.add),
          n.Expression(n.Expression(10), n.Expression(7), operator.sub),
          n.Expression(n.Expression(10), n.Expression(7), operator.mul),
          # 10 / 7, not integer
      ]
    )
    
    self.assertCountEqual(
      n.compute_expression_set([3, 20, 10000]),
      [
        # ----------------------------------------------------------------
        # Size 1
        # ----------------------------------------------------------------
          _10000 := n.Expression(10000),
          _20 := n.Expression(20),
          _3 := n.Expression(3),
        # ----------------------------------------------------------------
        # Size 2
        # ----------------------------------------------------------------
        # --------------------------------
        # (10000, 20)
        # --------------------------------
          _10000_a_20 := n.Expression(_10000, _20, operator.add),
          _10000_s_20 := n.Expression(_10000, _20, operator.sub),
          _10000_m_20 := n.Expression(_10000, _20, operator.mul),
          _10000_d_20 := n.Expression(_10000, _20, operator.truediv),
        # --------------------------------
        # (10000, 3)
        # --------------------------------
          _10000_a_3 := n.Expression(_10000, _3, operator.add),
          _10000_s_3 := n.Expression(_10000, _3, operator.sub),
          _10000_m_3 := n.Expression(_10000, _3, operator.mul),
          #_10000_d_3 := 10000 / 3, not integer
        # --------------------------------
        # (20, 3)
        # --------------------------------
          _20_a_3 := n.Expression(_20, _3, operator.add),
          _20_s_3 := n.Expression(_20, _3, operator.sub),
          _20_m_3 := n.Expression(_20, _3, operator.mul),
          #_20_d_3 := 20 / 3, not integer
        # ----------------------------------------------------------------
        # Size 3
        # ----------------------------------------------------------------
        # --------------------------------
        # (10000 + 20, 3)
        # --------------------------------
          _10000_a_20_a_3 := n.Expression(_10000_a_20, _3, operator.add),
          _10000_a_20_s_3 := n.Expression(_10000_a_20, _3, operator.sub),
          _10000_a_20_mm_3 := n.Expression(_10000_a_20, _3, operator.mul),
          _10000_a_20_dd_3 := n.Expression(_10000_a_20, _3, operator.truediv),
        # --------------------------------
        # (10000 - 20, 3)
        # --------------------------------
          #_10000_s_20_a_3 := 10000 - 20 + 3, equivalent to (10000 + 3) - 20
          _10000_s_20_s_3 := n.Expression(_10000_s_20, _3, operator.sub),
          _10000_s_20_mm_3 := n.Expression(_10000_s_20, _3, operator.mul),
          #_10000_s_20_dd_3 := (10000 - 20) / 3, not integer
        # --------------------------------
        # (10000 * 20, 3)
        # --------------------------------
          _10000_m_20_a_3 := n.Expression(_10000_m_20, _3, operator.add),
          _10000_m_20_s_3 := n.Expression(_10000_m_20, _3, operator.sub),
          _10000_m_20_m_3 := n.Expression(_10000_m_20, _3, operator.mul),
          #_10000_m_20_d_3 := (10000 * 20) / 3, not integer
        # --------------------------------
        # (10000 / 20, 3)
        # --------------------------------
          _10000_d_20_a_3 := n.Expression(_10000_d_20, _3, operator.add),
          _10000_d_20_s_3 := n.Expression(_10000_d_20, _3, operator.sub),
          _10000_d_20_m_3 := n.Expression(_10000_d_20, _3, operator.mul),
          #_10000_d_20_d_3 := (10000 / 20) / 3, not integer
        # --------------------------------
        # (10000 + 3, 20)
        # --------------------------------
          #_10000_a_3_a_20 := (10000 + 3) + 20, equivalent to (10000 + 20) + 3
          _10000_a_3_s_20 := n.Expression(_10000_a_3, _20, operator.sub),
          _10000_a_3_mm_20 := n.Expression(_10000_a_3, _20, operator.mul),
          #_10000_a_3_dd_20 := (10000 + 3) / 20, not integer
        # --------------------------------
        # (10000 - 3, 20)
        # --------------------------------
          #_10000_s_3_a_20 := (10000 - 3) + 20, equivalent to (10000 + 20) - 3
          #_10000_s_3_s_20 := (10000 - 3) - 20, equivalent to (10000 - 20) - 3
          _10000_s_3_mm_20 := n.Expression(_10000_s_3, _20, operator.mul),
          #_10000_s_3_dd_20 := (10000 - 3) / 20, not integer
        # --------------------------------
        # (10000 * 3, 20)
        # --------------------------------
          _10000_m_3_a_20 := n.Expression(_10000_m_3, _20, operator.add),
          _10000_m_3_s_20 := n.Expression(_10000_m_3, _20, operator.sub),
          #_10000_m_3_m_20 := (10000 * 3) * 20, equivalent to (10000 * 20) * 3
          #_10000_m_3_d_20 := (10000 * 3) / 20, not integer
        # --------------------------------
        # (10000, 20 + 3)
        # --------------------------------
          #_10000_aa_20_a_3 := 10000 + (20 + 3), equivalent to (10000 + 20) + 3
          #_10000_ss_20_a_3 := 10000 - (20 + 3), equivalent to (10000 - 20) - 3
          _10000_mm_20_a_3 := n.Expression(_10000, _20_a_3, operator.mul),
          #_10000_dd_20_a_3 := 10000 / (20 + 3), not integer
        # --------------------------------
        # (10000, 20 - 3)
        # --------------------------------
          #_10000_aa_20_s_3 := 10000 + (20 - 3), equivalent to (10000 + 20) - 3
          #_10000_ss_20_s_3 := 10000 - (20 - 3), equivalent to (10000 + 3) - 20
          _10000_mm_20_s_3 := n.Expression(_10000, _20_s_3, operator.mul),
          #_10000_dd_20_s_3 := 10000 / (20 - 3), not integer
        # --------------------------------
        # (10000, 20 * 3)
        # --------------------------------
          _10000_aa_20_m_3 := n.Expression(_10000, _20_m_3, operator.add),
          _10000_ss_20_m_3 := n.Expression(_10000, _20_m_3, operator.sub),
          #_10000_mm_20_m_3 := 10000 * (20 * 3), equivalent to (10000 * 20) * 3
          #_10000_dd_20_m_3 := 10000 / (20 * 3), not integer
      ]
    )
    
    self.assertCountEqual(
      n.compute_expression_set([1, 1, 2, 3]),
      [
        # ----------------------------------------------------------------
        # Size 1
        # ----------------------------------------------------------------
          _3 := n.Expression(3),
          _2 := n.Expression(2),
          _1 := n.Expression(1),
        # ----------------------------------------------------------------
        # Size 2
        # ----------------------------------------------------------------
        # --------------------------------
        # (3, 2)
        # --------------------------------
          _3_a_2 := n.Expression(_3, _2, operator.add),
          _3_s_2 := n.Expression(_3, _2, operator.sub),
          _3_m_2 := n.Expression(_3, _2, operator.mul),
          #_3_d_2 := 3 / 2, not integer
        # --------------------------------
        # (3, 1)
        # --------------------------------
          _3_a_1 := n.Expression(_3, _1, operator.add),
          _3_s_1 := n.Expression(_3, _1, operator.sub),
          #_3_m_1 := 3 * 1, redundant
          #_3_d_1 := 3 / 1, redundant
        # --------------------------------
        # (2, 1)
        # --------------------------------
          _2_a_1 := n.Expression(_2, _1, operator.add),
          _2_s_1 := n.Expression(_2, _1, operator.sub),
          #_2_m_1 := 2 * 1, redundant
          #_2_d_1 := 2 / 1, redundant
        # --------------------------------
        # (1, 1)
        # --------------------------------
          _1_a_1 := n.Expression(_1, _1, operator.add),
          #_1_s_1 := 1 - 1, not positive
          #_1_m_1 := 1 * 1, redundant
          #_1_d_1 := 1 / 1, redundant
        # ----------------------------------------------------------------
        # Size 3
        # ----------------------------------------------------------------
        # --------------------------------
        # (3 + 2, 1)
        # --------------------------------
          _3_a_2_a_1 := n.Expression(_3_a_2, _1, operator.add),
          _3_a_2_s_1 := n.Expression(_3_a_2, _1, operator.sub),
          #_3_a_2_mm_1 := (3 + 2) * 1, redundant
          #_3_a_2_dd_1 := (3 + 2) / 1, redundant
        # --------------------------------
        # (3 - 2, 1)
        # --------------------------------
          #_1_aa_3_s_2 := 1 + (3 - 2), equivalent to (3 + 1) - 2
          #_1_ss_3_s_2 := 1 - (3 - 2), not positive
          #_1_mm_3_s_2 := 1 * (3 - 2), redundant
          #_1_dd_3_s_2 := 1 / (3 - 2), redundant
          #_3_s_2_dd_1 := (3 - 2) / 1, redundant
        # --------------------------------
        # (3 * 2, 1)
        # --------------------------------
          _3_m_2_a_1 := n.Expression(_3_m_2, _1, operator.add),
          _3_m_2_s_1 := n.Expression(_3_m_2, _1, operator.sub),
          #_3_m_2_m_1 := (3 * 2) * 1, redundant
          #_3_m_2_d_1 := (3 * 2) / 1, redundant
        # --------------------------------
        # (3 + 1, 2)
        # --------------------------------
          #_3_a_1_a_2 := (3 + 1) + 2, equivalent to (3 + 2) + 1
          _3_a_1_s_2 := n.Expression(_3_a_1, _2, operator.sub),
          _3_a_1_mm_2 := n.Expression(_3_a_1, _2, operator.mul),
          _3_a_1_dd_2 := n.Expression(_3_a_1, _2, operator.truediv),
        # --------------------------------
        # (3 + 1, 1)
        # --------------------------------
          _3_a_1_a_1 := n.Expression(_3_a_1, _1, operator.add),
          _3_a_1_s_1 := n.Expression(_3_a_1, _1, operator.sub),
          #_3_a_1_mm_1 := (3 + 1) * 1, redundant
          #_3_a_1_dd_1 := (3 + 1) / 1, redundant
        # --------------------------------
        # (3 - 1, 2)
        # --------------------------------
          #_2_aa_3_s_1 := 2 + (3 - 1), equivalent to (3 + 2) - 1
          #_2_ss_3_s_1 := 2 - (3 - 1), not positive
          _2_mm_3_s_1 := n.Expression(_2, _3_s_1, operator.mul),
          _2_dd_3_s_1 := n.Expression(_2, _3_s_1, operator.truediv),
          _3_s_1_dd_2 := n.Expression(_3_s_1, _2, operator.truediv),
        # --------------------------------
        # (3 - 1, 1)
        # --------------------------------
          #_3_s_1_a_1 := (3 - 1) + 1, equivalent to (3 + 1) - 1
          _3_s_1_s_1 := n.Expression(_3_s_1, _1, operator.sub),
          #_3_s_1_mm_1 := (3 - 1) * 1, redundant
          #_3_s_1_dd_1 := (3 - 1) / 1, redundant
        # --------------------------------
        # (2 + 1, 3)
        # --------------------------------
          #_3_aa_2_a_1 := 3 + (2 + 1), equivalent to (3 + 2) + 1
          #_3_ss_2_a_1 := 3 - (2 + 1), not positive
          _3_mm_2_a_1 := n.Expression(_3, _2_a_1, operator.mul),
          _3_dd_2_a_1 := n.Expression(_3, _2_a_1, operator.truediv),
          _2_a_1_dd_3 := n.Expression(_2_a_1, _3, operator.truediv),
        # --------------------------------
        # (2 + 1, 1)
        # --------------------------------
          _2_a_1_a_1 := n.Expression(_2_a_1, _1, operator.add),
          _2_a_1_s_1 := n.Expression(_2_a_1, _1, operator.sub),
          #_2_a_1_mm_1 := (2 + 1) * 1, redundant
          #_2_a_1_dd_1 := (2 + 1) / 1, redundant
        # --------------------------------
        # (2 - 1, 3)
        # --------------------------------
          #_3_aa_2_s_1 := 3 + (2 - 1), equivalent to (3 + 2) - 1
          #_3_ss_2_s_1 := 3 - (2 - 1), equivalent to (3 + 1) - 2
          #_3_mm_2_s_1 := 3 * (2 - 1), redundant
          #_3_dd_2_s_1 := 3 / (2 - 1), redundant
        # --------------------------------
        # (2 - 1, 1)
        # --------------------------------
          #_1_aa_2_s_1 := 1 + (2 - 1), equivalent to (2 + 1) - 1
          #_1_ss_2_s_1 := 1 - (2 - 1), not positive
          #_1_mm_2_s_1 := 1 * (2 - 1), redundant
          #_1_dd_2_s_1 := 1 / (2 - 1), (2 - 1) / 1, redundant
        # --------------------------------
        # (1 + 1, 3)
        # --------------------------------
          #_3_aa_1_a_1 := 3 + (1 + 1), equivalent to (3 + 1) + 1
          #_3_ss_1_a_1 := 3 - (1 + 1), equivalent to (3 - 1) - 1
          _3_mm_1_a_1 := n.Expression(_3, _1_a_1, operator.mul),
          #_3_dd_1_a_1 := 3 / (1 + 1), not integer
        # --------------------------------
        # (1 + 1, 2)
        # --------------------------------
          #_2_aa_1_a_1 := 2 + (1 + 1), equivalent to (2 + 1) + 1
          #_2_ss_1_a_1 := 2 - (1 + 1), not positive
          _2_mm_1_a_1 := n.Expression(_2, _1_a_1, operator.mul),
          _2_dd_1_a_1 := n.Expression(_2, _1_a_1, operator.truediv),
          _1_a_1_dd_2 := n.Expression(_1_a_1, _2, operator.truediv),
        # ----------------------------------------------------------------
        # Size 4
        # ----------------------------------------------------------------
        # --------------------------------
        # (3 + 2 + 1, 1)
        # --------------------------------
          _3_a_2_a_1_a_1 := n.Expression(_3_a_2_a_1, _1, operator.add),
          _3_a_2_a_1_s_1 := n.Expression(_3_a_2_a_1, _1, operator.sub),
          #_3_a_2_a_1_mm_1 := (3 + 2 + 1) * 1, redundant
          #_3_a_2_a_1_dd_1 := (3 + 2 + 1) / 1, redundant
        # --------------------------------
        # (3 + 2 - 1, 1)
        # --------------------------------
          #_3_a_2_s_1_aa_1 := (3 + 2 - 1) + 1, same as (3 + 2 + 1) - 1
          _3_a_2_s_1_s_1 := n.Expression(_3_a_2_s_1, _1, operator.sub),
          #_3_a_2_s_1_mm_1 := (3 + 2 - 1) * 1, redundant
          #_3_a_2_s_1_dd_1 ;= (3 + 2 - 1) / 1, redundant
        # --------------------------------
        # (3 * 2 + 1, 1)
        # --------------------------------
          _3_m_2_a_1_a_1 := n.Expression(_3_m_2_a_1, _1, operator.add),
          _3_m_2_a_1_s_1 := n.Expression(_3_m_2_a_1, _1, operator.sub),
          #_3_m_2_a_1_mm_1 := (3 * 2 + 1) * 1, redundant
          #_3_m_2_a_1_dd_1 := (3 * 2 + 1) / 1, redundant
        # --------------------------------
        # (3 * 2 - 1, 1)
        # --------------------------------
          #_3_m_2_s_1_aa_1 := (3 * 2 - 1) + 1, equivalent to (3 * 2 + 1) - 1
          _3_m_2_s_1_s_1 := n.Expression(_3_m_2_s_1, _1, operator.sub),
          #_3_m_2_s_1_mm_1 := (3 * 2 - 1) * 1, redundant
          #_3_m_2_s_1_dd_1 := (3 * 2 - 1) / 1, redundant
        # --------------------------------
        # (3 + 1 - 2, 1)
        # --------------------------------
          #_3_a_1_s_2_aa_1 := (3 + 1 - 2) + 1, equivalent to (3 + 1 + 1) - 2
          _3_a_1_s_2_ss_1 := n.Expression(_3_a_1_s_2, _1, operator.sub),
          #_3_a_1_s_2_mm_1 := (3 + 1 - 2) * 1, redundant
          #_3_a_1_s_2_dd_1 := (3 + 1 - 2) / 1, redundant
        # --------------------------------
        # ((3 + 1) * 2, 1)
        # --------------------------------
          _3_a_1_mm_2_aaa_1 := n.Expression(_3_a_1_mm_2, _1, operator.add),
          _3_a_1_mm_2_sss_1 := n.Expression(_3_a_1_mm_2, _1, operator.sub),
          #_3_a_1_mm_2_mm_1 := ((3 + 1) * 2) * 1, redundant
          #_3_a_1_mm_2_dd_1 := ((3 + 1) * 2) / 1, redundant
        # --------------------------------
        # ((3 + 1) / 2, 1)
        # --------------------------------
          _3_a_1_dd_2_aaa_1 := n.Expression(_3_a_1_dd_2, _1, operator.add),
          _3_a_1_dd_2_sss_1 := n.Expression(_3_a_1_dd_2, _1, operator.sub),
          #_3_a_1_dd_2_mmm_1 := ((3 + 1) / 2) * 1, redundant
          #_3_a_1_dd_2_dd_1 := ((3 + 1) / 2) / 1, redundant
        # --------------------------------
        # (3 + 1 + 1, 2)
        # --------------------------------
          #_3_a_1_a_1_aa_2 := (3 + 1 + 1) + 2, equivalent to (3 + 2 + 1) + 1
          _3_a_1_a_1_ss_2 := n.Expression(_3_a_1_a_1, _2, operator.sub),
          _3_a_1_a_1_mm_2 := n.Expression(_3_a_1_a_1, _2, operator.mul),
          #_3_a_1_a_1_dd_2 := (3 + 1 + 1) / 2, not integer
        # --------------------------------
        # (3 + 1 - 1, 2)
        # --------------------------------
          #_3_a_1_s_1_aa_2 := (3 + 1 - 1) + 2, equivalent to (3 + 2 + 1) - 1
          #_3_a_1_s_1_ss_2 := (3 + 1 - 1) - 2, equivalent to (3 + 1 - 2) - 1
          _3_a_1_s_1_mm_2 := n.Expression(_3_a_1_s_1, _2, operator.mul),
          #_3_a_1_s_1_dd_2 := (3 + 1 - 1) / 2, not integer
        # --------------------------------
        # (2 * (3 - 1), 1)
        # --------------------------------
          _2_mm_3_s_1_aa_1 := n.Expression(_2_mm_3_s_1, _1, operator.add),
          _2_mm_3_s_1_ss_1 := n.Expression(_2_mm_3_s_1, _1, operator.sub),
          #_2_mm_3_s_1_mm_1 := (2 * (3 - 1)) * 1, redundant
          #_2_mm_3_s_1_dd_1 := (2 * (3 - 1)) / 1, redundant
        # --------------------------------
        # (2 / (3 - 1), 1)
        # --------------------------------
          _2_dd_3_s_1_aa_1 := n.Expression(_2_dd_3_s_1, _1, operator.add),
          _3_s_1_dd_2_aa_1 := n.Expression(_3_s_1_dd_2, _1, operator.add),
          #_2_dd_3_s_1_ss_1 := 2 / (3 - 1) - 1, not positive
          #_2_dd_3_s_1_mm_1 := 2 / (3 - 1) * 1, redundant
          #_2_dd_3_s_1_dd_1 := 2 / (3 - 1) / 1, redundant
        # --------------------------------
        # (3 - 1 - 1, 2)
        # --------------------------------
          #(3 - 1 - 1) + 2, equivalent to (3 + 2 - 1) - 1
          #(3 - 1 - 1) - 2, not positive
          #(3 - 1 - 1) * 2, redundant
          #(3 - 1 - 1) / 2, not integer
        # --------------------------------
        # (3 * (2 + 1), 1)
        # --------------------------------
          _3_mm_2_a_1_aa_1 := n.Expression(_3_mm_2_a_1, _1, operator.add),
          _3_mm_2_a_1_ss_1 := n.Expression(_3_mm_2_a_1, _1, operator.sub),
          #_3_mm_2_a_1_mm_1 := 3 * (2 + 1) * 1, redundant
          #_3_mm_2_a_1_dd_1 := 3 * (2 + 1) / 1, redundant
        # --------------------------------
        # (3 / (2 + 1), 1)
        # --------------------------------
          _3_dd_2_a_1_aa_1 := n.Expression(_3_dd_2_a_1, _1, operator.add),
          _2_a_1_dd_3_aa_1 := n.Expression(_2_a_1_dd_3, _1, operator.add),
          #_3_dd_2_a_1_ss_1 := 3 / (2 + 1) - 1, not positive
          #_3_dd_2_a_1_mm_1 := 3 / (2 + 1) * 1, redundant
          #_3_dd_2_a_1_dd_1 := 3 / (2 + 1) / 1, redundant
        # --------------------------------
        # (2 + 1 + 1, 3)
        # --------------------------------
          #_2_a_1_a_1_aa_3 := (2 + 1 + 1) + 3, equivalent to (3 + 2 + 1) + 1
          _2_a_1_a_1_ss_3 := n.Expression(_2_a_1_a_1, _3, operator.sub),
          _2_a_1_a_1_mm_3 := n.Expression(_2_a_1_a_1, _3, operator.mul),
          #_2_a_1_a_1_dd_3 := (2 + 1 + 1) / 3, not integer
        # --------------------------------
        # (2 + 1 - 1, 3)
        # --------------------------------
          #_3_aa_2_a_1_s_1 := 3 + (2 + 1 - 1), equivalent to (3 + 2 + 1) - 1
          #_3_ss_2_a_1_s_1 := 3 - (2 + 1 - 1), equivalent to (3 + 1 - 2) - 1
          _3_mm_2_a_1_s_1 := n.Expression(_3, _2_a_1_s_1, operator.mul),
          #_3_dd_2_a_1_s_1 := 3 / (2 + 1 - 1), not integer
        # --------------------------------
        # (3 * (1 + 1), 2)
        # --------------------------------
          _3_mm_1_a_1_aa_2 := n.Expression(_3_mm_1_a_1, _2, operator.add),
          _3_mm_1_a_1_ss_2 := n.Expression(_3_mm_1_a_1, _2, operator.sub),
          #_3_mm_1_a_1_mm_2 := 3 * (1 + 1) * 2, equivalent to (3 * 2) * (1 + 1)
          _3_mm_1_a_1_dd_2 := n.Expression(_3_mm_1_a_1, _2, operator.truediv),
        # --------------------------------
        # (2 * (1 + 1), 3)
        # --------------------------------
          _2_mm_1_a_1_aa_3 := n.Expression(_2_mm_1_a_1, _3, operator.add),
          _2_mm_1_a_1_ss_3 := n.Expression(_2_mm_1_a_1, _3, operator.sub),
          #_2_mm_1_a_1_mm_3 := 2 * (1 + 1) * 3, equivalent to (3 * 2) * (1 + 1)
          #_2_mm_1_a_1_dd_3 := 2 * (1 + 1) / 3, not integer
        # --------------------------------
        # (2 / (1 + 1), 3)
        # --------------------------------
          _3_aa_2_dd_1_a_1 := n.Expression(_3, _2_dd_1_a_1, operator.add),
          _3_aa_1_a_1_dd_2 := n.Expression(_3, _1_a_1_dd_2, operator.add),
          _3_ss_2_dd_1_a_1 := n.Expression(_3, _2_dd_1_a_1, operator.sub),
          _3_ss_1_a_1_dd_2 := n.Expression(_3, _1_a_1_dd_2, operator.sub),
          #_3_mmm_2_dd_1_a_1 := 3 * ((1 + 1) / 2), redundant
          #_3_ddd_2_dd_1_a_1 := 3 / ((1 + 1) / 2), redundant
        # --------------------------------
        # (3 + 2, 1 + 1)
        # --------------------------------
          #_3_a_2_aa_1_a_1 := (3 + 2) + (1 + 1), equivalent to (3 + 2 + 1) + 1
          #_3_a_2_ss_1_a_1 := (3 + 2) - (1 + 1), equivalent to (3 + 2 + 1) - 1
          _3_a_2_mm_1_a_1 := n.Expression(_3_a_2, _1_a_1, operator.mul),
          #_3_a_2_dd_1_a_1 := (3 + 2) / (1 + 1), not integer
        # --------------------------------
        # (3 - 2, 1 + 1)
        # --------------------------------
          #_1_a_1_aa_3_s_2 := (1 + 1) + (3 - 2), equivalent to (3 + 1 + 1) - 2
          #_1_a_1_ss_3_s_2 := (1 + 1) - (3 - 2), equivalent to (2 + 1 + 1) - 3
          #_1_a_1_mm_3_s_2 := (1 + 1) * (3 - 2), redundant
          #_1_a_1_dd_3_s_2 := (1 + 1) / (3 - 2), redundant
        # --------------------------------
        # (3 * 2, 1 + 1)
        # --------------------------------
          #_3_m_2_aa_1_a_1 := (3 * 2) + (1 + 1), equivalent to (3 * 2 + 1) + 1
          #_3_m_2_ss_1_a_1 := (3 * 2) - (1 + 1), equivalent to (3 * 2 - 1) - 1
          _3_m_2_mm_1_a_1 := n.Expression(_3_m_2, _1_a_1, operator.mul),
          _3_m_2_dd_1_a_1 := n.Expression(_3_m_2, _1_a_1, operator.truediv),
        # --------------------------------
        # (3 + 1, 2 + 1)
        # --------------------------------
          #_3_a_1_aa_2_a_1 := (3 + 1) + (2 + 1), equivalent to (3 + 2 + 1) + 1
          #_3_a_1_ss_2_a_1 := (3 + 1) - (2 + 1), equivalent to (3 + 1 - 2) - 1
          _3_a_1_mm_2_a_1 := n.Expression(_3_a_1, _2_a_1, operator.mul),
          #_3_a_1_dd_2_a_1 := (3 + 1) / (2 + 1), not integer
        # --------------------------------
        # (3 + 1, 2 - 1)
        # --------------------------------
          #_3_a_1_aa_2_s_1 := (3 + 1) + (2 - 1), equivalent to (3 + 2 + 1) - 1
          #_3_a_1_ss_2_s_1 := (3 + 1) - (2 - 1), equivalent to (3 + 1 + 1) - 2
          #_3_a_1_mm_2_s_1 := (3 + 1) * (2 - 1), redundant
          #_3_a_1_dd_2_s_1 := (3 + 1) / (2 - 1), redundant
        # --------------------------------
        # (3 - 1, 2 + 1)
        # --------------------------------
          #_2_a_1_aa_3_s_1 := (2 + 1) + (3 - 1), equivalent to (3 + 2 + 1) - 1
          #_2_a_1_ss_3_s_1 := (2 + 1) - (3 - 1), equivalent to (2 + 1 + 1) - 3
          _2_a_1_mm_3_s_1 := n.Expression(_2_a_1, _3_s_1, operator.mul),
          #_2_a_1_dd_3_s_1 := (2 + 1) / (3 - 1), not integer
        # --------------------------------
        # (3 - 1, 2 - 1)
        # --------------------------------
          #_3_s_1_aa_2_s_1 := (3 - 1) + (2 - 1), equivalent to (3 + 2 - 1) - 1
          #_3_s_1_ss_2_s_1 := (3 - 1) - (2 - 1), equivalent to (3 + 1 - 2) - 1
          #_3_s_1_mm_2_s_1 := (3 - 1) * (2 - 1), redundant
          #_3_s_1_dd_2_s_1 := (3 - 1) / (2 - 1), redundant
      ]
    )


if __name__ == '__main__':
  
  unittest.main()
