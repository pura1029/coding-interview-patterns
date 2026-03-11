package patterns.designpatterns;

import java.util.Arrays;

/**
 * 10. STRATEGY PATTERN (Behavioral)
 *
 * Defines a family of algorithms, encapsulates each one, and makes them interchangeable.
 * The algorithm varies independently from clients that use it.
 *
 * When to use:
 * - Multiple algorithms for the same task (sorting, compression, payment)
 * - Replacing complex conditional logic (if/else or switch for behavior selection)
 * - Runtime algorithm switching
 *
 * Key idea: Extract each algorithm into its own class implementing a common interface.
 */
public class StrategyPattern {

    // ======================== Strategy Interface ========================
    interface SortStrategy {
        void sort(int[] data);
        String name();
    }

    // ======================== Concrete Strategies ========================
    static class BubbleSort implements SortStrategy {
        @Override
        public void sort(int[] data) {
            for (int i = 0; i < data.length - 1; i++)
                for (int j = 0; j < data.length - 1 - i; j++)
                    if (data[j] > data[j + 1]) {
                        int t = data[j]; data[j] = data[j + 1]; data[j + 1] = t;
                    }
        }
        @Override
        public String name() { return "BubbleSort"; }
    }

    static class QuickSort implements SortStrategy {
        @Override
        public void sort(int[] data) { Arrays.sort(data); }
        @Override
        public String name() { return "QuickSort"; }
    }

    static class InsertionSort implements SortStrategy {
        @Override
        public void sort(int[] data) {
            for (int i = 1; i < data.length; i++) {
                int key = data[i], j = i - 1;
                while (j >= 0 && data[j] > key) { data[j + 1] = data[j]; j--; }
                data[j + 1] = key;
            }
        }
        @Override
        public String name() { return "InsertionSort"; }
    }

    // ======================== Context ========================
    static class Sorter {
        private SortStrategy strategy;

        Sorter(SortStrategy strategy) { this.strategy = strategy; }

        public void setStrategy(SortStrategy strategy) { this.strategy = strategy; }

        public void sort(int[] data) {
            System.out.println("  Using " + strategy.name() + "...");
            strategy.sort(data);
            System.out.println("  Result: " + Arrays.toString(data));
        }
    }

    // ======================== Real-World: Discount Strategy ========================
    interface DiscountStrategy {
        double apply(double price);
        String description();
    }

    static class NoDiscount implements DiscountStrategy {
        @Override
        public double apply(double price) { return price; }
        @Override
        public String description() { return "No discount"; }
    }

    static class PercentageDiscount implements DiscountStrategy {
        private final int percent;
        PercentageDiscount(int percent) { this.percent = percent; }
        @Override
        public double apply(double price) { return price * (1 - percent / 100.0); }
        @Override
        public String description() { return percent + "% off"; }
    }

    static class FlatDiscount implements DiscountStrategy {
        private final double amount;
        FlatDiscount(double amount) { this.amount = amount; }
        @Override
        public double apply(double price) { return Math.max(0, price - amount); }
        @Override
        public String description() { return "$" + String.format("%.0f", amount) + " off"; }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== Strategy Pattern ===\n");

        System.out.println("--- Sorting Strategies ---");
        Sorter sorter = new Sorter(new BubbleSort());
        sorter.sort(new int[]{5, 2, 8, 1, 9});

        sorter.setStrategy(new InsertionSort());
        sorter.sort(new int[]{7, 3, 6, 4, 1});

        sorter.setStrategy(new QuickSort());
        sorter.sort(new int[]{10, 5, 3, 8, 2});

        System.out.println("\n--- Discount Strategies ---");
        double price = 100.00;
        DiscountStrategy[] strategies = {
            new NoDiscount(), new PercentageDiscount(20), new FlatDiscount(15)
        };
        for (DiscountStrategy ds : strategies) {
            System.out.printf("  $%.2f with %s → $%.2f%n", price, ds.description(), ds.apply(price));
        }

        System.out.println("\nBenefit: Swap algorithms at runtime without changing the client.");
    }
}
