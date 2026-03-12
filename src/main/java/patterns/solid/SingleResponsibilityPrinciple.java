package patterns.solid;

/**
 * S - Single Responsibility Principle (SRP)
 *
 * A class should have only one reason to change.
 * Each class should focus on a single concern or responsibility.
 *
 * VIOLATION: A monolithic User class handling authentication, profile, and email.
 * SOLUTION:  Split into UserAuth, UserProfile, and EmailService — each with one job.
 */
public class SingleResponsibilityPrinciple {

    // ======================== BAD (Violates SRP) ========================
    // This class has THREE reasons to change:
    //   1. Authentication logic changes
    //   2. Profile display changes
    //   3. Email sending changes

    static class UserGodClass {
        private String name;
        private String email;
        private String password;

        public UserGodClass(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
        }

        public boolean authenticate(String inputPassword) {
            return this.password.equals(inputPassword);
        }

        public String getProfileInfo() {
            return "Name: " + name + ", Email: " + email;
        }

        public void sendEmail(String subject, String body) {
            System.out.println("Sending email to " + email + ": [" + subject + "] " + body);
        }
    }

    // ======================== GOOD (Follows SRP) ========================
    // Each class has exactly ONE responsibility.

    static class User {
        private final String name;
        private final String email;
        private final String password;

        public User(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }

    static class UserAuth {
        public boolean authenticate(User user, String inputPassword) {
            return user.getPassword().equals(inputPassword);
        }
    }

    static class UserProfile {
        public String getProfileInfo(User user) {
            return "Name: " + user.getName() + ", Email: " + user.getEmail();
        }
    }

    static class EmailService {
        public void sendEmail(String to, String subject, String body) {
            System.out.println("Sending email to " + to + ": [" + subject + "] " + body);
        }
    }

    // ======================== DEMO ========================

    public static void main(String[] args) {
        System.out.println("=== Single Responsibility Principle (SRP) ===\n");

        System.out.println("--- BAD: God class doing everything ---");
        // new UserGodClass() → single class handles auth, profile, email — violates SRP; any change (email provider, auth logic) forces modifying this one class
        UserGodClass godUser = new UserGodClass("Alice", "alice@example.com", "secret123");
        // authenticate() uses if (password.equals(stored)) — auth logic mixed with profile and email in same class
        System.out.println("Auth: " + godUser.authenticate("secret123"));
        System.out.println("Profile: " + godUser.getProfileInfo());
        godUser.sendEmail("Welcome", "Hello Alice!");

        System.out.println("\n--- GOOD: Separated responsibilities ---");
        // new User("Alice", "alice@email.com") → simple data object with single responsibility (hold user data); no business logic here
        // new User() → data holder only; new UserAuth() → auth logic only; new EmailService() → email only — each class has single reason to change
        User user = new User("Alice", "alice@example.com", "secret123");
        // new UserAuth() → separate class: authenticate uses if (user.getPassword().equals(input)) — isolated responsibility
        UserAuth auth = new UserAuth();
        // new UserProfile() → creates object
        UserProfile profile = new UserProfile();
        // new EmailService() → separate class for sending emails; changing email provider only affects this class
        EmailService emailService = new EmailService();

        System.out.println("Auth: " + auth.authenticate(user, "secret123"));
        System.out.println("Profile: " + profile.getProfileInfo(user));
        emailService.sendEmail(user.getEmail(), "Welcome", "Hello Alice!");

        System.out.println("\nBenefit: Change email provider? Only EmailService changes.");
        System.out.println("Benefit: Change auth to hashing? Only UserAuth changes.");
    }
}
