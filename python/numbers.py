#!/usr/bin/env python3

"""
# numbers.py

Solve a Countdown numbers game.

Copyright 2022 Conway
Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
This is free software with NO WARRANTY etc. etc., see LICENSE.
"""


import argparse
import operator


class Constant:
  
  def __init__(self, number):
    self.value = number


class Expression:
  
  def __init__(self, binary_operator, expression_1, expression_2):
    self.value = binary_operator(expression_1.value, expression_2.value)


def parse_command_line_arguments() -> object:
  
  MAX_RESULTS_DEFAULT = 15
  
  parser = \
          argparse.ArgumentParser(
            description='Solve a Countdown numbers game.'
          )
  
  parser.add_argument(
    'target',
    metavar='TARGET',
    type=int,
    help='target number (positive integer)',
  )
  
  parser.add_argument(
    'input_numbers',
    metavar='NUMBER',
    type=int,
    nargs='+',
    help='number that can be used to obtain the target',
  )
  
  parser.add_argument(
    '-m', dest="max_results_count",
    metavar='MAX_RESULTS',
    type=int,
    default=MAX_RESULTS_DEFAULT,
    help=f'maximum number of output results (default {MAX_RESULTS_DEFAULT})',
  )
  
  return parser.parse_args()


def main():
  
  parsed_arguments = parse_command_line_arguments()
  
  target = parsed_arguments.target
  input_numbers = parsed_arguments.input_numbers
  max_results_count = parsed_arguments.max_results_count


if __name__ == '__main__':
  
  main()
