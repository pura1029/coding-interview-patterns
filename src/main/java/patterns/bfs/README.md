# Pattern 14: Breadth-First Search (BFS)

## What is it?

BFS explores a graph **level by level** using a queue. It visits all nodes at distance 1 before distance 2, distance 2 before distance 3, and so on. This guarantees the **shortest path** in unweighted graphs.

```
BFS vs DFS Traversal:

Tree:         1
            /   \
           2     3
          / \     \
         4   5     6

BFS (level-order): 1 → 2, 3 → 4, 5, 6   (breadth first)
DFS (pre-order):   1 → 2 → 4 → 5 → 3 → 6 (depth first)

BFS explores like ripples in water:
         ·····3·····
       ···2·········
     ··1··············
       ···2·········
         ·····3·····
     Level 0  1  2
```

> **Real-world analogy:** A virus spreading in a population. Each infected person passes it to all their contacts (level 1), who then pass it to their contacts (level 2), and so on. BFS models this wave-like propagation.

---

## How BFS Works — Step by Step

```
Graph:  A──B──D
        |  |
        C──E──F

Find shortest path from A to F:

Queue:  [A]              Visited: {A}
Level 0: Process A → neighbors B, C
Queue:  [B, C]           Visited: {A, B, C}

Level 1: Process B → neighbors D, E
         Process C → neighbor E (already visited)
Queue:  [D, E]           Visited: {A, B, C, D, E}

Level 2: Process D → no new neighbors
         Process E → neighbor F
Queue:  [F]              Visited: {A, B, C, D, E, F}

Level 3: Process F → FOUND! Distance = 3

Path: A → B → E → F (or A → C → E → F, both length 3)
```

### BFS Template

```
function bfs(start):
    queue = [start]
    visited = {start}
    level = 0
    while queue is not empty:
        size = queue.size()       ← process one level at a time
        for i in 0..size-1:
            node = queue.poll()
            if node is target → return level
            for neighbor in node.neighbors:
                if neighbor not in visited:
                    visited.add(neighbor)
                    queue.add(neighbor)
        level++
    return -1  (not reachable)
```

---

## Key BFS Patterns

### 1. Level-Order Tree Traversal

```
Tree:       3
          /   \
         9    20
              / \
            15   7

BFS with level tracking:
  Level 0: [3]
  Level 1: [9, 20]
  Level 2: [15, 7]

Code pattern:
  while queue not empty:
      levelSize = queue.size()     ← KEY: snapshot size before processing
      levelNodes = []
      for i in 0..levelSize-1:
          node = queue.poll()
          levelNodes.add(node.val)
          if node.left  → queue.add(node.left)
          if node.right → queue.add(node.right)
      result.add(levelNodes)
```

### 2. Multi-Source BFS

Start from **ALL sources simultaneously**. All sources are level 0.

```
Rotting Oranges:
  Grid:  2  1  1          2 = rotten (source)
         1  1  0          1 = fresh
         0  1  1          0 = empty

  Start: enqueue ALL rotten oranges → (0,0) at time 0

  Time 0: (0,0) is rotten
  Time 1: (0,1), (1,0) rot      ← neighbors of (0,0)
  Time 2: (0,2), (1,1) rot      ← neighbors of time-1 cells
  Time 3: (2,1) rots
  Time 4: (2,2) rots             ← last orange

  2  1  1       2  2  1       2  2  2       2  2  2       2  2  2
  1  1  0   →   2  1  0   →   2  2  0   →   2  2  0   →   2  2  0
  0  1  1       0  1  1       0  1  1       0  2  1       0  2  2
  t=0           t=1           t=2           t=3           t=4 ✅

  Answer: 4 minutes
```

### 3. State-Space BFS

Each "node" is a **state** (not just a position). Track `visited[state]`.

```
Open the Lock: "0000" → "0202" (target), deadends = ["0201","0101","0102"]

Each state is a 4-digit combination. From any state, you can
turn any of the 4 wheels up or down (8 neighbors per state).

  "0000" → "1000","9000","0100","0900","0010","0090","0001","0009"
                                   ↓
  "0100" → "0200","0000","0110","0190","0101"(dead!),"0109"
                     ↓
  "0200" → "0201"(dead!),"0209","0210","0290","0200","0200","0202" ✅

BFS guarantees minimum turns = 3
```

---

## BFS on Grids

```
Grid BFS: move in 4 directions (or 8 for diagonal)

  Directions: {(-1,0), (1,0), (0,-1), (0,1)}  ← up, down, left, right

  Shortest Path in Binary Matrix (8-directional):
  ┌───┬───┬───┐
  │ 0 │ 0 │ 0 │    0 = open, 1 = blocked
  ├───┼───┼───┤    Find shortest path from (0,0) to (2,2)
  │ 1 │ 1 │ 0 │
  ├───┼───┼───┤    BFS: (0,0)→(0,1)→(0,2)→(1,2)→(2,2)
  │ 1 │ 1 │ 0 │    Path length = 5
  └───┴───┴───┘

  BFS guarantees this is the SHORTEST path (no Dijkstra needed
  because all edges have weight 1).
```

---

## BFS vs Dijkstra

