# Pattern 15: Shortest Path

## What is it?

Shortest path algorithms find the **minimum cost path** between nodes in a graph. The choice of algorithm depends on edge weights and whether you need single-source or all-pairs.

```
Choosing the right algorithm:

  Unweighted graph?         → BFS (O(V+E))
  Non-negative weights?     → Dijkstra (O((V+E) log V))
  Negative weights?         → Bellman-Ford (O(V×E))
  All pairs needed?         → Floyd-Warshall (O(V³))
  Need MST?                 → Prim's or Kruskal's
```

---

## The Three Core Algorithms

### 1. Dijkstra's Algorithm (Greedy)

```
Graph:  A──(4)──B──(1)──D
        |       |       |
       (1)     (2)     (3)
        |       |       |
        C──(5)──E──(2)──F

Find shortest path from A to all nodes:

  Priority Queue (min-heap): [(0,A)]
  dist: A=0, B=∞, C=∞, D=∞, E=∞, F=∞

  Pop A(0): relax B→4, C→1
  dist: A=0, B=4, C=1   PQ: [(1,C), (4,B)]

  Pop C(1): relax E→1+5=6
  dist: A=0, B=4, C=1, E=6   PQ: [(4,B), (6,E)]

  Pop B(4): relax D→4+1=5, E→4+2=6 (not better)
  dist: A=0, B=4, C=1, D=5, E=6   PQ: [(5,D), (6,E)]

  Pop D(5): relax F→5+3=8
  dist: A=0, B=4, C=1, D=5, E=6, F=8   PQ: [(6,E), (8,F)]

  Pop E(6): relax F→6+2=8 (not better)
  Pop F(8): done

  Shortest: A→C=1, A→B=4, A→B→D=5, A→C→E=6, A→B→D→F=8
```

**Real-world:** Google Maps — Dijkstra (or A*) finds the fastest route between two points on the road network.

### 2. Bellman-Ford (Handles Negative Edges)

```
Why Dijkstra fails with negative edges:

  A──(1)──B──(-3)──C
  |                 |
  └──(2)──D──(1)───┘

  Dijkstra processes B(1) before discovering A→D→C→B = 2+1-3 = 0 < 1
  It greedily committed to dist[B]=1 and never reconsiders.

Bellman-Ford: relax ALL edges V-1 times:

  Round 1: A→B=1, A→D=2, B→C=-2, D→C=3
  Round 2: A→B→C=-2 updates C. C→B→... may update further.
  ...
  After V-1 rounds, all shortest paths are found.

  Bonus: Run one more round. If any distance updates → NEGATIVE CYCLE!
```

### 3. Floyd-Warshall (All Pairs)

```
dist[i][j] = shortest path from i to j

Triple loop: for each intermediate node k,
  dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])

Example (3 nodes):
  Initial:         After k=0:        After k=1:        After k=2:
    0  3  ∞          0  3  ∞           0  3  4           0  3  4
    ∞  0  1          ∞  0  1           ∞  0  1           ∞  0  1
    2  ∞  0          2  5  0           2  5  0           2  5  0
                          ↑                ↑
                     2→0→1=5          0→1→2=4

Use case: "Find shortest path between ALL pairs of cities"
```

---

## Algorithm Decision Tree

```
Is the graph weighted?
├── No → BFS (O(V+E)) ← simplest
└── Yes
    ├── All weights non-negative?
    │   ├── Single source → Dijkstra (O((V+E) log V))
    │   └── All pairs → Floyd-Warshall (O(V³))
    └── Has negative weights?
        ├── Need negative cycle detection? → Bellman-Ford (O(V×E))
        └── All pairs with negative → Floyd-Warshall (check diagonal)

Need minimum spanning tree?
├── Dense graph → Prim's (O(V²) or O(E log V) with heap)
└── Sparse graph → Kruskal's (O(E log E) with Union-Find)
```

---

## Real-World Applications

