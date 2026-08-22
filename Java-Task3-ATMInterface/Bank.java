import java.util.HashMap;
import java.util.Map;

/**
 * Bank
 * ----
 * Owns the full set of Accounts and is the only class allowed to look one
 * up by User ID. Handles authentication and cross-account transfers so
 * that the transfer logic lives in exactly one place.
 */
public class Bank {

    private final Map<String, Account> accounts = new HashMap<>();

    public Bank() {
        seedDemoAccounts();
    }

    private void seedDemoAccounts() {
        accounts.put("1001", new Account("1001", "1234", "Mushrifa", 5000.00));
        accounts.put("1002", new Account("1002", "5678", "Aravind", 3000.00));
        accounts.put("1003", new Account("1003", "0000", "Priya", 10000.00));
    }

    /**
     * Returns the Account if userId/pin match, otherwise null.
     * Deliberately doesn't distinguish "unknown user" from "wrong PIN" in
     * its return value — the caller should show one generic message for
     * both, which is standard practice so a wrong PIN attempt can't be
     * used to probe which user IDs exist.
     */
    public Account authenticate(String userId, String pin) {
        Account account = accounts.get(userId);
        if (account != null && account.checkPin(pin)) {
            return account;
        }
        return null;
    }

    /** Looks up an account by ID for transfer recipients. Null if not found. */
    public Account getAccount(String userId) {
        return accounts.get(userId);
    }

    /**
     * Moves money from one account to another, logging a TRANSFER_OUT on
     * the source and a TRANSFER_IN on the destination. Returns false
     * (without changing anything) if the source doesn't have enough funds.
     */
    public boolean transfer(Account from, Account to, double amount) {
        if (!from.hasSufficientFunds(amount)) {
            return false;
        }
        from.recordTransferOut(amount, to.getUserId());
        to.recordTransferIn(amount, from.getUserId());
        return true;
    }
}
