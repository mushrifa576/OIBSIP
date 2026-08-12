# 🎯 Number Guessing Game

A Java Swing desktop game where the computer picks a secret number and the
player tries to guess it, receiving "Too High!" / "Too Low!" hints until
they either guess correctly or run out of attempts.

Built as part of the **Oasis Infobyte Summer Internship Program (OIBSIP)** —
Java Development Track, Task 2.


## ✨ Features

- 🎲 Random number generated at the start of every round, in the range
  matching the selected difficulty
- ⌨️ Guess entered through a `JTextField` (press **Enter** or click **Guess**)
- 🔊 Instant feedback after every guess: **"Too High!"**, **"Too Low!"**, or
  **"Correct!"**
- 🔢 Live attempt counter, always visible during the round
- ⛔ Maximum attempts per round — the game ends with **"You Lost!"** and
  reveals the number if you run out of tries
- 🔁 **Play Again?** prompt after every round (Yes/No)
- 🏆 Running score/history log: `Round X — guessed in Y attempts`, with a
  win/loss summary
- 🎚️ **Bonus — Difficulty levels:**
  | Difficulty | Range   | Max Attempts |
  |------------|---------|--------------|
  | Easy       | 1–50    | 10           |
  | Medium     | 1–100   | 7            |
  | Hard       | 1–200   | 5            |

---

## 🛠️ Tech Stack

- **Language:** Java (JDK 17+ recommended)
- **UI:** Java Swing (custom-styled components — rounded buttons, dark
  theme, segmented difficulty picker, attempt-dot tracker, styled history
  list)
- **Core concepts used:** `java.util.Random`, event-driven Swing UI,
  `ActionListener`, custom `JComponent`/`JPanel` painting, `DefaultListModel`

---

## ▶️ How to Run

### Prerequisites
- Java Development Kit (JDK) 17 or later installed
- Verify with:
  ```bash
  java -version
  javac -version
  ```

### Compile and run
```bash
javac NumberGuessingGame.java
java NumberGuessingGame
```

The game window will open directly — no additional setup or dependencies
required.

---

## 🎮 How to Play

1. Pick a difficulty (**Easy**, **Medium**, or **Hard**).
2. Click **Start New Round**.
3. Enter a guess within the shown range and click **Guess** (or press Enter).
4. Read the hint — **Too High!**, **Too Low!**, or **Correct!** — and keep
   guessing until you either win or run out of attempts.
5. When the round ends, choose **Play Again** to start another round or stop
   and review your round history at the bottom of the window.

---

## 📁 Project Structure

```
OIBSIP/Java-Task2-NumberGuessingGame/
├── NumberGuessingGame.java   # Full source code (single file)
├── README.md                 # This file
└── screenshot.png            # App screenshot (add before submitting)
```

---

## 👤 Author

Mushrifa T K M
Java Development Intern — Oasis Infobyte (OIBSIP)

---

## 🙏 Acknowledgements

Built as Task 2 of the OIBSIP Java Development track.
`#oasisinfobyte` `#java` `#javadevelopment` `#internship`
