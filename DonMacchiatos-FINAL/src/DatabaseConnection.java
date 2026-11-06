import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/don_macchiatos",
                "root",
                ""
            );
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            System.out.println(e.getMessage());
            return null;
        }
    }
}