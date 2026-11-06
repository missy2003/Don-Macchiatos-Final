
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class NewOrder {

    private static JFrame frame;
    private static JPanel productPanel;
    private static JPanel orderPanel;
    private static JLabel totalLabel;

    private static final Color MOCHA = new Color(91, 58, 41);
    private static final Color CREAM = new Color(250, 244, 234);
    private static final Color CARAMEL = new Color(185, 130, 85);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_BROWN = new Color(232, 216, 197);

    private static final Map<Integer, OrderItem> cart =
            new LinkedHashMap<Integer, OrderItem>();

    private static double total = 0;

    static class OrderItem {
        int id;
        String name;
        double price;
        int quantity;

        OrderItem(int id, String name, double price, int quantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createWindow());
    }

    private static void createWindow() {
        cart.clear();
        total = 0;

        frame = new JFrame("Don Macchiatos - New Order");
        frame.setSize(950, 650);
        frame.setMinimumSize(new Dimension(800, 550));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(CREAM);
        frame.setLayout(new BorderLayout(12, 12));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MOCHA);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("DON MACCHIATOS");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(CREAM);

        JLabel subtitle = new JLabel("New Customer Order");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(LIGHT_BROWN);

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(MOCHA);
        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);

        header.add(heading, BorderLayout.WEST);
        frame.add(header, BorderLayout.NORTH);

        // Main area
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        mainPanel.setBackground(CREAM);
        mainPanel.setBorder(new EmptyBorder(0, 15, 10, 15));

        // Products section
        JPanel leftPanel = new JPanel(new BorderLayout(8, 8));
        leftPanel.setBackground(CREAM);

        JLabel productsTitle = new JLabel("  Available Products");
        productsTitle.setFont(new Font("Arial", Font.BOLD, 18));
        productsTitle.setForeground(MOCHA);
        leftPanel.add(productsTitle, BorderLayout.NORTH);

        productPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        productPanel.setBackground(CREAM);
        productPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JScrollPane productScroll = new JScrollPane(productPanel);
        productScroll.setBorder(BorderFactory.createLineBorder(LIGHT_BROWN));
        productScroll.getViewport().setBackground(CREAM);
        leftPanel.add(productScroll, BorderLayout.CENTER);

        // Current order section
        orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBackground(WHITE);
        orderPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane orderScroll = new JScrollPane(orderPanel);
        orderScroll.setBorder(BorderFactory.createLineBorder(LIGHT_BROWN));
        orderScroll.getViewport().setBackground(WHITE);

        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setBackground(CREAM);

        JLabel orderTitle = new JLabel("  Current Order");
        orderTitle.setFont(new Font("Arial", Font.BOLD, 18));
        orderTitle.setForeground(MOCHA);
        rightPanel.add(orderTitle, BorderLayout.NORTH);
        rightPanel.add(orderScroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(CREAM);

        totalLabel = new JLabel("Total: ₱0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalLabel.setForeground(MOCHA);
        totalLabel.setBorder(new EmptyBorder(5, 5, 10, 5));
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        buttonPanel.setBackground(CREAM);

        JButton payButton = createButton("Pay Order", CARAMEL);
        JButton clearButton = createButton("Clear Order", MOCHA);

        payButton.addActionListener(e -> processPayment());
        clearButton.addActionListener(e -> clearOrder());

        buttonPanel.add(payButton);
        buttonPanel.add(clearButton);

        bottomPanel.add(totalLabel);
        bottomPanel.add(buttonPanel);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        frame.add(mainPanel, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(CREAM);
        footer.setBorder(new EmptyBorder(0, 15, 8, 15));

        JButton backButton = createButton("Back to Dashboard", MOCHA);
        backButton.addActionListener(e -> {
            frame.dispose();
            CashierDashboard.main(new String[]{});
        });

        footer.add(backButton);
        frame.add(footer, BorderLayout.SOUTH);

        loadProducts();
        refreshOrder();

        frame.setVisible(true);
    }

    private static JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 38));
        return button;
    }

    private static void loadProducts() {
        productPanel.removeAll();

        String sql = "SELECT product_id, product_name, category, price "
                   + "FROM products WHERE status = 'Available' "
                   + "ORDER BY category, product_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("product_id");
                String name = rs.getString("product_name");
                String category = rs.getString("category");
                double price = rs.getDouble("price");

                JButton productButton = new JButton(
                        "<html><center><b>" + name
                        + "</b><br>" + category
                        + "<br>₱" + String.format("%.2f", price)
                        + "</center></html>");

                productButton.setFont(new Font("Arial", Font.PLAIN, 13));
                productButton.setBackground(WHITE);
                productButton.setForeground(MOCHA);
                productButton.setFocusPainted(false);
                productButton.setOpaque(true);
                productButton.setBorder(BorderFactory.createLineBorder(
                        LIGHT_BROWN, 1));
                productButton.setPreferredSize(new Dimension(170, 85));
                productButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

                productButton.addActionListener(e ->
                        addProduct(id, name, price));

                productPanel.add(productButton);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Could not load products:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        productPanel.revalidate();
        productPanel.repaint();
    }

    private static void addProduct(int id, String name, double price) {
        String input = JOptionPane.showInputDialog(frame,
                "Enter quantity for " + name + ":",
                "Add Product", JOptionPane.PLAIN_MESSAGE);

        if (input == null) {
            return;
        }

        try {
            int quantity = Integer.parseInt(input.trim());

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(frame,
                        "Please enter a quantity greater than zero.");
                return;
            }

            if (cart.containsKey(id)) {
                cart.get(id).quantity += quantity;
            } else {
                cart.put(id, new OrderItem(id, name, price, quantity));
            }

            refreshOrder();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Please enter a valid whole number.");
        }
    }

    private static void refreshOrder() {
        orderPanel.removeAll();
        total = 0;

        for (OrderItem item : cart.values()) {
            double subtotal = item.price * item.quantity;
            total += subtotal;

            JLabel itemLabel = new JLabel(
                    item.name + " x " + item.quantity
                    + " = ₱" + String.format("%.2f", subtotal));

            itemLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            itemLabel.setForeground(MOCHA);
            itemLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
            itemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            orderPanel.add(itemLabel);
        }

        totalLabel.setText("Total: ₱" + String.format("%.2f", total));

        orderPanel.revalidate();
        orderPanel.repaint();
    }

    private static void clearOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "The order is already empty.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(frame,
                "Clear all items from this order?",
                "Clear Order", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            cart.clear();
            refreshOrder();
        }
    }

    private static void processPayment() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Please add at least one product first.");
            return;
        }

        String input = JOptionPane.showInputDialog(frame,
                "Total: ₱" + String.format("%.2f", total)
                + "\nEnter cash received:",
                "Payment", JOptionPane.PLAIN_MESSAGE);

        if (input == null) {
            return;
        }

        double cash;

        try {
            cash = Double.parseDouble(input.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Please enter a valid amount.");
            return;
        }

        if (cash < total) {
            JOptionPane.showMessageDialog(frame,
                    "Insufficient cash. Please enter enough to pay.");
            return;
        }

        double change = cash - total;
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();

            if (conn == null) {
                JOptionPane.showMessageDialog(frame,
                        "Could not connect to the database.");
                return;
            }

            conn.setAutoCommit(false);

            int orderId;

            String orderSQL =
                    "INSERT INTO orders "
                    + "(total_amount, payment_status, order_status) "
                    + "VALUES (?, 'Paid', 'Pending')";

            try (PreparedStatement ps = conn.prepareStatement(
                    orderSQL, Statement.RETURN_GENERATED_KEYS)) {

                ps.setDouble(1, total);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Could not get the order ID.");
                    }
                    orderId = keys.getInt(1);
                }
            }

            String itemSQL =
                    "INSERT INTO order_items "
                    + "(order_id, product_id, quantity, subtotal) "
                    + "VALUES (?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(itemSQL)) {
                for (OrderItem item : cart.values()) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, item.id);
                    ps.setInt(3, item.quantity);
                    ps.setDouble(4, item.price * item.quantity);
                    ps.addBatch();
                }

                ps.executeBatch();
            }

            conn.commit();

            String receipt = "DON MACCHIATOS\n"
                    + "----------------------------\n"
                    + "Order Number: " + orderId + "\n";

            for (OrderItem item : cart.values()) {
                receipt += item.name + " x " + item.quantity
                        + " - ₱" + String.format("%.2f",
                        item.price * item.quantity) + "\n";
            }

            receipt += "----------------------------\n"
                    + "Total: ₱" + String.format("%.2f", total) + "\n"
                    + "Cash: ₱" + String.format("%.2f", cash) + "\n"
                    + "Change: ₱" + String.format("%.2f", change) + "\n"
                    + "Payment: PAID\n"
                    + "Order Status: PENDING\n"
                    + "Thank you for ordering!";

            JOptionPane.showMessageDialog(frame, receipt,
                    "Payment Successful", JOptionPane.INFORMATION_MESSAGE);

            cart.clear();
            refreshOrder();

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    ex.addSuppressed(rollbackEx);
                }
            }

            JOptionPane.showMessageDialog(frame,
                    "Payment could not be completed:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    System.out.println("Connection closing error: "
                            + ex.getMessage());
                }
            }
        }
    }
}
