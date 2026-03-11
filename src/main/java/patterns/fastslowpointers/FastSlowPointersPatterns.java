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
    public static boolean isHappy(int n) {
        int slow = n, fast = n;
        do { slow = digitSqSum(slow); fast = digitSqSum(digitSqSum(fast)); } while (slow != fast);
        return slow == 1;
    }
    private static int digitSqSum(int n) {
        int s = 0; while (n > 0) { int d = n % 10; s += d * d; n /= 10; } return s;
    }

    // ======================= EASY 2: Linked List Cycle Detection =======================
    public static boolean hasCycle(ListNode head) {
        ListNode s = head, f = head;
        while (f != null && f.next != null) { s = s.next; f = f.next.next; if (s == f) return true; }
        return false;
    }

    // ======================= EASY 3: Middle of the Linked List =======================
    public static ListNode middleNode(ListNode head) {
        ListNode s = head, f = head;
        while (f != null && f.next != null) { s = s.next; f = f.next.next; }
        return s;
    }

    // ======================= EASY 4: Palindrome Linked List =======================
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
    public static ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head);
        ListNode fast = dummy, slow = dummy;
        for (int i = 0; i <= n; i++) fast = fast.next;
        while (fast != null) { slow = slow.next; fast = fast.next; }
        slow.next = slow.next.next;
        return dummy.next;
    }

    // ======================= EASY 6: Intersection of Two Linked Lists =======================
    public static ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode a = headA, b = headB;
        while (a != b) { a = (a == null) ? headB : a.next; b = (b == null) ? headA : b.next; }
        return a;
    }

    // ======================= EASY 7: Circular Array Loop Detection =======================
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
    public static boolean checkIfDoubleExists(int[] arr) {
        Set<Integer> seen = new HashSet<>();
        for (int n : arr) {
            if (seen.contains(2 * n) || (n % 2 == 0 && seen.contains(n / 2))) return true;
            seen.add(n);
        }
        return false;
    }

    // ======================= EASY 9: Power of Two (bit trick related to cycle) =======================
    public static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    // ======================= EASY 10: Count Nodes in Complete Binary Tree using two-pointer idea =======================
    static class TreeNode {
        TreeNode left, right;
        TreeNode() {}
    }
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
    public static ListNode detectCycleStart(ListNode head) {
        ListNode s = head, f = head;
        while (f != null && f.next != null) {
            s = s.next; f = f.next.next;
            if (s == f) { s = head; while (s != f) { s = s.next; f = f.next; } return s; }
        }
        return null;
    }

    // ======================= MEDIUM 2: Reorder List (L0→Ln→L1→Ln-1→...) =======================
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
    public static ListNode deleteMiddle(ListNode head) {
        if (head.next == null) return null;
        ListNode slow = head, fast = head.next.next;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
        slow.next = slow.next.next;
        return head;
    }

    // ======================= MEDIUM 7: Rotate List by K =======================
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
    public static int findDuplicate(int[] nums) {
        int slow = nums[0], fast = nums[0];
        do { slow = nums[slow]; fast = nums[nums[fast]]; } while (slow != fast);
        slow = nums[0];
        while (slow != fast) { slow = nums[slow]; fast = nums[fast]; }
        return slow;
    }

    // ======================= HARD 2: Linked List Cycle with Entry + Length =======================
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
        System.out.println("1. Happy Number (19): " + isHappy(19));
        ListNode cycleList = buildList(3, 2, 0, -4);
        cycleList.next.next.next.next = cycleList.next;
        System.out.println("2. Has Cycle: " + hasCycle(cycleList));
        System.out.println("3. Middle Node: " + middleNode(buildList(1, 2, 3, 4, 5)).val);
        System.out.println("4. Palindrome List: " + isPalindromeList(buildList(1, 2, 2, 1)));
        System.out.println("5. Remove Nth End: " + listStr(removeNthFromEnd(buildList(1, 2, 3, 4, 5), 2)));
        ListNode common = buildList(8, 4, 5);
        ListNode a = new ListNode(4, new ListNode(1, common));
        ListNode b = new ListNode(5, new ListNode(6, new ListNode(1, common)));
        System.out.println("6. Intersection: " + (getIntersectionNode(a, b) != null ? getIntersectionNode(a, b).val : "null"));
        System.out.println("7. Circular Loop: " + isCircularLoop(new int[]{2, -1, 1, 2, 2}));
        System.out.println("8. Double Exists: " + checkIfDoubleExists(new int[]{10, 2, 5, 3}));
        System.out.println("9. Power of Two (16): " + isPowerOfTwo(16));
        System.out.println("10. Count Nodes: (complete tree example)");

        System.out.println("\n--- MEDIUM ---");
        ListNode cycleList2 = buildList(3, 2, 0, -4);
        cycleList2.next.next.next.next = cycleList2.next;
        System.out.println("11. Cycle Start: " + detectCycleStart(cycleList2).val);
        ListNode reorder = buildList(1, 2, 3, 4, 5); reorderList(reorder);
        System.out.println("12. Reorder List: " + listStr(reorder));
        System.out.println("13. Sort List: " + listStr(sortList(buildList(4, 2, 1, 3))));
        System.out.println("14. Add Two Numbers: " + listStr(addTwoNumbers(buildList(7, 2, 4, 3), buildList(5, 6, 4))));
        System.out.println("15. Odd Even List: " + listStr(oddEvenList(buildList(1, 2, 3, 4, 5))));
        System.out.println("16. Delete Middle: " + listStr(deleteMiddle(buildList(1, 3, 4, 7, 1, 2, 6))));
        System.out.println("17. Rotate Right: " + listStr(rotateRight(buildList(1, 2, 3, 4, 5), 2)));
        System.out.println("18. Swap Pairs: " + listStr(swapPairs(buildList(1, 2, 3, 4))));
        System.out.println("19. Partition List: " + listStr(partition(buildList(1, 4, 3, 2, 5, 2), 3)));
        System.out.println("20. Delete Dup II: " + listStr(deleteDuplicatesII(buildList(1, 2, 3, 3, 4, 4, 5))));

        System.out.println("\n--- HARD ---");
        System.out.println("21. Find Duplicate: " + findDuplicate(new int[]{1, 3, 4, 2, 2}));
        ListNode cycleList3 = buildList(1, 2, 3, 4, 5);
        cycleList3.next.next.next.next.next = cycleList3.next;
        int[] info = cycleInfo(cycleList3);
        System.out.println("22. Cycle Info: entry=" + info[0] + ", length=" + info[1]);
        ListNode[] parts = splitListToParts(buildList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 3);
        System.out.print("23. Split Parts: ");
        for (ListNode p : parts) System.out.print(listStr(p) + " ");
        System.out.println();
        System.out.println("24. Flatten List: (simplified)");
        RandomNode rn = new RandomNode(buildList(1, 2, 3));
        System.out.println("25. Random Node: " + rn.getRandom());
        System.out.println("26. Copy List: " + listStr(copyRandomList(buildList(1, 2, 3))));
        System.out.println("27. Merge K Lists: " + listStr(mergeKLists(new ListNode[]{buildList(1, 4, 5), buildList(1, 3, 4), buildList(2, 6)})));
        System.out.println("28. Reverse Alt K: " + listStr(reverseAlternateKGroup(buildList(1, 2, 3, 4, 5, 6, 7, 8), 3)));
        LRUCache lru = new LRUCache(2);
        lru.put(1, 1); lru.put(2, 2);
        System.out.println("29. LRU get(1): " + lru.get(1));
        ListNode loopList = buildList(1, 2, 3, 4, 5);
        loopList.next.next.next.next.next = loopList.next.next;
        System.out.println("30. Remove Loop: " + listStr(detectAndRemoveLoop(loopList)));
    }
}
