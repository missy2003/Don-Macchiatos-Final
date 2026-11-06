import javax.swing.*;
import java.awt.*;

public class CancelOrder {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Don Macchiatos - Cancel Order");
        frame.setSize(450, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel title = new JLabel("CANCEL ORDER");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel orderLabel = new JLabel("Order Number:");
        JTextField orderField = new JTextField(15);

        JButton cancelButton =
                new JButton("Cancel Order");

        JButton backButton =
                new JButton("Back");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(orderLabel, gbc);

        gbc.gridx = 1;
        panel.add(orderField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(cancelButton, gbc);

        gbc.gridx = 1;
        panel.add(backButton, gbc);

        cancelButton.addActionListener(e -> {

            String orderText =
                    orderField.getText();

            if (orderText.isEmpty()) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter an order number."
                );

                return;
            }

            try {

                int orderId =
                        Integer.parseInt(orderText);

                java.sql.Connection conn =
                        DatabaseConnection.getConnection();

                String checkSQL =
                        "SELECT order_status "
                        + "FROM orders "
                        + "WHERE order_id = ?";

                java.sql.PreparedStatement checkStatement =
                        conn.prepareStatement(checkSQL);

                checkStatement.setInt(1, orderId);

                java.sql.ResultSet result =
                        checkStatement.executeQuery();

                if (result.next()) {

                    String status =
                            result.getString("order_status");

                    if (status.equals("Pending")) {

                        int choice =
                                JOptionPane.showConfirmDialog(
                                        frame,
                                        "Cancel Order #"
                                        + orderId
                                        + "?",
                                        "Confirm Cancellation",
                                        JOptionPane.YES_NO_OPTION
                                );

                        if (choice ==
                                JOptionPane.YES_OPTION) {

                            String updateSQL =
                                    "UPDATE orders "
                                    + "SET order_status = 'Cancelled' "
                                    + "WHERE order_id = ?";

                            java.sql.PreparedStatement
                                    updateStatement =
                                    conn.prepareStatement(
                                            updateSQL
                                    );

                            updateStatement.setInt(
                                    1,
                                    orderId
                            );

                            updateStatement.executeUpdate();

                            updateStatement.close();

                            JOptionPane.showMessageDialog(
                                    frame,
                                    "Order #"
                                    + orderId
                                    + " has been cancelled."
                            );

                            orderField.setText("");
                        }

                    } else if (status.equals("Preparing")) {

                        JOptionPane.showMessageDialog(
                                frame,
                                "This order is already being prepared."
                        );

                    } else if (status.equals("Completed")) {

                        JOptionPane.showMessageDialog(
                                frame,
                                "This order is already completed."
                        );

                    } else if (status.equals("Cancelled")) {

                        JOptionPane.showMessageDialog(
                                frame,
                                "This order is already cancelled."
                        );

                    }

                } else {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Order not found."
                    );
                }

                result.close();
                checkStatement.close();
                conn.close();

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter a valid order number."
                );

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