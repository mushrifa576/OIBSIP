package reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm() {

        setTitle("Online Reservation System - Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel heading = new JLabel(
                "ONLINE RESERVATION SYSTEM"
        );

        heading.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        heading.setBounds(75, 25, 330, 30);
        panel.add(heading);

        JLabel userLabel =
                new JLabel("Username:");

        userLabel.setBounds(60, 90, 100, 25);
        panel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(170, 90, 200, 30);
        panel.add(usernameField);

        JLabel passwordLabel =
                new JLabel("Password:");

        passwordLabel.setBounds(60, 140, 100, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(170, 140, 200, 30);
        panel.add(passwordField);

        JButton loginButton =
                new JButton("Login");

        loginButton.setBounds(
                150, 195, 140, 35
        );

        panel.add(loginButton);

        JButton registerButton =
                new JButton("New User? Register");

        registerButton.setBounds(
                120, 245, 200, 35
        );

        panel.add(registerButton);

        loginButton.addActionListener(e -> login());

        registerButton.addActionListener(e -> {

            new RegisterForm().setVisible(true);

        });

        add(panel);
    }

    private void login() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(passwordField.getPassword());

        if (username.isEmpty() ||
                password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password."
            );

            return;
        }

        String sql =
                "SELECT role FROM users " +
                "WHERE username = ? AND password = ?";

        try (Connection con = Database.connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String role =
                        rs.getString("role");

                JOptionPane.showMessageDialog(
                        this,
                        "Login Successful!"
                );

                if (role.equalsIgnoreCase("ADMIN")) {

                    new AdminDashboard().setVisible(true);

                } else {

                    new UserDashboard(username)
                            .setVisible(true);
                }

                dispose();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
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
}