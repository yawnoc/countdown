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


BINARY_OPERATORS = [
  operator.add,
  operator.sub,
  operator.mul,
  operator.truediv,
]


def expression_will_be_useful(binary_operator, expression_1, expression_2):
  
  if binary_operator in [operator.add, operator.mul]:
    return expression_1.value >= expression_2.value
  else:
    return expression_1.value > expression_2.value


def is_positive_integer(number):
  return isinstance(number, int) and number > 0


def compute_expression_list(input_number_list):
  
  input_number_list.sort()
  input_number_count = len(input_number_list)
  
  expression_list_from_size = {}
  expression_list_from_size[1] = \
          [Constant(number) for number in input_number_list]
  
  for size in range(2, input_number_count + 1):
    expression_list_from_size[size] = []
    for size_1 in range(1, size):
      size_2 = size - size_1
      for binary_operator in BINARY_OPERATORS:
        for expression_1 in expression_list_from_size[size_1]:
          for expression_2  in expression_list_from_size[size_2]:
            if expression_will_be_useful(
              binary_operator,
              expression_1,
              expression_2
            ):
              expression = \
                      Expression(binary_operator, expression_1, expression_2)
              if is_positive_integer(expression.value):
                expression_list_from_size[size].append(expression)
  
  expression_list = [
    expression
      for expression_list in expression_list_from_size.values()
      for expression in expression_list
  ]
  
  return expression_list


def check_is_positive_integer(number_argument):
  
  try:
    number = int(number_argument)
  except ValueError:
    raise argparse.ArgumentTypeError(f"not integer: '{number_argument}'")
  
  if not number > 0:
    raise argparse.ArgumentTypeError(f"not positive: '{number_argument}'")
  
  return number


def parse_command_line_arguments():
  
  MAX_RESULTS_DEFAULT = 15
  
  parser = \
          argparse.ArgumentParser(
            description='Solve a Countdown numbers game.'
          )
  
  parser.add_argument(
    'target',
    metavar='TARGET',
    type=check_is_positive_integer,
    help='target number (positive integer)',
  )
  
  parser.add_argument(
    'input_number_list',
    metavar='NUMBER',
    type=check_is_positive_integer,
    nargs='+',
    help='number (positive integer) that can be used to obtain the target',
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
  input_number_list = parsed_arguments.input_number_list
  max_results_count = parsed_arguments.max_results_count
  
  print([
    expression.value
      for expression in compute_expression_list(input_number_list)
  ])


if __name__ == '__main__':
  
  main()
