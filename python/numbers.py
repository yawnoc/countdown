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


ADD = operator.add
SUBTRACT = operator.sub
MULTIPLY = operator.mul
DIVIDE = operator.truediv

OPERATORS_ADDITIVE = [ADD, SUBTRACT]
OPERATORS_MULTIPLICATIVE = [MULTIPLY, DIVIDE]
OPERATORS = [*OPERATORS_ADDITIVE, *OPERATORS_MULTIPLICATIVE]


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
  """
  
  TYPE_CONSTANT = 0
  TYPE_ADDITIVE = 1
  TYPE_MULTIPLICATIVE = 2
  
  OPERATOR_STRING_FROM_SIGN_FROM_TYPE = {
    TYPE_ADDITIVE: {1: '+', -1: '-'},
    TYPE_MULTIPLICATIVE: {1: '*', -1: '/'},
  }
  
  def __init__(self, child_1, child_2=None, binary_operator=None):
    """
    Instantiate an expression.
    
            Expression(integer)
            Expression(expression, expression, operator)
    
    In the second case, we do logic to flatten out child expressions
    which are of the same type (TYPE_ADDITIVE, TYPE_MULTIPLICATIVE)
    implied by the supplied binary operator.
    """
    
    if binary_operator is None:
      
      self.type = Expression.TYPE_CONSTANT
      
      integer = child_1
      self.constants = [integer]
      self.parts = ()
      self.signs = ()
      self.value = integer
      
    else:
      
      if binary_operator in OPERATORS_ADDITIVE:
        self.type = Expression.TYPE_ADDITIVE
      elif binary_operator in OPERATORS_MULTIPLICATIVE:
        self.type = Expression.TYPE_MULTIPLICATIVE
      else:
        raise ValueError(
          f'binary operator must be one of {OPERATORS}.'
        )
      
      self.constants = [
        *child_1.constants,
        *child_2.constants,
      ]
      
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
      
      self.parts = parts
      self.signs = signs
      self.value = binary_operator(child_1.value, child_2.value)
    
    self.mass = len(self.constants)
    self.depth = max([part.depth + 1 for part in self.parts], default=0)
    self.hash = hash((self.value, self.type, self.parts, self.signs))
    
    self.rank = (
      self.mass,
      self.depth,
      len(self.parts),
      tuple(part.rank for part in self.parts),
      -self.value,
      self.type,
    )
  
  def get_parts_for(self, child):
    
    if self.type == child.type:
      return child.parts # so as to flatten it out
    else:
      return (child,) # keep it as is
  
  def get_signs_for(self, child, binary_operator, is_first_child):
    
    if is_first_child or binary_operator in [ADD, MULTIPLY]:
      operator_sign = 1
    else:
      operator_sign = -1
    
    if self.type == child.type:
      return (operator_sign * sign for sign in child.signs)
    else:
      return (operator_sign,)
  
  @staticmethod
  def parts_and_signs_sort_key(part_and_sign):
    
    part, sign = part_and_sign
    return (-sign, -part.value, part)
  
  def __hash__(self):
    return self.hash
  
  def __eq__(self, other):
    return (
      self.value == other.value
        and
      self.type == other.type
        and
      self.parts == other.parts
        and
      self.signs == other.signs
    )
  
  def __lt__(self, other):
    return self.rank < other.rank
  
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


def might_be_useful(expression_1, expression_2, binary_operator):
  """
  Pre-screen the usefulness before building a new expression.
  
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
  
  if binary_operator == ADD:
    return expression_1.value >= expression_2.value
  
  if binary_operator == SUBTRACT:
    return expression_1.value > expression_2.value
  
  if binary_operator == MULTIPLY or binary_operator == DIVIDE:
    return expression_1.value >= expression_2.value > 1
  
  return False


def is_valid(expression_1, expression_2, input_number_list):
  
  constants = [*expression_1.constants, *expression_2.constants]
  
  return all(
    constants.count(constant) <= input_number_list.count(constant)
    for constant in constants
  )


def is_positive_integer(number):
  
  return int(number) == number and number > 0


def compute_expression_set(input_number_list):
  """
  Recursively compute the set of expressions.
  """
  
  input_number_count = len(input_number_list)
  
  expression_set_from_mass = {
    1: {Expression(number) for number in input_number_list}
  }
  
  for mass in range(2, input_number_count + 1):
    expression_set_from_mass[mass] = set()
    for mass_1 in range(1, mass):
      mass_2 = mass - mass_1
      for binary_operator in OPERATORS:
        for expression_1 in expression_set_from_mass[mass_1]:
          for expression_2 in expression_set_from_mass[mass_2]:
            if might_be_useful(expression_1, expression_2, binary_operator) \
            and is_valid(expression_1, expression_2, input_number_list):
              expression = \
                        Expression(expression_1, expression_2, binary_operator)
              if is_positive_integer(expression.value):
                expression_set_from_mass[mass].add(expression)
  
  return set.union(*expression_set_from_mass.values())


def check_is_positive_integer(number_argument):
  
  try:
    number = int(number_argument)
  except ValueError:
    raise argparse.ArgumentTypeError(f"not integer: '{number_argument}'")
  
  if not number > 0:
    raise argparse.ArgumentTypeError(f"not positive: '{number_argument}'")
  
  return number


MAX_RESULTS_DEFAULT = 30


def parse_command_line_arguments():
  
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
  
  def expression_sort_key(expression):
    return (abs(expression.value - target), expression)
  
  expression_list = \
          sorted(
            compute_expression_set(input_number_list),
            key=expression_sort_key
          )
  
  print_results(expression_list, max_results_count)


if __name__ == '__main__':
  
  main()
