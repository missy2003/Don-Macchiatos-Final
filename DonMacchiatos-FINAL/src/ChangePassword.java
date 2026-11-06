import javax.swing.*;
import java.awt.*;

public class ChangePassword {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Don Macchiatos - Change Password");
        frame.setSize(450, 350);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel title = new JLabel("CHANGE PASSWORD");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(18);

        JLabel currentLabel = new JLabel("Current Password:");
        JPasswordField currentField = new JPasswordField(18);

        JLabel newLabel = new JLabel("New Password:");
        JPasswordField newField = new JPasswordField(18);

        JButton changeButton =
                new JButton("Change Password");

        JButton backButton =
                new JButton("Back");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(currentLabel, gbc);

        gbc.gridx = 1;
        panel.add(currentField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(newLabel, gbc);

        gbc.gridx = 1;
        panel.add(newField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(changeButton, gbc);

        gbc.gridx = 1;
        panel.add(backButton, gbc);

        changeButton.addActionListener(e -> {

            String username =
                    usernameField.getText();

            String currentPassword =
                    new String(currentField.getPassword());

            String newPassword =
                    new String(newField.getPassword());

            if (username.isEmpty()
                    || currentPassword.isEmpty()
                    || newPassword.isEmpty()) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please fill in all fields."
                );

                return;
            }

            try {

                java.sql.Connection conn =
                        DatabaseConnection.getConnection();

                String checkSQL =
                        "SELECT * FROM users "
                        + "WHERE username = ? "
                        + "AND password = ?";

                java.sql.PreparedStatement checkStatement =
                        conn.prepareStatement(checkSQL);

                checkStatement.setString(
                        1,
                        username
                );

                checkStatement.setString(
                        2,
                        currentPassword
                );

                java.sql.ResultSet result =
                        checkStatement.executeQuery();

                if (result.next()) {

                    String updateSQL =
                            "UPDATE users "
                            + "SET password = ? "
                            + "WHERE username = ?";

                    java.sql.PreparedStatement updateStatement =
                            conn.prepareStatement(updateSQL);

                    updateStatement.setString(
                            1,
                            newPassword
                    );

                    updateStatement.setString(
                            2,
                            username
                    );

                    updateStatement.executeUpdate();

                    updateStatement.close();

                    JOptionPane.showMessageDialog(
                            frame,
                            "Password changed successfully!"
                    );

                    usernameField.setText("");
                    currentField.setText("");
                    newField.setText("");

                } else {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Username or current password is incorrect."
                    );
                }

                result.close();
                checkStatement.close();
                conn.close();

            } catch (java.sql.SQLException ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Database error:\n"
                        + ex.getMessage()
                );
            }
        });

        backButton.addActionListener(e -> {
            frame.dispose();
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}