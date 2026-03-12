# Pattern 10: Overlapping Intervals

## What is it?

Interval problems involve ranges `[start, end]`. The core technique: **sort** the intervals, then **sweep** left-to-right, making decisions based on overlap.

```
Two intervals overlap if:     They DON'T overlap if:

  A: [───────]                  A: [────]
  B:     [───────]              B:          [────]

  A.start < B.end AND           A.end ≤ B.start
  B.start < A.end               (no overlap)
```

> **Real-world analogy:** Meeting room scheduling. Each meeting has a start and end time. To find how many rooms you need, sort meetings and track overlaps.

---

## Core Techniques

### 1. Merge Intervals

```
Input:  [[1,3], [2,6], [8,10], [15,18]]

Sort by start: (already sorted)

Scan and merge overlapping:
  [1,3] + [2,6] → overlap (2 < 3) → merge to [1,6]
  [1,6] + [8,10] → no overlap (8 > 6) → keep both
  [8,10] + [15,18] → no overlap (15 > 10) → keep both

Result: [[1,6], [8,10], [15,18]]

  1  2  3  4  5  6  7  8  9  10     15 16 17 18
  [──────]                           
     [──────────]                    
  [──────────────]  ← merged         
                    [────────]       
                                    [──────────]
```

### 2. Sweep Line (Meeting Rooms II)

```
How many rooms needed for meetings [[0,30],[5,10],[15,20]]?

Events:  +1 at start, -1 at end
  time 0:  +1  → rooms = 1
  time 5:  +1  → rooms = 2 ← max
  time 10: -1  → rooms = 1
  time 15: +1  → rooms = 2
  time 20: -1  → rooms = 1
  time 30: -1  → rooms = 0

  Answer: 2 rooms

  Room 1: [═══════════════════════════════]  [0,30]
  Room 2:      [═════]    [═════]            [5,10] [15,20]
          0    5    10   15   20        30
```

### 3. Greedy by End Time (Min Removals)

```
Non-overlapping Intervals: remove minimum to eliminate all overlaps.

  Input: [[1,2], [2,3], [3,4], [1,3]]
  Sort by END time: [[1,2], [2,3], [1,3], [3,4]]

  Greedy: keep interval that ends earliest (leaves most room).

  Keep [1,2], end=2
  Keep [2,3], 2≥2, end=3
  Skip [1,3], 1<3 → overlaps!
  Keep [3,4], 3≥3, end=4

  Removed 1 interval ✅
```

---

## Real-World Applications

| Domain          | Application                   | Technique                |
| --------------- | ----------------------------- | ------------------------ |
| **Scheduling**  | Conference room booking       | Sweep line / min-heap    |
| **OS**          | Memory allocation / defrag    | Merge intervals          |
| **Airlines**    | Gate assignment               | Meeting rooms II         |
| **Video**       | Subtitle overlap merging      | Merge intervals          |
| **Networks**    | Bandwidth allocation windows  | Sweep line               |

---

## When to Use

- **Merge overlapping intervals** — sort by start, extend end boundary
- **Meeting room scheduling** — sweep line or min-heap of end times
- **Minimum groups / platforms** — sweep line counting concurrent events
- **Interval intersections** — two-pointer on two sorted lists
- **Interval insertions** — find position, merge neighbors

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| Sort intervals | O(n log n) | O(1) |
| Sweep line | O(n) | O(n) |
| Min-heap for rooms | O(n log n) | O(n) |

## Examples (30)

| # | Problem | Difficulty | Key Idea |
|---|---------|------------|----------|
| 1 | Merge Intervals | Easy | Sort by start, extend end |
| 2 | Meeting Rooms (can attend all?) | Easy | Check for any overlap |
| 3 | Check if Intervals Overlap | Easy | Sort and compare adjacent |
| 4 | Remove Covered Intervals | Easy | Sort, skip fully covered |
| 5 | Summary Ranges | Easy | Group consecutive numbers |
| 6 | Min Changes for Alternating String | Easy | Count mismatches for both patterns |
| 7 | Can Place Flowers | Easy | Check empty neighbors |
| 8 | Days Between Dates | Easy | Date arithmetic |
| 9 | Interval Lengths | Easy | Compute end - start for each |
| 10 | Event Conflict Check | Easy | Two events overlap if start < other end |
| 11 | Insert Interval | Medium | Find position, merge neighbors |
| 12 | Non-overlapping Intervals (min removals) | Medium | Sort by end, greedy keep |
| 13 | Interval List Intersections | Medium | Two-pointer on sorted lists |
| 14 | Min Arrows to Burst Balloons | Medium | Sort by end, count non-overlapping |
| 15 | Car Pooling | Medium | Sweep line on pickup/dropoff |
| 16 | My Calendar I | Medium | TreeMap for overlap detection |
| 17 | Add Bold Tag in String | Medium | Merge bold ranges, wrap |
| 18 | Divide Intervals Into Min Groups | Medium | Sweep line or min-heap |
| 19 | Min Interval for Each Query | Medium | Sort queries + intervals, sweep |
| 20 | Maximum Events Attended | Medium | Greedy: earliest ending first |
| 21 | Meeting Rooms II (min rooms) | Hard | Min-heap of end times |
| 22 | Employee Free Time | Hard | Merge all schedules, find gaps |
| 23 | Skyline Problem | Hard | Sweep line with max-heap of heights |
| 24 | Data Stream as Disjoint Intervals | Hard | TreeMap for interval merging |
| 25 | My Calendar III (max concurrent) | Hard | Sweep line counting overlaps |
| 26 | Range Module | Hard | TreeMap for tracked ranges |
| 27 | Min Taps to Water | Hard | Interval coverage greedy |
| 28 | Count Ways to Group Ranges | Hard | Merge + count independent groups |
| 29 | Maximum CPU Load | Hard | Sweep line for peak usage |
| 30 | Points That Intersect With Cars | Hard | Merge intervals, count total points |

## Key Insight

> Sort by **start** (for merge) or by **end** (for greedy selection), then sweep left-to-right tracking the current end boundary. For "how many concurrent" problems, use the **sweep line**: +1 at each start, -1 at each end, track the running maximum.
