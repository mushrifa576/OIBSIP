package reservation;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {

    private String username;

    public UserDashboard(String username) {

        this.username = username;

        setTitle("User Dashboard");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel title =
                new JLabel("USER DASHBOARD");

        title.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        title.setBounds(150, 30, 250, 35);
        panel.add(title);

        JLabel welcome =
                new JLabel("Welcome, " + username);

        welcome.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        welcome.setBounds(180, 75, 200, 30);
        panel.add(welcome);

        JButton bookButton =
                new JButton("Book Ticket");

        bookButton.setBounds(
                140, 125, 220, 40
        );

        panel.add(bookButton);

        JButton myBookingsButton =
                new JButton("My Bookings");

        myBookingsButton.setBounds(
                140, 185, 220, 40
        );

        panel.add(myBookingsButton);

        JButton cancelButton =
                new JButton("Cancel Ticket");

        cancelButton.setBounds(
                140, 245, 220, 40
        );

        panel.add(cancelButton);

        JButton logoutButton =
                new JButton("Logout");

        logoutButton.setBounds(
                180, 315, 140, 40
        );

        panel.add(logoutButton);

        bookButton.addActionListener(e ->
                new ReservationForm(username)
                        .setVisible(true)
        );

        myBookingsButton.addActionListener(e ->
                new MyBookingsForm(username)
                        .setVisible(true)
        );

        cancelButton.addActionListener(e ->
                new CancellationForm(username)
                        .setVisible(true)
        );

        logoutButton.addActionListener(e -> {

            new LoginForm().setVisible(true);

            dispose();
        });

        add(panel);
    }
}