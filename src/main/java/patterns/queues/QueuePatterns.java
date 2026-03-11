package patterns.queues;

import java.util.*;

/**
 * QUEUES — 30 Essential Problems
 * A Queue (FIFO — First In, First Out) is used for BFS traversal, task scheduling,
 * sliding window problems, and message processing. Variants include circular queue,
 * priority queue (heap), and double-ended queue (deque).
 *
 * 10 Easy | 10 Medium | 10 Hard
 */
public class QueuePatterns {

    // ======================= EASY 1: Implement Queue using Stacks =======================
    /** Two stacks: in-stack for push, out-stack for pop/peek; lazy transfer. Amortized O(1) per operation. */
    static class MyQueue {
        Deque<Integer> in = new ArrayDeque<>(), out = new ArrayDeque<>();
        public void push(int x) { in.push(x); }
        public int pop() { move(); return out.pop(); }
        public int peek() { move(); return out.peek(); }
        public boolean empty() { return in.isEmpty() && out.isEmpty(); }
        private void move() { if (out.isEmpty()) while (!in.isEmpty()) out.push(in.pop()); }
    }

    // ======================= EASY 2: Number of Recent Calls =======================
    /** Queue: add timestamp, evict entries older than t-3000; size = count. O(1) amortized per call. */
    static class RecentCounter {
        Queue<Integer> q = new LinkedList<>();
        public int ping(int t) { q.add(t); while (q.peek() < t - 3000) q.poll(); return q.size(); }
    }

    // ======================= EASY 3: Moving Average from Data Stream =======================
    /** Fixed-size queue with running sum; evict oldest when full. O(1) per next(). */
    static class MovingAverage {
        Queue<Integer> q = new LinkedList<>();
        int maxSize; double sum;
        MovingAverage(int size) { maxSize = size; }
        public double next(int val) {
            if (q.size() == maxSize) sum -= q.poll();
            q.add(val); sum += val;
            return sum / q.size();
        }
    }

    // ======================= EASY 4: Number of Students Unable to Eat =======================
    /** Simulate queue rotation: if front student can eat top sandwich, remove both; else rotate student. O(n²) time. */
    public static int countStudents(int[] students, int[] sandwiches) {
        Queue<Integer> q = new LinkedList<>();
        for (int s : students) q.add(s);
        int idx = 0, attempts = 0;
        while (!q.isEmpty() && attempts < q.size()) {
            if (q.peek() == sandwiches[idx]) { q.poll(); idx++; attempts = 0; }
            else { q.add(q.poll()); attempts++; }
        }
        return q.size();
    }

    // ======================= EASY 5: Time Needed to Buy Tickets =======================
    /** Simulate queue: each person buys one ticket per turn; count turns until person k finishes. O(n·max) time. */
    public static int timeRequiredToBuy(int[] tickets, int k) {
        int time = 0;
        for (int i = 0; i < tickets.length; i++) {
            time += Math.min(tickets[i], i <= k ? tickets[k] : tickets[k] - 1);
        }
        return time;
    }

    // ======================= EASY 6: Implement Stack using Queues =======================
    /** Single queue: after each push, rotate so newest is at front. O(n) push, O(1) pop. */
    static class MyStack {
        Queue<Integer> q = new LinkedList<>();
        public void push(int x) { q.add(x); for (int i = 1; i < q.size(); i++) q.add(q.poll()); }
        public int pop() { return q.poll(); }
        public int top() { return q.peek(); }
        public boolean empty() { return q.isEmpty(); }
    }

    // ======================= EASY 7: First Unique Number in Stream =======================
    /** Queue + frequency map: enqueue all, peek skipping non-unique from front. O(1) amortized per add. */
    static class FirstUnique {
        Queue<Integer> q = new LinkedList<>();
        Map<Integer, Integer> freq = new HashMap<>();
        FirstUnique(int[] nums) { for (int n : nums) add(n); }
        public int showFirstUnique() {
            while (!q.isEmpty() && freq.get(q.peek()) > 1) q.poll();
            return q.isEmpty() ? -1 : q.peek();
        }
        public void add(int value) { freq.merge(value, 1, Integer::sum); q.add(value); }
    }

