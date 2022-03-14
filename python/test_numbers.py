#!/usr/bin/env python3

"""
# test_numbers.py

Perform unit testing on `numbers.py`.

Copyright 2022 Conway
Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
This is free software with NO WARRANTY etc. etc., see LICENSE.
"""


import numbers
import operator
import unittest


class TestNumbers(unittest.TestCase):
  
  def test_expression(self):
    self.assertEqual(
      numbers.Expression(
        operator.add,
        numbers.Constant(2),
        numbers.Constant(2)
      ).value,
      2 + 2
    )
    self.assertEqual(
      numbers.Expression(
        operator.sub,
        numbers.Constant(9),
        numbers.Constant(4)
      ).value,
      9 - 4
    )
    self.assertEqual(
      numbers.Expression(
        operator.mul,
        numbers.Constant(3),
        numbers.Constant(2)
      ).value,
      3 * 2
    )
    self.assertEqual(
      numbers.Expression(
        operator.truediv,
        numbers.Constant(5),
        numbers.Constant(2)
      ).value,
      5 / 2
    )
  
  def test_is_positive_integer(self):
    self.assertTrue(numbers.is_positive_integer(1))
    self.assertTrue(numbers.is_positive_integer(2))
    self.assertTrue(numbers.is_positive_integer(1000))
    self.assertTrue(numbers.is_positive_integer(7.0))
    self.assertFalse(numbers.is_positive_integer(0))
    self.assertFalse(numbers.is_positive_integer(-1))
    self.assertFalse(numbers.is_positive_integer(2/3))
    self.assertFalse(numbers.is_positive_integer(7.77))
  
  def test_compute_expression_list(self):
    self.assertCountEqual(
      [
        expression.value
          for expression in numbers.compute_expression_list([1])
      ],
      [1]
    )
    self.assertCountEqual(
      [
        expression.value
          for expression in numbers.compute_expression_list([2, 3])
      ],
      [
        # Size 1
        2,
        3,
        # Size 2
        3 + 2,
        3 - 2,
        3 * 2,
      ]
    )
    self.assertCountEqual(
      [
        expression.value
          for expression in numbers.compute_expression_list([3, 20, 10000])
      ],
      [
        ## Size 1 ##
        3,
        20,
        10000,
        ## Size 2 ##
        20 + 3,
        20 - 3,
        20 * 3,
        #20 / 3,
        10000 + 3,
        10000 - 3,
        10000 * 3,
        #10000 / 3,
        10000 + 20,
        10000 - 20,
        10000 * 20,
        10000 / 20,
        ## Size 3 ##
        10000 + (20 + 3),
        10000 + (20 - 3),
        10000 + (20 * 3),
        #10000 + (20 / 3),
        10000 - (20 + 3),
        10000 - (20 - 3),
        10000 - (20 * 3),
        #10000 - (20 / 3),
        10000 * (20 + 3),
        10000 * (20 - 3),
        10000 * (20 * 3),
        #10000 * (20 / 3),
        (10000 + 3) + 20,
        (10000 - 3) + 20,
        (10000 * 3) + 20,
        #(10000 / 3) + 20,
        (10000 + 3) - 20,
        (10000 - 3) - 20,
        (10000 * 3) - 20,
        #(10000 / 3) - 20,
        (10000 + 3) * 20,
        (10000 - 3) * 20,
        (10000 * 3) * 20,
        #(10000 / 3) * 20,
        #(10000 + 3) / 20,
        #(10000 - 3) / 20,
        (10000 * 3) / 20,
        #(10000 / 3) / 20,
        (10000 + 20) + 3,
        (10000 - 20) + 3,
        (10000 * 20) + 3,
        (10000 / 20) + 3,
        (10000 + 20) - 3,
        (10000 - 20) - 3,
        (10000 * 20) - 3,
        (10000 / 20) - 3,
        (10000 + 20) * 3,
        (10000 - 20) * 3,
        (10000 * 20) * 3,
        (10000 / 20) * 3,
        (10000 + 20) / 3,
        #(10000 - 20) / 3,
        #(10000 * 20) / 3,
        #(10000 / 20) / 3,
      ]
    )


if __name__ == '__main__':
  
  unittest.main()
