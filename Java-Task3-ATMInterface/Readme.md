# 🏧 ATM Interface

A console-based Java simulation of an ATM machine — authenticate with a
User ID and PIN, then withdraw, deposit, transfer between accounts, and
review a running transaction history.

Built as part of the **Oasis Infobyte Summer Internship Program (OIBSIP)** —
Java Development Track.

---

## ✨ Features

- 🔐 **Login** — User ID + PIN prompt; **access denied after 3 incorrect
  attempts**, with a generic error message that doesn't reveal whether the
  ID or the PIN was wrong
- 📋 **Main menu** after login:
  1. **Transaction History** — every transaction made so far this session
  2. **Withdraw** — validates sufficient balance before allowing it
  3. **Deposit** — adds funds and logs the transaction
  4. **Transfer** — moves money to another account by User ID, validating
     the recipient exists and the sender has enough balance, and updates
     **both** accounts
  5. **Quit** — prints a goodbye message and exits
- ⚠️ **"Insufficient Funds"** shown before any Withdraw or Transfer that
  would overdraw the account
- 🧾 All transactions are stored in an `ArrayList<Transaction>` per account
  and printed clearly (type, amount, resulting balance, description,
  timestamp) in Transaction History
- 🛡️ Input is validated throughout — non-numeric menu choices, non-numeric
  or negative/zero amounts, and unknown recipient IDs are all handled
  gracefully without crashing

---

## 🛠️ Tech Stack

- **Language:** Java (console application, JDK 17+ recommended)
- **Design:** Object-oriented, split across **5 distinct classes**:

| Class | Responsibility |
|---|---|
| `Main` | Entry point — wires up the `Bank` and `ATM` and starts the session |
| `ATM` | Console I/O, login flow, and the main menu loop |
| `Bank` | Owns all `Account`s; handles authentication and transfers |
| `Account` | A single account's balance, PIN, and transaction history |
| `Transaction` | An immutable record of one withdrawal/deposit/transfer |

---

## ▶️ How to Run

### Prerequisites
- JDK 17 or later. Verify with:
  ```bash
  java -version
  javac -version
  ```

### Compile
All five `.java` files must be in the same folder:
```bash
javac *.java
```

### Run
```bash
java Main
```

---

## 🎮 Demo Accounts

The app seeds three demo accounts in memory on startup — no database or
setup required:

| User ID | PIN | Starting Balance | Holder |
|---|---|---|---|
| `1001` | `1234` | $5,000.00 | Mushrifa |
| `1002` | `5678` | $3,000.00 | Aravind |
| `1003` | `0000` | $10,000.00 | Priya |

Log in as `1001`, then try transferring to `1002` or `1003` to see both
accounts' balances update.

> **Note:** Balances and transaction history reset every time you restart
> the program — this is an in-memory simulation for a single session, per
> the task's "current session" requirement.

---

## 📁 Project Structure

```
OIBSIP/Java-Task3-ATMInterface/
├── Main.java
├── ATM.java
├── Bank.java
├── Account.java
├── Transaction.java
├── README.md
└── screenshot.png   (add before submitting)
```

---

## 👤 Author

**Mushrifa T K M**
Java Development Intern — Oasis Infobyte (OIBSIP)

---

## 🙏 Acknowledgements

Built as a Java Development track task for OIBSIP.
`#oasisinfobyte` `#java` `#javadevelopment` `#internship`
