package patterns.designpatterns;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 12. ITERATOR PATTERN (Behavioral)
 *
 * Provides a way to access elements of a collection sequentially
 * without exposing its underlying representation.
 *
 * When to use:
 * - Traversing custom data structures (trees, graphs, linked lists)
 * - Providing multiple traversal strategies for the same collection
 * - Hiding internal structure from clients
 *
 * Key idea: Define hasNext() and next() in an Iterator; the collection provides the iterator.
 */
public class IteratorPattern {

    // ======================== Custom Collection with Iterator ========================
    static class NumberRange implements Iterable<Integer> {
        private final int start;
        private final int end;
        private final int step;

        NumberRange(int start, int end, int step) {
            this.start = start;
            this.end = end;
            this.step = step;
        }

        @Override
        public Iterator<Integer> iterator() {
            return new Iterator<>() {
                private int current = start;

                @Override
                public boolean hasNext() { return current <= end; }

                @Override
                public Integer next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    int value = current;
                    current += step;
                    return value;
                }
            };
        }
    }

    // ======================== Binary Tree with Inorder Iterator ========================
    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val; this.left = left; this.right = right;
        }
    }

    static class BSTIterator implements Iterator<Integer> {
        private final java.util.Stack<TreeNode> stack = new java.util.Stack<>();

        BSTIterator(TreeNode root) { pushLeft(root); }

        private void pushLeft(TreeNode node) {
            while (node != null) { stack.push(node); node = node.left; }
        }

        @Override
        public boolean hasNext() { return !stack.isEmpty(); }

        @Override
        public Integer next() {
            TreeNode node = stack.pop();
            pushLeft(node.right);
            return node.val;
        }
    }

    // ======================== Fibonacci Iterator ========================
    static class FibonacciSequence implements Iterable<Long> {
        private final int count;

        FibonacciSequence(int count) { this.count = count; }

        @Override
        public Iterator<Long> iterator() {
            return new Iterator<>() {
                private int index = 0;
                private long a = 0, b = 1;

                @Override
                public boolean hasNext() { return index < count; }

                @Override
                public Long next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    long result = a;
                    long temp = a + b;
                    a = b;
                    b = temp;
                    index++;
                    return result;
                }
            };
        }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== Iterator Pattern ===\n");

        System.out.println("--- Custom Range Iterator (1 to 20, step 3) ---");
        System.out.print("  ");
        for (int n : new NumberRange(1, 20, 3)) {
            System.out.print(n + " ");
        }

        System.out.println("\n\n--- BST Inorder Iterator ---");
        TreeNode bst = new TreeNode(7,
            new TreeNode(3, new TreeNode(1), new TreeNode(5)),
            new TreeNode(11, new TreeNode(9), new TreeNode(13))
        );
        BSTIterator it = new BSTIterator(bst);
        System.out.print("  ");
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }

        System.out.println("\n\n--- Fibonacci Iterator (first 12) ---");
        System.out.print("  ");
        for (long fib : new FibonacciSequence(12)) {
            System.out.print(fib + " ");
        }

        System.out.println("\n\nBenefit: Clients traverse any collection with for-each — no knowledge of internals.");
    }
}
