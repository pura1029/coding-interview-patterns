# Pattern 14: Breadth-First Search (BFS)

## What is it?
Explores level by level using a queue. Guarantees shortest path in unweighted graphs. Essential for level-order tree traversal.

## When to Use
- Shortest path in unweighted graphs
- Level-order tree traversal
- Multi-source BFS (rotting oranges, distance maps)
- State-space search (puzzles, lock combinations)

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Graph BFS | O(V + E) | O(V) |
| Tree level-order | O(n) | O(w) |
| Grid BFS | O(m × n) | O(m × n) |

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
> Multi-source BFS: start from all sources simultaneously. For shortest path with states, track `visited[row][col][state]`.
