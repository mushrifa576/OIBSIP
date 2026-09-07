package reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ReservationForm extends JFrame {

    private String username;

    private JTextField passengerField;
    private JTextField trainNumberField;
    private JTextField trainNameField;
    private JComboBox<String> classBox;
    private JTextField dateField;
    private JTextField sourceField;
    private JTextField destinationField;

    public ReservationForm(String username) {

        this.username = username;

        setTitle("Book Reservation");
        setSize(550, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel title =
                new JLabel("RESERVATION FORM");

        title.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        title.setBounds(170, 20, 250, 30);
        panel.add(title);

        JLabel passengerLabel =
                new JLabel("Passenger Name:");

        passengerLabel.setBounds(
                60, 80, 150, 30
        );

        panel.add(passengerLabel);

        passengerField = new JTextField();

        passengerField.setBounds(
                230, 80, 220, 30
        );

        panel.add(passengerField);

        JLabel trainNumberLabel =
                new JLabel("Train Number:");

        trainNumberLabel.setBounds(
                60, 130, 150, 30
        );

        panel.add(trainNumberLabel);

        trainNumberField = new JTextField();

        trainNumberField.setBounds(
                230, 130, 220, 30
        );

        panel.add(trainNumberField);

        JButton findTrainButton =
                new JButton("Find Train");

        findTrainButton.setBounds(
                230, 165, 120, 30
        );

        panel.add(findTrainButton);

        JLabel trainNameLabel =
                new JLabel("Train Name:");

        trainNameLabel.setBounds(
                60, 215, 150, 30
        );

        panel.add(trainNameLabel);

        trainNameField = new JTextField();

        trainNameField.setBounds(
                230, 215, 220, 30
        );

        trainNameField.setEditable(false);

        panel.add(trainNameField);

        JLabel classLabel =
                new JLabel("Class Type:");

        classLabel.setBounds(
                60, 265, 150, 30
        );

        panel.add(classLabel);

        String[] classes = {
                "Sleeper",
                "AC 3 Tier",
                "AC 2 Tier",
                "AC First Class"
        };

        classBox =
                new JComboBox<>(classes);

        classBox.setBounds(
                230, 265, 220, 30
        );

        panel.add(classBox);

        JLabel dateLabel =
                new JLabel("Journey Date:");

        dateLabel.setBounds(
                60, 315, 150, 30
        );

        panel.add(dateLabel);

        dateField = new JTextField();

        dateField.setBounds(
                230, 315, 220, 30
        );

        panel.add(dateField);

        JLabel dateHint =
                new JLabel("Format: YYYY-MM-DD");

        dateHint.setBounds(
                230, 345, 200, 20
        );

        panel.add(dateHint);

        JLabel sourceLabel =
                new JLabel("Source Station:");

        sourceLabel.setBounds(
                60, 375, 150, 30
        );

        panel.add(sourceLabel);

        sourceField = new JTextField();

        sourceField.setBounds(
                230, 375, 220, 30
        );

        panel.add(sourceField);

        JLabel destinationLabel =
                new JLabel("Destination:");

        destinationLabel.setBounds(
                60, 425, 150, 30
        );

        panel.add(destinationLabel);

        destinationField = new JTextField();

        destinationField.setBounds(
                230, 425, 220, 30
        );

        panel.add(destinationField);

        JButton bookButton =
                new JButton("Book Ticket");

        bookButton.setBounds(
                190, 490, 160, 40
        );

        panel.add(bookButton);

        findTrainButton.addActionListener(
                e -> findTrain()
        );

        bookButton.addActionListener(
                e -> bookTicket()
        );

        add(panel);
    }

    private void findTrain() {

        String number =
                trainNumberField.getText().trim();

        if (number.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter train number."
            );

            return;
        }

        int trainNumber;

        try {

            trainNumber =
                    Integer.parseInt(number);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Train number must be numeric."
            );

            return;
        }

        String sql =
                "SELECT * FROM trains " +
                "WHERE train_number = ?";

        try (Connection con = Database.connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, trainNumber);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                trainNameField.setText(
                        rs.getString("train_name")
                );

                sourceField.setText(
                        rs.getString("source")
                );

                destinationField.setText(
                        rs.getString("destination")
                );

            } else {

                trainNameField.setText("");

                JOptionPane.showMessageDialog(
                        this,
                        "Train not found."
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: "
                            + e.getMessage()
            );
        }
    }

    private void bookTicket() {

        String passenger =
                passengerField.getText().trim();

        String trainNo =
                trainNumberField.getText().trim();

        String trainName =
                trainNameField.getText().trim();

        String date =
                dateField.getText().trim();

        String source =
                sourceField.getText().trim();

        String destination =
                destinationField.getText().trim();

        String classType =
                classBox.getSelectedItem().toString();

        if (passenger.isEmpty() ||
                trainNo.isEmpty() ||
                trainName.isEmpty() ||
                date.isEmpty() ||
                source.isEmpty() ||
                destination.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all required fields."
            );

            return;
        }

        int trainNumber;

        try {

            trainNumber =
                    Integer.parseInt(trainNo);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Train number must be numeric."
            );

            return;
        }

        try {

            LocalDate journeyDate =
                    LocalDate.parse(date);

            if (journeyDate.isBefore(
                    LocalDate.now())) {

                JOptionPane.showMessageDialog(
                        this,
                        "Journey date cannot be in the past."
                );

                return;
            }

        } catch (DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid date. Use YYYY-MM-DD."
            );

            return;
        }

        if (source.equalsIgnoreCase(destination)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Source and destination cannot be the same."
            );

            return;
        }

        String sql = """
                INSERT INTO reservations
                (username, passenger_name,
                 train_number, train_name,
                 class_type, journey_date,
                 source, destination)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = Database.connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passenger);
            ps.setInt(3, trainNumber);
            ps.setString(4, trainName);
            ps.setString(5, classType);
            ps.setString(6, date);
            ps.setString(7, source);
            ps.setString(8, destination);

            ps.executeUpdate();

            long pnr;

            try (Statement stmt =
                         con.createStatement();
                 ResultSet rs =
                         stmt.executeQuery(
                                 "SELECT last_insert_rowid()"
                         )) {

                rs.next();
                pnr = rs.getLong(1);
            }

            JOptionPane.showMessageDialog(
                    this,
                    """
                    BOOKING SUCCESSFUL!

                    PNR: %d
                    Passenger: %s
                    Train: %d - %s
                    Class: %s
                    Journey Date: %s
                    From: %s
                    To: %s
                    """.formatted(
                            pnr,
                            passenger,
                            trainNumber,
                            trainName,
                            classType,
                            date,
                            source,
                            destination
                    )
            );

            dispose();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Booking failed: "
                            + e.getMessage()
            );
        }
    }
}