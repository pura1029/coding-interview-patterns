# Pattern 17: Backtracking

## What is it?

Backtracking systematically explores **all possibilities** by building solutions incrementally and **abandoning invalid paths early** (pruning). It follows the pattern: **Choose → Explore → Unchoose**.

```
Backtracking Decision Tree for subsets of [1,2,3]:

                    []
                 /      \
              [1]        []
             /   \      /   \
          [1,2]  [1]  [2]    []
          / \    / \   / \   / \
      [1,2,3][1,2][1,3][1][2,3][2][3][]

  Include 1?  Include 2?  Include 3?

  All subsets: [], [1], [2], [3], [1,2], [1,3], [2,3], [1,2,3]

  PRUNING: if a path can never lead to a valid solution,
  abandon it immediately (don't explore subtree).
```

> **Real-world analogy:** Solving a maze. Walk forward until you hit a dead end, then **backtrack** to the last fork and try a different path. You never re-explore a path you already know fails.

---

## The Backtracking Template

```
function backtrack(state, choices):
    if state is a complete solution:
        record solution
        return
    
    for choice in choices:
        if choice is valid:          ← PRUNE invalid choices
            make choice              ← CHOOSE
            backtrack(state, ...)    ← EXPLORE
            undo choice              ← UNCHOOSE (backtrack)
```

---

## Classic Problems Visualized

### N-Queens

```
Place N queens on N×N board so no two attack each other.
N=4 solution:

  . Q . .      Place queen row by row.
  . . . Q      At each row, try each column.
  Q . . .      Check: no queen in same column,
  . . Q .      same diagonal, or same anti-diagonal.

  Row 0: try col 0 → ok. Place Q at (0,0).
  Row 1: try col 0 → same column! Skip.
         try col 1 → same diagonal! Skip.
         try col 2 → ok. Place Q at (1,2).
  Row 2: all columns conflict → BACKTRACK!
         Remove Q from (1,2). Try col 3 → ok.
  ...continue until all 4 queens placed.

  Pruning: use sets for columns, diagonals, anti-diagonals.
  Check in O(1) instead of scanning the board.
```

### Permutations (with duplicates)

```
Permutations of [1, 1, 2]:

  Without duplicate handling: [1,1,2] [1,2,1] [1,1,2] [1,2,1] [2,1,1] [2,1,1]
                               ↑ duplicates!

  Fix: sort first, then skip if nums[i] == nums[i-1] and i-1 not used.

  Decision tree (pruned):
        []
       / | \
     [1] [1]skip [2]
     / \         |
  [1,1] [1,2]  [2,1]
    |     |      |
  [1,1,2][1,2,1][2,1,1]

  Result: [1,1,2], [1,2,1], [2,1,1] — no duplicates ✅
```

### Sudoku Solver

```
  5 3 . | . 7 . | . . .
  6 . . | 1 9 5 | . . .
  . 9 8 | . . . | . 6 .
  ------+-------+------
  8 . . | . 6 . | . . 3
  4 . . | 8 . 3 | . . 1
  7 . . | . 2 . | . . 6
  ------+-------+------
  . 6 . | . . . | 2 8 .
  . . . | 4 1 9 | . . 5
  . . . | . 8 . | . 7 9

  Strategy: find first empty cell, try digits 1-9.
  For each digit: check row, column, and 3×3 box.
  If valid → place and recurse to next empty cell.
  If no digit works → BACKTRACK (remove digit, try next).

  Pruning: use boolean[9] for each row, column, and box.
  Eliminates most candidates instantly.
```

---

## Backtracking vs. Other Approaches

```
┌──────────────────┬──────────────────┬─────────────────────┐
│ Approach         │ When to Use      │ Example             │
├──────────────────┼──────────────────┼─────────────────────┤
│ Backtracking     │ Find ALL valid   │ Permutations,       │
│                  │ solutions        │ N-Queens, Sudoku    │
├──────────────────┼──────────────────┼─────────────────────┤
│ Greedy           │ ONE optimal      │ Interval scheduling │
│                  │ solution (local  │                     │
│                  │ choices work)    │                     │
├──────────────────┼──────────────────┼─────────────────────┤
│ DP               │ Count/optimize   │ Coin change,        │
│                  │ (overlapping     │ knapsack            │
│                  │ subproblems)     │                     │
└──────────────────┴──────────────────┴─────────────────────┘

Backtracking: O(n!) or O(2^n) — but pruning makes it practical
```

---

## When to Use

- **Permutations, combinations, subsets** — enumerate all arrangements
- **Constraint satisfaction** — N-Queens, Sudoku, crossword puzzles
- **Word search on grid** — DFS with visited tracking
- **Expression generation** — generate parentheses, add operators
- **Partitioning problems** — palindrome partitioning, K equal subsets

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| Subsets / Combinations | O(2^n) | O(n) |
| Permutations | O(n!) | O(n) |
| N-Queens | O(n!) | O(n) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Generate Parentheses (simplified) | Easy | Track open/close count |
| 2 | Phone Number Letters (single digit) | Easy | Map digit to letters |
| 3 | Binary Watch | Easy | Count bits in hour+minute |
| 4 | Subsets | Easy | Include/exclude each element |
| 5 | Permutations | Easy | Swap or visited-array approach |
| 6 | Combinations C(n,k) | Easy | Choose k from n with start index |
| 7 | Combination Sum (unlimited) | Easy | Reuse same element, no going back |
| 8 | Path Sum II | Easy | Collect paths matching target sum |
| 9 | Count Max OR Subsets | Easy | Track max OR, count subsets achieving it |
| 10 | Jump Game (can reach 0) | Easy | Simple backtrack or greedy |
| 11 | Generate Parentheses | Medium | Track open/close, prune invalid |
| 12 | Letter Combinations | Medium | DFS through digit-to-char mapping |
| 13 | Word Search | Medium | DFS on grid with visited tracking |
| 14 | Subsets II (with dups) | Medium | Sort + skip consecutive duplicates |
| 15 | Combination Sum II (use once) | Medium | Sort + skip duplicates at same level |
| 16 | Permutations II (with dups) | Medium | Sort + skip same value at same position |
| 17 | Palindrome Partitioning | Medium | Try all cuts, check palindrome |
| 18 | Restore IP Addresses | Medium | Try 1-3 digit segments, validate |
| 19 | Beautiful Arrangement | Medium | Count valid permutations by constraint |
| 20 | Combination Sum III | Medium | Choose k numbers summing to n |
| 21 | N-Queens | Hard | Place queens row by row, check constraints |
| 22 | Sudoku Solver | Hard | Fill cells, validate row/col/box |
| 23 | Word Search II | Hard | Trie + DFS for multiple words |
| 24 | Expression Add Operators | Hard | Insert +,-,* between digits |
| 25 | Total N-Queens | Hard | Count all valid placements |
| 26 | Partition into K Equal Subsets | Hard | Backtrack assignment to k buckets |
| 27 | Remove Invalid Parentheses | Hard | BFS removing one bracket at a time |
| 28 | Factor Combinations | Hard | Enumerate factor decompositions |
| 29 | Robot Room Cleaner | Hard | DFS with relative directions |
| 30 | Word Squares | Hard | Trie-guided row-by-row construction |

## Key Insight

> **Choose → Explore → Unchoose.** The most common pruning technique for duplicates: sort first, then `if(i > start && nums[i] == nums[i-1]) continue`. For constraint satisfaction (N-Queens, Sudoku), use sets or boolean arrays to check constraints in O(1). Backtracking is brute force made practical through aggressive pruning.
