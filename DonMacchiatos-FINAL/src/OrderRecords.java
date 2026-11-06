
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class OrderRecords {

    private static JFrame frame;
    private static DefaultListModel<String> orderListModel;
    private static JList<String> orderList;

    private static final Color MOCHA = new Color(91, 58, 41);
    private static final Color CREAM = new Color(250, 244, 234);
    private static final Color CARAMEL = new Color(185, 130, 85);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_BROWN = new Color(232, 216, 197);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createWindow());
    }

    private static void createWindow() {
        frame = new JFrame("Don Macchiatos - Order Records");
        frame.setSize(850, 600);
        frame.setMinimumSize(new Dimension(700, 450));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(CREAM);
        frame.setLayout(new BorderLayout(10, 10));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(MOCHA);
        header.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel title = new JLabel("DON MACCHIATOS");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(CREAM);

        JLabel subtitle = new JLabel("Order Records");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(LIGHT_BROWN);

        header.add(title);
        header.add(Box.createVerticalStrut(5));
        header.add(subtitle);

        frame.add(header, BorderLayout.NORTH);

        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
        mainPanel.setBackground(CREAM);
        mainPanel.setBorder(new EmptyBorder(15, 18, 5, 18));

        JLabel listTitle = new JLabel("  Order History");
        listTitle.setFont(new Font("Arial", Font.BOLD, 18));
        listTitle.setForeground(MOCHA);

        mainPanel.add(listTitle, BorderLayout.NORTH);

        orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);
        orderList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        orderList.setForeground(MOCHA);
        orderList.setBackground(WHITE);
        orderList.setSelectionBackground(LIGHT_BROWN);
        orderList.setSelectionForeground(MOCHA);
        orderList.setFixedCellHeight(38);
        orderList.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scrollPane = new JScrollPane(orderList);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(LIGHT_BROWN, 1));
        scrollPane.getViewport().setBackground(WHITE);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        frame.add(mainPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(CREAM);
        buttonPanel.setBorder(new EmptyBorder(0, 15, 8, 15));

        JButton refreshButton = createButton("Refresh", CARAMEL);
        JButton backButton = createButton("Back", MOCHA);

        refreshButton.addActionListener(e -> loadOrders());

        backButton.addActionListener(e -> {
            frame.dispose();
            CashierDashboard.main(new String[]{});
        });

        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        loadOrders();
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
        button.setPreferredSize(new Dimension(120, 38));
        return button;
    }

    private static void loadOrders() {
        orderListModel.clear();

        String sql = "SELECT order_id, order_date, total_amount, "
                   + "payment_status, order_status "
                   + "FROM orders ORDER BY order_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("order_id");
                Timestamp date = rs.getTimestamp("order_date");
                double total = rs.getDouble("total_amount");
                String payment = rs.getString("payment_status");
                String status = rs.getString("order_status");

                String dateText = (date == null)
                        ? "No date" : date.toString();

                String record = String.format(
                        "Order #%d | ₱%.2f | Payment: %s | Status: %s | %s",
                        id, total, payment, status, dateText);

                orderListModel.addElement(record);
            }

            if (orderListModel.isEmpty()) {
                orderListModel.addElement("No orders found.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Could not load order records:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
