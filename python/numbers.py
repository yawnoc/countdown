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
  """
  A class for distinguishable constants.
  
  Unlike expressions, we need distinguishability
  so that multiplicity of the input numbers is handled correctly.
  """
  
  def __init__(self, number):
    self.value = number


class Expression:
  """
  A God-class for expressions.
  
  Basically the goal is to have a canonical representation,
  so that e.g. a + (b + c) is the same as (a + b) + c.
  
  An expression can be either:
  0. TYPE_CONSTANT
          Just a constant, of the form a.
  1. TYPE_ADDITIVE
          Of the form x_1 + x_2 + ... - y_1 - y_2 - ...
          where the terms are non-TYPE_ADDITIVE expressions.
          The parts are (x_1, x_2, ..., y_1, y_2, ...)
          and the signs ( +1,  +1, ...,  -1,  -1, ...),
          with canonical order x_1 >= x_2 >= ...
          and y_1 >= y_2 >= ....
  2. TYPE_MULTIPLICATIVE
          Of the form x_1 * x_2 * ... / y_1 / y_2 / ...
          where the factors are non-TYPE_MULTIPLICATIVE expressions.
          The parts are (x_1, x_2, ..., y_1, y_2, ...)
          and the signs ( +1,  +1, ...,  -1,  -1, ...),
          with canonical order x_1 >= x_2 >= ...
          and y_1 >= y_2 >= ....
  The imposed canonical order ensures preference for
  positive integer results as required by the rules
  of the Countdown numbers game.
  
  Expressions are instantiated by calling either
          Expression(constant)
  or
          Expression(expression, expression, operator).
  In the second case, we do logic to flatten out child expressions
  which are of the same type (TYPE_ADDITIVE, TYPE_MULTIPLICATIVE)
  implied by the supplied binary operator.
  """
  
  TYPE_CONSTANT = 0
  TYPE_ADDITIVE = 1
  TYPE_MULTIPLICATIVE = 2
  
  OPERATOR_STRING_FROM_SIGN_FROM_TYPE = {
    TYPE_ADDITIVE: {1: '+', -1: '-'},
    TYPE_MULTIPLICATIVE: {1: '*', -1: '/'},
  }
  
  def __init__(self, child_1, child_2=None, binary_operator=None):
    
    if binary_operator in [operator.add, operator.sub]:
      self.type = Expression.TYPE_ADDITIVE
    elif binary_operator in [operator.mul, operator.truediv]:
      self.type = Expression.TYPE_MULTIPLICATIVE
    else:
      constant = child_1
      self.constants = {constant}
      self.type = Expression.TYPE_CONSTANT
      self.parts = ()
      self.signs = ()
      self.value = constant.value
      self.hash = hash(self.value)
      return
    
    constants = {
      *child_1.constants,
      *child_2.constants,
    }
    parts = (
      *self.get_parts_for(child_1),
      *self.get_parts_for(child_2),
    )
    signs = (
      *self.get_signs_for(child_1, binary_operator, is_first_child=True),
      *self.get_signs_for(child_2, binary_operator, is_first_child=False),
    )
    
    sorted_parts_and_signs = \
            sorted(
              zip(parts, signs),
              key=self.parts_and_signs_sort_key,
            )
    parts, signs = zip(*sorted_parts_and_signs)
    
    self.constants = constants
    self.parts = parts
    self.signs = signs
    self.value = binary_operator(child_1.value, child_2.value)
    self.hash = hash((self.type, self.parts, self.signs))
  
  def get_parts_for(self, child):
    
    if self.type == child.type:
      return child.parts
    else:
      return (child,)
  
  def get_signs_for(self, child, binary_operator, is_first_child):
    
    if is_first_child or binary_operator in [operator.add, operator.mul]:
      operator_sign = 1
    else:
      operator_sign = -1
    
    if self.type == child.type:
      return (operator_sign * sign for sign in child.signs)
    else:
      return (operator_sign,)
  
  def parts_and_signs_sort_key(self, part_and_sign):
    
    part, sign = part_and_sign
    return (-sign, -part.value, -part.type)
  
  def __hash__(self):
    return self.hash
  
  def __eq__(self, other):
    return self.__hash__() == other.__hash__()
  
  def __str__(self):
    
    if self.type == Expression.TYPE_CONSTANT:
      return str(self.value)
    else:
      operator_string_from_sign = \
              Expression.OPERATOR_STRING_FROM_SIGN_FROM_TYPE[self.type]
      string = \
              ' '.join(
                [
                  thingy
                    for part, sign in zip(self.parts, self.signs)
                    for thingy in (
                      operator_string_from_sign[sign],
                      self.stringify_part(part),
                    )
                ][1:]
              )
      return string
  
  def stringify_part(self, part):
    """
    Stringify a part, ensuring brackets for additive factors.
    
    Note that multiplicative terms don't need brackets.
    """
    
    part_string = str(part)
    
    if self.type == Expression.TYPE_MULTIPLICATIVE \
    and part.type == Expression.TYPE_ADDITIVE:
      part_string = f'({part_string})'
    
    return part_string


