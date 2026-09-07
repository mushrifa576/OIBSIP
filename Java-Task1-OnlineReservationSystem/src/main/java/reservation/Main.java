package reservation;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        Database.initializeDatabase();

        SwingUtilities.invokeLater(() -> {

            new LoginForm().setVisible(true);

        });
    }
}
