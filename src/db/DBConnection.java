import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static final String URL = "jdbc:mysql://localhost:3306/campus_bus_tracker";
    public static final String USER = "root";
    public static final String PASSWORD = "";

    public static Connection conn = null;

    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connection established!");
            } catch (SQLException e) {
                System.out.println("Connection failed!");
            }
        }
        return conn;
    }

    public static void logout() {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
                System.out.println("Connection closed!");
            } catch (SQLException e) {
                System.out.println("Connection failed!");
            }
        }
    }
}
