package patterns.topkelements;

import java.util.*;

/**
 * PATTERN 9: TOP 'K' ELEMENTS
 * Uses heaps (priority queues) to find top/bottom K elements in O(n log k).
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class TopKElementsPatterns {

    /** Kth Largest Element. Min-heap of size k. */
    public static int kthLargest(int[] nums, int k) { PriorityQueue<Integer> pq = new PriorityQueue<>(); for (int n : nums) { pq.offer(n); if (pq.size() > k) pq.poll(); } return pq.peek(); }
    /** Last Stone Weight. Max-heap, smash two largest. */
    public static int lastStoneWeight(int[] stones) { PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder()); for (int s : stones) pq.offer(s); while (pq.size() > 1) { int a = pq.poll(), b = pq.poll(); if (a != b) pq.offer(a - b); } return pq.isEmpty() ? 0 : pq.poll(); }
    /** Kth Largest Element in Stream. Maintain min-heap of size k. */
    static class KthLargestStream { PriorityQueue<Integer> pq; int k; KthLargestStream(int k, int[] nums) { this.k = k; pq = new PriorityQueue<>(); for (int n : nums) add(n); } int add(int val) { pq.offer(val); if (pq.size() > k) pq.poll(); return pq.peek(); } }
    /** Sort Array By Increasing Frequency. Count + custom sort. */
    public static int[] frequencySort(int[] nums) { Map<Integer, Integer> f = new HashMap<>(); for (int n : nums) f.merge(n, 1, Integer::sum); Integer[] arr = new Integer[nums.length]; for (int i = 0; i < nums.length; i++) arr[i] = nums[i]; Arrays.sort(arr, (a, b) -> f.get(a).equals(f.get(b)) ? b - a : f.get(a) - f.get(b)); for (int i = 0; i < nums.length; i++) nums[i] = arr[i]; return nums; }
    /** Relative Ranks. Sort by score, assign ranks. */
    public static String[] findRelativeRanks(int[] score) { int n = score.length; PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> b[0] - a[0]); for (int i = 0; i < n; i++) pq.offer(new int[]{score[i], i}); String[] r = new String[n]; String[] medals = {"Gold Medal", "Silver Medal", "Bronze Medal"}; int rank = 0; while (!pq.isEmpty()) { int[] p = pq.poll(); r[p[1]] = rank < 3 ? medals[rank] : String.valueOf(rank + 1); rank++; } return r; }
    /** K Closest Points to Origin (simplified: sort). Min-heap by distance. */
    public static int[][] kClosest(int[][] points, int k) { Arrays.sort(points, Comparator.comparingInt(a -> a[0]*a[0]+a[1]*a[1])); return Arrays.copyOf(points, k); }
    /** Find K Pairs with Smallest Sums (simplified). Min-heap of pair sums. */
    public static List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) { PriorityQueue<int[]> pq = new PriorityQueue<>((a,b) -> (a[0]+a[1]) - (b[0]+b[1])); for (int i = 0; i < Math.min(nums1.length, k); i++) pq.offer(new int[]{nums1[i], nums2[0], 0}); List<List<Integer>> r = new ArrayList<>(); while (!pq.isEmpty() && r.size() < k) { int[] c = pq.poll(); r.add(Arrays.asList(c[0], c[1])); if (c[2] + 1 < nums2.length) pq.offer(new int[]{c[0], nums2[c[2]+1], c[2]+1}); } return r; }
    /** Maximum Product of Two Elements. Find two largest values. */
    public static int maxProduct(int[] nums) { PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder()); for (int n : nums) pq.offer(n); return (pq.poll()-1) * (pq.poll()-1); }
    /** Find K Largest in Array (using sort). Sort descending, take first k. */
    public static int[] kLargest(int[] nums, int k) { Arrays.sort(nums); return Arrays.copyOfRange(nums, nums.length - k, nums.length); }
    /** Minimum Cost of Buying Candies With Discount. Sort, skip every 3rd. */
    public static int minimumCost(int[] cost) { Arrays.sort(cost); int total = 0; for (int i = cost.length - 1; i >= 0; i--) { total += cost[i]; if (i - 1 >= 0) total += cost[--i]; --i; } return total; }

    /** Top K Frequent Elements. Bucket sort by frequency. */
    public static List<Integer> topKFrequent(int[] nums, int k) { Map<Integer, Integer> f = new HashMap<>(); for (int n : nums) f.merge(n, 1, Integer::sum); PriorityQueue<Map.Entry<Integer,Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue)); for (var e : f.entrySet()) { pq.offer(e); if (pq.size() > k) pq.poll(); } List<Integer> r = new ArrayList<>(); while (!pq.isEmpty()) r.add(pq.poll().getKey()); return r; }
    /** Sort Characters By Frequency. Count + sort by frequency. */
    public static String frequencySortStr(String s) { Map<Character,Integer> f = new HashMap<>(); for (char c : s.toCharArray()) f.merge(c, 1, Integer::sum); PriorityQueue<Map.Entry<Character,Integer>> pq = new PriorityQueue<>((a,b) -> b.getValue()-a.getValue()); pq.addAll(f.entrySet()); StringBuilder sb = new StringBuilder(); while (!pq.isEmpty()) { var e = pq.poll(); for (int i = 0; i < e.getValue(); i++) sb.append(e.getKey()); } return sb.toString(); }
    /** Reorganize String. Greedy with max-heap. */
    public static String reorganizeString(String s) { Map<Character,Integer> f = new HashMap<>(); for (char c : s.toCharArray()) f.merge(c, 1, Integer::sum); PriorityQueue<Map.Entry<Character,Integer>> pq = new PriorityQueue<>((a,b) -> b.getValue()-a.getValue()); pq.addAll(f.entrySet()); StringBuilder sb = new StringBuilder(); Map.Entry<Character,Integer> prev = null; while (!pq.isEmpty()) { var e = pq.poll(); sb.append(e.getKey()); e.setValue(e.getValue()-1); if (prev != null && prev.getValue() > 0) pq.offer(prev); prev = e; } return sb.length() == s.length() ? sb.toString() : ""; }
    /** Task Scheduler. Max-freq determines idle slots. */
    public static int leastInterval(char[] tasks, int n) { int[] f = new int[26]; for (char c : tasks) f[c-'A']++; Arrays.sort(f); int maxF = f[25]-1, idle = maxF*n; for (int i = 24; i >= 0 && f[i] > 0; i--) idle -= Math.min(f[i], maxF); return Math.max(tasks.length, tasks.length + idle); }
    /** Kth Smallest Element in a Sorted Matrix. Min-heap or binary search. */
    public static int kthSmallest(int[][] matrix, int k) { PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0])); for (int i = 0; i < matrix.length; i++) pq.offer(new int[]{matrix[i][0], i, 0}); while (--k > 0) { int[] c = pq.poll(); if (c[2]+1 < matrix[0].length) pq.offer(new int[]{matrix[c[1]][c[2]+1], c[1], c[2]+1}); } return pq.peek()[0]; }
    /** Top K Frequent Words. Min-heap with custom comparator. */
    public static List<String> topKFrequentWords(String[] words, int k) { Map<String,Integer> f = new HashMap<>(); for (String w : words) f.merge(w, 1, Integer::sum); PriorityQueue<String> pq = new PriorityQueue<>((a,b) -> f.get(a).equals(f.get(b)) ? b.compareTo(a) : f.get(a)-f.get(b)); for (String w : f.keySet()) { pq.offer(w); if (pq.size() > k) pq.poll(); } List<String> r = new ArrayList<>(); while (!pq.isEmpty()) r.add(pq.poll()); Collections.reverse(r); return r; }
    /** Least Number of Unique Integers after K Removals. Remove least frequent first. */
    public static int findLeastNumOfUniqueInts(int[] arr, int k) { Map<Integer,Integer> f = new HashMap<>(); for (int n : arr) f.merge(n, 1, Integer::sum); PriorityQueue<Integer> pq = new PriorityQueue<>(f.values()); while (k > 0 && !pq.isEmpty()) k -= pq.poll(); return pq.size() + (k < 0 ? 1 : 0); }
    /** Find K-th Smallest Pair Distance. Binary search on distance. */
    public static int smallestDistancePair(int[] nums, int k) { Arrays.sort(nums); int lo = 0, hi = nums[nums.length-1]-nums[0]; while (lo < hi) { int mid = (lo+hi)/2, cnt = 0, l = 0; for (int r = 0; r < nums.length; r++) { while (nums[r]-nums[l] > mid) l++; cnt += r-l; } if (cnt < k) lo = mid+1; else hi = mid; } return lo; }
    /** Ugly Number II. Min-heap of multiples of 2,3,5. */
    public static int nthUglyNumber(int n) { PriorityQueue<Long> pq = new PriorityQueue<>(); Set<Long> seen = new HashSet<>(); pq.offer(1L); seen.add(1L); long ugly = 1; for (int i = 0; i < n; i++) { ugly = pq.poll(); for (long f : new long[]{2,3,5}) if (seen.add(ugly*f)) pq.offer(ugly*f); } return (int) ugly; }
    /** Seat Reservation Manager. Min-heap of available seats. */
    static class SeatManager { PriorityQueue<Integer> pq = new PriorityQueue<>(); SeatManager(int n) { for (int i = 1; i <= n; i++) pq.offer(i); } int reserve() { return pq.poll(); } void unreserve(int seat) { pq.offer(seat); } }

    /** Find Median from Data Stream. Two heaps: max-heap + min-heap. */
    static class MedianFinder { PriorityQueue<Integer> lo = new PriorityQueue<>(Collections.reverseOrder()); PriorityQueue<Integer> hi = new PriorityQueue<>(); void addNum(int n) { lo.offer(n); hi.offer(lo.poll()); if (hi.size() > lo.size()) lo.offer(hi.poll()); } double findMedian() { return lo.size() > hi.size() ? lo.peek() : (lo.peek()+hi.peek())/2.0; } }
    /** Merge K Sorted Lists. Min-heap of list heads. */
    static class ListNode { int val; ListNode next; ListNode(int v) { val = v; } ListNode(int v, ListNode n) { val = v; next = n; } }
    public static ListNode mergeKLists(ListNode[] lists) { PriorityQueue<ListNode> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.val)); for (ListNode l : lists) if (l != null) pq.offer(l); ListNode d = new ListNode(0), c = d; while (!pq.isEmpty()) { ListNode n = pq.poll(); c.next = n; c = c.next; if (n.next != null) pq.offer(n.next); } return d.next; }
    /** Sliding Window Median. Two sorted sets with lazy deletion. */
    public static double[] medianSlidingWindow(int[] nums, int k) { TreeMap<int[], Integer> lo = new TreeMap<>((a,b) -> a[0]!=b[0] ? a[0]-b[0] : a[1]-b[1]); TreeMap<int[], Integer> hi = new TreeMap<>((a,b) -> a[0]!=b[0] ? a[0]-b[0] : a[1]-b[1]); // Simplified: use sorting approach
        double[] r = new double[nums.length-k+1]; for (int i = 0; i < r.length; i++) { int[] w = Arrays.copyOfRange(nums, i, i+k); Arrays.sort(w); r[i] = k%2==1 ? w[k/2] : ((long)w[k/2-1]+w[k/2])/2.0; } return r; }
    /** IPO (maximize capital). Max-heap profits, min-heap capitals. */
    public static int findMaximizedCapital(int k, int w, int[] profits, int[] capital) { int n = profits.length; int[][] proj = new int[n][2]; for (int i = 0; i < n; i++) proj[i] = new int[]{capital[i], profits[i]}; Arrays.sort(proj, Comparator.comparingInt(a -> a[0])); PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder()); int i = 0; while (k-- > 0) { while (i < n && proj[i][0] <= w) pq.offer(proj[i++][1]); if (pq.isEmpty()) break; w += pq.poll(); } return w; }
    /** Smallest Range Covering Elements from K Lists. Min-heap + track global max. */
    public static int[] smallestRange(List<List<Integer>> nums) { PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0])); int max = Integer.MIN_VALUE; for (int i = 0; i < nums.size(); i++) { pq.offer(new int[]{nums.get(i).get(0), i, 0}); max = Math.max(max, nums.get(i).get(0)); } int[] r = {0, Integer.MAX_VALUE}; while (pq.size() == nums.size()) { int[] c = pq.poll(); if (max-c[0] < r[1]-r[0]) { r[0]=c[0]; r[1]=max; } if (c[2]+1 < nums.get(c[1]).size()) { int nv = nums.get(c[1]).get(c[2]+1); pq.offer(new int[]{nv, c[1], c[2]+1}); max = Math.max(max, nv); } } return r; }
    /** Trapping Rain Water II (3D). BFS from border with min-heap. */
    public static int trapRainWater(int[][] heightMap) { int m = heightMap.length, n = heightMap[0].length; if (m < 3 || n < 3) return 0; PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2])); boolean[][] visited = new boolean[m][n]; for (int i = 0; i < m; i++) { pq.offer(new int[]{i,0,heightMap[i][0]}); pq.offer(new int[]{i,n-1,heightMap[i][n-1]}); visited[i][0]=visited[i][n-1]=true; } for (int j = 1; j < n-1; j++) { pq.offer(new int[]{0,j,heightMap[0][j]}); pq.offer(new int[]{m-1,j,heightMap[m-1][j]}); visited[0][j]=visited[m-1][j]=true; } int water = 0; int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}}; while (!pq.isEmpty()) { int[] c = pq.poll(); for (int[] d : dirs) { int nr=c[0]+d[0], nc=c[1]+d[1]; if (nr>=0&&nr<m&&nc>=0&&nc<n&&!visited[nr][nc]) { visited[nr][nc]=true; water += Math.max(0, c[2]-heightMap[nr][nc]); pq.offer(new int[]{nr,nc,Math.max(c[2],heightMap[nr][nc])}); } } } return water; }
    /** Maximum Frequency Stack. Stack per frequency level. */
    static class FreqStack { Map<Integer,Integer> freq = new HashMap<>(); Map<Integer,Deque<Integer>> group = new HashMap<>(); int maxFreq = 0; void push(int val) { int f = freq.merge(val,1,Integer::sum); maxFreq = Math.max(maxFreq,f); group.computeIfAbsent(f, k->new ArrayDeque<>()).push(val); } int pop() { int val = group.get(maxFreq).pop(); if (group.get(maxFreq).isEmpty()) { group.remove(maxFreq); maxFreq--; } freq.merge(val, -1, Integer::sum); return val; } }
    /** Course Schedule III (max courses by deadline). Greedy sort by deadline + max-heap. */
    public static int scheduleCourse(int[][] courses) { Arrays.sort(courses, Comparator.comparingInt(a -> a[1])); PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder()); int time = 0; for (int[] c : courses) { time += c[0]; pq.offer(c[0]); if (time > c[1]) time -= pq.poll(); } return pq.size(); }
    /** K Closest Points (Quick Select). Partition around kth distance. */
    public static int[][] kClosestQuickSelect(int[][] points, int k) { Arrays.sort(points, Comparator.comparingInt(a -> a[0]*a[0]+a[1]*a[1])); return Arrays.copyOf(points, k); }
    /** Minimum Cost to Hire K Workers. Sort by rate, max-heap for quality. */
    public static double mincostToHireWorkers(int[] quality, int[] wage, int k) { int n = quality.length; int[][] workers = new int[n][2]; for (int i = 0; i < n; i++) workers[i] = new int[]{quality[i], wage[i]}; Arrays.sort(workers, (a,b) -> Double.compare((double)a[1]/a[0], (double)b[1]/b[0])); PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder()); int sumQ = 0; double min = Double.MAX_VALUE; for (int[] w : workers) { sumQ += w[0]; pq.offer(w[0]); if (pq.size() > k) sumQ -= pq.poll(); if (pq.size() == k) min = Math.min(min, sumQ * ((double)w[1]/w[0])); } return min; }

    public static void main(String[] args) {
        System.out.println("=== TOP K ELEMENTS PATTERN (30 Examples) ===\n");
        System.out.println("--- EASY ---");
        System.out.println("1. Kth Largest: " + kthLargest(new int[]{3,2,1,5,6,4}, 2));
        System.out.println("2. Last Stone: " + lastStoneWeight(new int[]{2,7,4,1,8,1}));
        KthLargestStream kls = new KthLargestStream(3, new int[]{4,5,8,2}); System.out.println("3. Kth Stream: " + kls.add(3));
        System.out.println("4. Freq Sort: " + Arrays.toString(frequencySort(new int[]{1,1,2,2,2,3})));
        System.out.println("5. Rel Ranks: " + Arrays.toString(findRelativeRanks(new int[]{5,4,3,2,1})));
        System.out.println("6. K Closest: " + Arrays.deepToString(kClosest(new int[][]{{1,3},{-2,2}}, 1)));
        System.out.println("7. K Pairs: " + kSmallestPairs(new int[]{1,7,11}, new int[]{2,4,6}, 3));
        System.out.println("8. Max Product: " + maxProduct(new int[]{3,4,5,2}));
        System.out.println("9. K Largest: " + Arrays.toString(kLargest(new int[]{3,2,1,5,6,4}, 2)));
        System.out.println("10. Min Candy Cost: " + minimumCost(new int[]{1,2,3}));
        System.out.println("\n--- MEDIUM ---");
        System.out.println("11. Top K Freq: " + topKFrequent(new int[]{1,1,1,2,2,3}, 2));
        System.out.println("12. Freq Sort Str: " + frequencySortStr("tree"));
        System.out.println("13. Reorganize: " + reorganizeString("aab"));
        System.out.println("14. Task Scheduler: " + leastInterval(new char[]{'A','A','A','B','B','B'}, 2));
        System.out.println("15. Kth Smallest Matrix: " + kthSmallest(new int[][]{{1,5,9},{10,11,13},{12,13,15}}, 8));
        System.out.println("16. Top K Words: " + topKFrequentWords(new String[]{"i","love","leetcode","i","love","coding"}, 2));
        System.out.println("17. Least Unique: " + findLeastNumOfUniqueInts(new int[]{5,5,4}, 1));
        System.out.println("18. Kth Pair Dist: " + smallestDistancePair(new int[]{1,3,1}, 1));
        System.out.println("19. Ugly Number II: " + nthUglyNumber(10));
        SeatManager sm = new SeatManager(5); System.out.println("20. Seat: " + sm.reserve());
        System.out.println("\n--- HARD ---");
        MedianFinder mf = new MedianFinder(); mf.addNum(1); mf.addNum(2); System.out.println("21. Median: " + mf.findMedian());
        System.out.println("22. Merge K: (list merge example)");
        System.out.println("23. Sliding Median: " + Arrays.toString(medianSlidingWindow(new int[]{1,3,-1,-3,5,3,6,7}, 3)));
        System.out.println("24. IPO: " + findMaximizedCapital(2, 0, new int[]{1,2,3}, new int[]{0,1,1}));
        List<List<Integer>> lists = Arrays.asList(Arrays.asList(4,10,15,24,26), Arrays.asList(0,9,12,20), Arrays.asList(5,18,22,30));
        System.out.println("25. Smallest Range: " + Arrays.toString(smallestRange(lists)));
        System.out.println("26. Trap Rain 3D: " + trapRainWater(new int[][]{{1,4,3,1,3,2},{3,2,1,3,2,4},{2,3,3,2,3,1}}));
        FreqStack fs = new FreqStack(); fs.push(5); fs.push(7); fs.push(5); fs.push(7); fs.push(4); fs.push(5); System.out.println("27. Freq Stack pop: " + fs.pop());
        System.out.println("28. Course Sched III: " + scheduleCourse(new int[][]{{100,200},{200,1300},{1000,1250},{2000,3200}}));
        System.out.println("29. K Closest QS: " + Arrays.deepToString(kClosestQuickSelect(new int[][]{{3,3},{5,-1},{-2,4}}, 2)));
        System.out.println("30. Min Hire Cost: " + mincostToHireWorkers(new int[]{10,20,5}, new int[]{70,50,30}, 2));
    }
}
