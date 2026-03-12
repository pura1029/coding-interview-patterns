package patterns.linkedlistreversal;

import java.util.*;

/**
 * PATTERN 5: LINKED LIST IN-PLACE REVERSAL
 *
 * Reverse linked list nodes by re-wiring pointers (prev/curr/next) without extra structures.
 *
 * 30 Examples: 10 Easy, 10 Medium, 10 Hard
 */
public class LinkedListReversalPatterns {

    static class ListNode {
        int val; ListNode next;
        ListNode(int v) { val = v; }
        ListNode(int v, ListNode n) { val = v; next = n; }
    }
    private static ListNode build(int... v) { ListNode d = new ListNode(0), c = d; for (int x : v) { c.next = new ListNode(x); c = c.next; } return d.next; }
    private static String str(ListNode h) { StringBuilder sb = new StringBuilder("["); while (h != null) { sb.append(h.val); if (h.next != null) sb.append(","); h = h.next; } return sb.append("]").toString(); }

    /**
     * Reverse Linked List
     *
     * <p><b>Approach:</b> Reverse Linked List. Iterative prev/curr/next swap.
     *
     * @param head the head parameter
     * @return the computed result
     */
    public static ListNode reverseList(ListNode head) { ListNode p = null, c = head; while (c != null) { ListNode n = c.next; c.next = p; p = c; c = n; } return p; }

    /**
     * Reverse Linked List (Recursive)
     *
     * <p><b>Approach:</b> Reverse Linked List (Recursive). Recurse to end, reverse on backtrack.
     *
     * @param head the head parameter
     * @return the computed result
     */
    public static ListNode reverseListRecursive(ListNode head) { if (head == null || head.next == null) return head; ListNode r = reverseListRecursive(head.next); head.next.next = head; head.next = null; return r; }

    /**
     * Remove Duplicates from Sorted List
     *
     * <p><b>Approach:</b> Remove Duplicates from Sorted List. Skip consecutive equal nodes.
     *
     * @param head the head parameter
     * @return the computed result
     */
    public static ListNode deleteDuplicates(ListNode head) { ListNode c = head; while (c != null && c.next != null) { if (c.val == c.next.val) c.next = c.next.next; else c = c.next; } return head; }

    /**
     * Remove Linked List Elements (remove all val)
     *
     * <p><b>Approach:</b> Remove Linked List Elements (remove all val). Dummy head, skip matching values.
     *
     * @param head the head parameter
     * @param val the val parameter
     * @return the computed result
     */
    public static ListNode removeElements(ListNode head, int val) { ListNode d = new ListNode(0, head), c = d; while (c.next != null) { if (c.next.val == val) c.next = c.next.next; else c = c.next; } return d.next; }

    /**
     * Merge Two Sorted Lists
     *
     * <p><b>Approach:</b> Merge Two Sorted Lists. Compare heads, append smaller.
     *
     * @param l1 the l1 parameter
     * @param l2 the l2 parameter
     * @return the computed result
     */
    public static ListNode mergeTwoLists(ListNode l1, ListNode l2) { ListNode d = new ListNode(0), c = d; while (l1 != null && l2 != null) { if (l1.val <= l2.val) { c.next = l1; l1 = l1.next; } else { c.next = l2; l2 = l2.next; } c = c.next; } c.next = l1 != null ? l1 : l2; return d.next; }

    /**
     * Convert Binary Number in LL to Integer
     *
     * <p><b>Approach:</b> Convert Binary Number in LL to Integer. Shift result left, add bit.
     *
     * @param head the head parameter
     * @return the computed result
     */
    public static int getDecimalValue(ListNode head) { int r = 0; while (head != null) { r = r * 2 + head.val; head = head.next; } return r; }

    /**
     * Delete Node in a Linked List (given only node)
     *
     * <p><b>Approach:</b> Delete Node in a Linked List (given only node). Copy next value, skip next node.
     *
     * @param node the node parameter
     */
    public static void deleteNode(ListNode node) { node.val = node.next.val; node.next = node.next.next; }

    /**
     * Linked List Length
     *
     * <p><b>Approach:</b> Linked List Length. Count nodes in traversal.
     *
     * @param head the head parameter
     * @return the computed result
     */
    public static int getLength(ListNode head) { int n = 0; while (head != null) { n++; head = head.next; } return n; }

