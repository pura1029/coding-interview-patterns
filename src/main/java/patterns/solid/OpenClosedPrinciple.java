package patterns.solid;

import java.util.List;

/**
 * O - Open/Closed Principle (OCP)
 *
 * Classes should be open for extension but closed for modification.
 * Add new behavior by adding new code (new classes), not changing existing code.
 *
 * VIOLATION: An AreaCalculator with if-else chains for each shape type.
 * SOLUTION:  A Shape interface with area() — add new shapes without touching existing code.
 */
public class OpenClosedPrinciple {

    // ======================== BAD (Violates OCP) ========================
    // Adding a new shape requires modifying this method every time.

    static class BadAreaCalculator {
        public double calculateArea(Object shape) {
            if (shape instanceof BadRectangle r) {
                return r.width * r.height;
            } else if (shape instanceof BadCircle c) {
                return Math.PI * c.radius * c.radius;
            }
            // Adding Triangle? Must modify this class!
            throw new IllegalArgumentException("Unknown shape");
        }
    }

    static class BadRectangle {
        double width, height;
        BadRectangle(double w, double h) { width = w; height = h; }
    }

    static class BadCircle {
        double radius;
        BadCircle(double r) { radius = r; }
    }

    // ======================== GOOD (Follows OCP) ========================
    // New shapes are added by implementing the interface — no existing code changes.

    interface Shape {
        double area();
        String name();
    }

    static class Rectangle implements Shape {
        private final double width, height;

        Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public double area() { return width * height; }

        @Override
        public String name() { return "Rectangle(" + width + "x" + height + ")"; }
    }

    static class Circle implements Shape {
        private final double radius;

        Circle(double radius) { this.radius = radius; }

        @Override
        public double area() { return Math.PI * radius * radius; }

        @Override
        public String name() { return "Circle(r=" + radius + ")"; }
    }

    static class Triangle implements Shape {
        private final double base, height;

        Triangle(double base, double height) {
            this.base = base;
            this.height = height;
        }

        @Override
        public double area() { return 0.5 * base * height; }

        @Override
        public String name() { return "Triangle(b=" + base + ", h=" + height + ")"; }
    }

    static class AreaCalculator {
        public double totalArea(List<Shape> shapes) {
            return shapes.stream().mapToDouble(Shape::area).sum();
        }
    }

    // ======================== DEMO ========================

    public static void main(String[] args) {
        System.out.println("=== Open/Closed Principle (OCP) ===\n");

        System.out.println("--- BAD: Must modify calculator for each new shape ---");
        // new BadAreaCalculator() → uses if-else chain: if (shape instanceof Rectangle) ... else if (Circle) — adding Triangle means editing this class (violates OCP)
        BadAreaCalculator badCalc = new BadAreaCalculator();
        System.out.println("Rectangle area: " + badCalc.calculateArea(new BadRectangle(5, 3)));
        System.out.println("Circle area: " + String.format("%.2f", badCalc.calculateArea(new BadCircle(4))));
        System.out.println("Adding Triangle? Must edit BadAreaCalculator!\n");

        System.out.println("--- GOOD: Just add a new Shape class ---");
        // List.of() → creates immutable List; each new Shape() implements area() — adding new shapes doesn't modify existing code (open for extension, closed for modification)
        List<Shape> shapes = List.of(
            new Rectangle(5, 3),
            new Circle(4),
            new Triangle(6, 4)
        );

        // new AreaCalculator() → uses polymorphism; calculateArea(shape) calls shape.area() — no if-else chain for shape types; open for extension via new Shape subclass
        AreaCalculator calc = new AreaCalculator();
        // for-each iterates shapes; s.area() dispatches polymorphically — no instanceof check or switch needed; each Shape knows its own area formula
        for (Shape s : shapes) {
            System.out.printf("  %s → area = %.2f%n", s.name(), s.area());
        }
        System.out.printf("  Total area = %.2f%n", calc.totalArea(shapes));

        System.out.println("\nBenefit: Added Triangle without modifying AreaCalculator or any existing Shape.");
    }
}
