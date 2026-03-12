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
    /**
     * Implement Queue using Stacks — simulate FIFO behavior with two LIFO stacks.
     *
     * <p><b>Approach:</b> In-stack receives pushes. Out-stack serves pops/peeks.
     * Transfer elements lazily from in to out only when out is empty, which reverses
     * the order to achieve FIFO.
     *
     * <p><b>Operations:</b> Push O(1), Pop/Peek amortized O(1), Empty O(1).
     */
    static class MyQueue {
        Deque<Integer> in = new ArrayDeque<>(), out = new ArrayDeque<>();
        public void push(int x) { in.push(x); }
        public int pop() { move(); return out.pop(); }
        public int peek() { move(); return out.peek(); }
        public boolean empty() { return in.isEmpty() && out.isEmpty(); }
        private void move() { if (out.isEmpty()) while (!in.isEmpty()) out.push(in.pop()); }
    }

    // ======================= EASY 2: Number of Recent Calls =======================
    /**
     * Number of Recent Calls — count how many requests have been made in the last 3000ms.
     *
     * <p><b>Approach:</b> Queue stores timestamps. On each ping, add the new timestamp
     * and evict entries older than t − 3000 from the front. Queue size = count.
     *
     * <p><b>Operations:</b> O(1) amortized per ping.
     */
    static class RecentCounter {
        Queue<Integer> q = new LinkedList<>();
        public int ping(int t) { q.add(t); while (q.peek() < t - 3000) q.poll(); return q.size(); }
    }

    // ======================= EASY 3: Moving Average from Data Stream =======================
    /**
     * Moving Average from Data Stream — compute the moving average over a fixed window.
     *
     * <p><b>Approach:</b> Queue of fixed capacity with a running sum.
     * When the queue is full, evict the oldest value and subtract it from the sum.
     * Add the new value and divide sum by queue size.
     *
     * <p><b>Operations:</b> O(1) per next() call.
     */
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
    /**
     * Number of Students Unable to Eat — students in a queue pick sandwiches from a stack.
     * If the front student doesn't want the top sandwich, they go to the back.
     *
     * <p><b>Approach:</b> Simulate the queue rotation. Track consecutive failed attempts;
     * if no student can eat the current sandwich (attempts = queue size), stop.
     *
     * <p><b>Example:</b> students=[1,1,0,0], sandwiches=[0,1,0,1] → 0.
     *
     * @param students   preferences of students (0 or 1)
     * @param sandwiches stack of sandwiches (top first)
     * @return number of students unable to eat
     *
     * <p><b>Time:</b> O(n²) worst case — each student may rotate multiple times.
     * <br><b>Space:</b> O(n) — queue of students.
     */
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
    /**
     * Time Needed to Buy Tickets — each person buys one ticket per turn and goes to the back.
     * Find how many turns until person k finishes buying all their tickets.
     *
     * <p><b>Approach:</b> For each person before or at k, they contribute min(tickets[i], tickets[k]) turns.
     * For each person after k, they contribute min(tickets[i], tickets[k] − 1) turns.
     *
     * <p><b>Example:</b> tickets=[2,3,2], k=2 → 6.
     *
     * @param tickets array of ticket counts each person wants
     * @param k       index of the person to track
     * @return total time until person k finishes
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — constant variables.
     */
    public static int timeRequiredToBuy(int[] tickets, int k) {
        int time = 0;
        for (int i = 0; i < tickets.length; i++) {
            time += Math.min(tickets[i], i <= k ? tickets[k] : tickets[k] - 1);
        }
        return time;
    }

    // ======================= EASY 6: Implement Stack using Queues =======================
    /**
     * Implement Stack using Queues — simulate LIFO behavior using a single FIFO queue.
     *
     * <p><b>Approach:</b> After each push, rotate all existing elements behind the new one
     * so the newest element is always at the front.
     *
     * <p><b>Operations:</b> Push O(n), Pop/Top O(1), Empty O(1).
     */
    static class MyStack {
        Queue<Integer> q = new LinkedList<>();
        public void push(int x) { q.add(x); for (int i = 1; i < q.size(); i++) q.add(q.poll()); }
        public int pop() { return q.poll(); }
        public int top() { return q.peek(); }
        public boolean empty() { return q.isEmpty(); }
    }

    // ======================= EASY 7: First Unique Number in Stream =======================
    /**
     * First Unique Number in Stream — maintain a stream of integers and efficiently
     * return the first unique (non-repeated) number.
     *
     * <p><b>Approach:</b> Queue + frequency map. On showFirstUnique(), skip front entries
     * whose frequency exceeds 1. The first entry with frequency 1 is the answer.
     *
     * <p><b>Operations:</b> add O(1), showFirstUnique amortized O(1).
     */
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
    /**
     * Reverse First K Elements of Queue — reverse the order of the first k elements
     * while keeping the remaining elements in order.
     *
     * <p><b>Approach:</b> Pop the first k elements onto a stack (reverses them),
     * push them back to the queue, then rotate the remaining (size − k) elements
     * to the back.
     *
     * <p><b>Example:</b> [1,2,3,4,5], k=3 → [3,2,1,4,5].
     *
     * @param q the input queue
     * @param k number of elements to reverse
     * @return the queue with first k elements reversed
     *
     * <p><b>Time:</b> O(n) — three passes over elements.
     * <br><b>Space:</b> O(k) — stack holds k elements.
     */
    public static Queue<Integer> reverseFirstK(Queue<Integer> q, int k) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < k; i++) stack.push(q.poll());
        while (!stack.isEmpty()) q.add(stack.pop());
        for (int i = 0; i < q.size() - k; i++) q.add(q.poll());
        return q;
    }

    // ======================= EASY 9: Generate Binary Numbers 1 to N =======================
    /**
     * Generate Binary Numbers 1 to N — produce the binary representation of numbers 1 through n.
     *
     * <p><b>Approach:</b> BFS-style generation: start with "1". Each iteration,
     * dequeue a value and enqueue it + "0" and it + "1".
     * The first n dequeued values are the binary numbers.
     *
     * <p><b>Example:</b> n=5 → ["1","10","11","100","101"].
     *
     * @param n the count of binary numbers to generate
     * @return array of binary string representations
     *
     * <p><b>Time:</b> O(n) — generate n binary strings.
     * <br><b>Space:</b> O(n) — queue and result array.
     */
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
    /**
     * Number of Islands (BFS) — count the number of connected components of '1's
     * in a 2D grid.
     *
     * <p><b>Approach:</b> BFS flood fill from each unvisited '1'. Mark visited cells
     * as '0'. Each BFS traversal discovers one island.
     *
     * <p><b>Example:</b> [['1','1','0'],['1','1','0'],['0','0','1']] → 2.
     *
     * @param grid 2D character grid of '1' (land) and '0' (water)
     * @return number of islands
     *
     * <p><b>Time:</b> O(m·n) — each cell visited at most once.
     * <br><b>Space:</b> O(min(m, n)) — BFS queue size.
     */
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
    /**
     * Design Circular Queue — implement a FIFO queue with fixed capacity using a circular array.
     *
     * <p><b>Approach:</b> Fixed-size array with head and tail indices using modular arithmetic.
     * Tail advances on enqueue, head advances on dequeue. Full/empty differentiated by size counter.
     *
     * <p><b>Operations:</b> All O(1) time, O(k) space.
     */
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
    /**
     * Design Circular Deque — implement a double-ended queue with fixed capacity
     * using a circular array.
     *
     * <p><b>Approach:</b> Circular array with front and rear pointers. insertFront
     * decrements front (mod capacity), insertLast increments rear. Supports both
     * ends in O(1).
     *
     * <p><b>Operations:</b> All O(1) time, O(k) space.
     */
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
    /**
     * Rotting Oranges — find the minimum time for all fresh oranges to rot via
     * adjacent spreading.
     *
     * <p><b>Approach:</b> Multi-source BFS from all initially rotten oranges.
     * Each BFS level = 1 minute. Track fresh orange count; return -1 if any remain.
     *
     * <p><b>Example:</b> [[2,1,1],[1,1,0],[0,1,1]] → 4.
     *
     * @param grid 2D grid: 0 = empty, 1 = fresh, 2 = rotten
     * @return minutes until all oranges rot, or -1 if impossible
     *
     * <p><b>Time:</b> O(m·n) — each cell processed at most once.
     * <br><b>Space:</b> O(m·n) — BFS queue.
     */
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
    /**
     * Walls and Gates — fill each empty room with its distance to the nearest gate.
     *
     * <p><b>Approach:</b> Multi-source BFS starting from all gates (cells with value 0).
     * Each level increments the distance by 1. Only fill rooms marked as INF.
     *
     * @param rooms 2D grid: -1 = wall, 0 = gate, INF = empty room
     *
     * <p><b>Time:</b> O(m·n) — each cell processed at most once.
     * <br><b>Space:</b> O(m·n) — BFS queue.
     */
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
    /**
     * Open the Lock — find the minimum turns to reach a target lock combination
     * from "0000", avoiding deadends.
     *
     * <p><b>Approach:</b> BFS from "0000". Each state has 8 neighbors (4 wheels × 2 directions).
     * Skip deadend combinations. The BFS level = number of turns.
     *
     * <p><b>Example:</b> deadends=["0201","0101","0102","1212","2002"], target="0202" → 6.
     *
     * @param deadends combinations to avoid
     * @param target   the target combination
     * @return minimum turns, or -1 if impossible
     *
     * <p><b>Time:</b> O(10^4 · 4) — at most 10,000 states × 8 neighbors.
     * <br><b>Space:</b> O(10^4) — visited set.
     */
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
    /**
     * Shortest Path in Binary Matrix — find the shortest path from top-left to
     * bottom-right in an n×n grid, moving in 8 directions (including diagonals).
     *
     * <p><b>Approach:</b> BFS from (0,0). Explore all 8 neighbors. Track path length.
     * The first time we reach (n-1, n-1) is the shortest path.
     *
     * <p><b>Example:</b> [[0,0,0],[1,1,0],[1,1,0]] → 4.
     *
     * @param grid binary grid: 0 = open, 1 = blocked
     * @return length of shortest clear path, or -1 if none exists
     *
     * <p><b>Time:</b> O(n²) — each cell visited at most once.
     * <br><b>Space:</b> O(n²) — BFS queue.
     */
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
    /**
     * Task Scheduler — find the minimum intervals needed to execute all tasks
     * with a cooldown period of n between same tasks.
     *
     * <p><b>Approach:</b> Greedy formula: total = max((maxFreq − 1) × (n + 1) + countMax, tasks.length).
     * The most frequent task creates a frame; other tasks fill idle slots.
     *
     * <p><b>Example:</b> tasks=['A','A','A','B','B','B'], n=2 → 8.
     *
     * @param tasks array of task characters
     * @param n     cooldown period between same tasks
     * @return minimum total intervals
     *
     * <p><b>Time:</b> O(n) — count frequencies + formula.
     * <br><b>Space:</b> O(1) — fixed-size frequency array (26 letters).
     */
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
    /**
     * Sliding Window Maximum — find the maximum value in each sliding window of size k.
     *
     * <p><b>Approach:</b> Monotonic decreasing deque of indices. The front always holds
     * the window maximum. Evict front if out-of-window; evict back if smaller than current.
     *
     * <p><b>Example:</b> [1,3,-1,-3,5,3,6,7], k=3 → [3,3,5,5,6,7].
     *
     * @param nums array of integers
     * @param k    window size
     * @return array of window maximums
     *
     * <p><b>Time:</b> O(n) — each element enqueued/dequeued once.
     * <br><b>Space:</b> O(k) — deque stores at most k indices.
     */
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
    /**
     * Kth Smallest Element in a Sorted Matrix — find the kth smallest element
     * in a matrix where rows and columns are sorted.
     *
     * <p><b>Approach:</b> Min-heap BFS starting from the top-left corner. Always expand
     * the smallest neighbor (right or down). The kth element polled is the answer.
     *
     * <p><b>Example:</b> [[1,5,9],[10,11,13],[12,13,15]], k=8 → 13.
     *
     * @param matrix n×n sorted matrix
     * @param k      the rank (1-based) of the desired element
     * @return the kth smallest element
     *
     * <p><b>Time:</b> O(k·log k) — at most k heap operations.
     * <br><b>Space:</b> O(k + n²) — heap and visited array.
     */
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
    /**
     * Design Hit Counter — count the number of hits in the past 5 minutes (300 seconds).
     *
     * <p><b>Approach:</b> Queue stores timestamps. On getHits(), evict all entries
     * older than (timestamp − 300). The remaining queue size = hit count.
     *
     * <p><b>Operations:</b> hit O(1), getHits amortized O(1).
     */
    static class HitCounter {
        Queue<Integer> q = new LinkedList<>();
        public void hit(int timestamp) { q.add(timestamp); }
        public int getHits(int timestamp) {
            while (!q.isEmpty() && q.peek() <= timestamp - 300) q.poll();
            return q.size();
        }
    }

    // ======================= HARD 1: Task Scheduler with Cooldown (Heap + Queue) =======================
    /**
     * Task Scheduler with Cooldown (Heap + Queue) — schedule tasks with cooldown
     * using a max-heap for priority and a queue for cooldown tracking.
     *
     * <p><b>Approach:</b> Max-heap orders tasks by remaining count. Each tick,
     * execute the highest-count task, decrement, and place in a cooldown queue.
     * Release tasks from cooldown when their wait expires.
     *
     * <p><b>Example:</b> tasks=['A','A','A','B','B','B'], n=2 → 8.
     *
     * @param tasks array of task characters
     * @param n     cooldown period
     * @return total time intervals needed
     *
     * <p><b>Time:</b> O(n·log 26) ≈ O(n) — heap has at most 26 entries.
     * <br><b>Space:</b> O(26) — heap and cooldown queue.
     */
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
    /**
     * Shortest Subarray with Sum at Least K — find the shortest contiguous subarray
     * whose sum is at least k (handles negative numbers).
     *
     * <p><b>Approach:</b> Monotonic deque on prefix sums. Remove from front when
     * prefix[i] − prefix[front] ≥ k (candidate answer). Remove from back when
     * prefix[i] ≤ prefix[back] (non-monotone — can't be useful).
     *
     * <p><b>Example:</b> [2,-1,2], k=3 → 3.
     *
     * @param nums array of integers (may contain negatives)
     * @param k    target sum threshold
     * @return length of shortest subarray with sum ≥ k, or -1 if none
     *
     * <p><b>Time:</b> O(n) — each index enqueued/dequeued once.
     * <br><b>Space:</b> O(n) — prefix array and deque.
     */
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
    /**
     * Jump Game IV — find the minimum jumps to reach the last index. From index i,
     * you can jump to i−1, i+1, or any index j where arr[j] == arr[i].
     *
     * <p><b>Approach:</b> BFS with adjacency by value. Group same-value indices.
     * Level-order traversal; after visiting a value group, remove it to avoid revisiting.
     *
     * <p><b>Example:</b> [100,-23,-23,404,100,23,23,23,3,404] → 3.
     *
     * @param arr array of integers
     * @return minimum jumps to reach the last index
     *
     * <p><b>Time:</b> O(n) — each index and value group visited at most once.
     * <br><b>Space:</b> O(n) — visited array and value-to-index map.
     */
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
    /**
     * Sliding Window Median — find the median of each sliding window of size k.
     *
     * <p><b>Approach:</b> Two heaps: max-heap for the lower half, min-heap for the upper half.
     * Maintain balance so the median is at the top of the max-heap (odd k) or average of both tops.
     * Remove outgoing elements and rebalance.
     *
     * <p><b>Example:</b> [1,3,-1,-3,5,3,6,7], k=3 → [1.0,-1.0,-1.0,3.0,5.0,6.0].
     *
     * @param nums array of integers
     * @param k    window size
     * @return array of medians for each window
     *
     * <p><b>Time:</b> O(n·log n) — heap operations per element.
     * <br><b>Space:</b> O(n) — heaps store window elements.
     */
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
    /**
     * Word Ladder — find the shortest transformation sequence from beginWord to endWord,
     * changing one letter at a time, using only words from the dictionary.
     *
     * <p><b>Approach:</b> BFS where each state is a word. For each word, try changing
     * every character to 'a'-'z'. If the result is in the dictionary, add it to the queue.
     * BFS level = number of transformations.
     *
     * <p><b>Example:</b> "hit" → "cog" via ["hot","dot","dog","lot","log","cog"] → 5.
     *
     * @param beginWord starting word
     * @param endWord   target word
     * @param wordList  dictionary of valid words
     * @return length of shortest transformation (0 if impossible)
     *
     * <p><b>Time:</b> O(n·L²) where n = dictionary size, L = word length.
     * <br><b>Space:</b> O(n·L) — dictionary set and BFS queue.
     */
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
    /**
     * Shortest Path to Get All Keys — find the shortest path in a grid to collect
     * all keys (lowercase letters), using BFS with bitmask state.
     *
     * <p><b>Approach:</b> BFS where state = (row, col, keysCollected).
     * Keys are represented as a bitmask. Locked doors require the corresponding key.
     * Return the step count when all keys are collected.
     *
     * @param grid the grid with '@' (start), '#' (wall), 'a'-'f' (keys), 'A'-'F' (locks)
     * @return shortest path to collect all keys, or -1 if impossible
     *
     * <p><b>Time:</b> O(m·n·2^k) where k = number of keys.
     * <br><b>Space:</b> O(m·n·2^k) — visited states.
     */
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
    /**
     * Minimum Cost to Make Valid Parentheses — find the minimum number of bracket
     * flips to make a parentheses string valid.
     *
     * <p><b>Approach:</b> Greedy with a balance counter. Unmatched ')' (balance goes negative)
     * costs one flip. Remaining unmatched '(' at the end also cost one flip each.
     *
     * <p><b>Example:</b> "(()))(" → 2.
     *
     * @param s string of parentheses
     * @return minimum cost (flips) to make the string valid
     *
     * <p><b>Time:</b> O(n) — single pass.
     * <br><b>Space:</b> O(1) — two counters.
     */
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
    /**
     * Process Tasks Using Servers — assign tasks to the lightest available server.
     * If no server is free, wait until the earliest one becomes available.
     *
     * <p><b>Approach:</b> Two priority queues: free servers (by weight, then index)
     * and busy servers (by availability time). Process tasks in order, releasing
     * servers as they become free.
     *
     * <p><b>Example:</b> servers=[3,3,2], tasks=[1,2,3,2,1,2] → [2,2,0,2,1,2].
     *
     * @param servers weight of each server
     * @param tasks   processing time of each task
     * @return array mapping each task to the server that processes it
     *
     * <p><b>Time:</b> O(n·log n) — heap operations per task.
     * <br><b>Space:</b> O(n) — heaps store all servers.
     */
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
    /**
     * Design Snake Game — simulate the classic snake game on a grid.
     *
     * <p><b>Approach:</b> Deque as the snake body: addFirst for the new head,
     * removeLast for the tail. If food is eaten, the tail stays (snake grows).
     * A HashSet tracks occupied cells for self-collision detection.
     *
     * <p><b>Operations:</b> O(1) per move.
     */
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
    /**
     * Maximum Frequency Stack — design a stack that pops the most frequent element.
     * Ties broken by most recent push.
     *
     * <p><b>Approach:</b> Frequency map + stacks grouped by frequency level.
     * Push increments frequency and adds to the corresponding group.
     * Pop removes from the highest frequency group.
     *
     * <p><b>Operations:</b> O(1) push and pop.
     */
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
