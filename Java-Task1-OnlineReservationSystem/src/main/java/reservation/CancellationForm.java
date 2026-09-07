package reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CancellationForm extends JFrame {

    private String username;

    private JTextField pnrField;
    private JTextArea detailsArea;

    private int currentPNR = -1;

    public CancellationForm(String username) {

        this.username = username;

        setTitle("Cancel Reservation");
        setSize(550, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel title =
                new JLabel("CANCEL RESERVATION");

        title.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        title.setBounds(
                145, 20, 280, 30
        );

        panel.add(title);

        JLabel pnrLabel =
                new JLabel("PNR Number:");

        pnrLabel.setBounds(
                60, 80, 120, 30
        );

        panel.add(pnrLabel);

        pnrField = new JTextField();

        pnrField.setBounds(
                180, 80, 170, 30
        );

        panel.add(pnrField);

        JButton fetchButton =
                new JButton("Fetch");

        fetchButton.setBounds(
                365, 80, 90, 30
        );

        panel.add(fetchButton);

        detailsArea =
                new JTextArea();

        detailsArea.setEditable(false);

        detailsArea.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        14
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(detailsArea);

        scrollPane.setBounds(
                60, 140, 395, 220
        );

        panel.add(scrollPane);

        JButton cancelButton =
                new JButton("Confirm Cancellation");

        cancelButton.setBounds(
                150, 390, 210, 40
        );

        cancelButton.setEnabled(false);

        panel.add(cancelButton);

        fetchButton.addActionListener(
                e -> fetchReservation(cancelButton)
        );

        cancelButton.addActionListener(
                e -> cancelReservation(cancelButton)
        );

        add(panel);
    }

    private void fetchReservation(
            JButton cancelButton) {

        String pnrText =
                pnrField.getText().trim();

        if (pnrText.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter PNR number."
            );

            return;
        }

        int pnr;

        try {

            pnr = Integer.parseInt(pnrText);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "PNR must be numeric."
            );

            return;
        }

        String sql =
                "SELECT * FROM reservations " +
                "WHERE pnr = ? AND username = ?";

        try (Connection con = Database.connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, pnr);
            ps.setString(2, username);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                currentPNR = pnr;

                detailsArea.setText(
                        """
                        PNR             : %d
                        Passenger       : %s
                        Train Number    : %d
                        Train Name      : %s
                        Class           : %s
                        Journey Date    : %s
                        Source          : %s
                        Destination     : %s
                        """.formatted(
                                rs.getInt("pnr"),
                                rs.getString(
                                        "passenger_name"
                                ),
                                rs.getInt(
                                        "train_number"
                                ),
                                rs.getString(
                                        "train_name"
                                ),
                                rs.getString(
                                        "class_type"
                                ),
                                rs.getString(
                                        "journey_date"
                                ),
                                rs.getString(
                                        "source"
                                ),
                                rs.getString(
                                        "destination"
                                )
                        )
                );

                cancelButton.setEnabled(true);

            } else {

                currentPNR = -1;

                detailsArea.setText("");

                cancelButton.setEnabled(false);

                JOptionPane.showMessageDialog(
                        this,
                        "Reservation not found or it does not belong to you."
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

    private void cancelReservation(
            JButton cancelButton) {

        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to cancel this reservation?",
                        "Confirm Cancellation",
                        JOptionPane.YES_NO_OPTION
                );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM reservations " +
                "WHERE pnr = ? AND username = ?";

        try (Connection con = Database.connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, currentPNR);
            ps.setString(2, username);

            int rows =
                    ps.executeUpdate();

            if (rows > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Reservation cancelled successfully."
                );

                detailsArea.setText("");
                pnrField.setText("");

                cancelButton.setEnabled(false);

                currentPNR = -1;
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cancellation failed: "
                            + e.getMessage()
            );
        }
    }
}