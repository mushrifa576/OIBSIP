import java.util.Scanner;

/**
 * Main
 * ----
 * Entry point. Wires up a Bank (with demo accounts already seeded) and an
 * ATM, then starts the session.
 *
 * Demo accounts you can log in with:
 *   User ID 1001 / PIN 1234  (Mushrifa, starting balance $5000.00)
 *   User ID 1002 / PIN 5678  (Aravind,  starting balance $3000.00)
 *   User ID 1003 / PIN 0000  (Priya,    starting balance $10000.00)
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Bank bank = new Bank();
        ATM atm = new ATM(bank, scanner);
        atm.start();
        scanner.close();
    }
}
