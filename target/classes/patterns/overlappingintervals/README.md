# Pattern 10: Overlapping Intervals

## What is it?
Sort intervals by start/end time, then process sequentially to detect, merge, or count overlaps.

## When to Use
- Merge overlapping intervals
- Meeting room scheduling
- Minimum groups / platforms needed
- Interval intersections and insertions

## Complexity
| Operation | Time | Space |
|-----------|------|-------|
| Sort intervals | O(n log n) | O(1) |
| Sweep line | O(n) | O(n) |

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
> Sort by start (or end for greedy), then sweep left-to-right tracking the current end boundary.
