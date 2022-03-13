#!/usr/bin/env python3

"""
# letters.py

Solve a Countdown letters game.

Copyright 2022 Conway
Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
This is free software with NO WARRANTY etc. etc., see LICENSE.
"""


import argparse
import typing


def normalise_letters(word: str) -> str:
  
  return word.strip().upper()


def is_valid(word: str, input_letters: str) -> bool:
  
  return all(
    word.count(letter) <= input_letters.count(letter)
    for letter in word
  )


def compute_valid_word_list(
  word_list: typing.List[str],
  input_letters : str,
) -> typing.List[str]:
  
  return [
    word
      for word in word_list
      if is_valid(word, input_letters)
  ]


def parse_command_line_arguments() -> object:
  
  MAX_RESULTS_DEFAULT = 10
  WORD_LIST_FILE_NAME_DEFAULT = 'yawl.txt'
  
  parser = \
          argparse.ArgumentParser(
            description='Solve a Countdown letters game.'
          )
  
  parser.add_argument(
    'input_letters',
    metavar='LETTERS',
    type=str,
    help='string containing the letters that can be used to form words',
  )
  
  parser.add_argument(
    '-m', dest="max_results_count",
    metavar='MAX_RESULTS',
    type=int,
    default=MAX_RESULTS_DEFAULT,
    help=f'maximum number of output results (default {MAX_RESULTS_DEFAULT})',
  )
  
  parser.add_argument(
    '-w', dest="word_list_file",
    metavar='WORD_LIST',
    type=argparse.FileType('r', encoding='UTF-8'),
    default=WORD_LIST_FILE_NAME_DEFAULT,
    help=f'word list file name (default {WORD_LIST_FILE_NAME_DEFAULT})',
  )
  
  return parser.parse_args()


def main():
  
  parsed_arguments = parse_command_line_arguments()
  
  input_letters = parsed_arguments.input_letters
  max_results_count = parsed_arguments.max_results_count
  word_list_file = parsed_arguments.word_list_file
  
  input_letters = normalise_letters(input_letters)
  word_list = [
    normalise_letters(word)
      for word in word_list_file.read().splitlines()
  ]
  
  valid_word_list = compute_valid_word_list(word_list, input_letters)
  print(valid_word_list)
  
  # TODO:
  # - Sort results and cull
  # - Write to stdout


if __name__ == '__main__':
  
  main()
