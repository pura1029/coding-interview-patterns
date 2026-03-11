package patterns.designpatterns;

/**
 * 3. BUILDER PATTERN (Creational)
 *
 * Constructs complex objects step by step, separating construction from representation.
 * Avoids telescoping constructors and allows optional parameters.
 *
 * When to use:
 * - Objects with many optional parameters
 * - Immutable objects that need flexible construction
 * - Complex object creation (HTTP requests, SQL queries, UI components)
 *
 * Key idea: Chain setter-like methods, call build() to get the final object.
 */
public class BuilderPattern {

    // ======================== Without Builder (Telescoping Constructors) ========================
    static class BadUser {
        String name, email, phone, address, company;

        BadUser(String name, String email) { this(name, email, null, null, null); }
        BadUser(String name, String email, String phone) { this(name, email, phone, null, null); }
        BadUser(String name, String email, String phone, String address) { this(name, email, phone, address, null); }
        BadUser(String name, String email, String phone, String address, String company) {
            this.name = name; this.email = email; this.phone = phone;
            this.address = address; this.company = company;
        }
    }

    // ======================== With Builder ========================
    static class User {
        private final String name;
        private final String email;
        private final String phone;
        private final String address;
        private final String company;
        private final int age;

        private User(Builder builder) {
            this.name = builder.name;
            this.email = builder.email;
            this.phone = builder.phone;
            this.address = builder.address;
            this.company = builder.company;
            this.age = builder.age;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("User{name='" + name + "', email='" + email + "'");
            if (phone != null) sb.append(", phone='").append(phone).append("'");
            if (address != null) sb.append(", address='").append(address).append("'");
            if (company != null) sb.append(", company='").append(company).append("'");
            if (age > 0) sb.append(", age=").append(age);
            sb.append("}");
            return sb.toString();
        }

        static class Builder {
            private final String name;
            private final String email;
            private String phone;
            private String address;
            private String company;
            private int age;

            Builder(String name, String email) {
                this.name = name;
                this.email = email;
            }

            Builder phone(String phone) { this.phone = phone; return this; }
            Builder address(String address) { this.address = address; return this; }
            Builder company(String company) { this.company = company; return this; }
            Builder age(int age) { this.age = age; return this; }

            User build() { return new User(this); }
        }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== Builder Pattern ===\n");

        System.out.println("--- Minimal user (only required fields) ---");
        User simple = new User.Builder("Alice", "alice@example.com").build();
        System.out.println("  " + simple);

        System.out.println("\n--- User with some optional fields ---");
        User withPhone = new User.Builder("Bob", "bob@example.com")
                .phone("+1-555-0123")
                .age(30)
                .build();
        System.out.println("  " + withPhone);

        System.out.println("\n--- Fully loaded user ---");
        User full = new User.Builder("Carol", "carol@example.com")
                .phone("+1-555-0456")
                .address("123 Main St")
                .company("TechCorp")
                .age(28)
                .build();
        System.out.println("  " + full);

        System.out.println("\nBenefit: No telescoping constructors, readable chaining, immutable result.");
    }
}
