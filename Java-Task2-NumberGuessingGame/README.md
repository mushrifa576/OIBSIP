
# Number Guessing Game

A Java Swing desktop game where the computer generates a random number and
the player tries to guess it, receiving **"Too High!"**, **"Too Low!"**, or
**"Correct!"** hints until they guess the number or run out of attempts.

Built as part of the **Oasis Infobyte Summer Internship Program (OIBSIP)** —
Java Development Track, Task 2.


## Features

- **Random Number Generation:** A new random number is generated at the start
  of every round.
- **Guess Input:** Player enters their guess through a `JTextField`.
- **Instant Feedback:** Displays **"Too High!"**, **"Too Low!"**, or
  **"Correct!"** after every guess.
- **Attempt Counter:** Shows the number of attempts used during the current
  round.
- **Maximum Attempts:** The player has a limited number of attempts to guess
  the number.
- **You Lost! Message:** If the maximum number of attempts is reached, the
  game ends and reveals the correct number.
- **Play Again:** Players can start a new round after completing a round.
- **Score Tracking:** Keeps track of completed rounds and displays a summary
  of the player's performance.
- **Difficulty Levels:** Players can select different difficulty levels with
  different number ranges and attempt limits.

### Difficulty Levels

| Difficulty | Number Range | Maximum Attempts |
|------------|--------------|------------------|
| **Easy** | 1–50 | 10 |
| **Medium** | 1–100 | 7 |
| **Hard** | 1–200 | 5 |


## Tech Stack

- **Language:** Java (JDK 17+ recommended)
- **UI:** Java Swing
- **Core concepts used:** `java.util.Random`, `JFrame`, `JPanel`,
  `JTextField`, `JButton`, `JLabel`, `JList`, `DefaultListModel`,
  `ActionListener`, event-driven programming, random number generation,
  conditional statements, loops, and score tracking


## How to Run

### Prerequisites

- Java Development Kit (JDK) 17 or later installed

Verify Java installation:

```bash
java -version
javac -version

----
## 👤 Author

Mushrifa T K M
Java Development Intern — Oasis Infobyte (OIBSIP)

---

## 🙏 Acknowledgements

Built as a Java Development track task for OIBSIP.
`#oasisinfobyte` `#java` `#javadevelopment` `#internship`
