package patterns.solid;

import java.util.List;

/**
 * L - Liskov Substitution Principle (LSP)
 *
 * Subtypes must be substitutable for their base types without breaking behavior.
 * If code works with a base class, it must also work correctly with any subclass.
 *
 * VIOLATION: Penguin extends Bird but throws an exception on fly() — breaks callers expecting all Birds can fly.
 * SOLUTION:  Separate FlyingBird from Bird. Penguin is a Bird but not a FlyingBird.
 */
public class LiskovSubstitutionPrinciple {

    // ======================== BAD (Violates LSP) ========================
    // Penguin IS-A Bird, but it can't fly — breaks any code calling bird.fly().

    static class BadBird {
        public String fly() { return "Flying!"; }
        public String name() { return "Bird"; }
    }

    static class BadEagle extends BadBird {
        @Override
        public String fly() { return "Eagle soaring high!"; }
        @Override
        public String name() { return "Eagle"; }
    }

    static class BadPenguin extends BadBird {
        @Override
        public String fly() {
            throw new UnsupportedOperationException("Penguins can't fly!");
        }
        @Override
        public String name() { return "Penguin"; }
    }

    // ======================== GOOD (Follows LSP) ========================
    // Separate what all birds can do from what only flying birds can do.

    interface Bird {
        String name();
        String eat();
    }

    interface FlyingBird extends Bird {
        String fly();
    }

    interface SwimmingBird extends Bird {
        String swim();
    }

    static class Eagle implements FlyingBird {
        @Override
        public String name() { return "Eagle"; }
        @Override
        public String eat() { return "Eagle eats fish"; }
        @Override
        public String fly() { return "Eagle soaring high!"; }
    }

    static class Sparrow implements FlyingBird {
        @Override
        public String name() { return "Sparrow"; }
        @Override
        public String eat() { return "Sparrow eats seeds"; }
        @Override
        public String fly() { return "Sparrow fluttering!"; }
    }

    static class Penguin implements SwimmingBird {
        @Override
        public String name() { return "Penguin"; }
        @Override
        public String eat() { return "Penguin eats krill"; }
        @Override
        public String swim() { return "Penguin swimming gracefully!"; }
    }

    static void makeBirdsFly(List<FlyingBird> birds) {
        for (FlyingBird b : birds) {
            System.out.println("  " + b.name() + ": " + b.fly());
        }
    }

    // ======================== DEMO ========================

    public static void main(String[] args) {
        System.out.println("=== Liskov Substitution Principle (LSP) ===\n");

        System.out.println("--- BAD: Penguin breaks the Bird contract ---");
        List<BadBird> badBirds = List.of(new BadEagle(), new BadPenguin());
        for (BadBird b : badBirds) {
            try {
                System.out.println("  " + b.name() + ": " + b.fly());
            } catch (UnsupportedOperationException e) {
                System.out.println("  " + b.name() + ": CRASH! " + e.getMessage());
            }
        }

        System.out.println("\n--- GOOD: Proper hierarchy, no surprises ---");
        List<FlyingBird> flyingBirds = List.of(new Eagle(), new Sparrow());
        System.out.println("Flying birds:");
        makeBirdsFly(flyingBirds);

        Penguin penguin = new Penguin();
        System.out.println("\nSwimming bird:");
        System.out.println("  " + penguin.name() + ": " + penguin.swim());
        System.out.println("  " + penguin.name() + ": " + penguin.eat());

        System.out.println("\nBenefit: Penguin is never passed where fly() is expected. No runtime surprises.");
    }
}
