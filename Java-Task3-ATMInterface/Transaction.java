import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Transaction
 * -----------
 * An immutable record of a single banking operation performed on an
 * Account. Every Withdraw, Deposit, and Transfer creates one (or two, in
 * the case of a Transfer) of these, which get stored in that Account's
 * transaction history ArrayList.
 */
public class Transaction {

    public enum Type {
        WITHDRAWAL, DEPOSIT, TRANSFER_OUT, TRANSFER_IN
    }

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final String description;
    private final LocalDateTime timestamp;

    public Transaction(Type type, double amount, double balanceAfter, String description) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /** True for money leaving the account (Withdrawal, Transfer Out). */
    private boolean isDebit() {
        return type == Type.WITHDRAWAL || type == Type.TRANSFER_OUT;
    }

    /** A single formatted line used when printing the transaction history. */
    public String toHistoryLine() {
        String sign = isDebit() ? "-" : "+";
        return String.format("[%s] %-14s %s$%-10.2f Balance: $%-10.2f %s",
                timestamp.format(TIME_FMT), type, sign, amount, balanceAfter, description);
    }

    @Override
    public String toString() {
        return toHistoryLine();
    }
}
