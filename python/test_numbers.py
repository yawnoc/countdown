#!/usr/bin/env python3

"""
# test_numbers.py

Perform unit testing on `numbers.py`.

Copyright 2022 Conway
Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
This is free software with NO WARRANTY etc. etc., see LICENSE.
"""


import numbers as n
import operator
import unittest


class TestNumbers(unittest.TestCase):
  
  def test_expression(self):
    
    two_plus_two = \
            n.Expression(n.Expression(2), n.Expression(2), operator.add)
    nine_minus_four = \
            n.Expression(n.Expression(9), n.Expression(4), operator.sub)
    three_times_two = \
            n.Expression(n.Expression(3), n.Expression(2), operator.mul)
    five_on_two = \
            n.Expression(n.Expression(5), n.Expression(2), operator.truediv)
    complicated_expression = \
            n.Expression(
              n.Expression(two_plus_two, nine_minus_four, operator.mul),
              n.Expression(three_times_two, five_on_two, operator.sub),
              operator.truediv
            )
    
    self.assertEqual(two_plus_two.value, 2 + 2)
    self.assertEqual(nine_minus_four.value, 9 - 4)
    self.assertEqual(three_times_two.value, 3 * 2)
    self.assertEqual(five_on_two.value, 5 / 2)
    self.assertEqual(
      complicated_expression.value,
      ((2 + 2) * (9 - 4)) / (3 * 2 - 5 / 2)
    )
  
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
    
    self.assertEqual(
      n.compute_expression_set([70]),
      {n.Expression(70)}
    )
    
    self.assertCountEqual(
      [
        expression.value
          for expression in n.compute_expression_set([1, 1, 2, 3])
      ],
      [
        # ----------------------------------------------------------------
        # Size 1
        # ----------------------------------------------------------------
          3,
          2,
          1,
        # ----------------------------------------------------------------
        # Size 2
        # ----------------------------------------------------------------
        # --------------------------------
        # (3, 2)
        # --------------------------------
          3 + 2,
          3 - 2,
          3 * 2,
          #3 / 2, not integer
        # --------------------------------
        # (3, 1)
        # --------------------------------
          3 + 1,
          3 - 1,
          #3 * 1, redundant
          #3 / 1, redundant
        # --------------------------------
        # (2, 1)
        # --------------------------------
          2 + 1,
          2 - 1,
          #2 * 1, redundant
          #2 / 1, redundant
        # --------------------------------
        # (1, 1)
        # --------------------------------
          1 + 1,
          #1 - 1, not positive
          #1 * 1, redundant
          #1 / 1, redundant
        # ----------------------------------------------------------------
        # Size 3
        # ----------------------------------------------------------------
        # --------------------------------
        # (3 + 2, 1)
        # --------------------------------
          (3 + 2) + 1,
          (3 + 2) - 1,
          #(3 + 2) * 1, redundant
          #(3 + 2) / 1, redundant
        # --------------------------------
        # (3 - 2, 1)
        # --------------------------------
          #1 + (3 - 2), equivalent to (3 + 1) - 2
          #1 - (3 - 2), not positive
          #1 * (3 - 2), redundant
          #1 / (3 - 2), (3 - 2) / 1 redundant
        # --------------------------------
        # (3 * 2, 1)
        # --------------------------------
          (3 * 2) + 1,
          (3 * 2) - 1,
          #(3 * 2) * 1, redundant
          #(3 * 2) / 1, redundant
        # --------------------------------
        # (3 + 1, 2)
        # --------------------------------
          #(3 + 1) + 2, equivalent to (3 + 2) + 1
          (3 + 1) - 2,
          (3 + 1) * 2,
          (3 + 1) / 2,
        # --------------------------------
        # (3 + 1, 1)
        # --------------------------------
          (3 + 1) + 1,
          (3 + 1) - 1,
          #(3 + 1) * 1, redundant
          #(3 + 1) / 1, redundant
        # --------------------------------
        # (3 - 1, 2)
        # --------------------------------
          #2 + (3 - 1), equivalent to (3 + 2) - 1
          #2 - (3 - 1), not positive
          2 * (3 - 1),
          2 / (3 - 1), (3 - 1) / 2,
        # --------------------------------
        # (3 - 1, 1)
        # --------------------------------
          #(3 - 1) + 1, equivalent to (3 + 1) - 1
          (3 - 1) - 1,
          #(3 - 1) * 1, redundant
          #(3 - 1) / 1, redundant
        # --------------------------------
        # (2 + 1, 3)
        # --------------------------------
          #3 + (2 + 1), equivalent to (3 + 2) + 1
          #3 - (2 + 1), not positive
          3 * (2 + 1),
          3 / (2 + 1), (2 + 1) / 3,
        # --------------------------------
        # (2 + 1, 1)
        # --------------------------------
          (2 + 1) + 1,
          (2 + 1) - 1,
          #(2 + 1) * 1, redundant
          #(2 + 1) / 1, redundant
        # --------------------------------
        # (2 - 1, 3)
        # --------------------------------
          #3 + (2 - 1), equivalent to (3 + 2) - 1
          #3 - (2 - 1), equivalent to (3 + 1) - 2
          #3 * (2 - 1), redundant
          #3 / (2 - 1), redundant
        # --------------------------------
        # (2 - 1, 1)
        # --------------------------------
          #1 + (2 - 1), equivalent to (2 + 1) - 1
          #1 - (2 - 1), not positive
          #1 * (2 - 1), redundant
          #1 / (2 - 1), (2 - 1) / 1, redundant
        # --------------------------------
        # (1 + 1, 3)
        # --------------------------------
          #3 + (1 + 1), equivalent to (3 + 1) + 1
          #3 - (1 + 1), equivalent to (3 - 1) - 1
          3 * (1 + 1),
          #3 / (1 + 1), not integer
        # --------------------------------
        # (1 + 1, 2)
        # --------------------------------
          #2 + (1 + 1), equivalent to (2 + 1) + 1
          #2 - (1 + 1), not positive
          2 * (1 + 1),
          2 / (1 + 1), (1 + 1) / 2,
        # ----------------------------------------------------------------
        # Size 4
        # ----------------------------------------------------------------
        # --------------------------------
        # (3 + 2 + 1, 1)
        # --------------------------------
          (3 + 2 + 1) + 1,
          (3 + 2 + 1) - 1,
          #(3 + 2 + 1) * 1, redundant
          #(3 + 2 + 1) / 1, redundant
        # --------------------------------
        # (3 + 2 - 1, 1)
        # --------------------------------
          #(3 + 2 - 1) + 1, same as (3 + 2 + 1) - 1
          (3 + 2 - 1) - 1,
          #(3 + 2 - 1) * 1, redundant
          #(3 + 2 - 1) / 1, redundant
        # --------------------------------
        # (3 * 2 + 1, 1)
        # --------------------------------
          (3 * 2 + 1) + 1,
          (3 * 2 + 1) - 1,
          #(3 * 2 + 1) * 1, redundant
          #(3 * 2 + 1) / 1, redundant
        # --------------------------------
        # (3 * 2 - 1, 1)
        # --------------------------------
          #(3 * 2 - 1) + 1, equivalent to (3 * 2 + 1) - 1
          (3 * 2 - 1) - 1,
          #(3 * 2 - 1) * 1, redundant
          #(3 * 2 - 1) / 1, redundant
        # --------------------------------
        # (3 + 1 - 2, 1)
        # --------------------------------
          #(3 + 1 - 2) + 1, equivalent to (3 + 1 + 1) - 2
          (3 + 1 - 2) - 1,
          #(3 + 1 - 2) * 1, redundant
          #(3 + 1 - 2) / 1, redundant
        # --------------------------------
        # ((3 + 1) * 2, 1)
        # --------------------------------
          ((3 + 1) * 2) + 1,
          ((3 + 1) * 2) - 1,
          #((3 + 1) * 2) * 1, redundant
          #((3 + 1) * 2) / 1, redundant
        # --------------------------------
        # ((3 + 1) / 2, 1)
        # --------------------------------
          ((3 + 1) / 2) + 1,
          ((3 + 1) / 2) - 1,
          #((3 + 1) / 2) * 1, redundant
          #((3 + 1) / 2) / 1, redundant
        # --------------------------------
        # (3 + 1 + 1, 2)
        # --------------------------------
          #(3 + 1 + 1) + 2, equivalent to (3 + 2 + 1) + 1
          (3 + 1 + 1) - 2,
          (3 + 1 + 1) * 2,
          #(3 + 1 + 1) / 2, not integer
        # --------------------------------
        # (3 + 1 - 1, 2)
        # --------------------------------
          #(3 + 1 - 1) + 2, equivalent to (3 + 2 + 1) - 1
          #(3 + 1 - 1) - 2, equivalent to (3 + 1 - 2) - 1
          (3 + 1 - 1) * 2,
          #(3 + 1 - 1) / 2, not integer
        # --------------------------------
        # (2 * (3 - 1), 1)
        # --------------------------------
          (2 * (3 - 1)) + 1,
          (2 * (3 - 1)) - 1,
          #(2 * (3 - 1)) * 1, redundant
          #(2 * (3 - 1)) / 1, redundant
        # --------------------------------
        # (2 / (3 - 1), 1)
        # --------------------------------
          2 / (3 - 1) + 1, (3 - 1) / 2 + 1,
          #2 / (3 - 1) - 1, not positive
          #2 / (3 - 1) * 1, redundant
          #2 / (3 - 1) / 1, redundant
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
          3 * (2 + 1) + 1,
          3 * (2 + 1) - 1,
          #3 * (2 + 1) * 1, redundant
          #3 * (2 + 1) / 1, redundant
        # --------------------------------
        # (3 / (2 + 1), 1)
        # --------------------------------
          3 / (2 + 1) + 1, (2 + 1) / 3 + 1,
          #3 / (2 + 1) - 1, not positive
          #3 / (2 + 1) * 1, redundant
          #3 / (2 + 1) / 1, redundant
        # --------------------------------
        # (2 + 1 + 1, 3)
        # --------------------------------
          #(2 + 1 + 1) + 3, equivalent to (3 + 2 + 1) + 1
          (2 + 1 + 1) - 3,
          (2 + 1 + 1) * 3,
          #(2 + 1 + 1) / 3, not integer
        # --------------------------------
        # (2 + 1 - 1, 3)
        # --------------------------------
          #3 + (2 + 1 - 1), equivalent to (3 + 2 + 1) - 1
          #3 - (2 + 1 - 1), equivalent to (3 + 1 - 2) - 1
          3 * (2 + 1 - 1),
          #3 / (2 + 1 - 1), not integer
        # --------------------------------
        # (3 * (1 + 1), 2)
        # --------------------------------
          3 * (1 + 1) + 2,
          3 * (1 + 1) - 2,
          #3 * (1 + 1) * 2, equivalent to (3 * 2) * (1 + 1)
          3 * (1 + 1) / 2,
        # --------------------------------
        # (2 * (1 + 1), 3)
        # --------------------------------
          2 * (1 + 1) + 3,
          2 * (1 + 1) - 3,
          #2 * (1 + 1) * 3, equivalent to (3 * 2) * (1 + 1)
          #2 * (1 + 1) / 3, not integer
        # --------------------------------
        # (2 / (1 + 1), 3)
        # --------------------------------
          3 + 2 / (1 + 1), 3 + (1 + 1) / 2,
          3 - 2 / (1 + 1), 3 - (1 + 1) / 2,
          #3 * ((1 + 1) / 2), redundant
          #3 / ((1 + 1) / 2), redundant
        # --------------------------------
        # (3 + 2, 1 + 1)
        # --------------------------------
          #(3 + 2) + (1 + 1), equivalent to (3 + 2 + 1) + 1
          #(3 + 2) - (1 + 1), equivalent to (3 + 2 + 1) - 1
          (3 + 2) * (1 + 1),
          #(3 + 2) / (1 + 1), not integer
        # --------------------------------
        # (3 - 2, 1 + 1)
        # --------------------------------
          #(1 + 1) + (3 - 2), equivalent to (3 + 1 + 1) - 2
          #(1 + 1) - (3 - 2), equivalent to (2 + 1 + 1) - 3
          #(1 + 1) * (3 - 2), redundant
          #(1 + 1) / (3 - 2), redundant
        # --------------------------------
        # (3 * 2, 1 + 1)
        # --------------------------------
          #(3 * 2) + (1 + 1), equivalent to (3 * 2 + 1) + 1
          #(3 * 2) - (1 + 1), equivalent to (3 * 2 - 1) - 1
          (3 * 2) * (1 + 1),
          (3 * 2) / (1 + 1),
        # --------------------------------
        # (3 + 1, 2 + 1)
        # --------------------------------
          #(3 + 1) + (2 + 1), equivalent to (3 + 2 + 1) + 1
          #(3 + 1) - (2 + 1), equivalent to (3 + 1 - 2) - 1
          (3 + 1) * (2 + 1),
          #(3 + 1) / (2 + 1), not integer
        # --------------------------------
        # (3 + 1, 2 - 1)
        # --------------------------------
          #(3 + 1) + (2 - 1), equivalent to (3 + 2 + 1) - 1
          #(3 + 1) - (2 - 1), equivalent to (3 + 1 + 1) - 2
          #(3 + 1) * (2 - 1), redundant
          #(3 + 1) / (2 - 1), redundant
        # --------------------------------
        # (3 - 1, 2 + 1)
        # --------------------------------
          #(2 + 1) + (3 - 1), equivalent to (3 + 2 + 1) - 1
          #(2 + 1) - (3 - 1), equivalent to (2 + 1 + 1) - 3
          (2 + 1) * (3 - 1),
          #(2 + 1) / (3 - 1), not integer
        # --------------------------------
        # (3 - 1, 2 - 1)
        # --------------------------------
          #(3 - 1) + (2 - 1), equivalent to (3 + 2 - 1) - 1
          #(3 - 1) - (2 - 1), equivalent to (3 + 1 - 2) - 1
          #(3 - 1) * (2 - 1), redundant
          #(3 - 1) / (2 - 1), redundant
      ]
    )
    
    self.assertCountEqual(
      [
        expression.value
          for expression in n.compute_expression_set([3, 20, 10000])
      ],
      [
        # ----------------------------------------------------------------
        # Size 1
        # ----------------------------------------------------------------
          10000,
          20,
          3,
        # ----------------------------------------------------------------
        # Size 2
        # ----------------------------------------------------------------
        # --------------------------------
        # (10000, 20)
        # --------------------------------
          10000 + 20,
          10000 - 20,
          10000 * 20,
          10000 / 20,
        # --------------------------------
        # (10000, 3)
        # --------------------------------
          10000 + 3,
          10000 - 3,
          10000 * 3,
          #10000 / 3, not integer
        # --------------------------------
        # (20, 3)
        # --------------------------------
          20 + 3,
          20 - 3,
          20 * 3,
          #20 / 3, not integer
        # ----------------------------------------------------------------
        # Size 3
        # ----------------------------------------------------------------
        # --------------------------------
        # (10000 + 20, 3)
        # --------------------------------
          (10000 + 20) + 3,
          (10000 + 20) - 3,
          (10000 + 20) * 3,
          (10000 + 20) / 3,
        # --------------------------------
        # (10000 - 20, 3)
        # --------------------------------
          #10000 - 20 + 3, equivalent to (10000 + 3) - 20
          (10000 - 20) - 3,
          (10000 - 20) * 3,
          #(10000 - 20) / 3, not integer
        # --------------------------------
        # (10000 * 20, 3)
        # --------------------------------
          (10000 * 20) + 3,
          (10000 * 20) - 3,
          (10000 * 20) * 3,
          #(10000 * 20) / 3, not integer
        # --------------------------------
        # (10000 / 20, 3)
        # --------------------------------
          (10000 / 20) + 3,
          (10000 / 20) - 3,
          (10000 / 20) * 3,
          #(10000 / 20) / 3, not integer
        # --------------------------------
        # (10000 + 3, 20)
        # --------------------------------
          #(10000 + 3) + 20, equivalent to (10000 + 20) + 3
          (10000 + 3) - 20,
          (10000 + 3) * 20,
          #(10000 + 3) / 20, not integer
        # --------------------------------
        # (10000 - 3, 20)
        # --------------------------------
          #(10000 - 3) + 20, equivalent to (10000 + 20) - 3
          #(10000 - 3) - 20, equivalent to (10000 - 20) - 3
          (10000 - 3) * 20,
          #(10000 - 3) / 20, not integer
        # --------------------------------
        # (10000 * 3, 20)
        # --------------------------------
          (10000 * 3) + 20,
          (10000 * 3) - 20,
          #(10000 * 3) * 20, equivalent to (10000 * 20) * 3
          #(10000 * 3) / 20, not integer
        # --------------------------------
        # (10000, 20 + 3)
        # --------------------------------
          #10000 + (20 + 3), equivalent to (10000 + 20) + 3
          #10000 - (20 + 3), equivalent to (10000 - 20) - 3
          10000 * (20 + 3),
          #10000 / (20 + 3), not integer
        # --------------------------------
        # (10000, 20 - 3)
        # --------------------------------
          #10000 + (20 - 3), equivalent to (10000 + 20) - 3
          #10000 - (20 - 3), equivalent to (10000 + 3) - 20
          10000 * (20 - 3),
          #10000 / (20 - 3), not integer
        # --------------------------------
        # (10000, 20 * 3)
        # --------------------------------
          10000 + (20 * 3),
          10000 - (20 * 3),
          #10000 * (20 * 3), equivalent to (10000 * 20) * 3
          #10000 / (20 * 3), not integer
      ]
    )


if __name__ == '__main__':
  
  unittest.main()
