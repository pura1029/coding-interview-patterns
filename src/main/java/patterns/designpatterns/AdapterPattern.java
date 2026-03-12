package patterns.designpatterns;

/**
 * 4. ADAPTER PATTERN (Structural)
 *
 * Converts the interface of a class into another interface that clients expect.
 * Allows incompatible interfaces to work together.
 *
 * When to use:
 * - Integrating legacy code or third-party libraries with different interfaces
 * - Wrapping an existing class to match a new interface
 * - Converting data formats (XML → JSON, Celsius → Fahrenheit)
 *
 * Key idea: Create a wrapper class that translates between two incompatible interfaces.
 */
public class AdapterPattern {

    // ======================== Target Interface ========================
    interface MediaPlayer {
        void play(String filename);
    }

    // ======================== Existing (Incompatible) Classes ========================
    static class VlcPlayer {
        public void playVlc(String filename) {
            System.out.println("  VlcPlayer playing: " + filename);
        }
    }

    static class Mp4Player {
        public void playMp4(String filename) {
            System.out.println("  Mp4Player playing: " + filename);
        }
    }

    // ======================== Adapters ========================
    static class VlcAdapter implements MediaPlayer {
        private final VlcPlayer vlcPlayer = new VlcPlayer();

        @Override
        public void play(String filename) {
            vlcPlayer.playVlc(filename);
        }
    }

    static class Mp4Adapter implements MediaPlayer {
        private final Mp4Player mp4Player = new Mp4Player();

        @Override
        public void play(String filename) {
            mp4Player.playMp4(filename);
        }
    }

    // ======================== Real-World Example: Payment Adapter ========================
    interface PaymentProcessor {
        void pay(double amount, String currency);
    }

    static class LegacyPayPal {
        public void makePayment(int amountInCents) {
            System.out.println("  LegacyPayPal charged: " + amountInCents + " cents");
        }
    }

    static class PayPalAdapter implements PaymentProcessor {
        private final LegacyPayPal legacyPayPal = new LegacyPayPal();

        @Override
        public void pay(double amount, String currency) {
            int cents = (int) (amount * 100);
            System.out.println("  Adapter converting $" + amount + " → " + cents + " cents");
            legacyPayPal.makePayment(cents);
        }
    }

    // ======================== DEMO ========================
    public static void main(String[] args) {
        System.out.println("=== Adapter Pattern ===\n");

        System.out.println("--- Media Player Adapters ---");
        // new VlcAdapter() → adapter wrapping VlcPlayer; implements MediaPlayer interface; play() internally calls vlcPlayer.playVlc() — interface bridging
        MediaPlayer vlc = new VlcAdapter();
        // new Mp4Adapter() → adapts Mp4Player to MediaPlayer interface; play() delegates to mp4Player.playMp4() — same interface, different implementation
        MediaPlayer mp4 = new Mp4Adapter();
        vlc.play("movie.vlc");
        mp4.play("song.mp4");

        System.out.println("\n--- Payment Adapter (dollars → cents) ---");
        // new PayPalAdapter() → adapts PayPal's API (cents) to PaymentProcessor interface (dollars); pay() converts: (int)(amount * 100) before delegating
        PaymentProcessor paypal = new PayPalAdapter();
        paypal.pay(49.99, "USD");
        paypal.pay(120.00, "USD");

        System.out.println("\nBenefit: Legacy/third-party code works with new interfaces without modification.");
    }
}
