import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Login {

    public static void main(String[] args) {
        showLogin();
    }

    public static void showLogin() {

        JFrame frame = new JFrame("Don Macchiatos");
        frame.setSize(500, 430);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Main background
        JPanel panel = new JPanel(new GridBagLayout()) {

    Image background = new ImageIcon(
        Login.class.getResource("/images/don_macchiatos_background_2.png")
    ).getImage();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(
            background,
            0,
            0,
            getWidth(),
            getHeight(),
            this
        );
    }
};
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        // Username
        JLabel userLabel = new JLabel("Username");
userLabel.setFont(new Font("Arial", Font.BOLD, 13));
userLabel.setForeground(new Color(91, 58, 41));

JTextField usernameField = new JTextField(18);
usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
usernameField.setPreferredSize(new Dimension(210, 32));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 15));
        usernameField.setPreferredSize(new Dimension(230, 35));

        // Password
        JLabel passLabel = new JLabel("Password");
passLabel.setFont(new Font("Arial", Font.BOLD, 13));
passLabel.setForeground(new Color(91, 58, 41));

JPasswordField passwordField = new JPasswordField(18);
passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
passwordField.setPreferredSize(new Dimension(210, 32));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 15));
        passwordField.setPreferredSize(new Dimension(230, 35));

        // Login button
        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(91, 58, 41));
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(210, 38));


        // Username label
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        panel.add(userLabel, gbc);

        // Username field
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Password label
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(passLabel, gbc);

        // Password field
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        // LOGIN FUNCTION - SAME AS BEFORE
        loginButton.addActionListener(e -> {

            String username = usernameField.getText();
            String password =
                    new String(passwordField.getPassword());

            String sql =
                    "SELECT * FROM users "
                    + "WHERE username = ? AND password = ?";

            try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement pst =
                        conn.prepareStatement(sql)
            ) {

                pst.setString(1, username);
                pst.setString(2, password);

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {

                    String role =
                            rs.getString("role");

                    JOptionPane.showMessageDialog(
                            frame,
                            "Login successful!\nRole: " + role
                    );

                    frame.dispose();


if (role.equalsIgnoreCase("Cashier")) {

    CashierDashboard.main(new String[]{});

} else if (role.equalsIgnoreCase("Manager")) {

    ManagerDashboard.main(new String[]{});

} else {

    JOptionPane.showMessageDialog(
        null,
        "This account does not have a supported role.\n"
        + "Please contact the manager."
    );

    Login.showLogin();
}


                } else {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Invalid username or password."
                    );
                }

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Database error: "
                        + ex.getMessage()
                );
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}