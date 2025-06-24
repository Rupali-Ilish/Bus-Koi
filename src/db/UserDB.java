import javax.swing.*;
import java.sql.*;
import java.sql.Connection;

public class UserDB {
    public static boolean register(User user) {
        if (!user.getEmail().endsWith("@juniv.edu")) {
            System.out.println("Invalid email domain!"); // dont use JOptionPane cause its backend
            return false;
        }
        if (!user.getRole().equals("admin") && !user.getRole().equals("student")) {
            System.out.println("Invalid role!");
            return false;
        }

        // check if already registered
        Connection conn = DBConnection.getConnection();

        String checkemail = "SELECT * FROM users WHERE email = ?";
        try {
            PreparedStatement checkemailStmt = conn.prepareStatement(checkemail);

            checkemailStmt.setString(1, user.getEmail());
            ResultSet rs = checkemailStmt.executeQuery();
            if (rs.next()) {
                System.out.println("Email already exists!");
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error already register: " + e);
            return false;
        }

        // new register
        String insert = "INSERT INTO users (name, email, password, role) VALUES (?,?,?,?)";
        try {
            PreparedStatement insertStmt = conn.prepareStatement(insert);

            insertStmt.setString(1, user.getUser_name());
            insertStmt.setString(2, user.getEmail());
            insertStmt.setString(3, user.getPassword());
            insertStmt.setString(4, user.getRole());

            int row_affected = insertStmt.executeUpdate();
            return row_affected > 0; // true if registration done

        } catch (Exception e) {
            System.out.println("Error new register: " + e);
            return false;
        }
    }

    public static User login(String email, String password) {
        String query = "SELECT *FROM users WHERE email = ? AND password = ?";
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement loginStmt = conn.prepareStatement(query);
            loginStmt.setString(1, email);
            loginStmt.setString(2, password);

            ResultSet rs = loginStmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("name"), rs.getString("email"), rs.getString("password"), rs.getString("role"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    public static void logout() {
        DBConnection.logout();
    }
}
