
# 📝 Online Examination System

A Java Swing GUI examination system: students log in, optionally update
their profile, then answer timed multiple-choice questions with
Next/Previous navigation and a live countdown that auto-submits when time
runs out.

Built as part of the **Oasis Infobyte Summer Internship Program (OIBSIP)** —
Java Development Track.

---

## ✨ Features

- 🔐 **Login screen** — username + password; on success, loads the profile
  screen
- 👤 **Profile update screen** — change your display name and/or password
  before starting (optional — leave password fields blank to keep the
  current one)
- 📄 **Exam screen** — one multiple-choice question at a time with 4 radio
  button options, plus a clickable question-number strip for quick
  navigation
- ↔️ **Next / Previous** buttons to move between questions, with your
  selections preserved as you navigate back and forth
- ⏱️ **Live countdown timer** (30:00 by default) always visible, turning
  amber under 5 minutes and red under 1 minute; **auto-submits the exam**
  the moment it hits zero
- ✅ **Manual Submit button** with an "Are you sure?" confirmation dialog
- 📊 **Result screen** — score (X out of Y), time taken, and a full
  question-by-question breakdown showing your answer vs. the correct
  answer, color-coded correct/incorrect/unanswered
- 🚪 **Session management** — clicking the window's close (X) button while
  an exam is in progress shows an "Are you sure you want to quit?"
  warning before actually closing
- 🚪 **Logout button** on the result screen, returning cleanly to the login
  screen

---

## 🛠️ Tech Stack

- **Language:** Java (JDK 17+ recommended)
- **UI:** Java Swing — `CardLayout` for screen switching, `javax.swing.Timer`
  for the countdown, `ButtonGroup` + `JRadioButton` for MCQ options
- **Design:** a distinct "exam paper" visual theme — cream background,
  serif (Georgia) font for question text to evoke a printed test, and a
  monospace digital-clock font for the countdown

---

## ▶️ How to Run

### Prerequisites
```bash
java -version
javac -version
```
(JDK 17 or later)

### Compile and run
```bash
javac OnlineExaminationSystem.java
java OnlineExaminationSystem
```

---

## 🎮 Demo Logins

| Username | Password |
|---|---|
| `student1` | `pass123` |
| `student2` | `pass456` |

The exam bank has 10 Java-fundamentals multiple-choice questions with a
30-minute timer.


## 👤 Author

Mushrifa
Java Development Intern — Oasis Infobyte (OIBSIP)

---

## 🙏 Acknowledgements

Built as a Java Development track task for OIBSIP.
`#oasisinfobyte` `#java` `#javadevelopment` `#internship`
