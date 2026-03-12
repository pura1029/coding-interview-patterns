package patterns.designpatterns;

/**
 * 2. FACTORY METHOD PATTERN (Creational)
 *
 * Defines an interface for creating objects, but lets subclasses decide which class to instantiate.
 * The factory method defers instantiation to subclasses.
 *
 * When to use:
 * - Object creation logic is complex or depends on input/config
 * - You want to decouple client code from concrete classes
 * - Multiple product families (notifications, vehicles, documents)
 *
 * Key idea: Replace `new ConcreteClass()` with a factory method that returns the right type.
 */
public class FactoryMethodPattern {

    // ======================== Product Interface ========================
    interface Notification {
        void send(String message);
    }

    static class EmailNotification implements Notification {
        @Override
        public void send(String message) {
            System.out.println("  [Email] Sending: " + message);
        }
    }

    static class SMSNotification implements Notification {
        @Override
        public void send(String message) {
            System.out.println("  [SMS] Sending: " + message);
        }
    }

    static class PushNotification implements Notification {
        @Override
        public void send(String message) {
            System.out.println("  [Push] Sending: " + message);
        }
    }

    // ======================== Factory ========================
    static class NotificationFactory {
        /**
         * Factory Method - Create Product
         *
         * <p><b>Approach:</b> Factory method pattern: subclasses override this method to instantiate the appropriate product type.
         *
         * @param type the product type to create
         * @return a new product instance of the specified type
         *
         * <p><b>Time:</b> O(1) time.
         * <br><b>Space:</b> O(1) space.
         */
        public static Notification create(String type) {
            return switch (type.toLowerCase()) {
                case "email" -> new EmailNotification();
                case "sms" -> new SMSNotification();
                case "push" -> new PushNotification();
                default -> throw new IllegalArgumentException("Unknown type: " + type);
            };
        }
    }

    // ======================== Abstract Factory Method ========================
    static abstract class NotificationService {
        abstract Notification createNotification();

        public void notify(String message) {
            Notification n = createNotification();
            n.send(message);
        }
    }

    static class EmailService extends NotificationService {
        @Override
        Notification createNotification() { return new EmailNotification(); }
    }

    static class SMSService extends NotificationService {
        @Override
        Notification createNotification() { return new SMSNotification(); }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== Factory Method Pattern ===\n");

        System.out.println("--- Simple Factory ---");
        // NotificationFactory.create("email") → if ("email") return new EmailNotification(), else if ("sms") return new SMSNotification() — conditional type creation
        Notification email = NotificationFactory.create("email");
        Notification sms = NotificationFactory.create("sms");
        Notification push = NotificationFactory.create("push");
        // send() → polymorphic call; actual class determined by factory's if-else logic — caller doesn't know concrete type
        email.send("Welcome to the platform!");
        sms.send("Your OTP is 123456");
        push.send("New message received");

        System.out.println("\n--- Factory Method (subclass decides) ---");
        // new EmailService() → concrete creator; overrides createNotification() returning new EmailNotification() — subclass decides which object to create
        NotificationService emailSvc = new EmailService();
        // new SMSService() → different creator; returns new SMSNotification() — each subclass encapsulates its creation logic
        NotificationService smsSvc = new SMSService();
        // notify() → template: calls createNotification().send() — caller uses abstract creator, concrete class handles the rest
        emailSvc.notify("Monthly report ready");
        smsSvc.notify("Account alert");
    }
}