```
┌──────────────────┬───────────────────┬──────────────────────┐
│                  │ BFS               │ Dijkstra             │
├──────────────────┼───────────────────┼──────────────────────┤
│ Edge weights     │ All equal (or 1)  │ Non-negative varying │
│ Data structure   │ Queue             │ Priority Queue       │
│ Time complexity  │ O(V + E)          │ O((V+E) log V)       │
│ Shortest path?   │ ✅ (unweighted)   │ ✅ (weighted)        │
│ When to use      │ Unweighted graph  │ Weighted graph       │
└──────────────────┴───────────────────┴──────────────────────┘

If edges are 0 or 1 → use 0-1 BFS (deque):
  weight 0: addFirst (front of deque)
  weight 1: addLast (back of deque)
  Still O(V + E)!
```

---

## Real-World Applications

| Domain              | Application                     | BFS Pattern                            |
| ------------------- | ------------------------------- | -------------------------------------- |
| **Social Networks** | "People you may know"           | BFS from user, level 2 = friends of friends |
| **Navigation**      | Google Maps shortest route      | BFS/Dijkstra on road graph             |
| **Web Crawling**    | Crawl all pages from a seed URL | BFS on hyperlink graph                 |
| **Game AI**         | Pathfinding (pac-man, chess)    | BFS for shortest path to target        |
| **Networking**      | Broadcast packet propagation    | Multi-source BFS from sender           |
| **Puzzle Solving**  | Rubik's cube, sliding puzzle    | State-space BFS (each config = node)   |

### LinkedIn — "Degrees of Connection"

```
You ──(1st)──► Alice ──(2nd)──► Bob ──(3rd)──► Charlie

BFS from "You":
  Level 1 (1st connections): Alice, Dave, Eve
  Level 2 (2nd connections): Bob, Frank, Grace
  Level 3 (3rd connections): Charlie, Henry

LinkedIn limits to 3 levels (3rd-degree connections).
BFS naturally computes connection degree.
```

---

## When to Use

- **Shortest path in unweighted graphs** — BFS guarantees minimum steps
- **Level-order tree traversal** — process all nodes at same depth
- **Multi-source BFS** — rotting oranges, walls & gates, distance maps
- **State-space search** — puzzles, lock combinations, word transformations
- **0-1 BFS** — graph with edge weights 0 and 1 (use deque)
- **Bidirectional BFS** — search from both start and end, meet in middle

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| Graph BFS | O(V + E) | O(V) |
| Tree level-order | O(n) | O(w) where w = max width |
| Grid BFS | O(m × n) | O(m × n) |
| State-space BFS | O(states × transitions) | O(states) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Level Order Traversal | Easy | Queue processes one level at a time |
| 2 | Minimum Depth of Binary Tree | Easy | First leaf found = minimum depth |
| 3 | Average of Levels | Easy | Sum / count per level |
| 4 | N-ary Tree Level Order | Easy | Same as binary but iterate children |
| 5 | Cousins in Binary Tree | Easy | Same depth, different parent |
| 6 | Univalued Binary Tree | Easy | BFS checking all values equal |
| 7 | Maximum Depth via BFS | Easy | Count number of levels |
| 8 | Find if Path Exists | Easy | BFS from source to destination |
| 9 | Symmetric Tree via BFS | Easy | Level-order mirror comparison |
| 10 | Nearest Exit from Maze | Easy | BFS from entrance to border cell |
| 11 | Rotting Oranges | Medium | Multi-source BFS from all rotten |
| 12 | Word Ladder | Medium | BFS on word graph (1 char diff) |
| 13 | 01 Matrix (distance to 0) | Medium | Multi-source BFS from all zeros |
| 14 | Open the Lock | Medium | BFS on 4-digit state space |
| 15 | Number of Islands (BFS) | Medium | BFS flood-fill from each '1' |
| 16 | Shortest Bridge | Medium | Find island, BFS expand to second |
| 17 | Cheapest Flights Within K Stops | Medium | BFS/Bellman-Ford with hop limit |
| 18 | As Far from Land as Possible | Medium | Multi-source BFS from all land cells |
| 19 | Snakes and Ladders | Medium | BFS on board with jumps |
| 20 | Minimum Genetic Mutation | Medium | BFS on gene string graph |
| 21 | Word Ladder II (all shortest) | Hard | BFS + backtrack for all paths |
| 22 | Sliding Puzzle | Hard | BFS on board state strings |
| 23 | Cut Off Trees for Golf | Hard | BFS between consecutive trees |
| 24 | Shortest Path in Binary Matrix | Hard | 8-directional BFS |
| 25 | Shortest Path With Obstacles | Hard | BFS with state (row, col, removals) |
| 26 | Minimum Knight Moves | Hard | BFS on chess knight graph |
| 27 | Word Ladder II (full paths) | Hard | BFS + parent tracking + DFS rebuild |
| 28 | Swim in Rising Water | Hard | Priority BFS (Dijkstra-like) |
| 29 | Multi-source BFS | Hard | Start from all sources simultaneously |
| 30 | Priority BFS (Dijkstra-like) | Hard | Min-heap based BFS for weighted grids |

## Key Insight

> BFS = Queue + Visited. For shortest path, BFS guarantees minimum steps. For **multi-source** BFS, start from all sources simultaneously (enqueue all at level 0). For **state-space** BFS, each "node" is a complete state — track `visited[state]` to avoid revisiting. When edges have weights, upgrade to Dijkstra (priority queue).
