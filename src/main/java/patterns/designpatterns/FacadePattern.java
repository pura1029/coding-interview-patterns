package patterns.designpatterns;

/**
 * 6. FACADE PATTERN (Structural)
 *
 * Provides a simplified interface to a complex subsystem.
 * Hides internal complexity behind a single, easy-to-use class.
 *
 * When to use:
 * - Simplifying a complex library or framework API
 * - Providing a unified interface to a set of subsystems
 * - Reducing coupling between client code and subsystem internals
 *
 * Key idea: One class wraps multiple subsystem calls into simple high-level methods.
 */
public class FacadePattern {

    // ======================== Complex Subsystems ========================
    static class CPU {
        public void freeze() { System.out.println("  CPU: Freezing..."); }
        public void jump(long address) { System.out.println("  CPU: Jumping to 0x" + Long.toHexString(address)); }
        public void execute() { System.out.println("  CPU: Executing instructions"); }
    }

    static class Memory {
        public void load(long address, byte[] data) {
            System.out.println("  Memory: Loading " + data.length + " bytes at 0x" + Long.toHexString(address));
        }
    }

    static class HardDrive {
        public byte[] read(long sector, int size) {
            System.out.println("  HardDrive: Reading " + size + " bytes from sector " + sector);
            return new byte[size];
        }
    }

    // ======================== Facade ========================
    static class ComputerFacade {
        private final CPU cpu = new CPU();
        private final Memory memory = new Memory();
        private final HardDrive hardDrive = new HardDrive();

        public void start() {
            System.out.println("  [ComputerFacade] Starting computer...");
            cpu.freeze();
            byte[] bootData = hardDrive.read(0, 1024);
            memory.load(0x0000, bootData);
            cpu.jump(0x0000);
            cpu.execute();
            System.out.println("  [ComputerFacade] Computer started successfully!\n");
        }
    }

    // ======================== Real-World: Order Facade ========================
    static class InventoryService {
        public boolean checkStock(String item) {
            System.out.println("  Inventory: " + item + " is in stock");
            return true;
        }
    }

    static class PaymentService {
        public boolean processPayment(double amount) {
            System.out.println("  Payment: Charged $" + String.format("%.2f", amount));
            return true;
        }
    }

    static class ShippingService {
        public String shipOrder(String item, String address) {
            String tracking = "TRK-" + (int)(Math.random() * 100000);
            System.out.println("  Shipping: " + item + " → " + address + " [" + tracking + "]");
            return tracking;
        }
    }

    static class OrderFacade {
        private final InventoryService inventory = new InventoryService();
        private final PaymentService payment = new PaymentService();
        private final ShippingService shipping = new ShippingService();

        public String placeOrder(String item, double price, String address) {
            System.out.println("  [OrderFacade] Processing order...");
            if (!inventory.checkStock(item)) return null;
            if (!payment.processPayment(price)) return null;
            return shipping.shipOrder(item, address);
        }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== Facade Pattern ===\n");

        System.out.println("--- Computer Startup Facade ---");
        // new ComputerFacade() → creates internal CPU, Memory, HardDrive objects; start() calls cpu.boot(), memory.load(), hd.read() in sequence — simplifies multi-step startup
        ComputerFacade computer = new ComputerFacade();
        computer.start();

        System.out.println("--- Order Facade ---");
        // new OrderFacade() → creates internal Inventory, Payment, Shipping objects; placeOrder() orchestrates: check stock, charge, ship — one call replaces three
        OrderFacade order = new OrderFacade();
        // placeOrder() → calls inventory.check(), payment.charge(), shipping.ship() internally; returns tracking number — complex workflow behind simple interface
        String tracking = order.placeOrder("Laptop", 999.99, "123 Main St");
        System.out.println("  Order placed! Tracking: " + tracking);

        System.out.println("\nBenefit: Client calls one method instead of coordinating multiple subsystems.");
    }
}
