
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProductManagement {

    static javax.swing.table.DefaultTableModel productTableModel;
    static JTable productTable;

    static Color mocha = new Color(91, 58, 41);
    static Color cream = new Color(250, 244, 234);
    static Color caramel = new Color(185, 130, 85);

    public static void main(String[] args) {

        JFrame frame = new JFrame("Don Macchiatos - Products");
        frame.setSize(950, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(cream);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        // Title
        JLabel title = new JLabel(
                "DON MACCHIATOS - PRODUCTS",
                SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(mocha);
        title.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 15, 10)
        );

        // Product table
        String[] columns = {
            "ID", "Product Name", "Category", "Price", "Status"
        };

        productTableModel =
                new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(productTableModel);
        productTable.setFont(new Font("Arial", Font.PLAIN, 14));
        productTable.setRowHeight(30);
        productTable.setForeground(mocha);
        productTable.setSelectionBackground(caramel);
        productTable.setSelectionForeground(Color.WHITE);

        productTable.getTableHeader().setBackground(mocha);
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(190, 190, 190)
                )
        );

        // Buttons
        JButton refreshButton = new JButton("Refresh");
        JButton addButton = new JButton("Add Product");
        JButton availableButton = new JButton("Set Available");
        JButton unavailableButton = new JButton("Set Unavailable");
        JButton backButton = new JButton("Back");

        JButton[] allButtons = {
            refreshButton,
            addButton,
            availableButton,
            unavailableButton,
            backButton
        };

        for (JButton button : allButtons) {
            button.setBackground(mocha);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(155, 38));
        }

        // Button panel
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 8, 8)
        );
        buttonPanel.setBackground(cream);

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(availableButton);
        buttonPanel.add(unavailableButton);
        buttonPanel.add(backButton);

        // Button actions
        refreshButton.addActionListener(e -> loadProducts());
        addButton.addActionListener(e -> addProduct());

        availableButton.addActionListener(
                e -> changeStatus("Available")
        );

        unavailableButton.addActionListener(
                e -> changeStatus("Unavailable")
        );

        backButton.addActionListener(e -> frame.dispose());

        // Arrange screen
        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);

        loadProducts();
    }

    // Load products from MySQL
    public static void loadProducts() {

        if (productTableModel == null) {
            return;
        }

        productTableModel.setRowCount(0);

        String sql = "SELECT * FROM products "
                   + "ORDER BY category, product_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {

                Object[] row = {
                    result.getInt("product_id"),
                    result.getString("product_name"),
                    result.getString("category"),
                    "₱" + String.format(
                            "%.2f", result.getDouble("price")
                    ),
                    result.getString("status")
                };

                productTableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Database error:\n" + e.getMessage()
            );
        }
    }

    // Change product availability
    public static void changeStatus(String newStatus) {

        int selectedRow = productTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please select a product first."
            );
            return;
        }

        int productId = Integer.parseInt(
                productTable.getValueAt(selectedRow, 0).toString()
        );

        String sql = "UPDATE products SET status = ? "
                   + "WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     conn.prepareStatement(sql)) {

            statement.setString(1, newStatus);
            statement.setInt(2, productId);

            int updated = statement.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(
                        null,
                        "Product status changed to "
                                + newStatus + "."
                );
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Product not found."
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Database error:\n" + e.getMessage()
            );
        }
    }

    // Add a new product
    public static void addProduct() {

        JTextField nameField = new JTextField();
        String[] categories = {"Beverage", "Food"};
        JComboBox<String> categoryBox =
                new JComboBox<>(categories);
        JTextField priceField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));

        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Category:"));
        panel.add(categoryBox);

        panel.add(new JLabel("Price:"));
        panel.add(priceField);

        int choice = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Add New Product",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText().trim();
        String category = categoryBox.getSelectedItem().toString();
        String priceText = priceField.getText().trim();

        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please fill in all fields."
            );
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            if (price <= 0) {
                JOptionPane.showMessageDialog(
                        null,
                        "Price must be greater than zero."
                );
                return;
            }

            String sql = "INSERT INTO products "
                       + "(product_name, category, price, status) "
                       + "VALUES (?, ?, ?, ?)";

            try (Connection conn =
                         DatabaseConnection.getConnection();
                 PreparedStatement statement =
                         conn.prepareStatement(sql)) {

                statement.setString(1, name);
                statement.setString(2, category);
                statement.setDouble(3, price);
                statement.setString(4, "Available");

                statement.executeUpdate();

                JOptionPane.showMessageDialog(
                        null,
                        "Product added successfully!"
                );

                loadProducts();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please enter a valid price, such as 99.00."
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Database error:\n" + e.getMessage()
            );
        }
    }
}
