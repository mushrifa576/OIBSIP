package reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class AllReservationsForm extends JFrame {

    public AllReservationsForm() {

        setTitle("All Reservations");
        setSize(950, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        String[] columns = {
                "PNR",
                "Username",
                "Passenger",
                "Train No.",
                "Train Name",
                "Class",
                "Journey Date",
                "Source",
                "Destination"
        };

        DefaultTableModel model =
                new DefaultTableModel(
                        columns,
                        0
                );

        JTable table =
                new JTable(model);

        JScrollPane scrollPane =
                new JScrollPane(table);

        add(scrollPane);

        loadReservations(model);
    }

    private void loadReservations(
            DefaultTableModel model) {

        String sql =
                "SELECT * FROM reservations " +
                "ORDER BY pnr DESC";

        try (Connection con = Database.connect();
             Statement stmt =
                     con.createStatement();
             ResultSet rs =
                     stmt.executeQuery(sql)) {

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("pnr"),
                        rs.getString("username"),
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