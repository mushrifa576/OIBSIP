# 📚 Digital Library Management System

A web-based library management system built with **Spring Boot**, **JPA/Hibernate**, **SQLite**, and **Thymeleaf** — with separate Admin and User roles, a full book catalogue, issuing/returns with automatic overdue fines, advance booking (reservations), and a contact/query form.

Built as part of the **Oasis Infobyte Summer Internship Program (OIBSIP)** — Java Development Track.

---

## ⚠️ Please read this before anything else

This is a much bigger project than the console/Swing tasks — it's a real Spring Boot web app with a database and a frontend. **I was not able to compile or run this project in my own environment** while building it, because that environment has no access to Maven Central (the repository Maven needs to download Spring Boot's dependencies from). Every other task you've had from me was compiled and tested; this one wasn't.

What I *did* do to maximize confidence before handing it to you:
- Verified the exact dependency versions (Spring Boot, SQLite driver, Hibernate's SQLite dialect) are real, current, and compatible with each other via web research
- Manually traced every controller method against the service layer and every service method against the repository layer to make sure names, types, and parameter counts all line up exactly
- Ran automated checks across all 20 Java files for balanced braces/parentheses and correct public-class-to-filename matching
- Ran automated checks across all 13 HTML templates for valid structure and balanced quotes
- Re-derived every Spring Data JPA query method name against the actual entity field names to make sure they'll resolve correctly at startup

That's a thorough review, but it is **not the same as actually running it**. Please build and test this yourself before recording your demo video, and don't be surprised if there's a small hiccup on first run — treat this build the way you'd treat code from a tutorial: read it, run it, and be ready to debug alongside me if something doesn't come up cleanly. I'm glad to help fix anything that comes up, the same way we worked through the Task 1 date-parsing bug together.

---

## ✨ Features

### Admin Module
- 🔐 Admin login with full access to every feature below
- ➕ Add new books (title, author, ISBN, category, quantity)
- ✏️ Edit or 🗑️ delete existing books (deletion is blocked if any copies are currently on loan)
- 📋 View every currently issued book and its due date, with overdue books flagged
- 👥 View all registered member accounts
- 💰 Fine management — see every fine ever charged and mark individual fines as paid

### User Module
- 📝 Self-registration and login
- 📖 Browse the catalogue by category, or 🔍 search by title/author
- 📥 Issue a book — decrements available copies and sets a 14-day due date
- 📤 Return a book — increments available copies and calculates any overdue fine automatically
- 💸 Fine generation — ₹5 per day late, calculated automatically at return time
- 📌 Advance booking — reserve a book that currently has zero copies available
- ✉️ Contact/query form — messages are stored in the database and visible to admins

---

## 🛠️ Tech Stack

- **Backend:** Java 17, Spring Boot 3.5.5 (Spring MVC, Spring Data JPA)
- **Database:** SQLite (a single local file, `library.db` — no server setup needed) via the `hibernate-community-dialects` SQLite dialect
- **Frontend:** Thymeleaf server-rendered HTML + plain CSS (no JS frameworks)
- **Auth:** Simple session-based login (no Spring Security) — a lightweight `HandlerInterceptor` protects `/admin/**` and `/user/**` routes based on session role

> **MySQL instead of SQLite?** The task allows either. `application.properties` has a commented-out MySQL configuration block at the bottom with instructions — swap it in if you'd rather use MySQL.

---

## 📁 Project Structure

```
Java-Task5-DigitalLibraryManagementSystem/
├── pom.xml
├── src/main/java/com/oibsip/library/
│   ├── LibraryApplication.java       (entry point)
│   ├── model/                        (Book, User, IssueRecord, Reservation, ContactMessage, + enums)
│   ├── repository/                   (Spring Data JPA interfaces)
│   ├── service/LibraryService.java   (all business logic lives here)
│   ├── controller/                   (AuthController, AdminController, UserController)
│   └── config/                       (session auth interceptor + demo data seeder)
├── src/main/resources/
│   ├── application.properties
│   ├── static/css/style.css
│   └── templates/                    (13 Thymeleaf HTML pages)
├── README.md
└── screenshot.png   (add before submitting)
```

---

## ▶️ How to Run

### Prerequisites
- **JDK 17 or later**
- **Maven** (or use the Maven wrapper if you generate one, or your IDE's built-in Maven support)
- An internet connection the *first* time you build (Maven needs to download the dependencies once; after that they're cached locally)

Verify Java:
```bash
java -version
```

### Option A — Command line
```bash
cd Java-Task5-DigitalLibraryManagementSystem
mvn spring-boot:run
```

### Option B — In an IDE (IntelliJ IDEA / Eclipse / VS Code)
1. Open the folder as a Maven project (the IDE should detect `pom.xml` automatically and download dependencies)
2. Run `LibraryApplication.java` (it has a `main` method)

### Then
Open your browser to: **http://localhost:8080**

A `library.db` SQLite file will be created automatically in the project folder on first run, pre-loaded with a demo admin, a demo user, and 10 sample books.

---

## 🎮 Demo Logins

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| User | `student1` | `pass123` |

Or register a brand new user account from the login page.

---

## 🧪 Suggested things to click through when testing

1. Log in as `admin` → add a new book → edit it → view it appear correctly
2. Log in as `student1` → browse the catalogue → issue a book → check "My Books" shows the due date
3. Log back in as `admin` → "Issued Books" should show that loan
4. As `student1`, return the book from "My Books" — try changing your system clock forward (or just wait) to test the overdue fine calculation, then check "Fines" as admin and mark it paid
5. Issue every copy of a book with only 1 copy (as one user), then register/log in as a second user and try to issue the same book — you should be offered "Reserve" instead
6. Submit the contact form as a user, then check "Contact Messages" as admin

---

## 👤 Author

**[Your Full Name]**
Java Development Intern — Oasis Infobyte (OIBSIP)

---

## 🙏 Acknowledgements

Built as a Java Development track task for OIBSIP.
`#oasisinfobyte` `#java` `#springboot` `#javadevelopment` `#internship`
