import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class loginForm extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton;

    public loginForm() {
        setTitle("Bank Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        add(loginButton);

        loginButton.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", "Santhosh@2004");
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                 JOptionPane.showMessageDialog(this, "Login successful!");
                 this.dispose(); // Close login window
                 new Dashboard(username);
                // Proceed to dashboard or next window
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }

            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new loginForm();
    }
}
