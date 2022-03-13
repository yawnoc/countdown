#!/usr/bin/env python3

"""
# test_letters.py

Perform unit testing on `letters.py`.
"""


import letters
import unittest


class TestLetters(unittest.TestCase):
  
  def test_normalise_word(self):
    self.assertEqual(letters.normalise_word('abc'), 'ABC')
    self.assertEqual(letters.normalise_word('HeRpdERP'), 'HERPDERP')
    self.assertEqual(letters.normalise_word(' whitespace\t'), 'WHITESPACE')


if __name__ == '__main__':
  
  unittest.main()
