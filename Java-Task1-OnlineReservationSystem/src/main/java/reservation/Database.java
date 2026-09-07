package reservation;

import java.sql.*;

public class Database {

    private static final String URL = "jdbc:sqlite:reservation.db";

    public static Connection connect() throws SQLException {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found.", e);
        }

        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {

        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL
                )
                """;

        String trainsTable = """
                CREATE TABLE IF NOT EXISTS trains (
                    train_number INTEGER PRIMARY KEY,
                    train_name TEXT NOT NULL,
                    source TEXT NOT NULL,
                    destination TEXT NOT NULL
                )
                """;

        String reservationsTable = """
                CREATE TABLE IF NOT EXISTS reservations (
                    pnr INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL,
                    passenger_name TEXT NOT NULL,
                    train_number INTEGER NOT NULL,
                    train_name TEXT NOT NULL,
                    class_type TEXT NOT NULL,
                    journey_date TEXT NOT NULL,
                    source TEXT NOT NULL,
                    destination TEXT NOT NULL
                )
                """;

        try (Connection con = connect();
             Statement stmt = con.createStatement()) {

            stmt.execute(usersTable);
            stmt.execute(trainsTable);
            stmt.execute(reservationsTable);

            // Create default admin
            String adminSQL =
                    "INSERT OR IGNORE INTO users " +
                    "(username, password, role) VALUES (?, ?, ?)";

            try (PreparedStatement ps =
                         con.prepareStatement(adminSQL)) {

                ps.setString(1, "admin");
                ps.setString(2, "1234");
                ps.setString(3, "ADMIN");

                ps.executeUpdate();
            }

            // Add sample trains
            addTrain(con, 12601, "Mangalore Mail",
                    "Mangalore", "Kozhikode");

            addTrain(con, 16649, "Parasuram Express",
                    "Mangalore", "Thiruvananthapuram");

            addTrain(con, 12082, "Jan Shatabdi Express",
                    "Thiruvananthapuram", "Kozhikode");

            addTrain(con, 16347, "Mangalore Express",
                    "Thiruvananthapuram", "Mangalore");

            System.out.println(
                    "Database initialized successfully."
            );

        } catch (SQLException e) {

            System.out.println(
                    "Database Error: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private static void addTrain(
            Connection con,
            int number,
            String name,
            String source,
            String destination) throws SQLException {

        String sql =
                "INSERT OR IGNORE INTO trains " +
                "(train_number, train_name, source, destination) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, number);
            ps.setString(2, name);
            ps.setString(3, source);
            ps.setString(4, destination);

            ps.executeUpdate();
        }
    }
}