# Pattern 13: Depth-First Search (DFS)

## What is it?

DFS explores as **deep as possible** before backtracking. It uses recursion (or an explicit stack) and is the core technique for trees and graphs.

```
DFS vs BFS on a tree:

Tree:         1
            /   \
           2     3
          / \     \
         4   5     6

DFS (pre-order):  1 → 2 → 4 → 5 → 3 → 6   (go deep first)
BFS (level-order): 1 → 2, 3 → 4, 5, 6       (go wide first)

DFS goes DOWN before going ACROSS:
  Visit 1 → go left to 2 → go left to 4 (leaf)
  → backtrack to 2 → go right to 5 (leaf)
  → backtrack to 1 → go right to 3 → go right to 6
```

> **Real-world analogy:** Exploring a maze by always turning left. You go as deep as possible until you hit a dead end, then backtrack to the last fork and try another direction.

---

## DFS on Trees — Three Traversal Orders

```
Tree:      1
         /   \
        2     3
       / \
      4   5

Pre-order  (root, left, right):  1, 2, 4, 5, 3  ← process BEFORE children
In-order   (left, root, right):  4, 2, 5, 1, 3  ← process BETWEEN children
Post-order (left, right, root):  4, 5, 2, 3, 1  ← process AFTER children

Applications:
  Pre-order:  serialize/copy tree
  In-order:   BST gives sorted order
  Post-order: delete tree, compute subtree sizes
```

## DFS on Graphs — Cycle Detection

```
Directed graph cycle detection using 3 states:
  WHITE (0) = unvisited
  GRAY  (1) = in current DFS path (in progress)
  BLACK (2) = fully processed (done)

  Course prerequisites: [[1,0], [2,1], [0,2]]
  Graph: 0 → 1 → 2 → 0  (CYCLE!)

  DFS from 0:
    0: WHITE→GRAY
    → visit 1: WHITE→GRAY
    → visit 2: WHITE→GRAY
    → visit 0: already GRAY! → CYCLE DETECTED ❌

  If we reach a GRAY node, it's on our current path → cycle.
  If we reach a BLACK node, it's already done → safe, skip.
```

## DFS on Grids — Island Counting

```
Grid:  1  1  0  0  0
       1  1  0  0  0
       0  0  1  0  0
       0  0  0  1  1

  Number of islands? DFS flood-fill from each unvisited '1':

  Start (0,0): DFS marks all connected '1's as visited.
    (0,0)→(0,1)→(1,0)→(1,1) → 1 island

  Start (2,2): DFS marks (2,2) → 1 island

  Start (3,3): DFS marks (3,3)→(3,4) → 1 island

  Answer: 3 islands

  ██ ██ ·  ·  ·       Island 1: 4 cells
  ██ ██ ·  ·  ·       Island 2: 1 cell
  ·  ·  ██ ·  ·       Island 3: 2 cells
  ·  ·  ·  ██ ██
```

---

## Topological Sort (DFS Post-Order)

```
Course prerequisites: to take course 3, you need 1 and 2.
  Graph: 0→1, 0→2, 1→3, 2→3

      0
     / \
    1   2
     \ /
      3

  DFS post-order from 0:
    Visit 0 → Visit 1 → Visit 3 → post-order: [3]
    Back to 1 → post-order: [3, 1]
    Visit 2 → 3 already visited → post-order: [3, 1, 2]
    Back to 0 → post-order: [3, 1, 2, 0]

  Reverse: [0, 2, 1, 3] = valid course order ✅
```

---

## Real-World Applications

| Domain          | Application                      | DFS Pattern                  |
| --------------- | -------------------------------- | ---------------------------- |
| **Compilers**   | Dependency resolution            | Topological sort             |
| **File Systems**| Directory traversal              | Recursive DFS on tree        |
| **Web Crawlers**| Deep crawl from seed URL         | DFS on hyperlink graph       |
| **Social**      | Connected components (friends)   | DFS flood-fill on graph      |
| **Gaming**      | Maze solving                     | DFS with backtracking        |
| **Networking**  | Detect routing loops             | Cycle detection (3-state)    |

---

## When to Use

- **Connected components, island counting** — DFS flood-fill from each unvisited node
- **Cycle detection** — 3-state DFS (white/gray/black) for directed graphs
- **Topological sort** — post-order DFS, then reverse
- **Path finding** — all paths from source to target
- **Tree problems** — path sum, subtree matching, serialization

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| Graph DFS | O(V + E) | O(V) |
| Tree DFS | O(n) | O(h) where h = height |
| Grid DFS | O(m × n) | O(m × n) recursion stack |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Number of Islands | Easy | DFS flood-fill from each '1' |
| 2 | Max Depth of Binary Tree | Easy | max(left, right) + 1 |
| 3 | Same Tree | Easy | Compare nodes recursively |
| 4 | Path Sum | Easy | Subtract value, check at leaf |
| 5 | Flood Fill | Easy | DFS from start, change color |
| 6 | Leaf-Similar Trees | Easy | Collect leaf sequences, compare |
| 7 | Range Sum of BST | Easy | Prune branches outside range |
| 8 | Subtree of Another Tree | Easy | Check subtree match at each node |
| 9 | Merge Two Binary Trees | Easy | Add values, recurse children |
| 10 | Sum of Left Leaves | Easy | Track left child flag |
| 11 | Course Schedule (Cycle Detection) | Medium | DFS with 3-state visited array |
| 12 | Course Schedule II (Topological Sort) | Medium | Post-order DFS gives reverse topo order |
| 13 | Clone Graph | Medium | DFS with visited map of clones |
| 14 | Number of Provinces | Medium | DFS on adjacency matrix |
| 15 | Pacific Atlantic Water Flow | Medium | DFS from both oceans, find intersection |
| 16 | Surrounded Regions | Medium | DFS from borders, mark safe 'O's |
| 17 | All Paths Source to Target | Medium | DFS on DAG collecting paths |
| 18 | Keys and Rooms | Medium | DFS visiting rooms with keys |
| 19 | Graph Valid Tree | Medium | n-1 edges + fully connected |
| 20 | Accounts Merge | Medium | DFS/Union-Find on email graph |
| 21 | Word Search II (Trie+DFS) | Hard | Trie prunes invalid prefixes |
| 22 | Critical Connections (Bridges) | Hard | Tarjan's algorithm with low-link values |
| 23 | Longest Increasing Path in Matrix | Hard | DFS + memoization on grid |
| 24 | Alien Dictionary | Hard | Build graph from word order, topo sort |
| 25 | Max Area of Island | Hard | DFS counting cells per island |
| 26 | Number of Enclaves | Hard | Eliminate border-connected land first |
| 27 | Count Connected Components | Hard | DFS from each unvisited node |
| 28 | Jump Game III (Can Reach) | Hard | DFS with index jumping |
| 29 | Closed Islands | Hard | Flood-fill borders, count remaining |
| 30 | Make Connected (min cables) | Hard | Components - 1 = cables needed |

## Key Insight

> **Mark visited** to avoid infinite loops. For cycle detection in directed graphs, use **3 states** (unvisited/in-progress/done) — a node found "in-progress" during DFS means a back edge (cycle). For topological sort, use **post-order** DFS and reverse the result. DFS + memoization = top-down DP.
