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
        Notification email = NotificationFactory.create("email");
        Notification sms = NotificationFactory.create("sms");
        Notification push = NotificationFactory.create("push");
        email.send("Welcome to the platform!");
        sms.send("Your OTP is 123456");
        push.send("New message received");

        System.out.println("\n--- Factory Method (subclass decides) ---");
        NotificationService emailSvc = new EmailService();
        NotificationService smsSvc = new SMSService();
        emailSvc.notify("Monthly report ready");
        smsSvc.notify("Account alert");
    }
}
