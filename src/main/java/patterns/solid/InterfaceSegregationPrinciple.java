package patterns.solid;

/**
 * I - Interface Segregation Principle (ISP)
 *
 * Don't force classes to implement interfaces they don't use.
 * Many small, specific interfaces are better than one large, general-purpose interface.
 *
 * VIOLATION: A fat Machine interface forces SimplePrinter to implement scan() and fax() it doesn't support.
 * SOLUTION:  Split into Printable, Scannable, and Faxable. Each class implements only what it needs.
 */
public class InterfaceSegregationPrinciple {

    // ======================== BAD (Violates ISP) ========================
    // SimplePrinter is forced to implement scan() and fax() even though it can't do either.

    interface FatMachine {
        void print(String doc);
        void scan(String doc);
        void fax(String doc);
    }

    static class BadAllInOnePrinter implements FatMachine {
        @Override
        public void print(String doc) { System.out.println("  Printing: " + doc); }
        @Override
        public void scan(String doc) { System.out.println("  Scanning: " + doc); }
        @Override
        public void fax(String doc) { System.out.println("  Faxing: " + doc); }
    }

    static class BadSimplePrinter implements FatMachine {
        @Override
        public void print(String doc) { System.out.println("  Printing: " + doc); }
        @Override
        public void scan(String doc) {
            throw new UnsupportedOperationException("SimplePrinter can't scan!");
        }
        @Override
        public void fax(String doc) {
            throw new UnsupportedOperationException("SimplePrinter can't fax!");
        }
    }

    // ======================== GOOD (Follows ISP) ========================
    // Small, focused interfaces. Each class implements only what it supports.

    interface Printable {
        void print(String doc);
    }

    interface Scannable {
        void scan(String doc);
    }

    interface Faxable {
        void fax(String doc);
    }

    static class SimplePrinter implements Printable {
        @Override
        public void print(String doc) { System.out.println("  SimplePrinter printing: " + doc); }
    }

    static class AllInOnePrinter implements Printable, Scannable, Faxable {
        @Override
        public void print(String doc) { System.out.println("  AllInOne printing: " + doc); }
        @Override
        public void scan(String doc) { System.out.println("  AllInOne scanning: " + doc); }
        @Override
        public void fax(String doc) { System.out.println("  AllInOne faxing: " + doc); }
    }

    static class ModernPrinter implements Printable, Scannable {
        @Override
        public void print(String doc) { System.out.println("  ModernPrinter printing: " + doc); }
        @Override
        public void scan(String doc) { System.out.println("  ModernPrinter scanning: " + doc); }
    }

    // ======================== DEMO ========================

    public static void main(String[] args) {
        System.out.println("=== Interface Segregation Principle (ISP) ===\n");

        System.out.println("--- BAD: SimplePrinter forced to implement scan/fax ---");
        // new BadSimplePrinter() → implements fat IMachine interface (print, scan, fax); scan/fax throw UnsupportedOperationException — forced empty implementations
        BadSimplePrinter badPrinter = new BadSimplePrinter();
        badPrinter.print("Report.pdf");
        // try-catch: badPrinter.scan() throws because SimplePrinter can't scan — ISP violation forces unusable method stubs
        try {
            badPrinter.scan("Photo.jpg");
        } catch (UnsupportedOperationException e) {
            System.out.println("  CRASH! " + e.getMessage());
        }

        System.out.println("\n--- GOOD: Each class implements only what it supports ---");
        // new SimplePrinter() → implements only Printable interface; no scan/fax methods exist — client isn't forced to depend on unused methods
        SimplePrinter simplePrinter = new SimplePrinter();
        simplePrinter.print("Report.pdf");

        // new AllInOnePrinter() → implements Printable, Scannable, AND Faxable — supports all capabilities voluntarily
        AllInOnePrinter allInOne = new AllInOnePrinter();
        allInOne.print("Report.pdf");
        allInOne.scan("Photo.jpg");
        allInOne.fax("Contract.pdf");

        // new ModernPrinter() → implements Printable + Scannable only, NOT Faxable — picks exactly the interfaces it needs
        ModernPrinter modern = new ModernPrinter();
        modern.print("Resume.pdf");
        modern.scan("ID.jpg");

        System.out.println("\nBenefit: SimplePrinter never needs to deal with scan() or fax().");
        System.out.println("Benefit: ModernPrinter supports print + scan without forced fax support.");
    }
}
