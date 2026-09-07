package reservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageTrainsForm extends JFrame {

    private DefaultTableModel model;

    public ManageTrainsForm() {

        setTitle("Manage Trains");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        JPanel topPanel = new JPanel();

        JButton addButton =
                new JButton("Add Train");

        topPanel.add(addButton);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {
                "Train Number",
                "Train Name",
                "Source",
                "Destination"
        };

        model =
                new DefaultTableModel(
                        columns,
                        0
                );

        JTable table =
                new JTable(model);

        add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        loadTrains();

        addButton.addActionListener(
                e -> addTrain()
        );
    }

    private void loadTrains() {

        model.setRowCount(0);

        String sql =
                "SELECT * FROM trains " +
                "ORDER BY train_number";

        try (Connection con = Database.connect();
             Statement stmt =
                     con.createStatement();
             ResultSet rs =
                     stmt.executeQuery(sql)) {

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("train_number"),
                        rs.getString("train_name"),
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

    private void addTrain() {

        JTextField numberField =
                new JTextField();

        JTextField nameField =
                new JTextField();

        JTextField sourceField =
                new JTextField();

        JTextField destinationField =
                new JTextField();

        Object[] fields = {
                "Train Number:",
                numberField,
                "Train Name:",
                nameField,
                "Source:",
                sourceField,
                "Destination:",
                destinationField
        };

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        fields,
                        "Add Train",
                        JOptionPane.OK_CANCEL_OPTION
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            int number =
                    Integer.parseInt(
                            numberField
                                    .getText()
                                    .trim()
                    );

            String name =
                    nameField.getText().trim();

            String source =
                    sourceField.getText().trim();

            String destination =
                    destinationField
                            .getText()
                            .trim();

            if (name.isEmpty() ||
                    source.isEmpty() ||
                    destination.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please fill all fields."
                );

                return;
            }

            String sql =
                    "INSERT INTO trains " +
                    "(train_number, train_name, source, destination) " +
                    "VALUES (?, ?, ?, ?)";

            try (Connection con =
                         Database.connect();
                 PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setInt(1, number);
                ps.setString(2, name);
                ps.setString(3, source);
                ps.setString(4, destination);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Train added successfully."
                );

                loadTrains();
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Train number must be numeric."
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not add train.\n"
                            + e.getMessage()
            );
        }
    }
}