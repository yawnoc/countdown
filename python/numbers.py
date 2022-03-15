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


class Expression:
  
  TYPE_CONSTANT = 0
  TYPE_ADDITIVE = 1
  TYPE_MULTIPLICATIVE = 2
  
  def __init__(self, child_1, child_2=None, binary_operator=None):
    
    if binary_operator in [operator.add, operator.sub]:
      self.type = Expression.TYPE_ADDITIVE
    elif binary_operator in [operator.mul, operator.truediv]:
      self.type = Expression.TYPE_MULTIPLICATIVE
    else:
      self.type = Expression.TYPE_CONSTANT
      self.parts = []
      self.signs = []
      self.value = child_1
      return
    
    parts = [
      *self.get_parts_for(child_1),
      *self.get_parts_for(child_2),
    ]
    signs = [
      *self.get_signs_for(child_1, binary_operator, is_first_child=True),
      *self.get_signs_for(child_2, binary_operator, is_first_child=False),
    ]
    
    parts_and_signs = zip(parts, signs)
    sorted_parts_and_signs = \
            sorted(
              parts_and_signs,
              key=self.parts_and_signs_sort_key,
            )
    parts, signs = zip(*sorted_parts_and_signs)
    
    self.parts = parts
    self.signs = signs
    self.value = binary_operator(child_1.value, child_2.value)
  
  def get_parts_for(self, child):
    
    if self.type == child.type:
      return child.parts
    else:
      return [child]
  
  def get_signs_for(self, child, binary_operator, is_first_child):
    
    if is_first_child or binary_operator in [operator.add, operator.mul]:
      operator_sign = 1
    else:
      operator_sign = -1
    
    if self.type == child.type:
      return [operator_sign * sign for sign in child.signs]
    else:
      return [operator_sign]
  
  def parts_and_signs_sort_key(self, part_and_sign):
    
    part, sign = part_and_sign
    return (-sign, -part.value)
  
  def __repr__(self): # TODO: change to __str__
    
    return str(self.value) # TODO: implement non-TYPE_CONSTANT case


def is_positive_integer(number):
  return int(number) == number and number > 0


def compute_expression_list(input_number_list):
  
  input_number_list.sort()
  input_number_count = len(input_number_list)
  
  expression_list_from_size = {}
  expression_list_from_size[1] = \
          [Expression(number) for number in input_number_list]
  
  for size in range(2, input_number_count + 1):
    expression_list_from_size[size] = []
    for size_1 in range(1, size):
      size_2 = size - size_1
      for binary_operator \
      in [operator.add, operator.sub, operator.mul, operator.truediv]:
        for expression_1 in expression_list_from_size[size_1]:
          for expression_2 in expression_list_from_size[size_2]:
            expression = \
                      Expression(expression_1, expression_2, binary_operator)
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
  
  # Ideally we should implement a way to check whether
  # two instances of Expression are effectively the same
  # (e.g. numbers added or multiplied in a different order),
  # but that seems hard.
  
  # In the meantime, do a shitty loop that checks for string matches.
  expression_string_set = set()
  for expression in expression_list:
    if len(expression_string_set) >= max_results_count:
      break
    expression_string = str(expression)
    expression_value = int(expression.value)
    if expression_string not in expression_string_set:
      print(f'{expression_value}\t{expression_string}')
      expression_string_set.add(expression_string)


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
