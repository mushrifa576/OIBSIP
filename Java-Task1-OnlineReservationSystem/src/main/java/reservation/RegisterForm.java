package reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegisterForm() {

        setTitle("User Registration");
        setSize(450, 400);
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel title =
                new JLabel("USER REGISTRATION");

        title.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        title.setBounds(115, 25, 250, 30);
        panel.add(title);

        JLabel usernameLabel =
                new JLabel("Username:");

        usernameLabel.setBounds(
                60, 90, 120, 30
        );

        panel.add(usernameLabel);

        usernameField = new JTextField();

        usernameField.setBounds(
                190, 90, 190, 30
        );

        panel.add(usernameField);

        JLabel passwordLabel =
                new JLabel("Password:");

        passwordLabel.setBounds(
                60, 140, 120, 30
        );

        panel.add(passwordLabel);

        passwordField =
                new JPasswordField();

        passwordField.setBounds(
                190, 140, 190, 30
        );

        panel.add(passwordField);

        JLabel confirmLabel =
                new JLabel("Confirm Password:");

        confirmLabel.setBounds(
                60, 190, 120, 30
        );

        panel.add(confirmLabel);

        confirmPasswordField =
                new JPasswordField();

        confirmPasswordField.setBounds(
                190, 190, 190, 30
        );

        panel.add(confirmPasswordField);

        JButton registerButton =
                new JButton("Register");

        registerButton.setBounds(
                145, 250, 150, 40
        );

        panel.add(registerButton);

        registerButton.addActionListener(
                e -> registerUser()
        );

        add(panel);
    }

    private void registerUser() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        String confirmPassword =
                new String(
                        confirmPasswordField.getPassword()
                );

        if (username.isEmpty() ||
                password.isEmpty() ||
                confirmPassword.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields."
            );

            return;
        }

        if (username.length() < 3) {

            JOptionPane.showMessageDialog(
                    this,
                    "Username must contain at least 3 characters."
            );

            return;
        }

        if (password.length() < 4) {

            JOptionPane.showMessageDialog(
                    this,
                    "Password must contain at least 4 characters."
            );

            return;
        }

        if (!password.equals(confirmPassword)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match."
            );

            return;
        }

        String checkSQL =
                "SELECT username FROM users WHERE username = ?";

        String insertSQL =
                "INSERT INTO users " +
                "(username, password, role) " +
                "VALUES (?, ?, 'USER')";

        try (Connection con = Database.connect();
             PreparedStatement check =
                     con.prepareStatement(checkSQL)) {

            check.setString(1, username);

            ResultSet rs =
                    check.executeQuery();

            if (rs.next()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Username already exists."
                );

                return;
            }

            try (PreparedStatement ps =
                         con.prepareStatement(insertSQL)) {

                ps.setString(1, username);
                ps.setString(2, password);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Registration successful!\n" +
                        "You can now login."
                );

                dispose();
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Registration failed: "
                            + e.getMessage()
            );
        }
    }
}