    /**
     * Get Kth Node from End
     *
     * <p><b>Approach:</b> Get Kth Node from End. Two-pointer with k-gap.
     *
     * @param head the head parameter
     * @param k the k parameter
     * @return the computed result
     */
    public static ListNode kthFromEnd(ListNode head, int k) { ListNode f = head; for (int i = 0; i < k; i++) f = f.next; ListNode s = head; while (f != null) { s = s.next; f = f.next; } return s; }

    /**
     * Insert at Position
     *
     * <p><b>Approach:</b> Insert at Position. Traverse to position, rewire.
     *
     * @param head the head parameter
     * @param val the val parameter
     * @param pos the pos parameter
     * @return the computed result
     */
    public static ListNode insertAtPosition(ListNode head, int val, int pos) {
        ListNode d = new ListNode(0, head), c = d;
        for (int i = 0; i < pos && c != null; i++) c = c.next;
        if (c != null) { c.next = new ListNode(val, c.next); }
        return d.next;
    }

    /**
     * Reverse Linked List II (between positions)
     *
     * <p><b>Approach:</b> Reverse Linked List II (between positions). Locate start, reverse sub-list.
     *
     * @param head the head parameter
     * @param left the left parameter
     * @param right the right parameter
     * @return the computed result
     */
    public static ListNode reverseBetween(ListNode head, int left, int right) {
        ListNode d = new ListNode(0, head), p = d;
        for (int i = 1; i < left; i++) p = p.next;
        ListNode c = p.next;
        for (int i = 0; i < right - left; i++) { ListNode n = c.next; c.next = n.next; n.next = p.next; p.next = n; }
        return d.next;
    }

    /**
     * Swap Nodes in Pairs
     *
     * <p><b>Approach:</b> Swap Nodes in Pairs. Swap adjacent pairs iteratively.
     *
     * @param head the head parameter
     * @return the computed result
     */
    public static ListNode swapPairs(ListNode head) {
        ListNode d = new ListNode(0, head), p = d;
        while (p.next != null && p.next.next != null) { ListNode a = p.next, b = a.next; a.next = b.next; b.next = a; p.next = b; p = a; }
        return d.next;
    }

    /**
     * Palindrome Linked List (reverse second half)
     *
     * <p><b>Approach:</b> Palindrome Linked List (reverse second half). Reverse second half, compare.
     *
     * @param head the head parameter
     * @return the computed result
     */
    public static boolean isPalindrome(ListNode head) {
        ListNode s = head, f = head;
        while (f != null && f.next != null) { s = s.next; f = f.next.next; }
        ListNode rev = reverseList(s);
        while (rev != null) { if (head.val != rev.val) return false; head = head.next; rev = rev.next; }
        return true;
    }

    /**
     * Odd Even Linked List
     *
     * <p><b>Approach:</b> Odd Even Linked List. Separate odd/even indexed nodes.
     *
     * @param head the head parameter
     * @return the computed result
     */
    public static ListNode oddEvenList(ListNode head) {
        if (head == null) return null;
        ListNode odd = head, even = head.next, evenH = even;
        while (even != null && even.next != null) { odd.next = even.next; odd = odd.next; even.next = odd.next; even = even.next; }
        odd.next = evenH; return head;
    }

