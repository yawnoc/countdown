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
    self.expression_1 = expression_1
    self.expression_2 = expression_2
    self.binary_operator = binary_operator
    self.constants = \
            set.union(get_constants(expression_1), get_constants(expression_2))
  
  def __str__(self):
    return to_string(self, should_force_bracket=False)


def get_constants(expression):
  
  if type(expression).__name__ == 'Constant':
    return {expression}
  elif type(expression).__name__ == 'Expression':
    return expression.constants
  else:
    return None


def to_string(expression, should_force_bracket=False):
  
  if type(expression).__name__ == 'Constant':
    return str(expression.value)
  elif type(expression).__name__ == 'Expression':
    expression_1_string = \
            to_string(expression.expression_1, should_force_bracket=True)
    expression_2_string = \
            to_string(expression.expression_2, should_force_bracket=True)
    binary_operator_string = \
            STRING_FROM_BINARY_OPERATOR[expression.binary_operator]
    expression_string = \
            ' '.join(
              [
                expression_1_string,
                binary_operator_string,
                expression_2_string,
              ]
            )
    if should_force_bracket:
      return f'({expression_string})'
    else:
      return expression_string
  else:
    return None


STRING_FROM_BINARY_OPERATOR = {
  operator.add: '+',
  operator.sub: '-',
  operator.mul: '*',
  operator.truediv: '/',
}

BINARY_OPERATORS = STRING_FROM_BINARY_OPERATOR.keys()


def expression_will_be_useful(binary_operator, expression_1, expression_2):
  
  if any(
    constant in get_constants(expression_1)
      for constant in get_constants(expression_2)
  ):
    return False
  
  if binary_operator in [operator.add, operator.mul]:
    return expression_1.value >= expression_2.value
  else:
    return expression_1.value > expression_2.value


def is_positive_integer(number):
  return int(number) == number and number > 0


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
          for expression_2 in expression_list_from_size[size_2]:
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


def print_results(expression_list, max_results_count):
  
  for expression in expression_list[:max_results_count]:
    value = int(expression.value)
    print(f'{value}\t{expression}')


def main():
  
  parsed_arguments = parse_command_line_arguments()
  
  target = parsed_arguments.target
  input_number_list = parsed_arguments.input_number_list
  max_results_count = parsed_arguments.max_results_count
  
  def distance_from_target(expression):
    return abs(expression.value - target)
  
  expression_list = \
          sorted(
            compute_expression_list(input_number_list),
            key=distance_from_target
          )
  
  print_results(expression_list, max_results_count)


if __name__ == '__main__':
  
  main()
