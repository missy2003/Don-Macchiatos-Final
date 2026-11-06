
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CashierDashboard {

    static DefaultListModel<String> orderListModel;
    static JList<String> orderList;
    
    // Don Macchiatos theme colors
static Color mocha = new Color(91, 58, 41);
static Color cream = new Color(250, 244, 234);
static Color caramel = new Color(185, 130, 85);

    public static void main(String[] args) {

        JFrame frame = new JFrame("Don Macchiatos - Cashier");
        frame.setSize(850, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

JPanel panel = new JPanel(new BorderLayout(10, 10));
panel.setBackground(cream);
panel.setBorder(
    BorderFactory.createEmptyBorder(15, 15, 15, 15)
);

JLabel title = new JLabel(
    "DON MACCHIATOS - ORDER STATION",
    SwingConstants.CENTER
);
title.setFont(new Font("Arial", Font.BOLD, 24));
title.setForeground(mocha);
title.setBorder(
    BorderFactory.createEmptyBorder(10, 10, 15, 10)
);

        // ORDER LIST
        orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);
        
        orderList.setFont(new Font("Arial", Font.PLAIN, 14));
orderList.setBackground(Color.WHITE);
orderList.setForeground(mocha);
orderList.setSelectionBackground(caramel);
orderList.setSelectionForeground(Color.WHITE);
orderList.setFixedCellHeight(30);

        JScrollPane scrollPane = new JScrollPane(orderList);

        // BUTTONS
        JButton newOrderButton = new JButton("New Order");
        JButton ordersButton = new JButton("View Orders");
        JButton cancelButton = new JButton("Cancel Order");
        JButton preparingButton = new JButton("Start Preparing");
        JButton completeButton = new JButton("Complete Order");
        JButton refreshButton = new JButton("Refresh");
        JButton logoutButton = new JButton("Logout");
        
        
JButton[] allButtons = {
    newOrderButton,
    ordersButton,
    cancelButton,
    preparingButton,
    completeButton,
    refreshButton,
    logoutButton
};

for (JButton button : allButtons) {
    button.setBackground(mocha);
    button.setForeground(Color.WHITE);
    button.setFont(new Font("Arial", Font.BOLD, 12));
    button.setFocusPainted(false);
    button.setPreferredSize(new Dimension(125, 38));
}


JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
buttons.setBackground(cream);

        buttons.add(newOrderButton);
        buttons.add(ordersButton);
        buttons.add(cancelButton);
        buttons.add(preparingButton);
        buttons.add(completeButton);
        buttons.add(refreshButton);
        buttons.add(logoutButton);

        // BUTTON ACTIONS
        newOrderButton.addActionListener(e -> {
            NewOrder.main(new String[]{});
        });

        ordersButton.addActionListener(e -> {
            OrderRecords.main(new String[]{});
        });

        cancelButton.addActionListener(e -> {
            CancelOrder.main(new String[]{});
            loadOrders();
        });

        refreshButton.addActionListener(e -> loadOrders());

        preparingButton.addActionListener(e -> {
            updateSelectedOrder(frame, "Preparing");
        });

        completeButton.addActionListener(e -> {
            updateSelectedOrder(frame, "Completed");
        });

        logoutButton.addActionListener(e -> {
            frame.dispose();
            Login.showLogin();
        });

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        loadOrders();
    }

    public static void loadOrders() {

        if (orderListModel == null) {
            return;
        }

        orderListModel.clear();

        String sql = "SELECT order_id, total_amount, payment_status, "
                   + "order_status FROM orders "
                   + "WHERE order_status <> 'Cancelled' "
                   + "ORDER BY order_id DESC";

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn == null) {
                JOptionPane.showMessageDialog(null,
                        "Could not connect to the database.");
                return;
            }

            try (Statement statement = conn.createStatement();
                 ResultSet result = statement.executeQuery(sql)) {

                while (result.next()) {

                    String text = "Order #" + result.getInt("order_id")
                            + " | Total: P"
                            + String.format("%.2f",
                                    result.getDouble("total_amount"))
                            + " | Payment: "
                            + result.getString("payment_status")
                            + " | Status: "
                            + result.getString("order_status");

                    orderListModel.addElement(text);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database error:\n" + e.getMessage());
        }
    }


private static void updateSelectedOrder(
        JFrame frame, String newStatus) {

    String selected = orderList.getSelectedValue();

    if (selected == null) {
        JOptionPane.showMessageDialog(
                frame, "Please select an order.");
        return;
    }

    int hashPosition = selected.indexOf("#");
    int spacePosition = selected.indexOf(" ", hashPosition);

    int orderId = Integer.parseInt(
            selected.substring(hashPosition + 1, spacePosition));

    String sql = "SELECT order_status FROM orders "
               + "WHERE order_id = ?";

    try (Connection conn = DatabaseConnection.getConnection()) {

        if (conn == null) {
            JOptionPane.showMessageDialog(
                    frame, "Could not connect to the database.");
            return;
        }

        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, orderId);

            try (ResultSet rs = pst.executeQuery()) {

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(
                            frame, "Order not found.");
                    return;
                }

                String currentStatus =
                        rs.getString("order_status");

                if (currentStatus.equals("Cancelled")) {
                    JOptionPane.showMessageDialog(
                            frame, "Cancelled orders cannot be updated.");
                    return;
                }

                if (newStatus.equals("Preparing")) {

                    if (!currentStatus.equals("Pending")) {
                        JOptionPane.showMessageDialog(
                                frame,
                                "Only pending orders can start preparing.");
                        return;
                    }

                } else if (newStatus.equals("Completed")) {

                    if (!currentStatus.equals("Preparing")) {
                        JOptionPane.showMessageDialog(
                                frame,
                                "Only orders being prepared can be completed.");
                        return;
                    }
                }
            }
        }

        String updateSql = "UPDATE orders SET order_status = ? "
                         + "WHERE order_id = ? AND order_status = ?";

        String expectedStatus =
                newStatus.equals("Preparing") ? "Pending" : "Preparing";

        try (PreparedStatement pst =
                conn.prepareStatement(updateSql)) {

            pst.setString(1, newStatus);
            pst.setInt(2, orderId);
            pst.setString(3, expectedStatus);

            int updated = pst.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Order #" + orderId
                        + " is now " + newStatus + ".");
                loadOrders();
            } else {
                JOptionPane.showMessageDialog(
                        frame,
                        "The order status changed. Please refresh and try again.");
                loadOrders();
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(
                frame, "Database error:\n" + e.getMessage());
    }
}
}