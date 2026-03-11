package patterns.designpatterns;

import java.util.ArrayList;
import java.util.List;

/**
 * 9. OBSERVER PATTERN (Behavioral)
 *
 * Defines a one-to-many dependency so that when one object changes state,
 * all its dependents are notified and updated automatically.
 *
 * When to use:
 * - Event systems, UI listeners, pub/sub messaging
 * - Stock price alerts, weather station broadcasts
 * - Decoupling event producers from consumers
 *
 * Key idea: Subject maintains a list of observers and notifies them on state changes.
 */
public class ObserverPattern {

    // ======================== Observer Interface ========================
    interface Observer {
        void update(String event, Object data);
    }

    // ======================== Subject ========================
    static class EventBus {
        private final List<Observer> observers = new ArrayList<>();

        public void subscribe(Observer observer) { observers.add(observer); }
        public void unsubscribe(Observer observer) { observers.remove(observer); }

        public void publish(String event, Object data) {
            System.out.println("  [EventBus] Publishing: " + event);
            for (Observer o : observers) {
                o.update(event, data);
            }
        }
    }

    // ======================== Concrete Observers ========================
    static class EmailAlert implements Observer {
        @Override
        public void update(String event, Object data) {
            System.out.println("    [EmailAlert] Sending email about: " + event + " → " + data);
        }
    }

    static class DashboardLogger implements Observer {
        @Override
        public void update(String event, Object data) {
            System.out.println("    [Dashboard] Logging: " + event + " = " + data);
        }
    }

    static class SlackNotifier implements Observer {
        @Override
        public void update(String event, Object data) {
            System.out.println("    [Slack] #alerts: " + event + " — " + data);
        }
    }

    // ======================== Real-World: Stock Price ========================
    static class StockMarket {
        private final List<Observer> observers = new ArrayList<>();
        private double price;

        public void addObserver(Observer o) { observers.add(o); }

        public void setPrice(String symbol, double newPrice) {
            this.price = newPrice;
            System.out.println("  [Stock] " + symbol + " → $" + String.format("%.2f", price));
            for (Observer o : observers) {
                o.update(symbol + "_PRICE_CHANGE", price);
            }
        }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== Observer Pattern ===\n");

        System.out.println("--- Event Bus ---");
        EventBus bus = new EventBus();
        EmailAlert email = new EmailAlert();
        DashboardLogger dashboard = new DashboardLogger();
        SlackNotifier slack = new SlackNotifier();

        bus.subscribe(email);
        bus.subscribe(dashboard);
        bus.subscribe(slack);

        bus.publish("USER_REGISTERED", "alice@example.com");
        System.out.println();
        bus.publish("ORDER_PLACED", "Order #12345");

        System.out.println("\n  Unsubscribing Slack...");
        bus.unsubscribe(slack);
        System.out.println();
        bus.publish("PAYMENT_RECEIVED", "$99.99");

        System.out.println("\n--- Stock Market ---");
        StockMarket market = new StockMarket();
        market.addObserver((event, data) ->
            System.out.println("    [Trader] Alert: " + event + " = $" + data));
        market.addObserver((event, data) ->
            System.out.println("    [App] Display: " + event + " = $" + data));

        market.setPrice("AAPL", 185.50);
        market.setPrice("AAPL", 187.25);
    }
}
