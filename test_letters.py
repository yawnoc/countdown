#!/usr/bin/env python3

"""
# test_letters.py

Perform unit testing on `letters.py`.

Copyright 2022 Conway
Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
This is free software with NO WARRANTY etc. etc., see LICENSE.
"""


import letters
import unittest


class TestLetters(unittest.TestCase):
  
  def test_normalise_letters(self):
    self.assertEqual(letters.normalise_letters('abc'), 'ABC')
    self.assertEqual(letters.normalise_letters('HeRpdERP'), 'HERPDERP')
    self.assertEqual(letters.normalise_letters(' whitespace\t'), 'WHITESPACE')


if __name__ == '__main__':
  
  unittest.main()