    /**
     * Rotate List
     *
     * <p><b>Approach:</b> Rotate List. Form cycle, break at new tail.
     *
     * @param head the head parameter
     * @param k the k parameter
     * @return the computed result
     */
    public static ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null) return head;
        int len = 1; ListNode tail = head; while (tail.next != null) { len++; tail = tail.next; }
        k %= len; if (k == 0) return head;
        tail.next = head;
        for (int i = 0; i < len - k; i++) tail = tail.next;
        head = tail.next; tail.next = null;
        return head;
    }

    /**
     * Partition List
     *
     * <p><b>Approach:</b> Partition List. Two lists: less-than and greater-equal.
     *
     * @param head the head parameter
     * @param x the x parameter
     * @return the computed result
     */
    public static ListNode partition(ListNode head, int x) {
        ListNode bd = new ListNode(0), ad = new ListNode(0), b = bd, a = ad;
        while (head != null) { if (head.val < x) { b.next = head; b = b.next; } else { a.next = head; a = a.next; } head = head.next; }
        a.next = null; b.next = ad.next; return bd.next;
    }

    /**
     * Remove Duplicates from Sorted List II
     *
     * <p><b>Approach:</b> Remove Duplicates from Sorted List II. Skip all copies of duplicate values.
     *
     * @param head the head parameter
     * @return the computed result
     */
    public static ListNode deleteDuplicatesII(ListNode head) {
        ListNode d = new ListNode(0, head), p = d;
        while (head != null) {
            if (head.next != null && head.val == head.next.val) { while (head.next != null && head.val == head.next.val) head = head.next; p.next = head.next; } else p = p.next;
            head = head.next;
        }
        return d.next;
    }

    /**
     * Add Two Numbers
     *
     * <p><b>Approach:</b> Add Two Numbers. Digit-by-digit addition with carry.
     *
     * @param l1 the l1 parameter
     * @param l2 the l2 parameter
     * @return the computed result
     */
    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode d = new ListNode(0), c = d; int carry = 0;
        while (l1 != null || l2 != null || carry > 0) {
            int s = carry; if (l1 != null) { s += l1.val; l1 = l1.next; } if (l2 != null) { s += l2.val; l2 = l2.next; }
            c.next = new ListNode(s % 10); carry = s / 10; c = c.next;
        }
        return d.next;
    }

    /**
     * Insertion Sort List
     *
     * <p><b>Approach:</b> Insertion Sort List. Insert each node into sorted portion.
     *
     * @param head the head parameter
     * @return the computed result
     */
    public static ListNode insertionSortList(ListNode head) {
        ListNode d = new ListNode(Integer.MIN_VALUE);
        while (head != null) {
            ListNode n = head.next, p = d;
            while (p.next != null && p.next.val < head.val) p = p.next;
            head.next = p.next; p.next = head; head = n;
        }
        return d.next;
    }

    /**
     * Next Greater Node In Linked List
     *
     * <p><b>Approach:</b> Next Greater Node In Linked List. Stack to find next greater value.
     *
     * @param head the head parameter
     * @return the computed result
     */
    public static int[] nextLargerNodes(ListNode head) {
        List<Integer> vals = new ArrayList<>();
        while (head != null) { vals.add(head.val); head = head.next; }
        int[] result = new int[vals.size()];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < vals.size(); i++) {
            while (!stack.isEmpty() && vals.get(stack.peek()) < vals.get(i)) result[stack.pop()] = vals.get(i);
            stack.push(i);
        }
        return result;
    }

    /**
     * Reverse Nodes in K-Group
     *
     * <p><b>Approach:</b> Reverse Nodes in K-Group. Reverse every k nodes, handle remainder.
     *
     * @param head the head parameter
     * @param k the k parameter
     * @return the computed result
     */
    public static ListNode reverseKGroup(ListNode head, int k) {
        ListNode d = new ListNode(0, head), gp = d;
        while (true) {
            ListNode kth = gp; for (int i = 0; i < k; i++) { kth = kth.next; if (kth == null) return d.next; }
            ListNode gn = kth.next, p = gn, c = gp.next;
            while (c != gn) { ListNode n = c.next; c.next = p; p = c; c = n; }
            ListNode t = gp.next; gp.next = kth; gp = t;
        }
    }

    /**
     * Merge K Sorted Lists
     *
     * <p><b>Approach:</b> Merge K Sorted Lists. Divide and conquer or min-heap.
     *
     * @param lists the lists parameter
     * @return the computed result
     */
    public static ListNode mergeKLists(ListNode[] lists) {
        PriorityQueue<ListNode> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.val));
        for (ListNode l : lists) if (l != null) pq.offer(l);
        ListNode d = new ListNode(0), c = d;
        while (!pq.isEmpty()) { ListNode n = pq.poll(); c.next = n; c = c.next; if (n.next != null) pq.offer(n.next); }
        return d.next;
    }

    /**
     * Reverse Alternating K-Group
     *
     * <p><b>Approach:</b> Reverse Alternating K-Group. Reverse odd groups, skip even groups.
     *
     * @param head the head parameter
     * @param k the k parameter
     * @return the computed result
     */
    public static ListNode reverseAlternateKGroup(ListNode head, int k) {
        ListNode c = head, p = null; int cnt = 0; ListNode t = head; while (t != null) { cnt++; t = t.next; }
        if (cnt < k) return head;
        p = null; c = head;
        for (int i = 0; i < k && c != null; i++) { ListNode n = c.next; c.next = p; p = c; c = n; }
        head.next = c;
        for (int i = 0; i < k && c != null; i++) c = c.next;
        if (c != null) { ListNode prev = head; while (prev.next != c) prev = prev.next; prev.next = reverseAlternateKGroup(c, k); }
        return p;
    }

    /**
     * Sort List
     *
     * <p><b>Approach:</b> Sort List. Split at mid, merge sorted halves.
     *
     * @param head the head parameter
     * @return the computed result
     */
    public static ListNode sortList(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode s = head, f = head.next;
        while (f != null && f.next != null) { s = s.next; f = f.next.next; }
        ListNode mid = s.next; s.next = null;
        return mergeTwoLists(sortList(head), sortList(mid));
    }

    /**
     * Reorder List
     *
     * <p><b>Approach:</b> Reorder List. Find mid, reverse second half, interleave.
     *
     * @param head the head parameter
     */
    public static void reorderList(ListNode head) {
        if (head == null || head.next == null) return;
        ListNode s = head, f = head;
        while (f.next != null && f.next.next != null) { s = s.next; f = f.next.next; }
        ListNode sec = reverseList(s.next); s.next = null;
        ListNode first = head;
        while (sec != null) { ListNode t1 = first.next, t2 = sec.next; first.next = sec; sec.next = t1; first = t1; sec = t2; }
    }

    /**
     * Flatten Nested List Iterator (linked list simulation)
     *
     * <p><b>Approach:</b> Flatten Nested List Iterator (linked list simulation). Recursively flatten nested structure.
     *
     * @param nestedList the nestedList parameter
     * @return the computed result
     */
    public static List<Integer> flattenNestedList(List<Object> nestedList) {
        List<Integer> result = new ArrayList<>();
        for (Object item : nestedList) {
            if (item instanceof Integer) result.add((Integer) item);
            else result.addAll(flattenNestedList((List<Object>) item));
        }
        return result;
    }

    /**
     * Reverse Linked List in Groups of Variable Size
     *
     * <p><b>Approach:</b> Reverse Linked List in Groups of Variable Size. Different group sizes per iteration.
     *
     * @param head the head parameter
     * @param groups the groups parameter
     * @return the computed result
     */
    public static ListNode reverseInGroups(ListNode head, int[] groups) {
        ListNode d = new ListNode(0, head), prev = d;
        int gi = 0;
        while (prev.next != null && gi < groups.length) {
            int k = groups[gi++];
            ListNode kth = prev; int count = 0;
            while (kth.next != null && count < k) { kth = kth.next; count++; }
            ListNode gn = kth.next, p = gn, c = prev.next;
            for (int i = 0; i < count; i++) { ListNode n = c.next; c.next = p; p = c; c = n; }
            ListNode tmp = prev.next; prev.next = kth; prev = tmp;
        }
        return d.next;
    }

    /**
     * Swap Kth Node from Begin and End
     *
     * <p><b>Approach:</b> Swap Kth Node from Begin and End. Find kth from start and end, swap values.
     *
     * @param head the head parameter
     * @param k the k parameter
     * @return the computed result
     */
    public static ListNode swapNodes(ListNode head, int k) {
        ListNode f = head; for (int i = 1; i < k; i++) f = f.next;
        ListNode first = f, second = head;
        while (f.next != null) { f = f.next; second = second.next; }
        int tmp = first.val; first.val = second.val; second.val = tmp;
        return head;
    }

    /**
     * Remove Zero Sum Consecutive Nodes
     *
     * <p><b>Approach:</b> Remove Zero Sum Consecutive Nodes. Prefix sum to detect zero-sum segments.
     *
     * @param head the head parameter
     * @return the computed result
     */
    public static ListNode removeZeroSumSublists(ListNode head) {
        ListNode d = new ListNode(0, head);
        Map<Integer, ListNode> prefixMap = new HashMap<>();
        int sum = 0;
        for (ListNode c = d; c != null; c = c.next) { sum += c.val; prefixMap.put(sum, c); }
        sum = 0;
        for (ListNode c = d; c != null; c = c.next) { sum += c.val; c.next = prefixMap.get(sum).next; }
        return d.next;
    }

    /**
     * Maximum Twin Sum of a Linked List
     *
     * <p><b>Approach:</b> Maximum Twin Sum of a Linked List. Reverse second half, sum with first half.
     *
     * @param head the head parameter
     * @return the computed result
     */
    public static int pairSum(ListNode head) {
        ListNode s = head, f = head;
        while (f != null && f.next != null) { s = s.next; f = f.next.next; }
        ListNode rev = reverseList(s); int max = 0;
        while (rev != null) { max = Math.max(max, head.val + rev.val); head = head.next; rev = rev.next; }
        return max;
    }

    public static void main(String[] args) {
        System.out.println("=== LINKED LIST IN-PLACE REVERSAL (30 Examples) ===\n");
        // --- EASY (1-10) ---
        System.out.println("--- EASY ---");
        // build() creates new ListNode chain; reverseList uses while-loop: prev/curr/next pointer swap — head.next = prev reverses direction
        System.out.println("1. Reverse: " + str(reverseList(build(1,2,3,4,5))));
        // reverseListRecursive: if (head == null || head.next == null) return head; recursive call, then head.next.next = head, head.next = null — base case + pointer swap
        System.out.println("2. Reverse Recursive: " + str(reverseListRecursive(build(1,2,3))));
        // while (curr.next != null): if (curr.val == curr.next.val) curr.next = curr.next.next (skip), else curr = curr.next — conditional skip
        System.out.println("3. Delete Dups: " + str(deleteDuplicates(build(1,1,2,3,3))));
        // new ListNode(0) dummy head; while (curr.next != null): if (curr.next.val == val) unlink, else advance — conditional removal
        System.out.println("4. Remove Elements: " + str(removeElements(build(1,2,6,3,4,5,6), 6)));
        // while (l1 != null && l2 != null): if (l1.val <= l2.val) take l1, else take l2 — merge comparison with conditional selection
        System.out.println("5. Merge Two: " + str(mergeTwoLists(build(1,2,4), build(1,3,4))));
        // while (head != null): result = result * 2 + head.val; head = head.next — no conditional, pure accumulation
        System.out.println("6. Binary to Int: " + getDecimalValue(build(1,0,1)));
        // deleteNode: node.val = node.next.val; node.next = node.next.next — copies next node's data and bypasses it (no prev pointer needed)
        ListNode dn = build(4,5,1,9); deleteNode(dn.next); System.out.println("7. Delete Node: " + str(dn));
        // while (node != null) count++; node = node.next — simple traversal counter with while-loop condition
        System.out.println("8. Length: " + getLength(build(1,2,3,4)));
        // two pointers: advance fast k steps, then while (fast != null) move both — gap technique finds kth from end
        System.out.println("9. Kth from End: " + kthFromEnd(build(1,2,3,4,5), 2).val);
        // for-loop to position; new ListNode(val) inserted: newNode.next = curr.next, curr.next = newNode — pointer insertion
        System.out.println("10. Insert at 2: " + str(insertAtPosition(build(1,2,4,5), 3, 2)));

        // --- MEDIUM (11-20) ---
        System.out.println("\n--- MEDIUM ---");
        // for-loop navigates to position m-1; for-loop (m to n) reverses sublist: prev/curr/next pointer swap within bounds
        System.out.println("11. Reverse Between: " + str(reverseBetween(build(1,2,3,4,5), 2, 4)));
        // new ListNode(0) dummy; while (curr.next && curr.next.next) swap pair: rewire a.next→b, b.next→a — pairwise pointer manipulation
        System.out.println("12. Swap Pairs: " + str(swapPairs(build(1,2,3,4))));
        // fast/slow find middle; reverse second half; while-compare: if (val mismatch) return false — three-phase algorithm
        System.out.println("13. Palindrome: " + isPalindrome(build(1,2,2,1)));
        // while (even && even.next): odd.next = even.next, even.next = odd.next.next — alternating pointer reassignment
        System.out.println("14. Odd Even: " + str(oddEvenList(build(1,2,3,4,5))));
        // count length; k %= len; if (k==0) return; form circle, for-loop to cut point — modular rotation with conditional shortcut
        System.out.println("15. Rotate: " + str(rotateRight(build(1,2,3,4,5), 2)));
        // two dummies (before/after); while: if (val < x) append to before, else to after — conditional partitioning
        System.out.println("16. Partition: " + str(partition(build(1,4,3,2,5,2), 3)));
        // dummy head; while: if (val == next.val) inner while skips ALL dupes with while (curr && curr.val == dupVal) — nested while for multi-dupe skip
        System.out.println("17. Del Dup II: " + str(deleteDuplicatesII(build(1,2,3,3,4,4,5))));
        // while (l1 || l2 || carry): sum = carry + (l1 ? l1.val : 0) + (l2 ? l2.val : 0); new ListNode(sum%10) — ternary null checks
        System.out.println("18. Add Two: " + str(addTwoNumbers(build(2,4,3), build(5,6,4))));
        // new ListNode(0) dummy sorted head; while: find correct position via while (prev.next.val <= curr.val), insert — insertion sort with linked list
        System.out.println("19. Insertion Sort: " + str(insertionSortList(build(4,2,1,3))));
        // ArrayDeque<>() monotonic stack; for-loop with while (stack && top.val < curr.val) assign next larger — stack-based next greater element
        System.out.println("20. Next Larger: " + Arrays.toString(nextLargerNodes(build(2,7,4,3,5))));

        // --- HARD (21-30) ---
        System.out.println("\n--- HARD ---");
        // count k nodes; if (count < k) return as-is; for-loop reverses k, recursive call for rest — conditional group reversal with recursion
        System.out.println("21. Reverse K-Group: " + str(reverseKGroup(build(1,2,3,4,5,6,7,8), 3)));
        // new ListNode[]{...} → array of sorted lists; PriorityQueue<>() min-heap with Comparator; while (pq) poll min, if (node.next) offer
        System.out.println("22. Merge K: " + str(mergeKLists(new ListNode[]{build(1,4,5), build(1,3,4), build(2,6)})));
        // count k; reverse k nodes, then for-loop skips k (without reversing), recursive call — alternating reverse/skip logic
        System.out.println("23. Rev Alt K: " + str(reverseAlternateKGroup(build(1,2,3,4,5,6,7,8), 3)));
        // merge sort: fast/slow split; recursive sort; while-merge with if (l1.val <= l2.val) — divide-and-conquer on linked list
        System.out.println("24. Sort List: " + str(sortList(build(4,2,1,3))));
        // find middle, reverse second half, while-interleave: t1=first.next, t2=second.next, rewire — three-phase reordering
        ListNode rl = build(1,2,3,4,5); reorderList(rl); System.out.println("25. Reorder: " + str(rl));
        // Flatten: uses internal conditional logic (if/else, for/while) for computation
        System.out.println("26. Flatten: (nested list example)");
        // new int[]{2,3,3} → group sizes; for each group size: if (count < groupSize) stop; for-loop reverses groupSize nodes — variable-sized groups
        System.out.println("27. Rev Groups: " + str(reverseInGroups(build(1,2,3,4,5,6,7,8), new int[]{2,3,3})));
        // advance first pointer k steps, second to n-k+1; swap values — two-pointer with counted positioning
        System.out.println("28. Swap Kth: " + str(swapNodes(build(1,2,3,4,5), 2)));
        // HashMap<Integer,ListNode> prefix sum → node; while: if (prefix seen) skip zero-sum segment by rewiring prev.next — prefix sum technique on linked list
        System.out.println("29. Remove Zero Sum: " + str(removeZeroSumSublists(build(1,2,-3,3,1))));
        // fast/slow find middle; reverse second half; while sum pairs: Math.max(first.val + second.val, max) — twin sum from both ends
        System.out.println("30. Twin Sum: " + pairSum(build(5,4,2,1)));
    }
}