    // ======================= EASY 8: Reverse First K Elements of Queue =======================
    /** Push first k elements onto stack, then push back to queue, then rotate remaining. O(n) time. */
    public static Queue<Integer> reverseFirstK(Queue<Integer> q, int k) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < k; i++) stack.push(q.poll());
        while (!stack.isEmpty()) q.add(stack.pop());
        for (int i = 0; i < q.size() - k; i++) q.add(q.poll());
        return q;
    }

    // ======================= EASY 9: Generate Binary Numbers 1 to N =======================
    /** BFS-style: start with "1", each iteration append "0" and "1" to generate next binary numbers. O(n) time. */
    public static String[] generateBinary(int n) {
        String[] result = new String[n];
        Queue<String> q = new LinkedList<>();
        q.add("1");
        for (int i = 0; i < n; i++) {
            String s = q.poll();
            result[i] = s;
            q.add(s + "0");
            q.add(s + "1");
        }
        return result;
    }

    // ======================= EASY 10: Number of Islands (BFS approach) =======================
    /** BFS flood fill from each unvisited '1'; count connected components. O(m·n) time. */
    public static int numIslands(char[][] grid) {
        int count = 0, m = grid.length, n = grid[0].length;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    count++;
                    Queue<int[]> q = new LinkedList<>();
                    q.add(new int[]{i, j}); grid[i][j] = '0';
                    while (!q.isEmpty()) {
                        int[] cell = q.poll();
                        for (int[] d : new int[][]{{0,1},{0,-1},{1,0},{-1,0}}) {
                            int r = cell[0] + d[0], c = cell[1] + d[1];
                            if (r >= 0 && r < m && c >= 0 && c < n && grid[r][c] == '1') { grid[r][c] = '0'; q.add(new int[]{r, c}); }
                        }
                    }
                }
            }
        }
        return count;
    }

    // ======================= MEDIUM 1: Design Circular Queue =======================
    /** Fixed array with head/tail indices using modular arithmetic. O(1) all operations. */
    static class MyCircularQueue {
        int[] data; int head, tail, size, capacity;
        MyCircularQueue(int k) { data = new int[k]; capacity = k; head = 0; tail = -1; size = 0; }
        public boolean enQueue(int value) { if (isFull()) return false; tail = (tail + 1) % capacity; data[tail] = value; size++; return true; }
        public boolean deQueue() { if (isEmpty()) return false; head = (head + 1) % capacity; size--; return true; }
        public int Front() { return isEmpty() ? -1 : data[head]; }
        public int Rear() { return isEmpty() ? -1 : data[tail]; }
        public boolean isEmpty() { return size == 0; }
        public boolean isFull() { return size == capacity; }
    }

    // ======================= MEDIUM 2: Design Circular Deque =======================
    /** Fixed array with front/rear indices using modular arithmetic. O(1) all operations. */
    static class MyCircularDeque {
        int[] data; int front, rear, size, capacity;
        MyCircularDeque(int k) { data = new int[k]; capacity = k; front = 0; rear = k - 1; size = 0; }
        public boolean insertFront(int value) { if (isFull()) return false; front = (front - 1 + capacity) % capacity; data[front] = value; size++; return true; }
        public boolean insertLast(int value) { if (isFull()) return false; rear = (rear + 1) % capacity; data[rear] = value; size++; return true; }
        public boolean deleteFront() { if (isEmpty()) return false; front = (front + 1) % capacity; size--; return true; }
        public boolean deleteLast() { if (isEmpty()) return false; rear = (rear - 1 + capacity) % capacity; size--; return true; }
        public int getFront() { return isEmpty() ? -1 : data[front]; }
        public int getRear() { return isEmpty() ? -1 : data[rear]; }
        public boolean isEmpty() { return size == 0; }
        public boolean isFull() { return size == capacity; }
    }

    // ======================= MEDIUM 3: Rotting Oranges =======================
    /** Multi-source BFS: start from all rotten oranges simultaneously; minutes = levels. O(m·n) time. */
    public static int orangesRotting(int[][] grid) {
        int m = grid.length, n = grid[0].length, fresh = 0, minutes = 0;
        Queue<int[]> q = new LinkedList<>();
        for (int i = 0; i < m; i++) for (int j = 0; j < n; j++) {
            if (grid[i][j] == 2) q.add(new int[]{i, j});
            else if (grid[i][j] == 1) fresh++;
        }
        while (!q.isEmpty() && fresh > 0) {
            int size = q.size(); minutes++;
            for (int s = 0; s < size; s++) {
                int[] cell = q.poll();
                for (int[] d : new int[][]{{0,1},{0,-1},{1,0},{-1,0}}) {
                    int r = cell[0] + d[0], c = cell[1] + d[1];
                    if (r >= 0 && r < m && c >= 0 && c < n && grid[r][c] == 1) { grid[r][c] = 2; fresh--; q.add(new int[]{r, c}); }
                }
            }
        }
        return fresh == 0 ? minutes : -1;
    }

    // ======================= MEDIUM 4: Walls and Gates =======================
    /** Multi-source BFS from all gates (0); fill each empty room with shortest distance. O(m·n) time. */
    public static void wallsAndGates(int[][] rooms) {
        int INF = Integer.MAX_VALUE, m = rooms.length, n = rooms[0].length;
        Queue<int[]> q = new LinkedList<>();
        for (int i = 0; i < m; i++) for (int j = 0; j < n; j++) if (rooms[i][j] == 0) q.add(new int[]{i, j});
        while (!q.isEmpty()) {
            int[] cell = q.poll();
            for (int[] d : new int[][]{{0,1},{0,-1},{1,0},{-1,0}}) {
                int r = cell[0] + d[0], c = cell[1] + d[1];
                if (r >= 0 && r < m && c >= 0 && c < n && rooms[r][c] == INF) { rooms[r][c] = rooms[cell[0]][cell[1]] + 1; q.add(new int[]{r, c}); }
            }
        }
    }

    // ======================= MEDIUM 5: Open the Lock =======================
    /** BFS from "0000"; try all 8 neighbor combinations each step; skip deadends. O(10^4) time. */
    public static int openLock(String[] deadends, String target) {
        Set<String> dead = new HashSet<>(Arrays.asList(deadends));
        if (dead.contains("0000")) return -1;
        Queue<String> q = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        q.add("0000"); visited.add("0000");
        int steps = 0;
        while (!q.isEmpty()) {
            int size = q.size();
            for (int s = 0; s < size; s++) {
                String cur = q.poll();
                if (cur.equals(target)) return steps;
                for (int i = 0; i < 4; i++) {
                    for (int d : new int[]{1, -1}) {
                        char[] arr = cur.toCharArray();
                        arr[i] = (char)((arr[i] - '0' + d + 10) % 10 + '0');
                        String next = new String(arr);
                        if (!dead.contains(next) && visited.add(next)) q.add(next);
                    }
                }
            }
            steps++;
        }
        return -1;
    }

    // ======================= MEDIUM 6: Shortest Path in Binary Matrix =======================
    /** BFS on 8-directional grid from (0,0) to (n-1,n-1). O(n²) time. */
    public static int shortestPathBinaryMatrix(int[][] grid) {
        int n = grid.length;
        if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1) return -1;
        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{0, 0, 1}); grid[0][0] = 1;
        int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1}};
        while (!q.isEmpty()) {
            int[] cell = q.poll();
            if (cell[0] == n - 1 && cell[1] == n - 1) return cell[2];
            for (int[] d : dirs) {
                int r = cell[0] + d[0], c = cell[1] + d[1];
                if (r >= 0 && r < n && c >= 0 && c < n && grid[r][c] == 0) { grid[r][c] = 1; q.add(new int[]{r, c, cell[2] + 1}); }
            }
        }
        return -1;
    }

    // ======================= MEDIUM 7: Task Scheduler =======================
    /** Greedy formula: total = max((maxFreq - 1) * (n + 1) + countMax, tasks.length). O(n) time. */
    public static int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char c : tasks) freq[c - 'A']++;
        int maxFreq = 0, countMax = 0;
        for (int f : freq) {
            if (f > maxFreq) { maxFreq = f; countMax = 1; }
            else if (f == maxFreq) countMax++;
        }
        return Math.max(tasks.length, (maxFreq - 1) * (n + 1) + countMax);
    }

    // ======================= MEDIUM 8: Sliding Window Maximum (Deque) =======================
    /** Monotonic deque: maintain decreasing order; front is window max. O(n) time, O(k) space. */
    public static int[] maxSlidingWindow(int[] nums, int k) {
        Deque<Integer> dq = new ArrayDeque<>();
        int[] res = new int[nums.length - k + 1];
        for (int i = 0; i < nums.length; i++) {
            while (!dq.isEmpty() && dq.peekFirst() < i - k + 1) dq.pollFirst();
            while (!dq.isEmpty() && nums[dq.peekLast()] < nums[i]) dq.pollLast();
            dq.offerLast(i);
            if (i >= k - 1) res[i - k + 1] = nums[dq.peekFirst()];
        }
        return res;
    }

    // ======================= MEDIUM 9: Kth Smallest Element in Sorted Matrix =======================
    /** Min-heap BFS: start from top-left, always expand smallest neighbor. O(k·log k) time. */
    public static int kthSmallest(int[][] matrix, int k) {
        int n = matrix.length;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{matrix[0][0], 0, 0});
        boolean[][] visited = new boolean[n][n];
        visited[0][0] = true;
        int count = 0;
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            if (++count == k) return cur[0];
            int r = cur[1], c = cur[2];
            if (r + 1 < n && !visited[r + 1][c]) { visited[r + 1][c] = true; pq.offer(new int[]{matrix[r + 1][c], r + 1, c}); }
            if (c + 1 < n && !visited[r][c + 1]) { visited[r][c + 1] = true; pq.offer(new int[]{matrix[r][c + 1], r, c + 1}); }
        }
        return -1;
    }

    // ======================= MEDIUM 10: Design Hit Counter =======================
    /** Queue stores timestamps; evict old entries (> 300s) on each call. O(1) amortized. */
    static class HitCounter {
        Queue<Integer> q = new LinkedList<>();
        public void hit(int timestamp) { q.add(timestamp); }
        public int getHits(int timestamp) {
            while (!q.isEmpty() && q.peek() <= timestamp - 300) q.poll();
            return q.size();
        }
    }

    // ======================= HARD 1: Task Scheduler with Cooldown (Heap + Queue) =======================
    /** Max-heap for task counts + cooldown queue; process highest-freq task each tick. O(n·log 26) time. */
    public static int leastIntervalHeap(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char c : tasks) freq[c - 'A']++;
        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());
        for (int f : freq) if (f > 0) pq.offer(f);
        Queue<int[]> cooldown = new LinkedList<>();
        int time = 0;
        while (!pq.isEmpty() || !cooldown.isEmpty()) {
            time++;
            if (!pq.isEmpty()) { int cnt = pq.poll() - 1; if (cnt > 0) cooldown.add(new int[]{cnt, time + n}); }
            if (!cooldown.isEmpty() && cooldown.peek()[1] == time) pq.offer(cooldown.poll()[0]);
        }
        return time;
    }

    // ======================= HARD 2: Shortest Subarray with Sum >= K =======================
    /** Monotonic deque on prefix sums: remove front when sum difference >= k; remove back when not monotone. O(n) time. */
    public static int shortestSubarray(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + nums[i];
        Deque<Integer> dq = new ArrayDeque<>();
        int minLen = n + 1;
        for (int i = 0; i <= n; i++) {
            while (!dq.isEmpty() && prefix[i] - prefix[dq.peekFirst()] >= k) minLen = Math.min(minLen, i - dq.pollFirst());
            while (!dq.isEmpty() && prefix[i] <= prefix[dq.peekLast()]) dq.pollLast();
            dq.addLast(i);
        }
        return minLen <= n ? minLen : -1;
    }

    // ======================= HARD 3: Jump Game IV =======================
    /** BFS with adjacency by value: group same-value indices; level-order traversal to reach end. O(n) time. */
    public static int minJumps(int[] arr) {
        int n = arr.length;
        if (n == 1) return 0;
        Map<Integer, List<Integer>> valueToIdx = new HashMap<>();
        for (int i = 0; i < n; i++) valueToIdx.computeIfAbsent(arr[i], k -> new ArrayList<>()).add(i);
        Queue<Integer> q = new LinkedList<>();
        boolean[] visited = new boolean[n];
        q.add(0); visited[0] = true;
        int steps = 0;
        while (!q.isEmpty()) {
            int size = q.size(); steps++;
            for (int s = 0; s < size; s++) {
                int idx = q.poll();
                for (int next : new int[]{idx - 1, idx + 1}) {
                    if (next >= 0 && next < n && !visited[next]) {
                        if (next == n - 1) return steps;
                        visited[next] = true; q.add(next);
                    }
                }
                if (valueToIdx.containsKey(arr[idx])) {
                    for (int next : valueToIdx.get(arr[idx])) {
                        if (!visited[next]) {
                            if (next == n - 1) return steps;
                            visited[next] = true; q.add(next);
                        }
                    }
                    valueToIdx.remove(arr[idx]);
                }
            }
        }
        return -1;
    }

    // ======================= HARD 4: Sliding Window Median =======================
    /** Two heaps (max-heap for lower half, min-heap for upper half) with lazy deletion. O(n·log n) time. */
    public static double[] medianSlidingWindow(int[] nums, int k) {
        PriorityQueue<Integer> lo = new PriorityQueue<>(Collections.reverseOrder());
        PriorityQueue<Integer> hi = new PriorityQueue<>();
        double[] result = new double[nums.length - k + 1];
        for (int i = 0; i < nums.length; i++) {
            lo.add(nums[i]);
            hi.add(lo.poll());
            if (hi.size() > lo.size()) lo.add(hi.poll());
            if (i >= k - 1) {
                result[i - k + 1] = k % 2 == 1 ? lo.peek() : ((double) lo.peek() + hi.peek()) / 2;
                int remove = nums[i - k + 1];
                if (remove <= lo.peek()) lo.remove(remove); else hi.remove(remove);
                if (hi.size() > lo.size()) lo.add(hi.poll());
                else if (lo.size() > hi.size() + 1) hi.add(lo.poll());
            }
        }
        return result;
    }

    // ======================= HARD 5: Word Ladder =======================
    /** BFS: transform one letter at a time, level = number of transformations. O(n·L²) time. */
    public static int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> dict = new HashSet<>(wordList);
        if (!dict.contains(endWord)) return 0;
        Queue<String> q = new LinkedList<>();
        q.add(beginWord);
        int level = 1;
        while (!q.isEmpty()) {
            int size = q.size(); level++;
            for (int s = 0; s < size; s++) {
                char[] word = q.poll().toCharArray();
                for (int i = 0; i < word.length; i++) {
                    char orig = word[i];
                    for (char c = 'a'; c <= 'z'; c++) {
                        word[i] = c;
                        String next = new String(word);
                        if (next.equals(endWord)) return level;
                        if (dict.remove(next)) q.add(next);
                    }
                    word[i] = orig;
                }
            }
        }
        return 0;
    }

    // ======================= HARD 6: Shortest Path to Get All Keys =======================
    /** BFS with bitmask state (position + keys collected). O(m·n·2^k) time. */
    public static int shortestPathAllKeys(String[] grid) {
        int m = grid.length, n = grid[0].length(), allKeys = 0;
        Queue<int[]> q = new LinkedList<>();
        for (int i = 0; i < m; i++) for (int j = 0; j < n; j++) {
            char c = grid[i].charAt(j);
            if (c == '@') q.add(new int[]{i, j, 0});
            if (c >= 'a' && c <= 'f') allKeys |= (1 << (c - 'a'));
        }
        Set<String> visited = new HashSet<>();
        visited.add(q.peek()[0] + "," + q.peek()[1] + ",0");
        int steps = 0;
        while (!q.isEmpty()) {
            int size = q.size(); steps++;
            for (int s = 0; s < size; s++) {
                int[] cur = q.poll();
                for (int[] d : new int[][]{{0,1},{0,-1},{1,0},{-1,0}}) {
                    int r = cur[0] + d[0], c = cur[1] + d[1], keys = cur[2];
                    if (r < 0 || r >= m || c < 0 || c >= n || grid[r].charAt(c) == '#') continue;
                    char ch = grid[r].charAt(c);
                    if (ch >= 'A' && ch <= 'F' && (keys & (1 << (ch - 'A'))) == 0) continue;
                    if (ch >= 'a' && ch <= 'f') keys |= (1 << (ch - 'a'));
                    if (keys == allKeys) return steps;
                    String state = r + "," + c + "," + keys;
                    if (visited.add(state)) q.add(new int[]{r, c, keys});
                }
            }
        }
        return -1;
    }

    // ======================= HARD 7: Minimum Cost to Make Valid Parentheses (Queue simulation) =======================
    /** Greedy with balance counter: cost to flip unmatched open/close brackets. O(n) time, O(1) space. */
    public static int minCostValidParens(String s) {
        int openCount = 0, cost = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') openCount++;
            else {
                if (openCount > 0) openCount--;
                else cost++;
            }
        }
        return cost + openCount;
    }

    // ======================= HARD 8: Process Tasks Using Servers =======================
    /** Two priority queues: free servers and busy servers (by availability time). O(n·log n) time. */
    public static int[] assignTasks(int[] servers, int[] tasks) {
        PriorityQueue<int[]> free = new PriorityQueue<>((a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);
        PriorityQueue<int[]> busy = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        for (int i = 0; i < servers.length; i++) free.offer(new int[]{servers[i], i});
        int[] result = new int[tasks.length];
        for (int i = 0; i < tasks.length; i++) {
            while (!busy.isEmpty() && busy.peek()[0] <= i) { int[] s = busy.poll(); free.offer(new int[]{servers[s[1]], s[1]}); }
            if (free.isEmpty()) {
                int[] next = busy.poll();
                free.offer(new int[]{servers[next[1]], next[1]});
                while (!busy.isEmpty() && busy.peek()[0] == next[0]) { int[] s = busy.poll(); free.offer(new int[]{servers[s[1]], s[1]}); }
                int[] s = free.poll();
                result[i] = s[1];
                busy.offer(new int[]{next[0] + tasks[i], s[1]});
            } else {
                int[] s = free.poll();
                result[i] = s[1];
                busy.offer(new int[]{i + tasks[i], s[1]});
            }
        }
        return result;
    }

    // ======================= HARD 9: Design Snake Game =======================
    /** Deque as snake body: addFirst for new head, removeLast for tail move. O(1) per move. */
    static class SnakeGame {
        int width, height, score;
        int[][] food;
        int foodIdx;
        Deque<int[]> body = new ArrayDeque<>();
        Set<String> occupied = new HashSet<>();
        SnakeGame(int width, int height, int[][] food) {
            this.width = width; this.height = height; this.food = food;
            body.addFirst(new int[]{0, 0}); occupied.add("0,0");
        }
        public int move(String direction) {
            int[] head = body.peekFirst();
            int r = head[0], c = head[1];
            switch (direction) {
                case "U": r--; break; case "D": r++; break; case "L": c--; break; case "R": c++; break;
            }
            if (r < 0 || r >= height || c < 0 || c >= width) return -1;
            if (foodIdx < food.length && r == food[foodIdx][0] && c == food[foodIdx][1]) {
                score++; foodIdx++;
            } else {
                int[] tail = body.removeLast();
                occupied.remove(tail[0] + "," + tail[1]);
            }
            if (occupied.contains(r + "," + c)) return -1;
            body.addFirst(new int[]{r, c});
            occupied.add(r + "," + c);
            return score;
        }
    }

    // ======================= HARD 10: Maximum Frequency Stack (Queue variant) =======================
    /** Freq map + stacks grouped by frequency; pop from highest frequency group. O(1) push/pop. */
    static class FreqStack {
        Map<Integer, Integer> freq = new HashMap<>();
        Map<Integer, Deque<Integer>> group = new HashMap<>();
        int maxFreq = 0;
        public void push(int val) {
            int f = freq.merge(val, 1, Integer::sum);
            maxFreq = Math.max(maxFreq, f);
            group.computeIfAbsent(f, k -> new ArrayDeque<>()).push(val);
        }
        public int pop() {
            int val = group.get(maxFreq).pop();
            if (group.get(maxFreq).isEmpty()) { group.remove(maxFreq); maxFreq--; }
            freq.merge(val, -1, Integer::sum);
            return val;
        }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== QUEUES (30 Examples) ===\n");

        System.out.println("--- EASY ---");
        MyQueue mq = new MyQueue(); mq.push(1); mq.push(2);
        System.out.println("1. Queue via Stacks: peek=" + mq.peek() + " pop=" + mq.pop());
        RecentCounter rc = new RecentCounter(); rc.ping(1); rc.ping(100); rc.ping(3001);
        System.out.println("2. Recent Calls: " + rc.ping(3002));
        MovingAverage ma = new MovingAverage(3); ma.next(1); ma.next(10);
        System.out.println("3. Moving Avg: " + ma.next(3));
        System.out.println("4. Students Eat: " + countStudents(new int[]{1, 1, 0, 0}, new int[]{0, 1, 0, 1}));
        System.out.println("5. Ticket Time: " + timeRequiredToBuy(new int[]{2, 3, 2}, 2));
        MyStack ms = new MyStack(); ms.push(1); ms.push(2);
        System.out.println("6. Stack via Queues: top=" + ms.top());
        FirstUnique fu = new FirstUnique(new int[]{2, 3, 5});
        System.out.println("7. First Unique: " + fu.showFirstUnique());
        Queue<Integer> rq = new LinkedList<>(Arrays.asList(1, 2, 3, 4, 5));
        System.out.println("8. Reverse First 3: " + reverseFirstK(rq, 3));
        System.out.println("9. Gen Binary: " + Arrays.toString(generateBinary(5)));
        System.out.println("10. Num Islands: " + numIslands(new char[][]{{'1','1','0'},{'1','1','0'},{'0','0','1'}}));

        System.out.println("\n--- MEDIUM ---");
        MyCircularQueue cq = new MyCircularQueue(3); cq.enQueue(1); cq.enQueue(2); cq.enQueue(3);
        System.out.println("11. Circular Queue: front=" + cq.Front() + " rear=" + cq.Rear() + " full=" + cq.isFull());
        MyCircularDeque cd = new MyCircularDeque(3); cd.insertLast(1); cd.insertLast(2); cd.insertFront(3);
        System.out.println("12. Circular Deque: front=" + cd.getFront() + " rear=" + cd.getRear());
        System.out.println("13. Rotting Oranges: " + orangesRotting(new int[][]{{2,1,1},{1,1,0},{0,1,1}}));
        int[][] rooms = {{Integer.MAX_VALUE,-1,0,Integer.MAX_VALUE},{Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,-1},{Integer.MAX_VALUE,-1,Integer.MAX_VALUE,-1},{0,-1,Integer.MAX_VALUE,Integer.MAX_VALUE}};
        wallsAndGates(rooms); System.out.println("14. Walls&Gates: " + Arrays.deepToString(rooms));
        System.out.println("15. Open Lock: " + openLock(new String[]{"0201","0101","0102","1212","2002"}, "0202"));
        System.out.println("16. Shortest Path: " + shortestPathBinaryMatrix(new int[][]{{0,0,0},{1,1,0},{1,1,0}}));
        System.out.println("17. Task Scheduler: " + leastInterval(new char[]{'A','A','A','B','B','B'}, 2));
        System.out.println("18. Sliding Max: " + Arrays.toString(maxSlidingWindow(new int[]{1,3,-1,-3,5,3,6,7}, 3)));
        System.out.println("19. Kth Smallest: " + kthSmallest(new int[][]{{1,5,9},{10,11,13},{12,13,15}}, 8));
        HitCounter hc = new HitCounter(); hc.hit(1); hc.hit(2); hc.hit(3);
        System.out.println("20. Hit Counter: " + hc.getHits(4));

        System.out.println("\n--- HARD ---");
        System.out.println("21. Task Sched Heap: " + leastIntervalHeap(new char[]{'A','A','A','B','B','B'}, 2));
        System.out.println("22. Shortest Sub>=K: " + shortestSubarray(new int[]{2,-1,2}, 3));
        System.out.println("23. Jump Game IV: " + minJumps(new int[]{100,-23,-23,404,100,23,23,23,3,404}));
        System.out.println("24. Sliding Median: " + Arrays.toString(medianSlidingWindow(new int[]{1,3,-1,-3,5,3,6,7}, 3)));
        System.out.println("25. Word Ladder: " + ladderLength("hit", "cog", Arrays.asList("hot","dot","dog","lot","log","cog")));
        System.out.println("26. All Keys: " + shortestPathAllKeys(new String[]{"@.a..","###.#","b.A.B"}));
        System.out.println("27. Min Cost Parens: " + minCostValidParens("(())))("));
        System.out.println("28. Assign Tasks: " + Arrays.toString(assignTasks(new int[]{3,3,2}, new int[]{1,2,3,2,1,2})));
        SnakeGame sg = new SnakeGame(3, 2, new int[][]{{1,2},{0,1}});
        System.out.println("29. Snake: " + sg.move("R") + " " + sg.move("D") + " " + sg.move("R"));
        FreqStack fq = new FreqStack(); fq.push(5); fq.push(7); fq.push(5); fq.push(7); fq.push(4); fq.push(5);
        System.out.println("30. Freq Stack: " + fq.pop() + " " + fq.pop());
    }
}
