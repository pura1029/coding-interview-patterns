# Pattern 12: Binary Tree Traversal

## What is it?
Visit every node in specific order: Inorder (L-Root-R), Preorder (Root-L-R), Postorder (L-R-Root), Level-order (BFS).

## When to Use
- Tree structure validation (BST property)
- Path sums, depth calculations
- Serialization/deserialization
- View problems (right side, vertical order)

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Any traversal | O(n) | O(h) |
| Level-order | O(n) | O(w) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Inorder Traversal | Easy | Left → Root → Right (sorted for BST) |
| 2 | Preorder Traversal | Easy | Root → Left → Right |
| 3 | Postorder Traversal | Easy | Left → Right → Root |
| 4 | Maximum Depth | Easy | max(left, right) + 1 |
| 5 | Symmetric Tree | Easy | Mirror comparison of left/right |
| 6 | Invert Binary Tree | Easy | Swap left and right recursively |
| 7 | Same Tree | Easy | Compare structure and values |
| 8 | Minimum Depth | Easy | Shortest root-to-leaf path |
| 9 | Path Sum | Easy | Subtract node value, check leaf |
| 10 | Count Complete Tree Nodes | Easy | Binary search on last level |
| 11 | Level Order Traversal | Medium | BFS with level grouping |
| 12 | Zigzag Level Order | Medium | Alternate left-right per level |
| 13 | Validate BST | Medium | Inorder must be strictly increasing |
| 14 | Kth Smallest in BST | Medium | Inorder traversal, count to k |
| 15 | Right Side View | Medium | Last node at each BFS level |
| 16 | Lowest Common Ancestor | Medium | Recurse: if both sides return, curr is LCA |
| 17 | Flatten to Linked List | Medium | Preorder rewiring of next pointers |
| 18 | Build from Preorder + Inorder | Medium | Root from preorder, split by inorder |
| 19 | Diameter of Binary Tree | Medium | Max (leftDepth + rightDepth) at any node |
| 20 | All Root-to-Leaf Paths | Medium | DFS collecting path strings |
| 21 | Maximum Path Sum | Hard | Track max through each node |
| 22 | Serialize / Deserialize | Hard | Preorder with null markers |
| 23 | Vertical Order Traversal | Hard | Column index + BFS/DFS grouping |
| 24 | Binary Tree Cameras | Hard | Greedy post-order: cover parent |
| 25 | Recover BST | Hard | Find two swapped nodes in inorder |
| 26 | Count Good Nodes | Hard | Track max from root to current |
| 27 | Tree to Doubly Linked List | Hard | Inorder threading of nodes |
| 28 | Sum of Distances in Tree | Hard | Re-root DP: parent + child contributions |
| 29 | Longest Path Different Adjacent | Hard | DFS tracking max two child paths |
| 30 | Maximum Width of Binary Tree | Hard | Level-order with position indexing |

## Key Insight
> Inorder on BST gives sorted order. Use DFS for depth problems, BFS for level problems. Post-order processes children before parent.
