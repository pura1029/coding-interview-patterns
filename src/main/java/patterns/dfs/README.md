# Pattern 13: Depth-First Search (DFS)

## What is it?
Explores as deep as possible before backtracking. Uses recursion or explicit stack. Core technique for trees and graphs.

## When to Use
- Connected components, island counting
- Cycle detection in directed/undirected graphs
- Topological sort, path finding
- Tree structure problems (path sum, subtree)

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Graph DFS | O(V + E) | O(V) |
| Tree DFS | O(n) | O(h) |
| Grid DFS | O(m × n) | O(m × n) |

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
> Mark visited to avoid cycles. Use `state[]` (unvisited/in-progress/done) for cycle detection in directed graphs.
