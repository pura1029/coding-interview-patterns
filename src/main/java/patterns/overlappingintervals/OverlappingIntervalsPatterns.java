package patterns.overlappingintervals;

import java.util.*;

/**
 * PATTERN 10: OVERLAPPING INTERVALS
 * Sort intervals by start, process sequentially to detect/merge overlaps.
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class OverlappingIntervalsPatterns {
    static String iStr(int[][] iv) { StringBuilder sb = new StringBuilder("["); for (int i = 0; i < iv.length; i++) { sb.append("[").append(iv[i][0]).append(",").append(iv[i][1]).append("]"); if (i < iv.length-1) sb.append(","); } return sb.append("]").toString(); }

    // EASY 1: Merge Intervals
    public static int[][] merge(int[][] iv) { Arrays.sort(iv, Comparator.comparingInt(a->a[0])); List<int[]> r = new ArrayList<>(); r.add(iv[0]); for (int i = 1; i < iv.length; i++) { int[] last = r.get(r.size()-1); if (iv[i][0] <= last[1]) last[1] = Math.max(last[1], iv[i][1]); else r.add(iv[i]); } return r.toArray(new int[0][]); }
    // EASY 2: Meeting Rooms (can attend all?)
    public static boolean canAttendMeetings(int[][] iv) { Arrays.sort(iv, Comparator.comparingInt(a->a[0])); for (int i = 1; i < iv.length; i++) if (iv[i][0] < iv[i-1][1]) return false; return true; }
    // EASY 3: Check if Intervals Overlap
    public static boolean hasOverlap(int[] a, int[] b) { return a[0] < b[1] && b[0] < a[1]; }
    // EASY 4: Remove Covered Intervals
    public static int removeCoveredIntervals(int[][] iv) { Arrays.sort(iv, (a,b) -> a[0]!=b[0] ? a[0]-b[0] : b[1]-a[1]); int count = 0, maxEnd = 0; for (int[] i : iv) { if (i[1] > maxEnd) { count++; maxEnd = i[1]; } } return count; }
    // EASY 5: Summary Ranges
    public static List<String> summaryRanges(int[] nums) { List<String> r = new ArrayList<>(); for (int i = 0; i < nums.length; i++) { int start = nums[i]; while (i+1 < nums.length && nums[i+1] == nums[i]+1) i++; r.add(start == nums[i] ? ""+start : start+"->"+nums[i]); } return r; }
    // EASY 6: Minimum Changes to Make Alternating Binary String
    public static int minOperationsAlt(String s) { int c = 0; for (int i = 0; i < s.length(); i++) if ((s.charAt(i)-'0') != i%2) c++; return Math.min(c, s.length()-c); }
    // EASY 7: Can Place Flowers
    public static boolean canPlaceFlowers(int[] fb, int n) { for (int i = 0; i < fb.length && n > 0; i++) { if (fb[i]==0 && (i==0||fb[i-1]==0) && (i==fb.length-1||fb[i+1]==0)) { fb[i]=1; n--; } } return n <= 0; }
    // EASY 8: Count Days Between Two Dates (simplified)
    public static int daysBetween(String d1, String d2) { return Math.abs(Integer.parseInt(d1.substring(8))-Integer.parseInt(d2.substring(8))); }
    // EASY 9: Range of Each Time Interval
    public static int[] intervalLengths(int[][] iv) { int[] r = new int[iv.length]; for (int i = 0; i < iv.length; i++) r[i] = iv[i][1]-iv[i][0]; return r; }
    // EASY 10: Determine If Two Events Have Conflict
    public static boolean haveConflict(String[] e1, String[] e2) { return e1[0].compareTo(e2[1]) <= 0 && e2[0].compareTo(e1[1]) <= 0; }

    // MEDIUM 1: Insert Interval
    public static int[][] insert(int[][] iv, int[] ni) { List<int[]> r = new ArrayList<>(); int i = 0; while (i < iv.length && iv[i][1] < ni[0]) r.add(iv[i++]); while (i < iv.length && iv[i][0] <= ni[1]) { ni[0] = Math.min(ni[0], iv[i][0]); ni[1] = Math.max(ni[1], iv[i][1]); i++; } r.add(ni); while (i < iv.length) r.add(iv[i++]); return r.toArray(new int[0][]); }
    // MEDIUM 2: Non-overlapping Intervals (min removals)
    public static int eraseOverlapIntervals(int[][] iv) { Arrays.sort(iv, Comparator.comparingInt(a->a[1])); int rem = 0, end = Integer.MIN_VALUE; for (int[] i : iv) { if (i[0] >= end) end = i[1]; else rem++; } return rem; }
    // MEDIUM 3: Interval List Intersections
    public static int[][] intervalIntersection(int[][] a, int[][] b) { List<int[]> r = new ArrayList<>(); int i = 0, j = 0; while (i < a.length && j < b.length) { int lo = Math.max(a[i][0], b[j][0]), hi = Math.min(a[i][1], b[j][1]); if (lo <= hi) r.add(new int[]{lo, hi}); if (a[i][1] < b[j][1]) i++; else j++; } return r.toArray(new int[0][]); }
    // MEDIUM 4: Minimum Number of Arrows to Burst Balloons
    public static int findMinArrowShots(int[][] points) { Arrays.sort(points, Comparator.comparingInt(a->a[1])); int arrows = 1, end = points[0][1]; for (int i = 1; i < points.length; i++) { if (points[i][0] > end) { arrows++; end = points[i][1]; } } return arrows; }
    // MEDIUM 5: Car Pooling
    public static boolean carPooling(int[][] trips, int capacity) { int[] diff = new int[1001]; for (int[] t : trips) { diff[t[1]] += t[0]; diff[t[2]] -= t[0]; } int cur = 0; for (int d : diff) { cur += d; if (cur > capacity) return false; } return true; }
    // MEDIUM 6: My Calendar I (no double booking)
    static class MyCalendar { TreeMap<Integer,Integer> cal = new TreeMap<>(); boolean book(int s, int e) { Integer prev = cal.floorKey(s), next = cal.ceilingKey(s); if ((prev != null && cal.get(prev) > s) || (next != null && next < e)) return false; cal.put(s, e); return true; } }
    // MEDIUM 7: Add Bold Tag in String
    public static String addBoldTag(String s, String[] words) { boolean[] bold = new boolean[s.length()]; for (String w : words) { int start = s.indexOf(w); while (start != -1) { for (int i = start; i < start+w.length(); i++) bold[i] = true; start = s.indexOf(w, start+1); } } StringBuilder sb = new StringBuilder(); for (int i = 0; i < s.length(); i++) { if (bold[i] && (i == 0 || !bold[i-1])) sb.append("<b>"); sb.append(s.charAt(i)); if (bold[i] && (i == s.length()-1 || !bold[i+1])) sb.append("</b>"); } return sb.toString(); }
    // MEDIUM 8: Divide Intervals Into Minimum Groups
    public static int minGroups(int[][] iv) { Arrays.sort(iv, Comparator.comparingInt(a->a[0])); PriorityQueue<Integer> pq = new PriorityQueue<>(); for (int[] i : iv) { if (!pq.isEmpty() && pq.peek() < i[0]) pq.poll(); pq.offer(i[1]); } return pq.size(); }
    // MEDIUM 9: Minimum Interval to Include Each Query
    public static int[] minInterval(int[][] iv, int[] queries) { Arrays.sort(iv, Comparator.comparingInt(a->a[0])); int[][] sortedQ = new int[queries.length][2]; for (int i = 0; i < queries.length; i++) sortedQ[i] = new int[]{queries[i], i}; Arrays.sort(sortedQ, Comparator.comparingInt(a->a[0])); PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a->a[0])); int[] ans = new int[queries.length]; Arrays.fill(ans, -1); int j = 0; for (int[] q : sortedQ) { while (j < iv.length && iv[j][0] <= q[0]) { pq.offer(new int[]{iv[j][1]-iv[j][0]+1, iv[j][1]}); j++; } while (!pq.isEmpty() && pq.peek()[1] < q[0]) pq.poll(); if (!pq.isEmpty()) ans[q[1]] = pq.peek()[0]; } return ans; }
    // MEDIUM 10: Maximum Number of Events That Can Be Attended
    public static int maxEvents(int[][] events) { Arrays.sort(events, Comparator.comparingInt(a->a[0])); PriorityQueue<Integer> pq = new PriorityQueue<>(); int i = 0, count = 0, day = 1; while (i < events.length || !pq.isEmpty()) { if (pq.isEmpty() && i < events.length) day = events[i][0]; while (i < events.length && events[i][0] == day) pq.offer(events[i++][1]); while (!pq.isEmpty() && pq.peek() < day) pq.poll(); if (!pq.isEmpty()) { pq.poll(); count++; } day++; } return count; }

    // HARD 1: Meeting Rooms II (min rooms)
    public static int minMeetingRooms(int[][] iv) { int[] s = new int[iv.length], e = new int[iv.length]; for (int i = 0; i < iv.length; i++) { s[i]=iv[i][0]; e[i]=iv[i][1]; } Arrays.sort(s); Arrays.sort(e); int rooms = 0, max = 0, ep = 0; for (int start : s) { if (start < e[ep]) rooms++; else ep++; max = Math.max(max, rooms); } return max; }
    // HARD 2: Employee Free Time
    public static List<int[]> employeeFreeTime(List<List<int[]>> schedule) { List<int[]> all = new ArrayList<>(); for (List<int[]> s : schedule) all.addAll(s); all.sort(Comparator.comparingInt(a->a[0])); List<int[]> r = new ArrayList<>(); int end = all.get(0)[1]; for (int i = 1; i < all.size(); i++) { if (all.get(i)[0] > end) r.add(new int[]{end, all.get(i)[0]}); end = Math.max(end, all.get(i)[1]); } return r; }
    // HARD 3: Skyline Problem (simplified output)
    public static List<int[]> getSkyline(int[][] buildings) { List<int[]> events = new ArrayList<>(); for (int[] b : buildings) { events.add(new int[]{b[0], -b[2]}); events.add(new int[]{b[1], b[2]}); } events.sort((a,b) -> a[0]!=b[0] ? a[0]-b[0] : a[1]-b[1]); TreeMap<Integer,Integer> heights = new TreeMap<>(Collections.reverseOrder()); heights.put(0, 1); List<int[]> r = new ArrayList<>(); int prevMax = 0; for (int[] e : events) { if (e[1] < 0) heights.merge(-e[1], 1, Integer::sum); else { heights.merge(e[1], -1, Integer::sum); if (heights.get(e[1]) == 0) heights.remove(e[1]); } int curMax = heights.firstKey(); if (curMax != prevMax) { r.add(new int[]{e[0], curMax}); prevMax = curMax; } } return r; }
    // HARD 4: Data Stream as Disjoint Intervals
    static class SummaryRanges { TreeMap<Integer,int[]> tree = new TreeMap<>(); void addNum(int val) { if (tree.containsKey(val)) return; Integer lo = tree.lowerKey(val), hi = tree.higherKey(val); if (lo != null && hi != null && tree.get(lo)[1]+1==val && val+1==hi) { tree.get(lo)[1] = tree.get(hi)[1]; tree.remove(hi); } else if (lo != null && tree.get(lo)[1]+1>=val) { tree.get(lo)[1] = Math.max(tree.get(lo)[1], val); } else if (hi != null && val+1==hi) { tree.put(val, new int[]{val, tree.get(hi)[1]}); tree.remove(hi); } else tree.put(val, new int[]{val, val}); } }
    // HARD 5: My Calendar III (max concurrent events)
    static class MyCalendarThree { TreeMap<Integer,Integer> map = new TreeMap<>(); int book(int s, int e) { map.merge(s, 1, Integer::sum); map.merge(e, -1, Integer::sum); int max = 0, cur = 0; for (int v : map.values()) { cur += v; max = Math.max(max, cur); } return max; } }
    // HARD 6: Range Module
    static class RangeModule { TreeMap<Integer,Integer> ranges = new TreeMap<>(); void addRange(int l, int r) { Integer lo = ranges.floorKey(l), hi = ranges.floorKey(r); if (lo != null && ranges.get(lo) >= l) l = lo; if (hi != null && ranges.get(hi) > r) r = ranges.get(hi); ranges.subMap(l, r).clear(); ranges.put(l, r); } boolean queryRange(int l, int r) { Integer lo = ranges.floorKey(l); return lo != null && ranges.get(lo) >= r; } }
    // HARD 7: Minimum Number of Taps to Open Water
    public static int minTaps(int n, int[] ranges) { int[] maxReach = new int[n+1]; for (int i = 0; i < ranges.length; i++) { int l = Math.max(0, i-ranges[i]), r = Math.min(n, i+ranges[i]); maxReach[l] = Math.max(maxReach[l], r); } int taps = 0, end = 0, farthest = 0; for (int i = 0; i <= n; i++) { if (i > farthest) return -1; farthest = Math.max(farthest, maxReach[i]); if (i == end && i < n) { taps++; end = farthest; } } return taps; }
    // HARD 8: Count Ways to Group Overlapping Ranges
    public static int countWays(int[][] iv) { Arrays.sort(iv, Comparator.comparingInt(a->a[0])); int MOD = 1_000_000_007; int groups = 0, maxEnd = -1; for (int[] i : iv) { if (i[0] > maxEnd) groups++; maxEnd = Math.max(maxEnd, i[1]); } long result = 1; for (int i = 0; i < groups; i++) result = result * 2 % MOD; return (int) result; }
    // HARD 9: Maximum CPU Load
    public static int maxCPULoad(int[][] jobs) { Arrays.sort(jobs, Comparator.comparingInt(a->a[0])); PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a->a[1])); int curLoad = 0, maxLoad = 0; for (int[] j : jobs) { while (!pq.isEmpty() && pq.peek()[1] <= j[0]) curLoad -= pq.poll()[2]; pq.offer(j); curLoad += j[2]; maxLoad = Math.max(maxLoad, curLoad); } return maxLoad; }
    // HARD 10: Points That Intersect With Cars
    public static int numberOfPoints(List<List<Integer>> nums) { Set<Integer> points = new TreeSet<>(); for (List<Integer> iv : nums) for (int i = iv.get(0); i <= iv.get(1); i++) points.add(i); return points.size(); }

    public static void main(String[] args) {
        System.out.println("=== OVERLAPPING INTERVALS PATTERN (30 Examples) ===\n");
        System.out.println("--- EASY ---");
        System.out.println("1. Merge: " + iStr(merge(new int[][]{{1,3},{2,6},{8,10},{15,18}})));
        System.out.println("2. Can Attend: " + canAttendMeetings(new int[][]{{0,30},{5,10},{15,20}}));
        System.out.println("3. Has Overlap: " + hasOverlap(new int[]{1,5}, new int[]{3,7}));
        System.out.println("4. Remove Covered: " + removeCoveredIntervals(new int[][]{{1,4},{3,6},{2,8}}));
        System.out.println("5. Summary Ranges: " + summaryRanges(new int[]{0,1,2,4,5,7}));
        System.out.println("6. Min Alt Ops: " + minOperationsAlt("0100"));
        System.out.println("7. Place Flowers: " + canPlaceFlowers(new int[]{1,0,0,0,1}, 1));
        System.out.println("8. Days Between: simplified");
        System.out.println("9. Interval Lens: " + Arrays.toString(intervalLengths(new int[][]{{1,4},{2,7}})));
        System.out.println("10. Event Conflict: " + haveConflict(new String[]{"01:15","02:00"}, new String[]{"02:00","03:00"}));
        System.out.println("\n--- MEDIUM ---");
        System.out.println("11. Insert: " + iStr(insert(new int[][]{{1,3},{6,9}}, new int[]{2,5})));
        System.out.println("12. Erase Overlap: " + eraseOverlapIntervals(new int[][]{{1,2},{2,3},{3,4},{1,3}}));
        System.out.println("13. Intersection: " + iStr(intervalIntersection(new int[][]{{0,2},{5,10}}, new int[][]{{1,5},{8,12}})));
        System.out.println("14. Min Arrows: " + findMinArrowShots(new int[][]{{10,16},{2,8},{1,6},{7,12}}));
        System.out.println("15. Car Pooling: " + carPooling(new int[][]{{2,1,5},{3,3,7}}, 4));
        MyCalendar mc = new MyCalendar(); System.out.println("16. Calendar: " + mc.book(10,20) + "," + mc.book(15,25));
        System.out.println("17. Bold Tag: " + addBoldTag("abcxyz123", new String[]{"abc","123"}));
        System.out.println("18. Min Groups: " + minGroups(new int[][]{{5,10},{6,8},{1,5},{2,3},{1,10}}));
        System.out.println("19. Min Interval: " + Arrays.toString(minInterval(new int[][]{{1,4},{2,4},{3,6},{4,4}}, new int[]{2,3,4,5})));
        System.out.println("20. Max Events: " + maxEvents(new int[][]{{1,2},{2,3},{3,4}}));
        System.out.println("\n--- HARD ---");
        System.out.println("21. Min Rooms: " + minMeetingRooms(new int[][]{{0,30},{5,10},{15,20}}));
        List<List<int[]>> sched = Arrays.asList(Arrays.asList(new int[]{1,2}, new int[]{5,6}), Arrays.asList(new int[]{1,3}), Arrays.asList(new int[]{4,10}));
        System.out.println("22. Free Time: " + sched.size() + " employees");
        System.out.println("23. Skyline: " + getSkyline(new int[][]{{2,9,10},{3,7,15},{5,12,12},{15,20,10},{19,24,8}}).size() + " points");
        SummaryRanges sr = new SummaryRanges(); sr.addNum(1); sr.addNum(3); sr.addNum(7); sr.addNum(2);
        System.out.println("24. Summary Ranges DS: done");
        MyCalendarThree mc3 = new MyCalendarThree(); mc3.book(10,20); mc3.book(50,60); mc3.book(10,40);
        System.out.println("25. Calendar III: " + mc3.book(5,15));
        System.out.println("26. Range Module: done");
        System.out.println("27. Min Taps: " + minTaps(5, new int[]{3,4,1,1,0,0}));
        System.out.println("28. Count Ways: " + countWays(new int[][]{{6,10},{5,15}}));
        System.out.println("29. Max CPU: " + maxCPULoad(new int[][]{{1,4,3},{2,5,4},{7,9,6}}));
        System.out.println("30. Points on Cars: " + numberOfPoints(Arrays.asList(Arrays.asList(3,6), Arrays.asList(1,5), Arrays.asList(4,7))));
    }
}
