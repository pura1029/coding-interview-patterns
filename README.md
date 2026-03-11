# Coding Interview Patterns - Java

A comprehensive Java project covering **20 essential coding interview patterns**, each with **30 examples** (10 Easy, 10 Medium, 10 Hard) — **600 total coding solutions**.

## Project Structure

```
coding-interview-patterns/
├── pom.xml
├── README.md
└── src/main/java/patterns/
    ├── prefixsum/               # Pattern 1:  Prefix Sum
    ├── twopointers/             # Pattern 2:  Two Pointers
    ├── slidingwindow/           # Pattern 3:  Sliding Window
    ├── fastslowpointers/        # Pattern 4:  Fast & Slow Pointers
    ├── linkedlistreversal/      # Pattern 5:  LinkedList In-place Reversal
    ├── frequencycounting/       # Pattern 6:  Frequency Counting
    ├── monotonicstack/          # Pattern 7:  Monotonic Stack
    ├── bitmanipulation/         # Pattern 8:  Bit Manipulation
    ├── topkelements/            # Pattern 9:  Top 'K' Elements
    ├── overlappingintervals/    # Pattern 10: Overlapping Intervals
    ├── binarysearch/            # Pattern 11: Binary Search Variants
    ├── binarytreetraversal/     # Pattern 12: Binary Tree Traversal
    ├── dfs/                     # Pattern 13: Depth-First Search (DFS)
    ├── bfs/                     # Pattern 14: Breadth-First Search (BFS)
    ├── shortestpath/            # Pattern 15: Shortest Path
    ├── matrixtraversal/         # Pattern 16: Matrix Traversal
    ├── backtracking/            # Pattern 17: Backtracking
    ├── trie/                    # Pattern 18: Prefix Search (Trie)
    ├── greedy/                  # Pattern 19: Greedy
    └── dynamicprogramming/      # Pattern 20: Dynamic Programming
```

## Pattern Summary

| # | Pattern | When to Use | Key Idea | Time |
|---|---------|-------------|----------|------|
| 1 | **Prefix Sum** | Range sum queries, subarray sums | Pre-compute cumulative sums for O(1) range queries | O(n) |
| 2 | **Two Pointers** | Pair sums, palindromes, sorted arrays | Two references moving inward or at different speeds | O(n) |
| 3 | **Sliding Window** | Subarray/substring with constraint | Maintain a window that slides over data | O(n) |
| 4 | **Fast & Slow Pointers** | Cycle detection, middle of list | Two pointers at different speeds (tortoise & hare) | O(n) |
| 5 | **LinkedList Reversal** | Reverse list or sub-list | Manipulate prev/curr/next pointers in-place | O(n) |
| 6 | **Frequency Counting** | Anagrams, duplicates, majority element | Hash map to count occurrences | O(n) |
| 7 | **Monotonic Stack** | Next greater/smaller element | Stack maintaining increasing/decreasing order | O(n) |
| 8 | **Bit Manipulation** | Single number, power of 2, subsets | Bitwise operators (XOR, AND, shifts) | O(n) |
| 9 | **Top K Elements** | Kth largest, K most frequent | Heap (priority queue) of size K | O(n log k) |
| 10 | **Overlapping Intervals** | Merge intervals, meeting rooms | Sort by start, compare with previous end | O(n log n) |
| 11 | **Binary Search** | Sorted data, rotated arrays, answer space | Halve search space each iteration | O(log n) |
| 12 | **Binary Tree Traversal** | Tree operations, BST validation | Inorder / Preorder / Postorder / Level-order | O(n) |
| 13 | **DFS** | Paths, cycles, connected components | Explore depth-first with stack/recursion | O(V+E) |
| 14 | **BFS** | Shortest path (unweighted), level-order | Explore breadth-first with queue | O(V+E) |
| 15 | **Shortest Path** | Weighted graphs, network routing | Dijkstra / Bellman-Ford / Floyd-Warshall | O((V+E)logV) |
| 16 | **Matrix Traversal** | Grid problems, flood fill, spiral | DFS/BFS on 2D grid with direction arrays | O(m*n) |
| 17 | **Backtracking** | Permutations, combinations, N-Queens | Choose-Explore-Unchoose with pruning | O(2^n) |
| 18 | **Trie** | Prefix search, autocomplete, word search | Tree where each node is a character | O(L) |
| 19 | **Greedy** | Scheduling, jump games, stock trading | Make locally optimal choice at each step | O(n log n) |
| 20 | **Dynamic Programming** | Knapsack, LCS, counting paths | Store sub-problem solutions to avoid recomputation | O(n*m) |

## Examples Per Pattern

Each pattern file contains **30 examples** organized by difficulty:

| Difficulty | Count | Description |
|------------|-------|-------------|
| **Easy** | 10 | Fundamental application of the pattern |
| **Medium** | 10 | Requires combining the pattern with additional logic |
| **Hard** | 10 | Advanced problems often seen in top-tier coding interviews |

## How to Run

### Prerequisites
- Java 17+
- Maven 3.6+

### Compile
```bash
cd coding-interview-patterns
mvn compile
```

### Run any pattern
```bash
# Example: run the Two Pointers pattern
mvn exec:java -Dexec.mainClass="patterns.twopointers.TwoPointersPatterns"

# Example: run the Dynamic Programming pattern
mvn exec:java -Dexec.mainClass="patterns.dynamicprogramming.DynamicProgrammingPatterns"
```

### Run directly with javac
```bash
cd src/main/java
javac patterns/prefixsum/PrefixSumPatterns.java
java patterns.prefixsum.PrefixSumPatterns
```

## Resources

1. [Prefix Sum](https://lnkd.in/gCHQegHZ)
2. [Two Pointers](https://lnkd.in/gxVkhHvi)
3. [Sliding Window](https://lnkd.in/g_AapGpf)
4. [Fast & Slow Pointers](https://lnkd.in/gnwzQn9f)
5. [LinkedList In-place Reversal](https://lnkd.in/gkRpq8hA)
6. [Frequency Counting](https://lnkd.in/gAefPAiH)
7. [Monotonic Stack](https://lnkd.in/gnx5JvUB)
8. [Bit Manipulation](https://lnkd.in/g6qnCCqq)
9. [Top 'K' Elements](https://lnkd.in/gwgjChNa)
10. [Overlapping Intervals](https://lnkd.in/g45zauYS)
11. [Binary Search Variants](https://lnkd.in/g93YZeEU)
12. [Binary Tree Traversal](https://lnkd.in/gS-FRRxR)
13. [Depth-First Search (DFS)](https://lnkd.in/gQUpRGdk)
14. [Breadth-First Search (BFS)](https://lnkd.in/gFnJTxfH)
15. [Shortest Path](https://lnkd.in/ggGCX35B)
16. [Matrix Traversal](https://lnkd.in/gHSCeH5H)
17. [Backtracking](https://lnkd.in/g5EfE_5W)
18. [Prefix Search (Trie)](https://lnkd.in/gjwmvjrj)
19. [Greedy](https://lnkd.in/gWfq8B25)
20. [Dynamic Programming Patterns](https://lnkd.in/gJZXfmHK)

Full list of patterns at [algomaster.io](https://algomaster.io)
