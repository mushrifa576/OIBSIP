package reservation;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {

        setTitle("Admin Dashboard");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel title =
                new JLabel("ADMIN DASHBOARD");

        title.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        title.setBounds(145, 30, 250, 35);
        panel.add(title);

        JButton reservationsButton =
                new JButton("View All Reservations");

        reservationsButton.setBounds(
                130, 100, 240, 40
        );

        panel.add(reservationsButton);

        JButton trainsButton =
                new JButton("Manage Trains");

        trainsButton.setBounds(
                130, 160, 240, 40
        );

        panel.add(trainsButton);

        JButton logoutButton =
                new JButton("Logout");

        logoutButton.setBounds(
                175, 250, 150, 40
        );

        panel.add(logoutButton);

        reservationsButton.addActionListener(e ->
                new AllReservationsForm()
                        .setVisible(true)
        );

        trainsButton.addActionListener(e ->
                new ManageTrainsForm()
                        .setVisible(true)
        );

        logoutButton.addActionListener(e -> {

            new LoginForm().setVisible(true);

            dispose();
        });

        add(panel);
    }
}