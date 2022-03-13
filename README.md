# countdown

Python solvers for Countdown (letters and numbers).


## License

**Copyright 2022 Conway** <br>
Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
This is free software with NO WARRANTY etc. etc., see [LICENSE].

[LICENSE]: LICENSE


## Letters

```bash
$ ./letters.py [-m MAX_RESULTS] [-w WORD_LIST] LETTERS

Solve a Countdown letters game.

positional arguments:
  LETTERS         string containing the letters that can be used to form words

optional arguments:
  -h, --help      show this help message and exit
  -m MAX_RESULTS  maximum number of output results (default 10)
  -w WORD_LIST    word list file name (default yawl.txt)
```

The default word list is `yawl.txt`,
the result of taking Yet Another Word List (YAWL) from
<<https://github.com/elasticdog/yawl/blob/86875d7/yawl-0.3.2.03/word.list>>
and adding the single-letter words "a" and "I".


## Numbers

To be written.
