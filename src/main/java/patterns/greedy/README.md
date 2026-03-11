# Pattern 19: Greedy Algorithms

## What is it?
Make locally optimal choices at each step to find the global optimum. Works when local optima lead to global optima.

## When to Use
- Scheduling, interval selection
- Jump games, stock trading
- Huffman coding, minimum spanning tree
- Sorting-based optimization

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Sort + greedy | O(n log n) | O(1) |
| Single scan | O(n) | O(1) |
| With heap | O(n log n) | O(n) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Best Time to Buy/Sell Stock | Easy | Track min price, max profit |
| 2 | Assign Cookies | Easy | Sort both, match smallest cookie to child |
| 3 | Lemonade Change | Easy | Greedy: prefer giving $10 change over $5s |
| 4 | Maximum Units on Truck | Easy | Sort by units/box descending |
| 5 | Can Place Flowers | Easy | Greedily plant if neighbors empty |
| 6 | Buy/Sell Stock II (multiple) | Easy | Add all positive price differences |
| 7 | Longest Palindrome (build) | Easy | Count pairs + one odd center |
| 8 | Min Ops to Make Increasing | Easy | Increment each to be > previous |
| 9 | Array Partition | Easy | Sort, sum even-indexed elements |
| 10 | Walking Robot Simulation | Easy | Simulate moves, track max distance |
| 11 | Jump Game | Medium | Track farthest reachable index |
| 12 | Jump Game II | Medium | Greedy BFS levels for min jumps |
| 13 | Gas Station | Medium | Track surplus, find valid start |
| 14 | Task Scheduler | Medium | Most frequent task determines idle slots |
| 15 | Non-overlapping Intervals | Medium | Sort by end, keep non-overlapping |
| 16 | Minimum Arrows | Medium | Sort by end, count groups |
| 17 | Partition Labels | Medium | Last occurrence defines partition end |
| 18 | Boats to Save People | Medium | Pair heaviest with lightest |
| 19 | Remove K Digits | Medium | Monotonic stack removes k digits |
| 20 | Minimum Platforms | Medium | Sort arrivals/departures, sweep |
| 21 | Candy Distribution | Hard | Two passes: left-to-right, right-to-left |
| 22 | IPO (Maximize Capital) | Hard | Sort by capital, max-heap for profits |
| 23 | Min Refueling Stops | Hard | Max-heap of passed station fuel |
| 24 | Course Schedule III | Hard | Sort by deadline, max-heap for duration |
| 25 | Maximum Performance of Team | Hard | Sort by efficiency, max-heap for speed |
| 26 | Min Cost to Hire K Workers | Hard | Sort by wage/quality ratio |
| 27 | Min Interval for Each Query | Hard | Sort + sweep with priority queue |
| 28 | Patching Array | Hard | Greedily patch gaps in reachable sums |
| 29 | Create Maximum Number | Hard | Merge two max subsequences of total k |
| 30 | Reorganize String | Hard | Alternate most frequent characters |

## Key Insight
> Sort by the right criterion (deadline, end time, ratio), then greedily pick the best available option. Often combined with heaps.
