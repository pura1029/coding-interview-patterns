package patterns.designpatterns;

/**
 * 13. STATE PATTERN (Behavioral)
 *
 * Allows an object to alter its behavior when its internal state changes.
 * The object will appear to change its class.
 *
 * When to use:
 * - Objects with behavior that depends on state (vending machines, document workflows)
 * - Replacing complex state-dependent conditional logic
 * - Finite state machines (order processing, TCP connections)
 *
 * Key idea: Encapsulate each state in its own class implementing a common State interface.
 */
public class StatePattern {

    // ======================== State Interface ========================
    interface OrderState {
        void next(Order order);
        void prev(Order order);
        String status();
    }

    // ======================== Concrete States ========================
    static class NewState implements OrderState {
        @Override
        public void next(Order order) {
            order.setState(new PaidState());
            System.out.println("  Order paid → " + order.getStatus());
        }
        @Override
        public void prev(Order order) {
            System.out.println("  Already at the beginning — cannot go back");
        }
        @Override
        public String status() { return "NEW"; }
    }

    static class PaidState implements OrderState {
        @Override
        public void next(Order order) {
            order.setState(new ShippedState());
            System.out.println("  Order shipped → " + order.getStatus());
        }
        @Override
        public void prev(Order order) {
            order.setState(new CancelledState());
            System.out.println("  Order cancelled → " + order.getStatus());
        }
        @Override
        public String status() { return "PAID"; }
    }

    static class ShippedState implements OrderState {
        @Override
        public void next(Order order) {
            order.setState(new DeliveredState());
            System.out.println("  Order delivered → " + order.getStatus());
        }
        @Override
        public void prev(Order order) {
            System.out.println("  Cannot un-ship an order");
        }
        @Override
        public String status() { return "SHIPPED"; }
    }

    static class DeliveredState implements OrderState {
        @Override
        public void next(Order order) {
            System.out.println("  Order already delivered — final state");
        }
        @Override
        public void prev(Order order) {
            System.out.println("  Cannot reverse a delivered order");
        }
        @Override
        public String status() { return "DELIVERED"; }
    }

    static class CancelledState implements OrderState {
        @Override
        public void next(Order order) {
            System.out.println("  Cannot proceed — order is cancelled");
        }
        @Override
        public void prev(Order order) {
            System.out.println("  Cannot reverse a cancellation");
        }
        @Override
        public String status() { return "CANCELLED"; }
    }

    // ======================== Context ========================
    static class Order {
        private OrderState state;
        private final String id;

        Order(String id) {
            this.id = id;
            this.state = new NewState();
        }

        public void setState(OrderState state) { this.state = state; }
        public String getStatus() { return "[Order " + id + ": " + state.status() + "]"; }
        public void next() { state.next(this); }
        public void prev() { state.prev(this); }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== State Pattern ===\n");

        System.out.println("--- Order #1: Happy path ---");
        // new Order("001") → creates object with initial state (e.g., new PendingState()); state field holds current State object — state pattern entry
        Order order1 = new Order("001");
        System.out.println("  " + order1.getStatus());
        // next() → delegates to currentState.next(order); each state class decides transition: if (Pending) → Processing, if (Processing) → Shipped, etc.
        order1.next();
        order1.next();
        order1.next();
        order1.next();

        System.out.println("\n--- Order #2: Cancellation ---");
        // new Order("002") → starts in PendingState; prev() from Pending might cancel; each state's prev() has its own if-else transition logic
        Order order2 = new Order("002");
        System.out.println("  " + order2.getStatus());
        order2.next();
        // prev() → currentState.prev(order): if (ProcessingState) go back to Pending — each state handles forward/backward transitions independently
        order2.prev();
        order2.next();

        System.out.println("\nBenefit: State transitions are explicit; adding new states doesn't change existing ones.");
    }
}
