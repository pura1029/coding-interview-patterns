package patterns.designpatterns;

/**
 * 15. CHAIN OF RESPONSIBILITY PATTERN (Behavioral)
 *
 * Passes a request along a chain of handlers. Each handler decides either
 * to process the request or pass it to the next handler in the chain.
 *
 * When to use:
 * - Middleware pipelines (auth → logging → rate-limiting → handler)
 * - Event bubbling, exception handling chains
 * - Approval workflows (manager → director → VP)
 * - Input validation chains
 *
 * Key idea: Each handler has a reference to the next; if it can't handle, it delegates forward.
 */
public class ChainOfResponsibilityPattern {

    // ======================== Handler Interface ========================
    static abstract class SupportHandler {
        protected SupportHandler next;

        public SupportHandler setNext(SupportHandler next) {
            this.next = next;
            return next;
        }

        public void handle(String issue, int severity) {
            if (canHandle(severity)) {
                process(issue, severity);
            } else if (next != null) {
                System.out.println("  [" + name() + "] Escalating...");
                next.handle(issue, severity);
            } else {
                System.out.println("  [End of chain] No handler for severity " + severity);
            }
        }

        abstract boolean canHandle(int severity);
        abstract void process(String issue, int severity);
        abstract String name();
    }

    // ======================== Concrete Handlers ========================
    static class BotSupport extends SupportHandler {
        @Override
        boolean canHandle(int severity) { return severity <= 1; }
        @Override
        void process(String issue, int severity) {
            System.out.println("  [Bot] Auto-resolved: " + issue);
        }
        @Override
        String name() { return "Bot"; }
    }

    static class Level1Support extends SupportHandler {
        @Override
        boolean canHandle(int severity) { return severity <= 3; }
        @Override
        void process(String issue, int severity) {
            System.out.println("  [L1 Agent] Handling: " + issue + " (severity: " + severity + ")");
        }
        @Override
        String name() { return "L1 Agent"; }
    }

    static class Level2Support extends SupportHandler {
        @Override
        boolean canHandle(int severity) { return severity <= 5; }
        @Override
        void process(String issue, int severity) {
            System.out.println("  [L2 Engineer] Investigating: " + issue + " (severity: " + severity + ")");
        }
        @Override
        String name() { return "L2 Engineer"; }
    }

    static class ManagerSupport extends SupportHandler {
        @Override
        boolean canHandle(int severity) { return true; }
        @Override
        void process(String issue, int severity) {
            System.out.println("  [Manager] CRITICAL escalation: " + issue + " (severity: " + severity + ")");
        }
        @Override
        String name() { return "Manager"; }
    }

    // ======================== Middleware Chain Example ========================
    static abstract class Middleware {
        protected Middleware next;

        public Middleware linkWith(Middleware next) {
            this.next = next;
            return next;
        }

        public boolean check(String user, String password) {
            if (next != null) return next.check(user, password);
            return true;
        }
    }

    static class AuthMiddleware extends Middleware {
        @Override
        public boolean check(String user, String password) {
            if (!"admin".equals(user) || !"secret".equals(password)) {
                System.out.println("    [Auth] Failed: invalid credentials");
                return false;
            }
            System.out.println("    [Auth] Passed");
            return super.check(user, password);
        }
    }

    static class RateLimitMiddleware extends Middleware {
        private int requestCount = 0;
        @Override
        public boolean check(String user, String password) {
            requestCount++;
            if (requestCount > 3) {
                System.out.println("    [RateLimit] Blocked: too many requests");
                return false;
            }
            System.out.println("    [RateLimit] Passed (" + requestCount + "/3)");
            return super.check(user, password);
        }
    }

    static class LoggingMiddleware extends Middleware {
        @Override
        public boolean check(String user, String password) {
            System.out.println("    [Logging] Request from: " + user);
            return super.check(user, password);
        }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== Chain of Responsibility Pattern ===\n");

        System.out.println("--- Support Ticket Chain ---");
        BotSupport bot = new BotSupport();
        Level1Support l1 = new Level1Support();
        Level2Support l2 = new Level2Support();
        ManagerSupport mgr = new ManagerSupport();
        bot.setNext(l1).setNext(l2).setNext(mgr);

        System.out.println("Ticket: \"Password reset\" (severity 1)");
        bot.handle("Password reset", 1);
        System.out.println("\nTicket: \"App not loading\" (severity 3)");
        bot.handle("App not loading", 3);
        System.out.println("\nTicket: \"Data corruption\" (severity 5)");
        bot.handle("Data corruption", 5);
        System.out.println("\nTicket: \"System down\" (severity 8)");
        bot.handle("System down", 8);

        System.out.println("\n--- Middleware Chain ---");
        AuthMiddleware auth = new AuthMiddleware();
        RateLimitMiddleware rateLimit = new RateLimitMiddleware();
        LoggingMiddleware logging = new LoggingMiddleware();
        logging.linkWith(rateLimit).linkWith(auth);

        System.out.println("Request 1:");
        logging.check("admin", "secret");
        System.out.println("Request 2:");
        logging.check("admin", "secret");
        System.out.println("Request 3:");
        logging.check("admin", "secret");
        System.out.println("Request 4 (rate limited):");
        logging.check("admin", "secret");

        System.out.println("\nBenefit: Add/remove/reorder handlers without changing client code.");
    }
}
