# countdown/python

Python solvers for Countdown (letters and numbers).


## Letters

```bash
$ ./letters.py [-m MAX_RESULTS] [-w WORD_LIST] LETTERS

Solve a Countdown letters game.

positional arguments:
  LETTERS         string containing the letters that can be used to form words

optional arguments:
  -h, --help      show this help message and exit
  -m MAX_RESULTS  maximum number of output results (default 15)
  -w WORD_LIST    word list file name (default ../yawl.txt)
```

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

Currently very slow.

```bash
$ ./numbers.py [-m MAX_RESULTS] TARGET NUMBER [NUMBER ...]

Solve a Countdown numbers game.

positional arguments:
  TARGET          target number (positive integer)
  NUMBER          number (positive integer) that can be used to obtain the
                  target

optional arguments:
  -h, --help      show this help message and exit
  -m MAX_RESULTS  maximum number of output results (default 15)
```

Example:

```bash
$ ./numbers.py 420 75 50 6 9 4 2
420	((75 * 50 - 6) / 9 + 4)
420	(((75 - 50) * 2 - 4) * 9 + 6)
420	(50 - 75 / (9 - 4)) * 6 * 2
420	(75 + 9) * 50 / (6 + 4)
420	(75 + (9 + 6) * 2) * 4
420	((50 - 4) * 9 + 6)
420	((50 - 4) * (9 + 6) / 2 + 75)
420	(75 + 4 - 9) * 6
420	((50 + 4 - 9) * 6 + 75 * 2)
420	(50 * 2 + 75 / (9 + 6)) * 4
420	((75 - 9 - 4) * 6 + 50 - 2)
420	((75 - 50 / 2 - 4) * 9 + 6)
420	((50 + 4 + 2) * 6 + 75 + 9)
420	75 * (50 + 6) / (9 - 4) / 2
420	(75 * 6 + 4 - 50 / 2 - 9)
```