| Domain          | Application                           | Algorithm                    |
| --------------- | ------------------------------------- | ---------------------------- |
| **Navigation**  | Google Maps / Waze shortest route     | Dijkstra / A* (with heuristic) |
| **Networking**  | OSPF routing protocol                 | Dijkstra (link-state)         |
| **Finance**     | Currency arbitrage detection          | Bellman-Ford (negative cycles)|
| **Gaming**      | NPC pathfinding (A*)                  | Dijkstra + heuristic          |
| **Airlines**    | Cheapest flight with K stops          | Modified Bellman-Ford         |
| **Social**      | Degrees of separation                 | BFS (unweighted)             |

---

## When to Use

- **Single-source shortest path** — Dijkstra (non-negative), Bellman-Ford (negative edges)
- **All-pairs shortest path** — Floyd-Warshall (O(V³))
- **Minimum spanning tree** — Prim's (dense), Kruskal's (sparse)
- **Grid-based weighted pathfinding** — Dijkstra on cells with varying costs
- **Negative cycle detection** — Bellman-Ford (V-th relaxation finds cycles)

## Complexity

| Algorithm | Time | Space |
|-----------|------|-------|
| Dijkstra | O((V+E) log V) | O(V) |
| Bellman-Ford | O(V × E) | O(V) |
| Floyd-Warshall | O(V³) | O(V²) |
| Prim's (heap) | O(E log V) | O(V) |
| Kruskal's | O(E log E) | O(V) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Dijkstra's Algorithm | Easy | Priority queue, relax min-cost edges |
| 2 | Valid Path in Graph | Easy | BFS/DFS reachability check |
| 3 | Minimum Cost Path (grid) | Easy | Dijkstra on grid cells |
| 4 | Find Center of Star Graph | Easy | Node appearing in all edges |
| 5 | Count Paths | Easy | Graph traversal counting |
| 6 | Shortest Path Length | Easy | Dijkstra between two nodes |
| 7 | The Maze (ball rolling) | Easy | BFS with rolling until wall |
| 8 | Minimum Path Sum (grid) | Easy | DP: min from top or left |
| 9 | Maximum Distance to Water | Easy | Multi-source BFS from land |
| 10 | Unique Paths | Easy | DP: dp[i][j] = dp[i-1][j] + dp[i][j-1] |
| 11 | Cheapest Flights Within K Stops | Medium | Bellman-Ford with hop limit |
| 12 | Network Delay Time | Medium | Dijkstra, return max distance |
| 13 | Min Cost Connect All Points | Medium | Prim's MST with Manhattan distance |
| 14 | Bellman-Ford Algorithm | Medium | Relax all edges V-1 times |
| 15 | Floyd-Warshall All Pairs | Medium | Triple loop: via each intermediate |
| 16 | Shortest Path in Binary Matrix | Medium | 8-directional BFS |
| 17 | Swim in Rising Water | Medium | Priority BFS on max elevation |
| 18 | Path with Maximum Minimum Value | Medium | Max-heap BFS for bottleneck path |
| 19 | Minimum Effort Path | Medium | Dijkstra on max absolute difference |
| 20 | Find the City (fewest reachable) | Medium | Floyd-Warshall + count within threshold |
| 21 | Dijkstra with Constraints | Hard | Modified Dijkstra with extra state |
| 22 | Multi-source Shortest Path | Hard | BFS/Dijkstra from multiple sources |
| 23 | MST (Prim's/Kruskal's) | Hard | Greedy edge selection |
| 24 | A* Search | Hard | Heuristic-guided Dijkstra |
| 25 | Shortest Path with Alternating Colors | Hard | BFS with color state |
| 26 | Number of Shortest Paths | Hard | Track count during Dijkstra |
| 27 | Swim in Water (priority BFS) | Hard | Min-heap on max cell value |
| 28 | Path with Max Probability | Hard | Modified Dijkstra (max product) |
| 29 | Reachable Nodes in Subdivided Graph | Hard | Dijkstra + edge subdivision counting |
| 30 | Parallel Courses III | Hard | Topological sort with max path |

## Key Insight

> **Dijkstra** = BFS with a priority queue (greedy on min cost). **Bellman-Ford** relaxes all edges V-1 times and detects negative cycles on the V-th round. **Floyd-Warshall** uses a triple loop with intermediate nodes. Most interview shortest-path problems use Dijkstra — recognize when the graph is weighted with non-negative edges.
