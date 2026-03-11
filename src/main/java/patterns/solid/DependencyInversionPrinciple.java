package patterns.solid;

/**
 * D - Dependency Inversion Principle (DIP)
 *
 * High-level modules should not depend on low-level modules. Both should depend on abstractions.
 * Abstractions should not depend on details. Details should depend on abstractions.
 *
 * VIOLATION: OrderService directly creates and uses StripePayment — tightly coupled.
 * SOLUTION:  OrderService depends on a PaymentGateway interface. Stripe, PayPal, etc. implement it.
 */
public class DependencyInversionPrinciple {

    // ======================== BAD (Violates DIP) ========================
    // OrderService is tightly coupled to StripePayment. Switching to PayPal requires modifying OrderService.

    static class StripePaymentDirect {
        public void charge(double amount) {
            System.out.println("  Stripe charged $" + String.format("%.2f", amount));
        }
    }

    static class BadOrderService {
        private final StripePaymentDirect stripe = new StripePaymentDirect();

        public void placeOrder(String item, double price) {
            System.out.println("  Placing order for: " + item);
            stripe.charge(price);
            System.out.println("  Order confirmed!");
        }
    }

    // ======================== GOOD (Follows DIP) ========================
    // OrderService depends on the PaymentGateway abstraction, not a concrete implementation.

    interface PaymentGateway {
        void processPayment(double amount);
        String name();
    }

    static class StripePayment implements PaymentGateway {
        @Override
        public void processPayment(double amount) {
            System.out.println("  Stripe processed $" + String.format("%.2f", amount));
        }
        @Override
        public String name() { return "Stripe"; }
    }

    static class PayPalPayment implements PaymentGateway {
        @Override
        public void processPayment(double amount) {
            System.out.println("  PayPal processed $" + String.format("%.2f", amount));
        }
        @Override
        public String name() { return "PayPal"; }
    }

    static class CryptoPayment implements PaymentGateway {
        @Override
        public void processPayment(double amount) {
            System.out.println("  Crypto wallet processed $" + String.format("%.2f", amount));
        }
        @Override
        public String name() { return "Crypto"; }
    }

    static class OrderService {
        private final PaymentGateway paymentGateway;

        OrderService(PaymentGateway paymentGateway) {
            this.paymentGateway = paymentGateway;
        }

        public void placeOrder(String item, double price) {
            System.out.println("  Placing order for: " + item + " via " + paymentGateway.name());
            paymentGateway.processPayment(price);
            System.out.println("  Order confirmed!\n");
        }
    }

    // ======================== DEMO ========================

    public static void main(String[] args) {
        System.out.println("=== Dependency Inversion Principle (DIP) ===\n");

        System.out.println("--- BAD: OrderService hardcoded to Stripe ---");
        BadOrderService badService = new BadOrderService();
        badService.placeOrder("Laptop", 999.99);
        System.out.println("  Want PayPal? Must rewrite BadOrderService!\n");

        System.out.println("--- GOOD: OrderService works with any PaymentGateway ---");

        OrderService stripeOrder = new OrderService(new StripePayment());
        stripeOrder.placeOrder("Laptop", 999.99);

        OrderService paypalOrder = new OrderService(new PayPalPayment());
        paypalOrder.placeOrder("Headphones", 199.99);

        OrderService cryptoOrder = new OrderService(new CryptoPayment());
        cryptoOrder.placeOrder("Keyboard", 149.99);

        System.out.println("Benefit: Switch payment providers by injecting a different implementation.");
        System.out.println("Benefit: Easy to mock PaymentGateway in unit tests.");
    }
}
