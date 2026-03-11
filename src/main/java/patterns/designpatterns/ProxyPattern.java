package patterns.designpatterns;

/**
 * 7. PROXY PATTERN (Structural)
 *
 * Provides a surrogate or placeholder for another object to control access to it.
 *
 * Types:
 * - Virtual Proxy: Delays expensive object creation until needed (lazy loading)
 * - Protection Proxy: Controls access based on permissions
 * - Logging Proxy: Adds logging around method calls
 *
 * Key idea: Same interface as the real object, but adds a layer of control.
 */
public class ProxyPattern {

    // ======================== Subject Interface ========================
    interface Database {
        void query(String sql);
    }

    // ======================== Real Subject ========================
    static class RealDatabase implements Database {
        RealDatabase() {
            System.out.println("  [RealDatabase] Heavy initialization (connecting, pooling...)");
        }

        @Override
        public void query(String sql) {
            System.out.println("  [RealDatabase] Executing: " + sql);
        }
    }

    // ======================== Virtual Proxy (Lazy Loading) ========================
    static class LazyDatabaseProxy implements Database {
        private RealDatabase realDatabase;

        @Override
        public void query(String sql) {
            if (realDatabase == null) {
                System.out.println("  [Proxy] First use — creating real database...");
                realDatabase = new RealDatabase();
            }
            realDatabase.query(sql);
        }
    }

    // ======================== Protection Proxy (Access Control) ========================
    static class SecureDatabaseProxy implements Database {
        private final RealDatabase realDatabase = new RealDatabase();
        private final String userRole;

        SecureDatabaseProxy(String userRole) {
            this.userRole = userRole;
        }

        @Override
        public void query(String sql) {
            if (sql.toUpperCase().startsWith("DROP") && !"ADMIN".equals(userRole)) {
                System.out.println("  [SecureProxy] ACCESS DENIED: " + userRole + " cannot execute DROP");
                return;
            }
            System.out.println("  [SecureProxy] Access granted for " + userRole);
            realDatabase.query(sql);
        }
    }

    // ======================== Logging Proxy ========================
    static class LoggingDatabaseProxy implements Database {
        private final Database wrapped;

        LoggingDatabaseProxy(Database db) { this.wrapped = db; }

        @Override
        public void query(String sql) {
            long start = System.nanoTime();
            System.out.println("  [LogProxy] >>> " + sql);
            wrapped.query(sql);
            long elapsed = (System.nanoTime() - start) / 1000;
            System.out.println("  [LogProxy] <<< Completed in " + elapsed + " µs");
        }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== Proxy Pattern ===\n");

        System.out.println("--- Virtual Proxy (Lazy Loading) ---");
        Database lazy = new LazyDatabaseProxy();
        System.out.println("  Proxy created — no real DB yet");
        lazy.query("SELECT * FROM users");
        lazy.query("SELECT * FROM orders");

        System.out.println("\n--- Protection Proxy (Access Control) ---");
        Database adminDb = new SecureDatabaseProxy("ADMIN");
        adminDb.query("DROP TABLE temp");

        Database userDb = new SecureDatabaseProxy("USER");
        userDb.query("SELECT * FROM products");
        userDb.query("DROP TABLE users");

        System.out.println("\n--- Logging Proxy ---");
        Database logged = new LoggingDatabaseProxy(new LazyDatabaseProxy());
        logged.query("SELECT COUNT(*) FROM orders");
    }
}
