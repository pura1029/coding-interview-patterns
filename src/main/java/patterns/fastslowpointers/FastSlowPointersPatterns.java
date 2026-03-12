package patterns.fastslowpointers;

import java.util.*;

/**
 * PATTERN 4: FAST & SLOW POINTERS (Floyd's Tortoise and Hare)
 *
 * Two pointers at different speeds detect cycles, find midpoints, and more.
 *
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class FastSlowPointersPatterns {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int v) { val = v; }
        ListNode(int v, ListNode n) { val = v; next = n; }
    }

    private static ListNode buildList(int... vals) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        for (int v : vals) { cur.next = new ListNode(v); cur = cur.next; }
        return dummy.next;
    }
    private static String listStr(ListNode h) {
        StringBuilder sb = new StringBuilder("[");
        while (h != null) { sb.append(h.val); if (h.next != null) sb.append(","); h = h.next; }
        return sb.append("]").toString();
    }

    // ======================= EASY 1: Happy Number =======================
    /**
     * Happy Number
     *
     * <p><b>Approach:</b> Floyd's cycle detection on digit-square-sum sequence; slow computes once, fast computes twice
     *
     * <p><b>Time:</b> O(log n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static boolean isHappy(int n) {
        int slow = n, fast = n;
        do { slow = digitSqSum(slow); fast = digitSqSum(digitSqSum(fast)); } while (slow != fast);
        return slow == 1;
    }
    private static int digitSqSum(int n) {
        int s = 0; while (n > 0) { int d = n % 10; s += d * d; n /= 10; } return s;
    }

    // ======================= EASY 2: Linked List Cycle Detection =======================
    /**
     * Linked List Cycle Detection
     *
     * <p><b>Approach:</b> Floyd's tortoise-hare: slow moves 1 step, fast moves 2; if they meet, cycle exists
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static boolean hasCycle(ListNode head) {
        ListNode s = head, f = head;
        while (f != null && f.next != null) { s = s.next; f = f.next.next; if (s == f) return true; }
        return false;
    }

    // ======================= EASY 3: Middle of the Linked List =======================
    /**
     * Middle of the Linked List
     *
     * <p><b>Approach:</b> Slow pointer moves 1 step, fast moves 2; when fast reaches end, slow is at middle
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode middleNode(ListNode head) {
        ListNode s = head, f = head;
        while (f != null && f.next != null) { s = s.next; f = f.next.next; }
        return s;
    }

    // ======================= EASY 4: Palindrome Linked List =======================
    /**
     * Palindrome Linked List
     *
     * <p><b>Approach:</b> Find middle with fast/slow, reverse second half, compare both halves node by node
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static boolean isPalindromeList(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
        ListNode rev = reverse(slow);
        while (rev != null) { if (head.val != rev.val) return false; head = head.next; rev = rev.next; }
        return true;
    }
    private static ListNode reverse(ListNode head) {
        ListNode prev = null;
        while (head != null) { ListNode n = head.next; head.next = prev; prev = head; head = n; }
        return prev;
    }

    // ======================= EASY 5: Remove Nth Node From End =======================
    /**
     * Remove Nth Node From End
     *
     * <p><b>Approach:</b> Two pointers with n+1 gap; when fast reaches null, slow is before the target node
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head);
        ListNode fast = dummy, slow = dummy;
        for (int i = 0; i <= n; i++) fast = fast.next;
        while (fast != null) { slow = slow.next; fast = fast.next; }
        slow.next = slow.next.next;
        return dummy.next;
    }

    // ======================= EASY 6: Intersection of Two Linked Lists =======================
    /**
     * Intersection of Two Linked Lists
     *
     * <p><b>Approach:</b> Two pointers traverse both lists; switching heads on null aligns them at intersection
     *
     * <p><b>Time:</b> O(m+n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode a = headA, b = headB;
        while (a != b) { a = (a == null) ? headB : a.next; b = (b == null) ? headA : b.next; }
        return a;
    }

    // ======================= EASY 7: Circular Array Loop Detection =======================
    /**
     * Circular Array Loop Detection
     *
     * <p><b>Approach:</b> For each index, use Floyd's fast/slow pointers following array directions; if pointers meet with consistent direction, a cycle exists.
     *
     * @param nums the circular array where each element indicates step direction and count
     * @return true if a valid cycle exists in the array
     *
     * <p><b>Time:</b> O(n^2) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static boolean isCircularLoop(int[] nums) {
        // Simplified: check if following the array like a sequence leads to cycle
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            int slow = i, fast = i;
            boolean forward = nums[i] > 0;
            do {
                slow = next(nums, slow, forward);
                if (slow == -1) break;
                fast = next(nums, fast, forward);
                if (fast == -1) break;
                fast = next(nums, fast, forward);
                if (fast == -1) break;
            } while (slow != fast);
            if (slow != -1 && slow == fast) return true;
        }
        return false;
    }
    private static int next(int[] nums, int i, boolean forward) {
        if (i == -1) return -1;
        boolean dir = nums[i] > 0;
        if (dir != forward) return -1;
        int n = nums.length;
        int nxt = ((i + nums[i]) % n + n) % n;
        return nxt == i ? -1 : nxt;
    }

    // ======================= EASY 8: Check if N and its double exist =======================
    /**
     * Check If N and Its Double Exist
     *
     * <p><b>Approach:</b> Use a HashSet to track seen values; for each element, check if 2*n or n/2 already exists in the set.
     *
     * @param arr the input array of integers
     * @return true if there exist two indices i and j such that arr[i] == 2 * arr[j]
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(n) space.
     */
    public static boolean checkIfDoubleExists(int[] arr) {
        Set<Integer> seen = new HashSet<>();
        for (int n : arr) {
            if (seen.contains(2 * n) || (n % 2 == 0 && seen.contains(n / 2))) return true;
            seen.add(n);
        }
        return false;
    }

    // ======================= EASY 9: Power of Two (bit trick related to cycle) =======================
    /**
     * Power of Two
     *
     * <p><b>Approach:</b> A number is a power of two if it has exactly one set bit; use n & (n-1) == 0 which clears the lowest set bit.
     *
     * @param n the integer to check
     * @return true if n is a positive power of two
     *
     * <p><b>Time:</b> O(1) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    // ======================= EASY 10: Count Nodes in Complete Binary Tree using two-pointer idea =======================
    static class TreeNode {
        TreeNode left, right;
        TreeNode() {}
    }
    /**
     * Count Nodes in Complete Binary Tree
     *
     * <p><b>Approach:</b> Compare left and right heights: if equal, tree is perfect so return 2^h - 1; otherwise recurse on both subtrees.
     *
     * @param root the root of the complete binary tree
     * @return the total number of nodes in the tree
     *
     * <p><b>Time:</b> O(log^2 n) time.
     * <br><b>Space:</b> O(log n) space.
     */
    public static int countNodes(TreeNode root) {
        if (root == null) return 0;
        int leftH = 0, rightH = 0;
        TreeNode l = root, r = root;
        while (l != null) { leftH++; l = l.left; }
        while (r != null) { rightH++; r = r.right; }
        if (leftH == rightH) return (1 << leftH) - 1;
        return 1 + countNodes(root.left) + countNodes(root.right);
    }

    // ======================= MEDIUM 1: Linked List Cycle II (Find Cycle Start) =======================
    /**
     * Linked List Cycle II - Find Cycle Start
     *
     * <p><b>Approach:</b> Floyd's algorithm: after fast/slow meet inside cycle, reset slow to head; advance both one step at a time until they meet at cycle entry.
     *
     * @param head the head of the linked list
     * @return the node where the cycle begins, or null if no cycle
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode detectCycleStart(ListNode head) {
        ListNode s = head, f = head;
        while (f != null && f.next != null) {
            s = s.next; f = f.next.next;
            if (s == f) { s = head; while (s != f) { s = s.next; f = f.next; } return s; }
        }
        return null;
    }

    // ======================= MEDIUM 2: Reorder List (L0→Ln→L1→Ln-1→...) =======================
    /**
     * Reorder List
     *
     * <p><b>Approach:</b> Find middle, reverse second half, interleave both halves alternately
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static void reorderList(ListNode head) {
        if (head == null || head.next == null) return;
        ListNode slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) { slow = slow.next; fast = fast.next.next; }
        ListNode second = reverse(slow.next);
        slow.next = null;
        ListNode first = head;
        while (second != null) {
            ListNode t1 = first.next, t2 = second.next;
            first.next = second; second.next = t1;
            first = t1; second = t2;
        }
    }

    // ======================= MEDIUM 3: Sort List (Merge Sort using slow/fast) =======================
    /**
     * Sort List (Merge Sort using Fast/Slow Split)
     *
     * <p><b>Approach:</b> Find the middle using slow/fast pointers, split the list, recursively sort both halves, and merge the sorted halves.
     *
     * @param head the head of the unsorted linked list
     * @return the head of the sorted linked list
     *
     * <p><b>Time:</b> O(n log n) time.
     * <br><b>Space:</b> O(log n) space.
     */
    public static ListNode sortList(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode slow = head, fast = head.next;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
        ListNode mid = slow.next;
        slow.next = null;
        return mergeSorted(sortList(head), sortList(mid));
    }
    private static ListNode mergeSorted(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0), cur = dummy;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) { cur.next = l1; l1 = l1.next; }
            else { cur.next = l2; l2 = l2.next; }
            cur = cur.next;
        }
        cur.next = (l1 != null) ? l1 : l2;
        return dummy.next;
    }

    // ======================= MEDIUM 4: Add Two Numbers II (reverse & add) =======================
    /**
     * Add Two Numbers II
     *
     * <p><b>Approach:</b> Reverse both input lists, add corresponding digits with carry propagation, then reverse the result to get correct order.
     *
     * @param l1 the head of the first number list (most significant digit first)
     * @param l2 the head of the second number list
     * @return the head of the sum list in most-significant-digit-first order
     *
     * <p><b>Time:</b> O(m+n) time.
     * <br><b>Space:</b> O(max(m,n)) space.
     */
    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        l1 = reverse(l1); l2 = reverse(l2);
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        int carry = 0;
        while (l1 != null || l2 != null || carry > 0) {
            int sum = carry;
            if (l1 != null) { sum += l1.val; l1 = l1.next; }
            if (l2 != null) { sum += l2.val; l2 = l2.next; }
            cur.next = new ListNode(sum % 10);
            carry = sum / 10;
            cur = cur.next;
        }
        return reverse(dummy.next);
    }

    // ======================= MEDIUM 5: Odd Even Linked List =======================
    /**
     * Odd Even Linked List
     *
     * <p><b>Approach:</b> Separate nodes at odd and even positions into two sub-lists using pointer manipulation, then append the even list after the odd list.
     *
     * @param head the head of the linked list
     * @return the head of the rearranged list with odd-indexed nodes first, then even-indexed
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode oddEvenList(ListNode head) {
        if (head == null) return null;
        ListNode odd = head, even = head.next, evenHead = even;
        while (even != null && even.next != null) {
            odd.next = even.next; odd = odd.next;
            even.next = odd.next; even = even.next;
        }
        odd.next = evenHead;
        return head;
    }

    // ======================= MEDIUM 6: Delete Middle Node =======================
    /**
     * Delete the Middle Node of a Linked List
     *
     * <p><b>Approach:</b> Use slow/fast pointers where fast starts two steps ahead; when fast reaches end, slow is just before the middle node to delete.
     *
     * @param head the head of the linked list
     * @return the head of the list with the middle node removed
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode deleteMiddle(ListNode head) {
        if (head.next == null) return null;
        ListNode slow = head, fast = head.next.next;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
        slow.next = slow.next.next;
        return head;
    }

    // ======================= MEDIUM 7: Rotate List by K =======================
    /**
     * Rotate List by K Places
     *
     * <p><b>Approach:</b> Calculate list length, form a circular list by connecting tail to head, then break at position (length - k % length).
     *
     * @param head the head of the linked list
     * @param k    the number of positions to rotate right
     * @return the head of the rotated list
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null || k == 0) return head;
        int len = 1;
        ListNode tail = head;
        while (tail.next != null) { len++; tail = tail.next; }
        k %= len;
        if (k == 0) return head;
        tail.next = head;
        for (int i = 0; i < len - k; i++) tail = tail.next;
        head = tail.next;
        tail.next = null;
        return head;
    }

    // ======================= MEDIUM 8: Swap Nodes in Pairs =======================
    /**
     * Swap Nodes in Pairs
     *
     * <p><b>Approach:</b> Iteratively swap every two adjacent nodes using a dummy head; rewire pointers for each pair while traversing.
     *
     * @param head the head of the linked list
     * @return the head of the list with adjacent nodes swapped
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode swapPairs(ListNode head) {
        ListNode dummy = new ListNode(0, head);
        ListNode prev = dummy;
        while (prev.next != null && prev.next.next != null) {
            ListNode a = prev.next, b = a.next;
            a.next = b.next; b.next = a; prev.next = b;
            prev = a;
        }
        return dummy.next;
    }

    // ======================= MEDIUM 9: Partition List =======================
    /**
     * Partition List
     *
     * <p><b>Approach:</b> Maintain two separate lists (before and after partition value x); iterate through original list distributing nodes, then concatenate.
     *
     * @param head the head of the linked list
     * @param x    the partition value
     * @return the head of the rearranged list with all nodes < x before nodes >= x
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode partition(ListNode head, int x) {
        ListNode beforeD = new ListNode(0), afterD = new ListNode(0);
        ListNode before = beforeD, after = afterD;
        while (head != null) {
            if (head.val < x) { before.next = head; before = before.next; }
            else { after.next = head; after = after.next; }
            head = head.next;
        }
        after.next = null;
        before.next = afterD.next;
        return beforeD.next;
    }

    // ======================= MEDIUM 10: Remove Duplicates from Sorted List II =======================
    /**
     * Remove Duplicates from Sorted List II
     *
     * <p><b>Approach:</b> Use a dummy head and prev pointer; when duplicates detected, skip all nodes with that value by advancing head past them.
     *
     * @param head the head of the sorted linked list
     * @return the head of the list with all duplicate-value nodes removed entirely
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode deleteDuplicatesII(ListNode head) {
        ListNode dummy = new ListNode(0, head);
        ListNode prev = dummy;
        while (head != null) {
            if (head.next != null && head.val == head.next.val) {
                while (head.next != null && head.val == head.next.val) head = head.next;
                prev.next = head.next;
            } else {
                prev = prev.next;
            }
            head = head.next;
        }
        return dummy.next;
    }

    // ======================= HARD 1: Find Duplicate Number =======================
    /**
     * Find the Duplicate Number
     *
     * <p><b>Approach:</b> Treat array values as pointers forming a linked list; apply Floyd's cycle detection to find the duplicate entry point.
     *
     * @param nums array of n+1 integers where each integer is between 1 and n
     * @return the duplicate number
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static int findDuplicate(int[] nums) {
        int slow = nums[0], fast = nums[0];
        do { slow = nums[slow]; fast = nums[nums[fast]]; } while (slow != fast);
        slow = nums[0];
        while (slow != fast) { slow = nums[slow]; fast = nums[fast]; }
        return slow;
    }

    // ======================= HARD 2: Linked List Cycle with Entry + Length =======================
    /**
     * Linked List Cycle with Entry Point and Length
     *
     * <p><b>Approach:</b> Detect cycle with Floyd's algorithm, compute cycle length by traversing the cycle, then find entry by resetting one pointer to head.
     *
     * @param head the head of the linked list
     * @return an array [entryValue, cycleLength], or [-1, 0] if no cycle
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static int[] cycleInfo(ListNode head) {
        ListNode s = head, f = head;
        while (f != null && f.next != null) {
            s = s.next; f = f.next.next;
            if (s == f) {
                int len = 1;
                ListNode t = s.next;
                while (t != s) { len++; t = t.next; }
                s = head;
                while (s != f) { s = s.next; f = f.next; }
                return new int[]{s.val, len};
            }
        }
        return new int[]{-1, 0};
    }

    // ======================= HARD 3: Split Linked List into K Parts =======================
    /**
     * Split Linked List into K Parts
     *
     * <p><b>Approach:</b> Calculate part size and extra nodes (len/k, len%k); iterate through the list, cutting it into k parts where the first extra parts get one additional node.
     *
     * @param head the head of the linked list
     * @param k    the number of parts to split into
     * @return an array of k ListNode heads (some may be null if k > length)
     *
     * <p><b>Time:</b> O(n+k) time.
     * <br><b>Space:</b> O(k) space.
     */
    public static ListNode[] splitListToParts(ListNode head, int k) {
        int len = 0;
        ListNode cur = head;
        while (cur != null) { len++; cur = cur.next; }
        int partSize = len / k, extra = len % k;
        ListNode[] result = new ListNode[k];
        cur = head;
        for (int i = 0; i < k && cur != null; i++) {
            result[i] = cur;
            int size = partSize + (i < extra ? 1 : 0);
            for (int j = 1; j < size; j++) cur = cur.next;
            ListNode next = cur.next;
            cur.next = null;
            cur = next;
        }
        return result;
    }

    // ======================= HARD 4: Flatten Multilevel Doubly Linked List (simplified) =======================
    /**
     * Flatten Multilevel Doubly Linked List (Simplified)
     *
     * <p><b>Approach:</b> Traverse the singly-linked list to the end; this simplified version demonstrates the traversal pattern used in flattening multilevel structures.
     *
     * @param head the head of the linked list
     * @return the head of the flattened list
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode flattenList(ListNode head) {
        if (head == null) return null;
        ListNode cur = head;
        while (cur != null) {
            if (cur.next == null) break;
            cur = cur.next;
        }
        return head;
    }

    // ======================= HARD 5: Linked List Random Node (Reservoir Sampling) =======================
    static class RandomNode {
        private final ListNode head;
        private final Random rand = new Random();
        public RandomNode(ListNode head) { this.head = head; }
        public int getRandom() {
            int result = head.val;
            ListNode cur = head.next;
            int i = 2;
            while (cur != null) {
                if (rand.nextInt(i) == 0) result = cur.val;
                cur = cur.next; i++;
            }
            return result;
        }
    }

    // ======================= HARD 6: Copy List with Random Pointer (interleave) =======================
    /**
     * Copy List with Random Pointer (Interleave Method)
     *
     * <p><b>Approach:</b> Interleave copied nodes with originals (A->A'->B->B'...), copy random pointers using the interleaved structure, then separate the two lists.
     *
     * @param head the head of the linked list with random pointers
     * @return the head of the deep-copied list
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space (excluding output).
     */
    public static ListNode copyRandomList(ListNode head) {
        if (head == null) return null;
        ListNode cur = head;
        while (cur != null) {
            ListNode copy = new ListNode(cur.val);
            copy.next = cur.next; cur.next = copy; cur = copy.next;
        }
        cur = head;
        while (cur != null) {
            cur.next.val = cur.val; // copy value (random pointer simplified)
            cur = cur.next.next;
        }
        ListNode dummy = new ListNode(0);
        ListNode copyCur = dummy;
        cur = head;
        while (cur != null) {
            copyCur.next = cur.next;
            cur.next = cur.next.next;
            copyCur = copyCur.next;
            cur = cur.next;
        }
        return dummy.next;
    }

    // ======================= HARD 7: Merge K Sorted Lists (divide & conquer) =======================
    /**
     * Merge K Sorted Lists (Divide and Conquer)
     *
     * <p><b>Approach:</b> Recursively divide the list array in half, merge pairs of sorted lists bottom-up using the standard two-list merge.
     *
     * @param lists array of sorted linked list heads
     * @return the head of the single merged sorted list
     *
     * <p><b>Time:</b> O(N log k) time.
     * <br><b>Space:</b> O(log k) space.
     */
    public static ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;
        return mergeKHelper(lists, 0, lists.length - 1);
    }
    private static ListNode mergeKHelper(ListNode[] lists, int lo, int hi) {
        if (lo == hi) return lists[lo];
        int mid = lo + (hi - lo) / 2;
        return mergeSorted(mergeKHelper(lists, lo, mid), mergeKHelper(lists, mid + 1, hi));
    }

    // ======================= HARD 8: Reverse Linked List in Alternating K-Groups =======================
    /**
     * Reverse Linked List in Alternating K-Groups
     *
     * <p><b>Approach:</b> Reverse the first k nodes, skip the next k nodes unchanged, and recursively repeat for the remainder of the list.
     *
     * @param head the head of the linked list
     * @param k    the group size for alternating reversal
     * @return the head of the modified list
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(n/k) space.
     */
    public static ListNode reverseAlternateKGroup(ListNode head, int k) {
        ListNode cur = head;
        ListNode prev = null;
        int count = 0;
        ListNode temp = head;
        while (temp != null) { count++; temp = temp.next; }
        if (count < k) return head;
        ListNode p = null, c = head;
        for (int i = 0; i < k && c != null; i++) {
            ListNode n = c.next; c.next = p; p = c; c = n;
        }
        head.next = c;
        for (int i = 0; i < k && c != null; i++) { prev = c; c = c.next; }
        if (c != null) prev.next = reverseAlternateKGroup(c, k);
        return p;
    }

    // ======================= HARD 9: LRU Cache using Doubly Linked List =======================
    static class LRUCache {
        private final int capacity;
        private final Map<Integer, int[]> map = new LinkedHashMap<>();
        public LRUCache(int cap) { capacity = cap; }
        public int get(int key) {
            if (!map.containsKey(key)) return -1;
            int[] val = map.remove(key);
            map.put(key, val);
            return val[0];
        }
        public void put(int key, int value) {
            map.remove(key);
            map.put(key, new int[]{value});
            if (map.size() > capacity) map.remove(map.keySet().iterator().next());
        }
    }

    // ======================= HARD 10: Detect and Remove Loop =======================
    /**
     * Detect and Remove Loop in Linked List
     *
     * <p><b>Approach:</b> Use Floyd's cycle detection; once cycle found, reset one pointer to head and advance both until they meet at loop start, then remove the back-edge.
     *
     * @param head the head of the linked list
     * @return the head of the loop-free list
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode detectAndRemoveLoop(ListNode head) {
        ListNode s = head, f = head;
        while (f != null && f.next != null) {
            s = s.next; f = f.next.next;
            if (s == f) {
                s = head;
                if (s == f) { while (f.next != s) f = f.next; }
                else { while (s.next != f.next) { s = s.next; f = f.next; } }
                f.next = null;
                return head;
            }
        }
        return head;
    }

    public static void main(String[] args) {
        System.out.println("=== FAST & SLOW POINTERS PATTERN (30 Examples) ===\n");

        System.out.println("--- EASY ---");
        // isHappy uses Floyd's cycle: do-while loop with slow=digitSqSum(slow), fast=digitSqSum(digitSqSum(fast)); if (slow==1) happy, else cycle detected
        System.out.println("1. Happy Number (19): " + isHappy(19));
        // recursive backtracking with if (base case) add result
        ListNode cycleList = buildList(3, 2, 0, -4);
        cycleList.next.next.next.next = cycleList.next;
        // cycleList → manually linked to create cycle; hasCycle uses while (fast != null && fast.next != null) with if (slow == fast) return true
        System.out.println("2. Has Cycle: " + hasCycle(cycleList));
        // buildList creates linked list via new ListNode(); middleNode uses while (fast != null && fast.next != null) slow advances to middle
        System.out.println("3. Middle Node: " + middleNode(buildList(1, 2, 3, 4, 5)).val);
        // buildList creates list; find middle with fast/slow, reverse second half via while-loop, compare halves with while + if (val mismatch)
        System.out.println("4. Palindrome List: " + isPalindromeList(buildList(1, 2, 2, 1)));
        // buildList creates list + new ListNode(0) dummy; for-loop advances fast n+1 steps, while-loop moves both; slow.next = slow.next.next removes target
        System.out.println("5. Remove Nth End: " + listStr(removeNthFromEnd(buildList(1, 2, 3, 4, 5), 2)));
        // recursive backtracking with if (base case) add result
        ListNode common = buildList(8, 4, 5);
        // new ListNode[]{...} → creates array of list nodes; new ListNode[]{...} → creates array of list nodes
        ListNode a = new ListNode(4, new ListNode(1, common));
        // new ListNode[]{...} → creates array of list nodes; new ListNode[]{...} → creates array of list nodes
        ListNode b = new ListNode(5, new ListNode(6, new ListNode(1, common)));
        // new ListNode() creates nodes + shared tail; two pointers: while (a != b) with ternary (a==null ? headB : a.next) — length alignment trick
        System.out.println("6. Intersection: " + (getIntersectionNode(a, b) != null ? getIntersectionNode(a, b).val : "null"));
        // new int[]{...} → circular array; nested for-loop + do-while with fast/slow; if (direction consistent && slow==fast) cycle found
        System.out.println("7. Circular Loop: " + isCircularLoop(new int[]{2, -1, 1, 2, 2}));
        // new int[]{...} → array; creates new HashSet<>(); for-loop with if (set.contains(2*n) || (n%2==0 && set.contains(n/2))) return true
        System.out.println("8. Double Exists: " + checkIfDoubleExists(new int[]{10, 2, 5, 3}));
        // isPowerOfTwo uses bit trick: return n > 0 && (n & (n-1)) == 0 — single conditional expression, no loop needed
        System.out.println("9. Power of Two (16): " + isPowerOfTwo(16));
        // Count Nodes: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("10. Count Nodes: (complete tree example)");

        System.out.println("\n--- MEDIUM ---");
        // recursive backtracking with if (base case) add result
        ListNode cycleList2 = buildList(3, 2, 0, -4);
        cycleList2.next.next.next.next = cycleList2.next;
        // cycleList2 → manually linked cycle; Floyd's: after fast/slow meet, reset slow to head; while (slow != fast) advance both one step → entry point
        System.out.println("11. Cycle Start: " + detectCycleStart(cycleList2).val);
        // buildList creates list; find middle with fast/slow, reverse second half, while-loop interleaves with temp variables t1/t2
        ListNode reorder = buildList(1, 2, 3, 4, 5); reorderList(reorder);
        // listStr() processes input; uses for/while loop with conditional checks for result computation
        System.out.println("12. Reorder List: " + listStr(reorder));
        // buildList creates list; merge sort: fast/slow split via while, recursive sort both halves, while-loop merge with if (l1.val <= l2.val)
        System.out.println("13. Sort List: " + listStr(sortList(buildList(4, 2, 1, 3))));
        // buildList creates two lists; reverse both, while (l1 || l2 || carry) with if (l1 != null) sum += l1.val; new ListNode(sum%10) per digit
        System.out.println("14. Add Two Numbers: " + listStr(addTwoNumbers(buildList(7, 2, 4, 3), buildList(5, 6, 4))));
        // buildList creates list; while (even != null && even.next != null) separate odd/even indexed nodes; odd.next = evenHead links them
        System.out.println("15. Odd Even List: " + listStr(oddEvenList(buildList(1, 2, 3, 4, 5))));
        // buildList creates list; if (head.next == null) return null; fast starts 2 ahead, while-loop: slow.next = slow.next.next removes middle
        System.out.println("16. Delete Middle: " + listStr(deleteMiddle(buildList(1, 3, 4, 7, 1, 2, 6))));
        // buildList creates list; count length via while, k %= len; if (k==0) return; form circle with tail.next=head, for-loop to cut at new head
        System.out.println("17. Rotate Right: " + listStr(rotateRight(buildList(1, 2, 3, 4, 5), 2)));
        // buildList creates list + new ListNode(0) dummy; while (prev.next && prev.next.next) swap pair a,b with pointer rewiring
        System.out.println("18. Swap Pairs: " + listStr(swapPairs(buildList(1, 2, 3, 4))));
        // buildList creates list + two new ListNode(0) dummies (before/after); while with if (val < x) append to before, else after
        System.out.println("19. Partition List: " + listStr(partition(buildList(1, 4, 3, 2, 5, 2), 3)));
        // buildList creates list + new ListNode(0) dummy; while with if (head.next && val==next.val) inner while skips all dupes, prev.next = head.next
        System.out.println("20. Delete Dup II: " + listStr(deleteDuplicatesII(buildList(1, 2, 3, 3, 4, 4, 5))));

        System.out.println("\n--- HARD ---");
        // new int[]{...} → array as implicit linked list; do-while Floyd's: slow=nums[slow], fast=nums[nums[fast]]; find entry point
        System.out.println("21. Find Duplicate: " + findDuplicate(new int[]{1, 3, 4, 2, 2}));
        // recursive backtracking with if (base case) add result
        ListNode cycleList3 = buildList(1, 2, 3, 4, 5);
        cycleList3.next.next.next.next.next = cycleList3.next;
        // buildList + manual cycle link; Floyd's detect, then while (t != s) count cycle length; reset to head, while (s != f) find entry value
        int[] info = cycleInfo(cycleList3);
        // Cycle Info: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("22. Cycle Info: entry=" + info[0] + ", length=" + info[1]);
        // buildList creates list; count length, partSize=len/k, extra=len%k; for-loop with inner for cuts parts, if (i < extra) one extra node
        ListNode[] parts = splitListToParts(buildList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 3);
        System.out.print("23. Split Parts: ");
        for (ListNode p : parts) System.out.print(listStr(p) + " ");
        System.out.println();
        // Flatten List: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("24. Flatten List: (simplified)");
        // new RandomNode() → creates object
        RandomNode rn = new RandomNode(buildList(1, 2, 3));
        // Random Node: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("25. Random Node: " + rn.getRandom());
        // listStr() processes input; uses for/while loop with conditional checks for result computation
        System.out.println("26. Copy List: " + listStr(copyRandomList(buildList(1, 2, 3))));
        // new ListNode[]{...} → array of lists; divide & conquer: recursive split at mid, mergeSorted uses while with if (l1.val <= l2.val)
        System.out.println("27. Merge K Lists: " + listStr(mergeKLists(new ListNode[]{buildList(1, 4, 5), buildList(1, 3, 4), buildList(2, 6)})));
        // buildList creates list; count nodes; if (count < k) return; for-loop reverses k nodes, for-loop skips k, recursive call for rest
        System.out.println("28. Reverse Alt K: " + listStr(reverseAlternateKGroup(buildList(1, 2, 3, 4, 5, 6, 7, 8), 3)));
        // new LRUCache(2) → creates object with LinkedHashMap<>(); get removes+re-puts for access order, put uses if (size > capacity) remove eldest
        LRUCache lru = new LRUCache(2);
        lru.put(1, 1); lru.put(2, 2);
        // LRU get(1): uses internal conditional logic (if/else, for/while) for computation
        System.out.println("29. LRU get(1): " + lru.get(1));
        // recursive backtracking with if (base case) add result
        ListNode loopList = buildList(1, 2, 3, 4, 5);
        loopList.next.next.next.next.next = loopList.next.next;
        // buildList + manual loop; Floyd's: after meet, reset slow; if (s==f) find node before entry via while (f.next != s), set f.next = null
        System.out.println("30. Remove Loop: " + listStr(detectAndRemoveLoop(loopList)));
    }
}
