
import javax.swing.*;
import java.awt.*;

public class ManagerDashboard {

    static Color mocha = new Color(91, 58, 41);
    static Color cream = new Color(250, 244, 234);
    static Color caramel = new Color(185, 130, 85);

    public static void main(String[] args) {

        JFrame frame = new JFrame("Don Macchiatos - Manager");
        frame.setSize(820, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(cream);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        // Title
        JLabel title = new JLabel(
                "DON MACCHIATOS - MANAGER",
                SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(mocha);
        title.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 15, 10)
        );

        // Center area
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(
                BorderFactory.createLineBorder(new Color(190, 190, 190))
        );

        JLabel welcomeLabel = new JLabel(
                "<html><center>Welcome to Don Macchiatos<br><br>"
                + "Manage products and view customer orders here.</center></html>",
                SwingConstants.CENTER
        );
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        welcomeLabel.setForeground(mocha);
        centerPanel.add(welcomeLabel);

        // Buttons
JButton productsButton = new JButton("Manage Products");
JButton ordersButton = new JButton("View Orders");
JButton passwordButton = new JButton("Change Password");
JButton logoutButton = new JButton("Logout");

JButton[] allButtons = {
    productsButton,
    ordersButton,
    passwordButton,
    logoutButton
};


for (JButton button : allButtons) {
    button.setBackground(mocha);
    button.setForeground(Color.WHITE);
    button.setFont(new Font("Arial", Font.BOLD, 12));
    button.setFocusPainted(false);
    button.setPreferredSize(new Dimension(125, 38));
}

productsButton.setPreferredSize(new Dimension(170, 38));
passwordButton.setPreferredSize(new Dimension(160, 38));


        // Button panel
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 8, 8)
        );
        buttonPanel.setBackground(cream);

buttonPanel.add(productsButton);
buttonPanel.add(ordersButton);
buttonPanel.add(passwordButton);
buttonPanel.add(logoutButton);

        // Button actions
        productsButton.addActionListener(e -> {
            ProductManagement.main(new String[]{});
        });

        ordersButton.addActionListener(e -> {
            OrderRecords.main(new String[]{});
        });

        logoutButton.addActionListener(e -> {
            frame.dispose();
            Login.showLogin();
        });
        
        passwordButton.addActionListener(e -> {
    ChangePassword.main(new String[]{});
});

        // Arrange dashboard
        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }
}
