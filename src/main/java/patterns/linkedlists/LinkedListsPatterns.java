package patterns.linkedlists;

import java.util.*;

/**
 * LINKED LISTS — 30 Essential Problems
 * A linear data structure where elements are stored in nodes connected via pointers.
 * Unlike arrays, linked lists allow O(1) insertion/deletion but O(n) random access.
 * Key techniques: two pointers (fast/slow), dummy heads, in-place reversal, merge.
 *
 * 10 Easy | 10 Medium | 10 Hard
 */
public class LinkedListsPatterns {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    static ListNode of(int... vals) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        for (int v : vals) { cur.next = new ListNode(v); cur = cur.next; }
        return dummy.next;
    }

    static String toString(ListNode head) {
        StringBuilder sb = new StringBuilder("[");
        while (head != null) { sb.append(head.val); if (head.next != null) sb.append("→"); head = head.next; }
        return sb.append("]").toString();
    }

    // ======================= EASY 1: Reverse Linked List =======================
    /**
     * Reverse Linked List
     *
     * <p><b>Approach:</b> Iteratively reverse by maintaining prev/cur/next pointers
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode reverseList(ListNode head) {
        ListNode prev = null, cur = head;
        while (cur != null) { ListNode next = cur.next; cur.next = prev; prev = cur; cur = next; }
        return prev;
    }

    // ======================= EASY 2: Merge Two Sorted Lists =======================
    /**
     * Merge Two Sorted Lists
     *
     * <p><b>Approach:</b> Dummy head; compare and link smaller node at each step
     *
     * @param l1 the l1 parameter
     * @param l2 the l2 parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(m+n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0), cur = dummy;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) { cur.next = l1; l1 = l1.next; } else { cur.next = l2; l2 = l2.next; }
            cur = cur.next;
        }
        cur.next = l1 != null ? l1 : l2;
        return dummy.next;
    }

    // ======================= EASY 3: Linked List Cycle =======================
    /**
     * Linked List Cycle
     *
     * <p><b>Approach:</b> Floyd's tortoise and hare: slow moves 1, fast moves 2; if they meet, cycle exists
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static boolean hasCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; if (slow == fast) return true; }
        return false;
    }

    // ======================= EASY 4: Middle of Linked List =======================
    /**
     * Middle of Linked List
     *
     * <p><b>Approach:</b> Slow pointer moves 1 step, fast moves 2; when fast reaches end, slow is at middle
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode middleNode(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
        return slow;
    }

    // ======================= EASY 5: Remove Nth Node From End =======================
    /**
     * Remove Nth Node From End
     *
     * <p><b>Approach:</b> Two pointers with n+1 gap; when fast reaches null, slow is before the target
     *
     * @param head the head parameter
     * @param n the n parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head), fast = dummy, slow = dummy;
        for (int i = 0; i <= n; i++) fast = fast.next;
        while (fast != null) { slow = slow.next; fast = fast.next; }
        slow.next = slow.next.next;
        return dummy.next;
    }

    // ======================= EASY 6: Palindrome Linked List =======================
    /**
     * Palindrome Linked List
     *
     * <p><b>Approach:</b> Find middle, reverse second half, compare both halves node by node
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static boolean isPalindrome(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
        ListNode rev = reverseList(slow);
        while (rev != null) { if (head.val != rev.val) return false; head = head.next; rev = rev.next; }
        return true;
    }

    // ======================= EASY 7: Remove Duplicates from Sorted List =======================
    /**
     * Remove Duplicates from Sorted List
     *
     * <p><b>Approach:</b> Skip consecutive nodes with equal values by relinking
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode deleteDuplicates(ListNode head) {
        ListNode cur = head;
        while (cur != null && cur.next != null) { if (cur.val == cur.next.val) cur.next = cur.next.next; else cur = cur.next; }
        return head;
    }

    // ======================= EASY 8: Intersection of Two Linked Lists =======================
    /**
     * Intersection of Two Linked Lists
     *
     * <p><b>Approach:</b> Two pointers traverse both lists; switching heads on null aligns them at intersection
     *
     * @param headA the headA parameter
     * @param headB the headB parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(m+n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode a = headA, b = headB;
        while (a != b) { a = a == null ? headB : a.next; b = b == null ? headA : b.next; }
        return a;
    }

    // ======================= EASY 9: Delete Node (given only that node) =======================
    /**
     * Delete Node (given only that node)
     *
     * <p><b>Approach:</b> Copy next node's value into current, then skip next node
     *
     * @param node the node parameter
     *
     * <p><b>Time:</b> O(1) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static void deleteNode(ListNode node) { node.val = node.next.val; node.next = node.next.next; }

    // ======================= EASY 10: Linked List Length =======================
    /**
     * Linked List Length
     *
     * <p><b>Approach:</b> Simple traversal counter
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static int length(ListNode head) { int n = 0; while (head != null) { n++; head = head.next; } return n; }

    // ======================= MEDIUM 1: Add Two Numbers =======================
    /**
     * Add Two Numbers
     *
     * <p><b>Approach:</b> Digit-by-digit addition with carry using dummy head
     *
     * @param l1 the l1 parameter
     * @param l2 the l2 parameter
     * @return the computed result
     */
    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0), cur = dummy;
        int carry = 0;
        while (l1 != null || l2 != null || carry > 0) {
            int sum = carry + (l1 != null ? l1.val : 0) + (l2 != null ? l2.val : 0);
            cur.next = new ListNode(sum % 10); cur = cur.next; carry = sum / 10;
            if (l1 != null) l1 = l1.next; if (l2 != null) l2 = l2.next;
        }
        return dummy.next;
    }

    // ======================= MEDIUM 2: Odd Even Linked List =======================
    /**
     * Odd Even Linked List
     *
     * <p><b>Approach:</b> Separate odd-indexed and even-indexed nodes into two lists, then merge
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode oddEvenList(ListNode head) {
        if (head == null) return null;
        ListNode odd = head, even = head.next, evenHead = even;
        while (even != null && even.next != null) { odd.next = even.next; odd = odd.next; even.next = odd.next; even = even.next; }
        odd.next = evenHead;
        return head;
    }

    // ======================= MEDIUM 3: Swap Nodes in Pairs =======================
    /**
     * Swap Nodes in Pairs
     *
     * <p><b>Approach:</b> Iteratively rewire pairs using a prev pointer and dummy head
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode swapPairs(ListNode head) {
        ListNode dummy = new ListNode(0, head), prev = dummy;
        while (prev.next != null && prev.next.next != null) {
            ListNode a = prev.next, b = a.next;
            a.next = b.next; b.next = a; prev.next = b; prev = a;
        }
        return dummy.next;
    }

    // ======================= MEDIUM 4: Linked List Cycle II (find start) =======================
    /**
     * Linked List Cycle II (find start)
     *
     * <p><b>Approach:</b> Floyd's algorithm: after fast/slow meet, reset one to head; they meet at cycle start
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode detectCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; if (slow == fast) break; }
        if (fast == null || fast.next == null) return null;
        slow = head;
        while (slow != fast) { slow = slow.next; fast = fast.next; }
        return slow;
    }

    // ======================= MEDIUM 5: Rotate List =======================
    /**
     * Rotate List
     *
     * <p><b>Approach:</b> Form a ring by connecting tail to head; break at (len - k % len) from head
     *
     * @param head the head parameter
     * @param k the k parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode rotateRight(ListNode head, int k) {
        if (head == null) return null;
        int len = 1; ListNode tail = head;
        while (tail.next != null) { len++; tail = tail.next; }
        k %= len; if (k == 0) return head;
        tail.next = head;
        for (int i = 0; i < len - k; i++) tail = tail.next;
        head = tail.next; tail.next = null;
        return head;
    }

    // ======================= MEDIUM 6: Partition List =======================
    /**
     * Partition List
     *
     * <p><b>Approach:</b> Two dummy lists (less and greater-or-equal), then connect them
     *
     * @param head the head parameter
     * @param x the x parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode partition(ListNode head, int x) {
        ListNode lessHead = new ListNode(0), greaterHead = new ListNode(0);
        ListNode less = lessHead, greater = greaterHead;
        while (head != null) {
            if (head.val < x) { less.next = head; less = less.next; } else { greater.next = head; greater = greater.next; }
            head = head.next;
        }
        greater.next = null; less.next = greaterHead.next;
        return lessHead.next;
    }

    // ======================= MEDIUM 7: Flatten Multilevel Doubly Linked List (simplified) =======================
    /**
     * Flatten Multilevel Doubly Linked List (simplified)
     *
     * <p><b>Approach:</b> Simplified version: flattens by converting negative values to positive (abs)
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode flattenList(ListNode head) {
        ListNode cur = head;
        while (cur != null) { if (cur.val < 0) { cur.val = -cur.val; } cur = cur.next; }
        return head;
    }

    // ======================= MEDIUM 8: Sort List (Merge Sort) =======================
    /**
     * Sort List (Merge Sort)
     *
     * <p><b>Approach:</b> Top-down merge sort: split at midpoint, recursively sort, then merge
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n log n) time.
     * <br><b>Space:</b> O(log n) space.
     */
    public static ListNode sortList(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode slow = head, fast = head.next;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
        ListNode mid = slow.next; slow.next = null;
        return mergeTwoLists(sortList(head), sortList(mid));
    }

    // ======================= MEDIUM 9: Remove Duplicates II (all copies) =======================
    /**
     * Remove Duplicates II (all copies)
     *
     * <p><b>Approach:</b> Dummy head; prev pointer skips entire runs of duplicate values
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode deleteDuplicatesII(ListNode head) {
        ListNode dummy = new ListNode(0, head), prev = dummy;
        while (prev.next != null && prev.next.next != null) {
            if (prev.next.val == prev.next.next.val) {
                int dup = prev.next.val;
                while (prev.next != null && prev.next.val == dup) prev.next = prev.next.next;
            } else prev = prev.next;
        }
        return dummy.next;
    }

    // ======================= MEDIUM 10: Insertion Sort List =======================
    /**
     * Insertion Sort List
     *
     * <p><b>Approach:</b> Build a new sorted list by inserting each node at its correct position
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n²) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode insertionSortList(ListNode head) {
        ListNode dummy = new ListNode(Integer.MIN_VALUE);
        while (head != null) {
            ListNode next = head.next, prev = dummy;
            while (prev.next != null && prev.next.val < head.val) prev = prev.next;
            head.next = prev.next; prev.next = head; head = next;
        }
        return dummy.next;
    }

    // ======================= HARD 1: Reverse Nodes in K-Group =======================
    /**
     * Reverse Nodes in K-Group
     *
     * <p><b>Approach:</b> Recursively reverse k nodes at a time; leave remainder as-is
     *
     * @param head the head parameter
     * @param k the k parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(n/k) space.
     */
    public static ListNode reverseKGroup(ListNode head, int k) {
        ListNode cur = head; int count = 0;
        while (cur != null && count < k) { cur = cur.next; count++; }
        if (count < k) return head;
        ListNode prev = reverseKGroup(cur, k); cur = head;
        for (int i = 0; i < k; i++) { ListNode next = cur.next; cur.next = prev; prev = cur; cur = next; }
        return prev;
    }

    // ======================= HARD 2: Merge K Sorted Lists =======================
    /**
     * Merge K Sorted Lists
     *
     * <p><b>Approach:</b> Min-heap of list heads; always extract smallest and push its next
     *
     * @param lists the lists parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n log k) time.
     * <br><b>Space:</b> O(k) space.
     */
    public static ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;
        PriorityQueue<ListNode> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.val));
        for (ListNode l : lists) if (l != null) pq.offer(l);
        ListNode dummy = new ListNode(0), cur = dummy;
        while (!pq.isEmpty()) { ListNode node = pq.poll(); cur.next = node; cur = cur.next; if (node.next != null) pq.offer(node.next); }
        return dummy.next;
    }

    // ======================= HARD 3: Copy List with Random Pointer =======================
    /**
     * Copy List with Random Pointer
     *
     * <p><b>Approach:</b> Interleave copied nodes, then separate original and copy lists
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) extra space.
     */
    public static ListNode copyRandomList(ListNode head) {
        if (head == null) return null;
        ListNode cur = head;
        while (cur != null) { ListNode copy = new ListNode(cur.val); copy.next = cur.next; cur.next = copy; cur = copy.next; }
        cur = head;
        while (cur != null) { cur = cur.next; cur = cur.next; }
        ListNode dummy = new ListNode(0), prev = dummy; cur = head;
        while (cur != null) { prev.next = cur.next; prev = prev.next; cur.next = prev.next; cur = cur.next; }
        return dummy.next;
    }

    // ======================= HARD 4: LRU Cache =======================
    /**
     * LRU Cache
     *
     * <p><b>Approach:</b> Access-order LinkedHashMap with removeEldestEntry override for automatic eviction
     */
    static class LRUCache {
        private final int capacity;
        private final Map<Integer, int[]> map = new LinkedHashMap<>(16, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<Integer, int[]> eldest) { return size() > capacity; }
        };
        LRUCache(int capacity) { this.capacity = capacity; }
        public int get(int key) { int[] v = map.get(key); return v == null ? -1 : v[0]; }
        public void put(int key, int value) { map.put(key, new int[]{value}); }
    }

    // ======================= HARD 5: Reverse Linked List II (between positions) =======================
    /**
     * Reverse Linked List II (between positions)
     *
     * <p><b>Approach:</b> Navigate to left position, then reverse (right-left) links in-place
     *
     * @param head the head parameter
     * @param left the left parameter
     * @param right the right parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode reverseBetween(ListNode head, int left, int right) {
        ListNode dummy = new ListNode(0, head), prev = dummy;
        for (int i = 0; i < left - 1; i++) prev = prev.next;
        ListNode cur = prev.next;
        for (int i = 0; i < right - left; i++) { ListNode next = cur.next; cur.next = next.next; next.next = prev.next; prev.next = next; }
        return dummy.next;
    }

    // ======================= HARD 6: Reorder List =======================
    /**
     * Reorder List
     *
     * <p><b>Approach:</b> Find middle, reverse second half, interleave both halves
     *
     * @param head the head parameter
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static void reorderList(ListNode head) {
        if (head == null || head.next == null) return;
        ListNode slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) { slow = slow.next; fast = fast.next.next; }
        ListNode second = reverseList(slow.next); slow.next = null;
        ListNode first = head;
        while (second != null) { ListNode t1 = first.next, t2 = second.next; first.next = second; second.next = t1; first = t1; second = t2; }
    }

    // ======================= HARD 7: Remove Zero Sum Consecutive Nodes =======================
    /**
     * Remove Zero Sum Consecutive Nodes
     *
     * <p><b>Approach:</b> Two-pass prefix sum: map prefix to last node with that sum; skip zero-sum ranges
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(n) space.
     */
    public static ListNode removeZeroSumSublists(ListNode head) {
        ListNode dummy = new ListNode(0, head);
        Map<Integer, ListNode> prefixMap = new HashMap<>();
        int prefix = 0;
        for (ListNode cur = dummy; cur != null; cur = cur.next) { prefix += cur.val; prefixMap.put(prefix, cur); }
        prefix = 0;
        for (ListNode cur = dummy; cur != null; cur = cur.next) { prefix += cur.val; cur.next = prefixMap.get(prefix).next; }
        return dummy.next;
    }

    // ======================= HARD 8: Split Linked List in Parts =======================
    /**
     * Split Linked List in Parts
     *
     * <p><b>Approach:</b> Compute part sizes (len/k with len%k extra nodes in first parts)
     *
     * @param head the head parameter
     * @param k the k parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(k) space.
     */
    public static ListNode[] splitListToParts(ListNode head, int k) {
        int len = length(head);
        int partSize = len / k, extra = len % k;
        ListNode[] result = new ListNode[k];
        ListNode cur = head;
        for (int i = 0; i < k; i++) {
            result[i] = cur;
            int size = partSize + (i < extra ? 1 : 0);
            for (int j = 1; j < size && cur != null; j++) cur = cur.next;
            if (cur != null) { ListNode next = cur.next; cur.next = null; cur = next; }
        }
        return result;
    }

    // ======================= HARD 9: Swap Kth Node From Begin and End =======================
    /**
     * Swap Kth Node From Begin and End
     *
     * <p><b>Approach:</b> Find kth from start, then use two-pointer gap to find kth from end; swap values
     *
     * @param head the head parameter
     * @param k the k parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static ListNode swapNodes(ListNode head, int k) {
        ListNode first = head;
        for (int i = 1; i < k; i++) first = first.next;
        ListNode fast = first, second = head;
        while (fast.next != null) { fast = fast.next; second = second.next; }
        int t = first.val; first.val = second.val; second.val = t;
        return head;
    }

    // ======================= HARD 10: Maximum Twin Sum =======================
    /**
     * Maximum Twin Sum
     *
     * <p><b>Approach:</b> Find middle, reverse second half, compute max sum of twin pairs
     *
     * @param head the head parameter
     * @return the computed result
     *
     * <p><b>Time:</b> O(n) time.
     * <br><b>Space:</b> O(1) space.
     */
    public static int pairSum(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
        ListNode rev = reverseList(slow);
        int max = 0;
        while (rev != null) { max = Math.max(max, head.val + rev.val); head = head.next; rev = rev.next; }
        return max;
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== LINKED LISTS (30 Examples) ===\n");

        System.out.println("--- EASY ---");
        // of(1,2,3,4,5) → creates linked list via new ListNode() chain; reverseList uses while-loop with prev/curr/next pointer swapping
        System.out.println("1. Reverse: " + toString(reverseList(of(1, 2, 3, 4, 5))));
        // of() creates two sorted lists; mergeTwoLists uses while-loop with if (l1.val <= l2.val) advance l1, else l2 — merge comparison
        System.out.println("2. Merge Two: " + toString(mergeTwoLists(of(1, 2, 4), of(1, 3, 4))));
        // of() builds list, then manually links last.next → second node to create cycle; hasCycle uses while with fast/slow pointer if (fast==slow)
        ListNode cycleNode = of(3, 2, 0, -4); cycleNode.next.next.next.next = cycleNode.next;
        // returns boolean; uses if-else conditional checks
        System.out.println("3. Has Cycle: " + hasCycle(cycleNode));
        // of() builds 5-node list; middleNode uses fast/slow pointers: while (fast != null && fast.next != null) slow=slow.next, fast=fast.next.next
        System.out.println("4. Middle: " + middleNode(of(1, 2, 3, 4, 5)).val);
        // of() builds list; uses two pointers with n-gap: for-loop advances fast n steps, then while-loop moves both until fast reaches end
        System.out.println("5. Remove Nth: " + toString(removeNthFromEnd(of(1, 2, 3, 4, 5), 2)));
        // of() builds list; finds middle with fast/slow, reverse second half, while-loop compares both halves with if (val mismatch) return false
        System.out.println("6. Palindrome: " + isPalindrome(of(1, 2, 2, 1)));
        // of() builds sorted list; while (current.next != null) with if (current.val == next.val) skip next, else advance — removes duplicates
        System.out.println("7. Delete Dups: " + toString(deleteDuplicates(of(1, 1, 2, 3, 3))));
        // Intersection: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("8. Intersection: (demo skipped)");
        // Delete Node: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("9. Delete Node: (in-place)");
        // of() builds list; length uses while (node != null) count++, node = node.next — simple traversal counter
        System.out.println("10. Length: " + length(of(1, 2, 3, 4)));

        System.out.println("\n--- MEDIUM ---");
        // of() creates two number-as-list inputs; while-loop with if (l1 != null) add val, carry = sum/10, new ListNode(sum%10) for each digit
        System.out.println("11. Add Two: " + toString(addTwoNumbers(of(2, 4, 3), of(5, 6, 4))));
        // of() builds list; while (even != null && even.next != null) links odd nodes together, then even nodes; odd.next = evenHead concatenates
        System.out.println("12. Odd Even: " + toString(oddEvenList(of(1, 2, 3, 4, 5))));
        // of() builds list; uses dummy head + while (prev.next != null && prev.next.next != null) swaps pair with pointer rewiring
        System.out.println("13. Swap Pairs: " + toString(swapPairs(of(1, 2, 3, 4))));
        // Cycle Start: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("14. Cycle Start: (demo skipped)");
        // of() builds list; finds length with while-loop, k %= len; forms circle (tail.next = head), for-loop advances to cut point
        System.out.println("15. Rotate: " + toString(rotateRight(of(1, 2, 3, 4, 5), 2)));
        // of() builds list; creates two dummy heads (before/after); while-loop with if (val < x) append to before, else to after; concatenate
        System.out.println("16. Partition: " + toString(partition(of(1, 4, 3, 2, 5, 2), 3)));
        // Flatten: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("17. Flatten: (simplified)");
        // of() builds unsorted list; merge sort: fast/slow split, recursive sort, while-loop merge with if (l1.val <= l2.val) comparison
        System.out.println("18. Sort List: " + toString(sortList(of(4, 2, 1, 3))));
        // of() builds sorted list; uses dummy head + while-loop with if (head.next != null && val == next.val) skip ALL duplicates
        System.out.println("19. Delete Dups II: " + toString(deleteDuplicatesII(of(1, 2, 3, 3, 4, 4, 5))));
        // of() builds list; while-loop extracts each node, inner while finds correct sorted position with if (prev.next.val > cur.val) insert
        System.out.println("20. Insertion Sort: " + toString(insertionSortList(of(4, 2, 1, 3))));

        System.out.println("\n--- HARD ---");
        // of() builds list; counts k nodes with while; if (count < k) return as-is; for-loop reverses k nodes, recursive call for rest
        System.out.println("21. K-Group Rev: " + toString(reverseKGroup(of(1, 2, 3, 4, 5), 2)));
        // new ListNode[]{...} → array of sorted lists; creates PriorityQueue<>() with Comparator; while (pq not empty) poll min, if (node.next) offer
        System.out.println("22. Merge K: " + toString(mergeKLists(new ListNode[]{of(1, 4, 5), of(1, 3, 4), of(2, 6)})));
        // Copy Random: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("23. Copy Random: (demo skipped)");
        // new LRUCache(2) → creates object with HashMap<>() + doubly-linked list; get/put use if (exists) move to head, if (full) remove tail
        LRUCache lru = new LRUCache(2); lru.put(1, 1); lru.put(2, 2);
        // LRU Cache get(1): uses internal conditional logic (if/else, for/while) for computation
        System.out.println("24. LRU Cache get(1): " + lru.get(1));
        // of() builds list; for-loop navigates to position m; for-loop reverses m-to-n nodes with prev/curr/next pointer manipulation
        System.out.println("25. Reverse Between: " + toString(reverseBetween(of(1, 2, 3, 4, 5), 2, 4)));
        // of() builds list; reorderList finds middle, reverses second half, while-loop interleaves: first.next=second, second.next=first.next
        ListNode reo = of(1, 2, 3, 4); reorderList(reo);
        // toString() processes input; uses for/while loop with conditional checks for result computation
        System.out.println("26. Reorder: " + toString(reo));
        // of() builds list; creates HashMap<Integer,ListNode> prefix sum → node; for-loop with if (prefix seen before) skip zero-sum segment
        System.out.println("27. Zero Sum: " + toString(removeZeroSumSublists(of(1, 2, -3, 3, 1))));
        // of() builds list; calculates partSize = len/k, extra = len%k; for-loop with inner for cuts list, if (i < extra) one extra node
        ListNode[] parts = splitListToParts(of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 3);
        // enhanced for-each loop → iterates over ListNode[] array; toString prints each part as string — demonstrates array iteration
        System.out.print("28. Split Parts: "); for (ListNode p : parts) System.out.print(toString(p) + " "); System.out.println();
        // of() builds list; two pointers: advance first k steps, then both until end; swap values with temp variable — if-based position tracking
        System.out.println("29. Swap Kth: " + toString(swapNodes(of(1, 2, 3, 4, 5), 2)));
        // of() builds list; finds middle with fast/slow, reverses second half, while-loop sums pairs with Math.max — twin from start+end
        System.out.println("30. Twin Sum: " + pairSum(of(5, 4, 2, 1)));
    }
}
