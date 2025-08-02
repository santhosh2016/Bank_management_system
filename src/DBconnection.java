import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    public static void main(String[] args) {
        try {
            String url = "jdbc:mysql://localhost:3306/bankdb";
            String user = "root";
            String password = "Santhosh@2004"; // Add your password if set

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to MySQL Database!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }
    }
}
