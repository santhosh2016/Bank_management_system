import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Dashboard extends JFrame {
    private String username;
    private JLabel balanceLabel;
    private JTextField amountField;
    private JTable historyTable;

    public Dashboard(String username) {
        this.username = username;
        setTitle("🏦 Dashboard - Welcome " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLayout(new BorderLayout());

        // Top Panel for deposit/withdraw
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        balanceLabel = new JLabel("Balance: ₹0.0");
        amountField = new JTextField(10);
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");

        // Row 0: Balance Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        topPanel.add(balanceLabel, gbc);

        // Row 1: Amount Field + Buttons
        gbc.gridwidth = 1;
        gbc.gridy = 1;

        gbc.gridx = 0;
        topPanel.add(new JLabel("Enter Amount:"), gbc);
        gbc.gridx = 1;
        topPanel.add(amountField, gbc);
        gbc.gridx = 2;
        topPanel.add(depositBtn, gbc);
        gbc.gridx = 3;
        topPanel.add(withdrawBtn, gbc);

        // Center Panel for table
        historyTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(historyTable);

        // Add panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(new JLabel("📄 Transaction History:"), BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);

        // Action listeners
        depositBtn.addActionListener(e -> {
            double amount = Double.parseDouble(amountField.getText());
            if (amount > 0) {
                updateBalance(amount, "deposit");
            } else {
                JOptionPane.showMessageDialog(this, "Enter a valid amount.");
            }
        });

        withdrawBtn.addActionListener(e -> {
            double amount = Double.parseDouble(amountField.getText());
            if (amount > 0) {
                updateBalance(-amount, "withdraw");
            } else {
                JOptionPane.showMessageDialog(this, "Enter a valid amount.");
            }
        });

        // Load initial balance and history
        loadBalance();
        loadTransactionHistory();

        setVisible(true);
    }

    private void updateBalance(double amount, String type) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "Santhosh@2004");

            // Get current balance
            PreparedStatement getBalStmt = conn.prepareStatement("SELECT balance FROM users WHERE username = ?");
            getBalStmt.setString(1, username);
            ResultSet rs = getBalStmt.executeQuery();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "User not found!");
                conn.close();
                return;
            }
            double currentBalance = rs.getDouble("balance");

            if (type.equals("withdraw") && currentBalance + amount < 0) {
                JOptionPane.showMessageDialog(this, "Insufficient balance!");
                conn.close();
                return;
            }

            // Update balance
            PreparedStatement updateStmt = conn.prepareStatement("UPDATE users SET balance = balance + ? WHERE username = ?");
            updateStmt.setDouble(1, amount);
            updateStmt.setString(2, username);
            updateStmt.executeUpdate();

            // Insert transaction
            double newBalance = currentBalance + amount; 
            PreparedStatement transStmt = conn.prepareStatement("INSERT INTO transactions (username, amount, type, balance, timestamp) VALUES (?, ?, ?, ?, NOW())");

            transStmt.setString(1, username);
            transStmt.setDouble(2, Math.abs(amount));
            transStmt.setString(3, type);
            transStmt.setDouble(4, newBalance);
            transStmt.executeUpdate();

            conn.close();

            loadBalance();
            loadTransactionHistory();
            amountField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadBalance() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "Santhosh@2004");
            PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                balanceLabel.setText("Balance: ₹" + rs.getDouble("balance"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTransactionHistory() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "Santhosh@2004");
            PreparedStatement stmt = conn.prepareStatement("SELECT amount, type, timestamp FROM transactions WHERE username = ? ORDER BY timestamp DESC");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int columns = rsmd.getColumnCount();
            DefaultTableModel model = new DefaultTableModel();

            for (int i = 1; i <= columns; i++) {
                model.addColumn(rsmd.getColumnName(i));
            }

            while (rs.next()) {
                Object[] row = new Object[columns];
                for (int i = 1; i <= columns; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            historyTable.setModel(model);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // You can call this from Login after success like: new Dashboard(username);
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard("Santhosh")); // test run
    }
}


