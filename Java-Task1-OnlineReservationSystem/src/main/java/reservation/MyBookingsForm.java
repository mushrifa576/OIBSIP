package reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MyBookingsForm extends JFrame {

    private String username;

    public MyBookingsForm(String username) {

        this.username = username;

        setTitle("My Bookings");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        String[] columns = {
                "PNR",
                "Passenger",
                "Train No.",
                "Train Name",
                "Class",
                "Journey Date",
                "Source",
                "Destination"
        };

        DefaultTableModel model =
                new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);

        JScrollPane scrollPane =
                new JScrollPane(table);

        add(scrollPane);

        loadBookings(model);
    }

    private void loadBookings(
            DefaultTableModel model) {

        String sql =
                "SELECT * FROM reservations " +
                "WHERE username = ? " +
                "ORDER BY pnr DESC";

        try (Connection con = Database.connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("pnr"),
                        rs.getString("passenger_name"),
                        rs.getInt("train_number"),
                        rs.getString("train_name"),
                        rs.getString("class_type"),
                        rs.getString("journey_date"),
                        rs.getString("source"),
                        rs.getString("destination")
                });
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: "
                            + e.getMessage()
            );
        }
    }
}