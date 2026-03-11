# Pattern 16: Matrix Traversal

## What is it?
Navigate 2D grids using DFS/BFS for connected regions, shortest paths, spiral ordering, and rotations.

## When to Use
- Flood fill, island counting/area
- Spiral/diagonal/zigzag traversal
- Rotation, transposition, reshaping
- Shortest path in grids with obstacles

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Full grid traversal | O(m × n) | O(m × n) |
| Spiral / diagonal | O(m × n) | O(1) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Flood Fill | Easy | DFS/BFS from start cell |
| 2 | Island Perimeter | Easy | Count edges adjacent to water |
| 3 | Transpose Matrix | Easy | Swap rows and columns |
| 4 | Reshape Matrix | Easy | Linear index mapping |
| 5 | Cells in Range (Excel) | Easy | Iterate column and row ranges |
| 6 | Count Negatives | Easy | Linear scan or staircase search |
| 7 | Toeplitz Matrix | Easy | Each cell equals top-left diagonal |
| 8 | Lucky Numbers in Matrix | Easy | Row min that is also column max |
| 9 | Matrix Diagonal Sum | Easy | Primary + secondary diagonals |
| 10 | Richest Customer Wealth | Easy | Max row sum |
| 11 | Spiral Matrix | Medium | Shrink boundaries after each pass |
| 12 | Rotate Image (90°) | Medium | Transpose + reverse rows |
| 13 | Set Matrix Zeroes | Medium | Use first row/col as markers |
| 14 | Game of Life | Medium | Encode state transitions in-place |
| 15 | Number of Islands | Medium | DFS flood-fill from each '1' |
| 16 | Max Area of Island | Medium | DFS counting connected cells |
| 17 | Surrounded Regions | Medium | DFS from borders, flip remaining |
| 18 | Where Will the Ball Fall | Medium | Simulate ball path per column |
| 19 | Diagonal Traverse | Medium | Alternate direction per diagonal |
| 20 | Spiral Matrix II (generate) | Medium | Fill with shrinking boundaries |
| 21 | Shortest Path in Binary Matrix | Hard | 8-directional BFS |
| 22 | Longest Increasing Path | Hard | DFS + memoization |
| 23 | Making a Large Island | Hard | Label islands, try flipping each 0 |
| 24 | Minimum Effort Path | Hard | Dijkstra on max abs difference |
| 25 | Unique Paths with Obstacles | Hard | DP skipping obstacle cells |
| 26 | Count Sub Islands | Hard | DFS checking g2 island ⊆ g1 |
| 27 | Number of Distinct Islands | Hard | Normalize island shape signatures |
| 28 | Swim in Rising Water | Hard | Priority BFS on elevation |
| 29 | Pacific Atlantic Water Flow | Hard | DFS from both oceans, intersect |
| 30 | Word Search | Hard | DFS backtracking on grid |

## Key Insight
> Use `int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}}` for 4-directional traversal. Mark visited in-place when possible.
