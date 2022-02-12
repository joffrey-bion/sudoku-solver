Dataset from https://github.com/t-dillon/tdoku (see LICENSE)

## kaggle

A dataset of 100000 puzzles sampled from a larger dataset created for ML experiments and hosted on Kaggle.
The Kaggle page has a link to the code used for puzzle generation.
These puzzles are extremely easy. Trivial even.
Each has a large number of clues with lots of redundancy.
100% of the puzzles are solved with naked singles only.
This is more a test of a parsing and initialization than actual solving speed.

## unbiased

A dataset of 1 million puzzles sampled with uniform probability (conditional on the number of clues) from the set of 
all minimal Sudoku puzzles.
This dataset was generated using the grid_tools puzzle sampler in https://github.com/t-dillon/tdoku.
This sampler over-samples low-clue puzzles relative to high-clue puzzles, but in a quantifiable way, and for a given
clue count every minimal Sudoku arises with the same probability.
This makes an interesting dataset for benchmarking since the puzzles are representative of Sudoku in general, whereas
other data sets often contain puzzles selected for special properties (clue counts, extremes of difficulty)
that may favor one solver or another.
The puzzles in this dataset are generally very easy because hard puzzles are rare.

## 17_clue

A complete or nearly-complete dataset of all 17-clue puzzles (49158 of them as of 2020-01) maintained by Gordon Royle 
of the University of Western Australia.
These puzzles are also very easy.
About half of them can be solved with naked and hidden singles only.
This is largely a test of how well the solver is optimized for hidden singles.

## magictour_top1465

An old list of 1465 hard puzzles that's been used as a common benchmark (moderately hard, but not among the hardest by modern standards).

## forum_hardest_1905

A list of over 2 million hard puzzles maintained by members of the Enjoy Sudoku Players Forum.
This was the hardest puzzle list as of May 2019, and it generally contains puzzles with Sudoku Explainer difficulty
ratings above 10.0.
Because this dataset is huge and its puzzles are hard this is a good dataset for testing solving speed.

## forum_hardest_1905_11+

A harder subset of the forum_hardest_1905 list sampled from puzzles having a Sudoku Explainer difficulty rating above
11.0 (around 49,000 puzzles; about the same size as the 17-clue list).

## forum_hardest_1106

The list of 376 puzzles that originally kicked off the linked player's forum hardest puzzle thread,
and which seems to contain on average the hardest puzzles of all, at least in terms of backtracking difficulty
(i.e., these puzzles take the longest to solve and require the most backtracking across a wide range of solvers).

## serg_benchmark

A benchmark dataset maintained by user Serg of the Enjoy Sudoku Player's Forum.
This dataset contains puzzles with two or more solutions.
The idea of hardness may not apply to these since they are not proper Sudoku.
However, backtracking solvers are commonly used in searches of various kinds where it is not known whether a puzzle has 
0, 1, or 2+ solutions, so this is an important use case.

## gen_puzzles

The raw output of a puzzle generator containing mostly invalid puzzles with 0 solutions.
As with the previous dataset the idea of hardness may not apply to these since they are not proper Sudoku.
However, backtracking solvers are commonly used in searches of various kinds where it is not known whether a puzzle has 
0, 1, or 2+ solutions, so this is an important use case.
