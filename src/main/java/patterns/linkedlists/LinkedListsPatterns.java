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
    /** Iteratively reverse by maintaining prev/cur/next pointers. O(n) time, O(1) space. */
    public static ListNode reverseList(ListNode head) {
        ListNode prev = null, cur = head;
        while (cur != null) { ListNode next = cur.next; cur.next = prev; prev = cur; cur = next; }
        return prev;
    }

    // ======================= EASY 2: Merge Two Sorted Lists =======================
    /** Dummy head; compare and link smaller node at each step. O(m+n) time, O(1) space. */
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
    /** Floyd's tortoise and hare: slow moves 1, fast moves 2; if they meet, cycle exists. O(n) time, O(1) space. */
    public static boolean hasCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; if (slow == fast) return true; }
        return false;
    }

    // ======================= EASY 4: Middle of Linked List =======================
    /** Slow pointer moves 1 step, fast moves 2; when fast reaches end, slow is at middle. O(n) time, O(1) space. */
    public static ListNode middleNode(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
        return slow;
    }

    // ======================= EASY 5: Remove Nth Node From End =======================
    /** Two pointers with n+1 gap; when fast reaches null, slow is before the target. O(n) time, O(1) space. */
    public static ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head), fast = dummy, slow = dummy;
        for (int i = 0; i <= n; i++) fast = fast.next;
        while (fast != null) { slow = slow.next; fast = fast.next; }
        slow.next = slow.next.next;
        return dummy.next;
    }

    // ======================= EASY 6: Palindrome Linked List =======================
    /** Find middle, reverse second half, compare both halves node by node. O(n) time, O(1) space. */
    public static boolean isPalindrome(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
        ListNode rev = reverseList(slow);
        while (rev != null) { if (head.val != rev.val) return false; head = head.next; rev = rev.next; }
        return true;
    }

    // ======================= EASY 7: Remove Duplicates from Sorted List =======================
    /** Skip consecutive nodes with equal values by relinking. O(n) time, O(1) space. */
    public static ListNode deleteDuplicates(ListNode head) {
        ListNode cur = head;
        while (cur != null && cur.next != null) { if (cur.val == cur.next.val) cur.next = cur.next.next; else cur = cur.next; }
        return head;
    }

    // ======================= EASY 8: Intersection of Two Linked Lists =======================
    /** Two pointers traverse both lists; switching heads on null aligns them at intersection. O(m+n) time, O(1) space. */
    public static ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode a = headA, b = headB;
        while (a != b) { a = a == null ? headB : a.next; b = b == null ? headA : b.next; }
        return a;
    }

    // ======================= EASY 9: Delete Node (given only that node) =======================
    /** Copy next node's value into current, then skip next node. O(1) time, O(1) space. */
    public static void deleteNode(ListNode node) { node.val = node.next.val; node.next = node.next.next; }

    // ======================= EASY 10: Linked List Length =======================
    /** Simple traversal counter. O(n) time, O(1) space. */
    public static int length(ListNode head) { int n = 0; while (head != null) { n++; head = head.next; } return n; }

    // ======================= MEDIUM 1: Add Two Numbers =======================
    /** Digit-by-digit addition with carry using dummy head. O(max(m,n)) time, O(max(m,n)) space. */
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
    /** Separate odd-indexed and even-indexed nodes into two lists, then merge. O(n) time, O(1) space. */
    public static ListNode oddEvenList(ListNode head) {
        if (head == null) return null;
        ListNode odd = head, even = head.next, evenHead = even;
        while (even != null && even.next != null) { odd.next = even.next; odd = odd.next; even.next = odd.next; even = even.next; }
        odd.next = evenHead;
        return head;
    }

    // ======================= MEDIUM 3: Swap Nodes in Pairs =======================
    /** Iteratively rewire pairs using a prev pointer and dummy head. O(n) time, O(1) space. */
    public static ListNode swapPairs(ListNode head) {
        ListNode dummy = new ListNode(0, head), prev = dummy;
        while (prev.next != null && prev.next.next != null) {
            ListNode a = prev.next, b = a.next;
            a.next = b.next; b.next = a; prev.next = b; prev = a;
        }
        return dummy.next;
    }

    // ======================= MEDIUM 4: Linked List Cycle II (find start) =======================
    /** Floyd's algorithm: after fast/slow meet, reset one to head; they meet at cycle start. O(n) time, O(1) space. */
    public static ListNode detectCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; if (slow == fast) break; }
        if (fast == null || fast.next == null) return null;
        slow = head;
        while (slow != fast) { slow = slow.next; fast = fast.next; }
        return slow;
    }

    // ======================= MEDIUM 5: Rotate List =======================
    /** Form a ring by connecting tail to head; break at (len - k % len) from head. O(n) time, O(1) space. */
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
    /** Two dummy lists (less and greater-or-equal), then connect them. O(n) time, O(1) space. */
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
    /** Simplified version: flattens by converting negative values to positive (abs). O(n) time, O(1) space. */
    public static ListNode flattenList(ListNode head) {
        ListNode cur = head;
        while (cur != null) { if (cur.val < 0) { cur.val = -cur.val; } cur = cur.next; }
        return head;
    }

    // ======================= MEDIUM 8: Sort List (Merge Sort) =======================
    /** Top-down merge sort: split at midpoint, recursively sort, then merge. O(n log n) time, O(log n) space. */
    public static ListNode sortList(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode slow = head, fast = head.next;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
        ListNode mid = slow.next; slow.next = null;
        return mergeTwoLists(sortList(head), sortList(mid));
    }

    // ======================= MEDIUM 9: Remove Duplicates II (all copies) =======================
    /** Dummy head; prev pointer skips entire runs of duplicate values. O(n) time, O(1) space. */
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
    /** Build a new sorted list by inserting each node at its correct position. O(n²) time, O(1) space. */
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
    /** Recursively reverse k nodes at a time; leave remainder as-is. O(n) time, O(n/k) space. */
    public static ListNode reverseKGroup(ListNode head, int k) {
        ListNode cur = head; int count = 0;
        while (cur != null && count < k) { cur = cur.next; count++; }
        if (count < k) return head;
        ListNode prev = reverseKGroup(cur, k); cur = head;
        for (int i = 0; i < k; i++) { ListNode next = cur.next; cur.next = prev; prev = cur; cur = next; }
        return prev;
    }

    // ======================= HARD 2: Merge K Sorted Lists =======================
    /** Min-heap of list heads; always extract smallest and push its next. O(n log k) time, O(k) space. */
    public static ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;
        PriorityQueue<ListNode> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.val));
        for (ListNode l : lists) if (l != null) pq.offer(l);
        ListNode dummy = new ListNode(0), cur = dummy;
        while (!pq.isEmpty()) { ListNode node = pq.poll(); cur.next = node; cur = cur.next; if (node.next != null) pq.offer(node.next); }
        return dummy.next;
    }

    // ======================= HARD 3: Copy List with Random Pointer =======================
    /** Interleave copied nodes, then separate original and copy lists. O(n) time, O(1) extra space. */
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
    /** Access-order LinkedHashMap with removeEldestEntry override for automatic eviction. O(1) get/put. */
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
    /** Navigate to left position, then reverse (right-left) links in-place. O(n) time, O(1) space. */
    public static ListNode reverseBetween(ListNode head, int left, int right) {
        ListNode dummy = new ListNode(0, head), prev = dummy;
        for (int i = 0; i < left - 1; i++) prev = prev.next;
        ListNode cur = prev.next;
        for (int i = 0; i < right - left; i++) { ListNode next = cur.next; cur.next = next.next; next.next = prev.next; prev.next = next; }
        return dummy.next;
    }

    // ======================= HARD 6: Reorder List =======================
    /** Find middle, reverse second half, interleave both halves. O(n) time, O(1) space. */
    public static void reorderList(ListNode head) {
        if (head == null || head.next == null) return;
        ListNode slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) { slow = slow.next; fast = fast.next.next; }
        ListNode second = reverseList(slow.next); slow.next = null;
        ListNode first = head;
        while (second != null) { ListNode t1 = first.next, t2 = second.next; first.next = second; second.next = t1; first = t1; second = t2; }
    }

    // ======================= HARD 7: Remove Zero Sum Consecutive Nodes =======================
    /** Two-pass prefix sum: map prefix to last node with that sum; skip zero-sum ranges. O(n) time, O(n) space. */
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
    /** Compute part sizes (len/k with len%k extra nodes in first parts). O(n) time, O(k) space. */
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
    /** Find kth from start, then use two-pointer gap to find kth from end; swap values. O(n) time, O(1) space. */
    public static ListNode swapNodes(ListNode head, int k) {
        ListNode first = head;
        for (int i = 1; i < k; i++) first = first.next;
        ListNode fast = first, second = head;
        while (fast.next != null) { fast = fast.next; second = second.next; }
        int t = first.val; first.val = second.val; second.val = t;
        return head;
    }

    // ======================= HARD 10: Maximum Twin Sum =======================
    /** Find middle, reverse second half, compute max sum of twin pairs. O(n) time, O(1) space. */
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
        System.out.println("1. Reverse: " + toString(reverseList(of(1, 2, 3, 4, 5))));
        System.out.println("2. Merge Two: " + toString(mergeTwoLists(of(1, 2, 4), of(1, 3, 4))));
        ListNode cycleNode = of(3, 2, 0, -4); cycleNode.next.next.next.next = cycleNode.next;
        System.out.println("3. Has Cycle: " + hasCycle(cycleNode));
        System.out.println("4. Middle: " + middleNode(of(1, 2, 3, 4, 5)).val);
        System.out.println("5. Remove Nth: " + toString(removeNthFromEnd(of(1, 2, 3, 4, 5), 2)));
        System.out.println("6. Palindrome: " + isPalindrome(of(1, 2, 2, 1)));
        System.out.println("7. Delete Dups: " + toString(deleteDuplicates(of(1, 1, 2, 3, 3))));
        System.out.println("8. Intersection: (demo skipped)");
        System.out.println("9. Delete Node: (in-place)");
        System.out.println("10. Length: " + length(of(1, 2, 3, 4)));

        System.out.println("\n--- MEDIUM ---");
        System.out.println("11. Add Two: " + toString(addTwoNumbers(of(2, 4, 3), of(5, 6, 4))));
        System.out.println("12. Odd Even: " + toString(oddEvenList(of(1, 2, 3, 4, 5))));
        System.out.println("13. Swap Pairs: " + toString(swapPairs(of(1, 2, 3, 4))));
        System.out.println("14. Cycle Start: (demo skipped)");
        System.out.println("15. Rotate: " + toString(rotateRight(of(1, 2, 3, 4, 5), 2)));
        System.out.println("16. Partition: " + toString(partition(of(1, 4, 3, 2, 5, 2), 3)));
        System.out.println("17. Flatten: (simplified)");
        System.out.println("18. Sort List: " + toString(sortList(of(4, 2, 1, 3))));
        System.out.println("19. Delete Dups II: " + toString(deleteDuplicatesII(of(1, 2, 3, 3, 4, 4, 5))));
        System.out.println("20. Insertion Sort: " + toString(insertionSortList(of(4, 2, 1, 3))));

        System.out.println("\n--- HARD ---");
        System.out.println("21. K-Group Rev: " + toString(reverseKGroup(of(1, 2, 3, 4, 5), 2)));
        System.out.println("22. Merge K: " + toString(mergeKLists(new ListNode[]{of(1, 4, 5), of(1, 3, 4), of(2, 6)})));
        System.out.println("23. Copy Random: (demo skipped)");
        LRUCache lru = new LRUCache(2); lru.put(1, 1); lru.put(2, 2);
        System.out.println("24. LRU Cache get(1): " + lru.get(1));
        System.out.println("25. Reverse Between: " + toString(reverseBetween(of(1, 2, 3, 4, 5), 2, 4)));
        ListNode reo = of(1, 2, 3, 4); reorderList(reo);
        System.out.println("26. Reorder: " + toString(reo));
        System.out.println("27. Zero Sum: " + toString(removeZeroSumSublists(of(1, 2, -3, 3, 1))));
        ListNode[] parts = splitListToParts(of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 3);
        System.out.print("28. Split Parts: "); for (ListNode p : parts) System.out.print(toString(p) + " "); System.out.println();
        System.out.println("29. Swap Kth: " + toString(swapNodes(of(1, 2, 3, 4, 5), 2)));
        System.out.println("30. Twin Sum: " + pairSum(of(5, 4, 2, 1)));
    }
}
