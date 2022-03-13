#!/usr/bin/env python3

"""
# letters.py

Solve a Countdown letters game.
"""


import argparse
import typing


def normalise_word(word: str) -> str:
  
  return word.strip().upper()


def extract_word_list(word_list_file: argparse.FileType) -> typing.List[str]:
  
  word_list = []
  
  for word in word_list_file.read().splitlines():
    word_list.append(normalise_word(word))
  
  return word_list


def parse_command_line_arguments() -> object:
  
  MAX_RESULTS_DEFAULT = 10
  WORD_LIST_FILE_NAME_DEFAULT = 'yawl.txt'
  
  parser = \
          argparse.ArgumentParser(
            description='Solve a Countdown letters game.'
          )
  
  parser.add_argument(
    'input_letters_string',
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
  
  input_letters_string = parsed_arguments.input_letters_string
  max_results_count = parsed_arguments.max_results_count
  word_list_file = parsed_arguments.word_list_file
  
  word_list = extract_word_list(word_list_file)
  print(word_list)
  
  # TODO:
  # - For each word in word list:
  #   - If word can be constructed from input letters:
  #     - Append to results list
  # - Sort results and cull
  # - Write to stdout


if __name__ == '__main__':
  
  main()
