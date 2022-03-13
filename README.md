# countdown

Python solvers for Countdown (letters and numbers).


## License

**Copyright 2022 Conway** <br>
Licensed under the GNU General Public License v3.0 (GPL-3.0-only). <br>
This is free software with NO WARRANTY etc. etc., see [LICENSE]. <br>

[LICENSE]: LICENSE


## Letters

```bash
$ ./letters.py [-m MAX_RESULTS] [-w WORD_LIST] LETTERS

Solve a Countdown letters game.

positional arguments:
  LETTERS         string containing the letters that can be used to form words

optional arguments:
  -h, --help      show this help message and exit
  -m MAX_RESULTS  maximum number of output results (default 15)
  -w WORD_LIST    word list file name (default yawl.txt)
```

The default word list `yawl.txt`
is the result of taking Yet Another Word List (YAWL) from
<<https://github.com/elasticdog/yawl/blob/86875d7/yawl-0.3.2.03/word.list>>
and adding the single-letter words "a" and "I".

Example:

```bash
$ ./letters.py ahgroient
8	ANTIHERO
8	EARTHING
8	HEARTING
8	INGATHER
8	THROEING
7	ANOTHER
7	GAHNITE
7	GENITOR
7	GOATIER
7	GRANITE
7	GRATINE
7	HAIRNET
7	HEARING
7	HEATING
7	HOARING
```


## Numbers

To be written.
