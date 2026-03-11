# Pattern 15: Shortest Path

## What is it?
Find minimum cost paths in weighted graphs. Dijkstra (non-negative), Bellman-Ford (negative edges), Floyd-Warshall (all pairs).

## When to Use
- Single-source shortest path (Dijkstra, Bellman-Ford)
- All-pairs shortest path (Floyd-Warshall)
- Minimum spanning tree (Prim's, Kruskal's)
- Grid-based weighted pathfinding

## Complexity
| Algorithm | Time | Space |
|-----------|------|-------|
| Dijkstra | O((V+E) log V) | O(V) |
| Bellman-Ford | O(V × E) | O(V) |
| Floyd-Warshall | O(V³) | O(V²) |

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
> Dijkstra = BFS with priority queue (greedy on min cost). Bellman-Ford relaxes all edges V-1 times.
