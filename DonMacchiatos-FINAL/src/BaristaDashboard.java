import javax.swing.*;
import java.awt.*;

public class BaristaDashboard {

    static DefaultListModel<String> orderListModel;
    static JList<String> orderList;

    public static void main(String[] args) {

        JFrame frame = new JFrame("Don Macchiatos - Barista");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel(
                "DON MACCHIATOS - BARISTA",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 24));

        // ORDER LIST
        orderListModel = new DefaultListModel<>();

        orderList = new JList<>(orderListModel);

        JScrollPane scrollPane =
                new JScrollPane(orderList);

        // BUTTONS
        JButton refreshButton =
                new JButton("Refresh");

        JButton preparingButton =
                new JButton("Start Preparing");

        JButton completeButton =
                new JButton("Complete Order");

        JPanel buttonPanel = new JPanel();

buttonPanel.add(refreshButton);
buttonPanel.add(preparingButton);
buttonPanel.add(completeButton);

JButton logoutButton = new JButton("Logout");

buttonPanel.add(logoutButton);

logoutButton.addActionListener(e -> {
    frame.dispose();
    Login.showLogin();
});

        // LOAD ORDERS
        loadOrders();

        // REFRESH
        refreshButton.addActionListener(e -> {
            loadOrders();
        });

        // START PREPARING
        preparingButton.addActionListener(e -> {

            String selected =
                    orderList.getSelectedValue();

            if (selected == null) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please select an order."
                );

            } else {

                int orderId =
                        getOrderId(selected);

                updateStatus(
                        orderId,
                        "Preparing"
                );

                loadOrders();
            }
        });

        // COMPLETE ORDER
        completeButton.addActionListener(e -> {

            String selected =
                    orderList.getSelectedValue();

            if (selected == null) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please select an order."
                );

            } else {

                int orderId =
                        getOrderId(selected);

                updateStatus(
                        orderId,
                        "Completed"
                );

                loadOrders();
            }
        });

        // ADD COMPONENTS
        mainPanel.add(
                title,
                BorderLayout.NORTH
        );

        mainPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        mainPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        frame.add(mainPanel);

        frame.setVisible(true);
    }


    // ==============================
    // LOAD ORDERS
    // ==============================

    public static void loadOrders() {

        orderListModel.clear();

        try {

            java.sql.Statement statement =
                    DatabaseConnection
                    .getConnection()
                    .createStatement();

            java.sql.ResultSet result =
                    statement.executeQuery(
"SELECT order_id, total_amount, "
+ "payment_status, order_status "
+ "FROM orders "
+ "WHERE order_status <> 'Cancelled' "
+ "ORDER BY order_id DESC"
                    );

            while (result.next()) {

                int orderId =
                        result.getInt("order_id");

                double total =
                        result.getDouble("total_amount");

                String payment =
                        result.getString("payment_status");

                String status =
                        result.getString("order_status");

                String text =
                        "Order #" + orderId
                        + " | Total: ₱"
                        + String.format("%.2f", total)
                        + " | Payment: "
                        + payment
                        + " | Status: "
                        + status;

                orderListModel.addElement(text);
            }

            result.close();
            statement.close();

        } catch (java.sql.SQLException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Database error:\n"
                    + e.getMessage()
            );
        }
    }


    // ==============================
    // GET ORDER ID
    // ==============================

    public static int getOrderId(String text) {

    int hashPosition = text.indexOf("#");

    int spacePosition =
            text.indexOf(" ", hashPosition);

    String number =
            text.substring(
                    hashPosition + 1,
                    spacePosition
            );

    return Integer.parseInt(number);
}


    // ==============================
    // UPDATE STATUS
    // ==============================

    public static void updateStatus(
            int orderId,
            String status
    ) {

        try {

            java.sql.Statement statement =
                    DatabaseConnection
                    .getConnection()
                    .createStatement();

            String sql =
                    "UPDATE orders SET order_status = '"
                    + status
                    + "' WHERE order_id = "
                    + orderId;

            statement.executeUpdate(sql);

            statement.close();

            JOptionPane.showMessageDialog(
                    null,
                    "Order #" + orderId
                    + " is now "
                    + status + "."
            );

        } catch (java.sql.SQLException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Database error:\n"
                    + e.getMessage()
            );
        }
    }
}