package patterns.designpatterns;

/**
 * 5. DECORATOR PATTERN (Structural)
 *
 * Attaches additional responsibilities to objects dynamically.
 * A flexible alternative to subclassing for extending functionality.
 *
 * When to use:
 * - Adding features to objects at runtime (logging, encryption, compression)
 * - Avoiding a class explosion from every combination of features
 * - Wrapping I/O streams (Java's InputStream/BufferedInputStream)
 *
 * Key idea: Wrap the original object, delegate to it, and add behavior before/after.
 */
public class DecoratorPattern {

    // ======================== Component Interface ========================
    interface Coffee {
        String description();
        double cost();
    }

    // ======================== Base Component ========================
    static class SimpleCoffee implements Coffee {
        @Override
        public String description() { return "Simple Coffee"; }
        @Override
        public double cost() { return 2.00; }
    }

    // ======================== Decorators ========================
    static abstract class CoffeeDecorator implements Coffee {
        protected final Coffee wrapped;
        CoffeeDecorator(Coffee coffee) { this.wrapped = coffee; }
    }

    static class MilkDecorator extends CoffeeDecorator {
        MilkDecorator(Coffee coffee) { super(coffee); }
        @Override
        public String description() { return wrapped.description() + " + Milk"; }
        @Override
        public double cost() { return wrapped.cost() + 0.50; }
    }

    static class SugarDecorator extends CoffeeDecorator {
        SugarDecorator(Coffee coffee) { super(coffee); }
        @Override
        public String description() { return wrapped.description() + " + Sugar"; }
        @Override
        public double cost() { return wrapped.cost() + 0.25; }
    }

    static class WhipDecorator extends CoffeeDecorator {
        WhipDecorator(Coffee coffee) { super(coffee); }
        @Override
        public String description() { return wrapped.description() + " + Whip"; }
        @Override
        public double cost() { return wrapped.cost() + 0.75; }
    }

    static class VanillaDecorator extends CoffeeDecorator {
        VanillaDecorator(Coffee coffee) { super(coffee); }
        @Override
        public String description() { return wrapped.description() + " + Vanilla"; }
        @Override
        public double cost() { return wrapped.cost() + 0.60; }
    }

    static void printOrder(Coffee coffee) {
        System.out.printf("  %-50s $%.2f%n", coffee.description(), coffee.cost());
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== Decorator Pattern ===\n");

        // new SimpleCoffee() → base component implementing Coffee interface; cost() returns base price, description() returns "Simple Coffee"
        Coffee plain = new SimpleCoffee();
        printOrder(plain);

        // new MilkDecorator(new SimpleCoffee()) → wraps base; cost() returns decoratedCoffee.cost() + 0.50 — each decorator adds to the chain
        Coffee withMilk = new MilkDecorator(new SimpleCoffee());
        printOrder(withMilk);

        // Nested decorators: Whip(Vanilla(Milk(Simple))) → cost chains: whip + vanilla + milk + base; description chains similarly — layered wrapping
        Coffee latte = new WhipDecorator(new VanillaDecorator(new MilkDecorator(new SimpleCoffee())));
        printOrder(latte);

        // Double SugarDecorator → same decorator applied twice; cost adds sugar price twice — flexible composition without new subclass per combination
        Coffee sweet = new SugarDecorator(new SugarDecorator(new MilkDecorator(new SimpleCoffee())));
        printOrder(sweet);

        System.out.println("\nBenefit: Mix-and-match any combination without creating a class for each.");
    }
}
