# Online Reservation System

A Java Swing desktop application for managing train reservations, allowing
users to register, log in, book tickets, view their bookings, and cancel
reservations using a unique PNR number.

Built as part of the **Oasis Infobyte Summer Internship Program (OIBSIP)** —
Java Development Track, Task 1.

## Features

* **User Registration:** New users can create an account before logging in
* **Secure Login:** Separate login access for Admin and regular Users
* **Role-Based Access:** Admin and User accounts have different functionalities
* **Train Search:** Enter a train number to automatically fetch the train name,
  source, and destination
* **Ticket Booking:** Users can enter passenger details, class type, journey
  date, source, and destination
* **Automatic PNR Generation:** A unique PNR number is automatically generated
  for every successful reservation
* **Booking Confirmation:** A confirmation dialog displays complete booking
  details and the generated PNR
* **My Bookings:** Users can view only their own reservation history
* **Ticket Cancellation:** Users can fetch their booking using the PNR number
  and cancel it after confirmation
* **Admin Dashboard:** Admin can view all reservations made by users
* **Train Management:** Admin can view existing trains and add new trains
* **Input Validation:** Validates required fields, numeric train numbers,
  journey dates, and other booking details
* **SQLite Database:** Automatically creates and initializes the required
  database and tables on first run
* **SQL Injection Prevention:** Uses `PreparedStatement` for database queries

## User Roles

| Role      | Functionalities                                                   |
| --------- | ----------------------------------------------------------------- |
| **User**  | Register, Login, Book Ticket, View My Bookings, Cancel Own Ticket |
| **Admin** | Login, View All Reservations, Manage Trains                       |

### Default Admin Account

| Username | Password |
| -------- | -------- |
| `admin`  | `1234`   |

New users must register through the **New User? Register** option before
logging in.

## Tech Stack

* **Language:** Java (JDK 17+ recommended)
* **UI:** Java Swing
* **Database:** SQLite
* **Database Connectivity:** JDBC
* **Build Tool:** Apache Maven
* **Core concepts used:** `JFrame`, `JPanel`, `JTextField`, `JPasswordField`,
  `JComboBox`, `JTable`, `JTextArea`, `JOptionPane`, event-driven programming,
  JDBC, SQL, `PreparedStatement`, role-based access control

## How to Run

### Prerequisites

* Java Development Kit (JDK) 17 or later installed
* Apache Maven installed

Verify Java:

```bash
java -version
javac -version
```

Verify Maven:

```bash
mvn -version
```

### Compile the project

Open CMD inside the project folder:

```bash
mvn clean compile
```

### Run the application

```bash
mvn exec:java -Dexec.mainClass="reservation.Main"
```

The Java Swing login window will open directly.

No separate database server is required because the application uses SQLite.

## How to Use

### 1. User Registration

1. Launch the application.
2. Click **New User? Register**.
3. Enter a username and password.
4. Confirm the password.
5. Click **Register**.
6. Return to the login screen and log in using the newly created account.

### 2. Book a Ticket

1. Log in as a registered user.
2. Open **Book Ticket**.
3. Enter the passenger name.
4. Enter the train number.
5. Click **Find Train**.
6. The train name, source, and destination are automatically populated.
7. Select the required class type.
8. Enter the journey date in `YYYY-MM-DD` format.
9. Click **Book Ticket**.
10. A confirmation dialog displays the complete booking details and generated
    PNR number.

### 3. View My Bookings

1. Open **My Bookings** from the User Dashboard.
2. All reservations made by the logged-in user are displayed in a table.
3. Other users' bookings are not displayed.

### 4. Cancel a Ticket

1. Open **Cancel Ticket**.
2. Enter the PNR number.
3. Click **Fetch**.
4. The booking details are displayed.
5. Click **Confirm Cancellation**.
6. Confirm the **Are you sure?** dialog.
7. The reservation is removed from the database.

### 5. Admin Functions

Log in using:

```text
Username: admin
Password: 1234
```

The Admin Dashboard provides:

* **View All Reservations** — view bookings made by all users
* **Manage Trains** — view existing trains and add new trains

## Database

The application automatically creates an SQLite database named:

```text
reservation.db
```

The database contains the following tables:

```text
users
├── id
├── username
├── password
└── role

trains
├── train_number
├── train_name
├── source
└── destination

reservations
├── pnr
├── username
├── passenger_name
├── train_number
├── train_name
├── class_type
├── journey_date
├── source
└── destination
```

The database file is generated automatically when the application is first
run.

## Project Structure

```text
OIBSP/Java-Task1-OnlineReservationSystem/
├── pom.xml
├── README.md
│
└── src/
    └── main/
        └── java/
            └── reservation/
                ├── Main.java
                ├── Database.java
                ├── LoginForm.java
                ├── RegisterForm.java
                ├── UserDashboard.java
                ├── AdminDashboard.java
                ├── ReservationForm.java
                ├── CancellationForm.java
                ├── MyBookingsForm.java
                ├── AllReservationsForm.java
                └── ManageTrainsForm.java
```

> **Note:** `reservation.db` and Maven's `target/` directory are generated
> locally and should not be committed to the repository.

## Author

Mushrifa T K M
Java Development Intern — Oasis Infobyte (OIBSIP)

## Acknowledgements

Built as Task 1 of the **OIBSIP Java Development Track**.

`#oasisinfobyte` `#java` `#javaswing` `#jdbc` `#sqlite` `#javadevelopment` `#internship`