def will_be_useful(expression_1, expression_2, binary_operator):
  """
  Pre-screen the usefulness before building a new expression.
  
  - Operands that have common constants are illegal.
  - Additions of the following forms are useless:
            x + y where x < y (prefer y + x)
  - Subtractions of the following forms are useless:
            x - y where x <= y (not positive)
  - Multiplications of the following forms are useless:
            x * y where x < y (prefer y * x)
            x * 1 (why bother)
  - Divisions of the following forms are useless:
            x / y where x < y (not integer)
            x / 1 (why bother)
  """
  
  if have_common_constants(expression_1, expression_2):
    return False
  
  if binary_operator == operator.add:
    return expression_1.value >= expression_2.value
  
  elif binary_operator == operator.sub:
    return expression_1.value > expression_2.value
  
  elif binary_operator == operator.mul:
    return expression_1.value >= expression_2.value > 1
  
  elif binary_operator == operator.truediv:
    return expression_1.value >= expression_2.value > 1
  
  else:
    return False

def have_common_constants(expression_1, expression_2):
  return any(
    constant in expression_1.constants
      for constant in expression_2.constants
  )


def is_positive_integer(number):
  return int(number) == number and number > 0


def compute_expression_set(input_number_list):
  """
  Recursively compute the set of expressions.
  """
  
  input_number_list.sort()
  input_number_count = len(input_number_list)
  
  input_constant_list = [Constant(number) for number in input_number_list]
  
  expression_list_from_size = {}
  expression_list_from_size[1] = \
          [Expression(constant) for constant in input_constant_list]
  
  for size in range(2, input_number_count + 1):
    expression_list_from_size[size] = []
    for size_1 in range(1, size):
      size_2 = size - size_1
      for binary_operator \
      in [operator.add, operator.sub, operator.mul, operator.truediv]:
        for expression_1 in expression_list_from_size[size_1]:
          for expression_2 in expression_list_from_size[size_2]:
            if will_be_useful(expression_1, expression_2, binary_operator):
              expression = \
                        Expression(expression_1, expression_2, binary_operator)
              if is_positive_integer(expression.value):
                expression_list_from_size[size].append(expression)
  
  expression_set = set(
    expression
      for expression_iterable in expression_list_from_size.values()
      for expression in expression_iterable
  )
  
  return expression_set


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
    print(f'{int(expression.value)}\t{expression}')


def main():
  
  parsed_arguments = parse_command_line_arguments()
  
  target = parsed_arguments.target
  input_number_list = parsed_arguments.input_number_list
  max_results_count = parsed_arguments.max_results_count
  
  def distance_from_target(expression):
    return abs(expression.value - target)
  
  expression_list = \
          sorted(
            compute_expression_set(input_number_list),
            key=distance_from_target
          )
  
  print_results(expression_list, max_results_count)


if __name__ == '__main__':
  
  main()
