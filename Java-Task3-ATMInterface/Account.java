import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Account
 * -------
 * Represents a single bank account: its identity (user ID, PIN, holder
 * name), its current balance, and the ArrayList of every Transaction made
 * against it during this session.
 *
 * All balance changes are made through the record*() methods so that a
 * Transaction is always logged alongside the balance update — the two can
 * never get out of sync.
 */
public class Account {

    private final String userId;
    private final String pin;
    private final String holderName;
    private double balance;
    private final List<Transaction> history = new ArrayList<>();

    public Account(String userId, String pin, String holderName, double openingBalance) {
        this.userId = userId;
        this.pin = pin;
        this.holderName = holderName;
        this.balance = openingBalance;
    }

    public String getUserId() {
        return userId;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public boolean checkPin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    /** Read-only view of this session's transaction history, oldest first. */
    public List<Transaction> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public boolean hasSufficientFunds(double amount) {
        return balance >= amount;
    }

    // ---- Balance-changing operations (each one logs its own Transaction) ----

    public void recordDeposit(double amount) {
        balance += amount;
        history.add(new Transaction(Transaction.Type.DEPOSIT, amount, balance, "Cash deposit"));
    }

    public void recordWithdrawal(double amount) {
        balance -= amount;
        history.add(new Transaction(Transaction.Type.WITHDRAWAL, amount, balance, "Cash withdrawal"));
    }

    public void recordTransferOut(double amount, String toUserId) {
        balance -= amount;
        history.add(new Transaction(Transaction.Type.TRANSFER_OUT, amount, balance, "Transfer to " + toUserId));
    }

    public void recordTransferIn(double amount, String fromUserId) {
        balance += amount;
        history.add(new Transaction(Transaction.Type.TRANSFER_IN, amount, balance, "Transfer from " + fromUserId));
    }
}
