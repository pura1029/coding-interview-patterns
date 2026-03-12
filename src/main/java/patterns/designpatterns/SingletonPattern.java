package patterns.designpatterns;

/**
 * 1. SINGLETON PATTERN (Creational)
 *
 * Ensures a class has only ONE instance and provides a global access point to it.
 *
 * When to use:
 * - Database connection pools, logging, configuration managers
 * - Any resource that should exist exactly once in the application
 *
 * Key idea: Private constructor + static instance + public accessor.
 */
public class SingletonPattern {

    // ======================== Eager Singleton ========================
    static class EagerSingleton {
        private static final EagerSingleton INSTANCE = new EagerSingleton();
        private int requestCount = 0;

        private EagerSingleton() {}

        /**
         * Get Singleton Instance
         *
         * <p><b>Approach:</b> Thread-safe lazy initialization: returns the single shared instance, creating it on first access if needed.
         *
         * @return the singleton instance
         *
         * <p><b>Time:</b> O(1) time.
         * <br><b>Space:</b> O(1) space.
         */
        public static EagerSingleton getInstance() { return INSTANCE; }

        public void serve(String request) {
            requestCount++;
            System.out.println("  [Eager] Serving: " + request + " (total: " + requestCount + ")");
        }
    }

    // ======================== Lazy Singleton (Thread-Safe) ========================
    static class LazySingleton {
        private static volatile LazySingleton instance;
        private int requestCount = 0;

        private LazySingleton() {}

        /**
         * Get Singleton Instance
         *
         * <p><b>Approach:</b> Thread-safe lazy initialization: returns the single shared instance, creating it on first access if needed.
         *
         * @return the singleton instance
         *
         * <p><b>Time:</b> O(1) time.
         * <br><b>Space:</b> O(1) space.
         */
        public static LazySingleton getInstance() {
            if (instance == null) {
                synchronized (LazySingleton.class) {
                    if (instance == null) {
                        instance = new LazySingleton();
                    }
                }
            }
            return instance;
        }

        public void serve(String request) {
            requestCount++;
            System.out.println("  [Lazy] Serving: " + request + " (total: " + requestCount + ")");
        }
    }

    // ======================== Enum Singleton (Recommended) ========================
    enum DatabaseConnection {
        INSTANCE;

        private int queryCount = 0;

        public void executeQuery(String sql) {
            queryCount++;
            System.out.println("  [Enum] Executing: " + sql + " (query #" + queryCount + ")");
        }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== Singleton Pattern ===\n");

        System.out.println("--- Eager Singleton ---");
        EagerSingleton s1 = EagerSingleton.getInstance();
        EagerSingleton s2 = EagerSingleton.getInstance();
        s1.serve("Login");
        s2.serve("Dashboard");
        System.out.println("  Same instance? " + (s1 == s2));

        System.out.println("\n--- Lazy Singleton (Double-Check Locking) ---");
        LazySingleton l1 = LazySingleton.getInstance();
        LazySingleton l2 = LazySingleton.getInstance();
        l1.serve("Search");
        l2.serve("Checkout");
        System.out.println("  Same instance? " + (l1 == l2));

        System.out.println("\n--- Enum Singleton (Best Practice) ---");
        DatabaseConnection.INSTANCE.executeQuery("SELECT * FROM users");
        DatabaseConnection.INSTANCE.executeQuery("INSERT INTO orders ...");
    }
}
