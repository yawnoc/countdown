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
  -m MAX_RESULTS  maximum number of output results (default 30)
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
7	INEARTH
7	INGRATE
7	NEGATOR
7	ORATING
7	ORIGANE
7	ORTHIAN
7	OTARINE
7	RIGHTEN
7	ROATING
7	TANGIER
7	TEARING
7	THERIAN
6	AIGRET
6	ANIGHT
6	ANTHER
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
  -m MAX_RESULTS  maximum number of output results (default 30)
```

Example:

```bash
$ ./numbers.py 420 75 50 6 9 4 2
420	(75 + 4 - 9) * 6
420	(50 - 4) * 9 + 6
420	(50 + 6 + 4) * (9 - 2)
420	(75 + 9) * 50 / (6 + 4)
420	(75 + 9) * (6 + 4) / 2
420	(75 + (9 + 6) * 2) * 4
420	(75 * 50 - 6) / 9 + 4
420	75 * (50 + 6) / (9 - 4) / 2
420	75 * 6 + 4 - 50 / 2 - 9
420	(50 - 2) * (9 + 6) - 75 * 4
420	(50 + 4 - 9) * 6 + 75 * 2
420	(50 - 4) * (9 + 6) / 2 + 75
420	(50 + 6) * (4 + 2) + 75 + 9
420	(75 - 9 - 4) * 6 + 50 - 2
420	(50 + 4 + 2) * 6 + 75 + 9
420	(75 - 50 / (9 - 4) / 2) * 6
420	(50 * 2 + 75 / (9 + 6)) * 4
420	(75 - 50 / 2 - 4) * 9 + 6
420	((50 - 4) * 6 + 9 - 75) * 2
420	(50 - 75 / (9 - 4)) * 6 * 2
420	(50 + (6 + 4) / 2) * 9 - 75
420	((75 - 50) * 2 - 4) * 9 + 6
421	75 * 6 - 50 / 2 - 4
421	(75 - 9) * 6 + 50 / 2
421	(75 - 6) * 9 - 50 * 4
419	75 * (9 - 4) + 50 - 6
419	50 * (9 - 2) + 75 - 6
421	50 * (9 - 2) + 75 - 4
419	(75 - 4) * 6 + 2 - 9
421	(75 + 2) * 6 + 9 - 50
```
