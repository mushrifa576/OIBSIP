import java.util.List;
import java.util.Scanner;

/**
 * ATM
 * ---
 * The console-facing class. Owns the Scanner, drives the login flow
 * (max 3 attempts), and runs the main menu loop once a user is
 * authenticated. Delegates all actual balance/ledger changes to Account
 * and Bank so this class stays focused on I/O and flow control.
 */
public class ATM {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private final Bank bank;
    private final Scanner scanner;

    public ATM(Bank bank, Scanner scanner) {
        this.bank = bank;
        this.scanner = scanner;
    }

    public void start() {
        printBanner();
        Account account = login();
        if (account == null) {
            return; // access already denied and explained inside login()
        }
        System.out.println("\nWelcome, " + account.getHolderName() + "!");
        runMenuLoop(account);
    }

    private void printBanner() {
        System.out.println("========================================");
        System.out.println("        WELCOME TO JAVA BANK ATM");
        System.out.println("========================================");
    }

    // =====================================================================
    // Login
    // =====================================================================

    private Account login() {
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            System.out.print("\nEnter User ID: ");
            String userId = scanner.nextLine().trim();
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            Account account = bank.authenticate(userId, pin);
            if (account != null) {
                return account;
            }

            int remaining = MAX_LOGIN_ATTEMPTS - attempt;
            if (remaining > 0) {
                System.out.println("Incorrect User ID or PIN. Attempts remaining: " + remaining);
            }
        }
        System.out.println("\nToo many incorrect attempts. Access denied.");
        return null;
    }

    // =====================================================================
    // Main menu
    // =====================================================================

    private void runMenuLoop(Account account) {
        boolean running = true;
        while (running) {
            printMenu(account);
            int choice = readMenuChoice();
            switch (choice) {
                case 1:
                    printTransactionHistory(account);
                    break;
                case 2:
                    handleWithdraw(account);
                    break;
                case 3:
                    handleDeposit(account);
                    break;
                case 4:
                    handleTransfer(account);
                    break;
                case 5:
                    System.out.println("\nThank you for banking with us, " + account.getHolderName()
                            + ". Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Please enter a number between 1 and 5.");
            }
        }
    }

    private void printMenu(Account account) {
        System.out.println("\n----------------------------------------");
        System.out.printf("Current Balance: $%.2f%n", account.getBalance());
        System.out.println("1. Transaction History");
        System.out.println("2. Withdraw");
        System.out.println("3. Deposit");
        System.out.println("4. Transfer");
        System.out.println("5. Quit");
        System.out.print("Choose an option (1-5): ");
    }

    private int readMenuChoice() {
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1; // falls into the default case in the switch
        }
    }

    // =====================================================================
    // Menu option 1 — Transaction History
    // =====================================================================

    private void printTransactionHistory(Account account) {
        List<Transaction> history = account.getHistory();
        System.out.println("\n--- Transaction History (this session) ---");
        if (history.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        int i = 1;
        for (Transaction t : history) {
            System.out.println(i++ + ". " + t.toHistoryLine());
        }
    }

    // =====================================================================
    // Menu option 2 — Withdraw
    // =====================================================================

    private void handleWithdraw(Account account) {
        Double amount = promptAmount("Enter amount to withdraw: $");
        if (amount == null) return;

        if (!account.hasSufficientFunds(amount)) {
            System.out.println("Insufficient Funds");
            return;
        }
        account.recordWithdrawal(amount);
        System.out.printf("Withdrawal successful. New balance: $%.2f%n", account.getBalance());
    }

    // =====================================================================
    // Menu option 3 — Deposit
    // =====================================================================

    private void handleDeposit(Account account) {
        Double amount = promptAmount("Enter amount to deposit: $");
        if (amount == null) return;

        account.recordDeposit(amount);
        System.out.printf("Deposit successful. New balance: $%.2f%n", account.getBalance());
    }

    // =====================================================================
    // Menu option 4 — Transfer
    // =====================================================================

    private void handleTransfer(Account account) {
        System.out.print("Enter recipient User ID: ");
        String recipientId = scanner.nextLine().trim();

        if (recipientId.equals(account.getUserId())) {
            System.out.println("You cannot transfer to your own account.");
            return;
        }

        Account recipient = bank.getAccount(recipientId);
        if (recipient == null) {
            System.out.println("Recipient account not found.");
            return;
        }

        Double amount = promptAmount("Enter amount to transfer: $");
        if (amount == null) return;

        if (!account.hasSufficientFunds(amount)) {
            System.out.println("Insufficient Funds");
            return;
        }

        boolean success = bank.transfer(account, recipient, amount);
        if (success) {
            System.out.printf("Transfer of $%.2f to %s (%s) successful. New balance: $%.2f%n",
                    amount, recipient.getHolderName(), recipient.getUserId(), account.getBalance());
        } else {
            // Defensive fallback; hasSufficientFunds() above should already catch this.
            System.out.println("Insufficient Funds");
        }
    }

    // =====================================================================
    // Shared input helper
    // =====================================================================

    /**
     * Prompts for a dollar amount and validates it: must be a real number
     * and strictly positive. Returns null (after printing an error) if the
     * input was invalid, so callers can just bail out on null.
     */
    private Double promptAmount(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        double amount;
        try {
            amount = Double.parseDouble(line);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid numeric amount.");
            return null;
        }
        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return null;
        }
        // Round to 2 decimal places, like real currency.
        return Math.round(amount * 100.0) / 100.0;
    }
}
