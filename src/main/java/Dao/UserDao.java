
package dao;

import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserDao {

    // CREATE SAMPLE USERS FOR TESTING
    public void createDefaultUsers() {

        String sql = """
                INSERT OR IGNORE INTO users
                (username, password, roles, status)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String[][] users = {
                    {"admin", "admin123", "ADMIN", "APPROVED"},
                    {"receptionist", "rec123", "RECEPTIONIST", "APPROVED"},
                    {"doctor", "doc123", "DOCTOR", "PENDING"}
            };

            for (String[] user : users) {
                ps.setString(1, user[0]);
                ps.setString(2, user[1]);
                ps.setString(3, user[2]);
                ps.setString(4, user[3]);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Error creating users: " + e.getMessage());
        }
    }

    // LOGIN
    public User login(String username, String password) {

        String sql = """
                SELECT * FROM users
                WHERE username = ? AND password = ?
                AND UPPER(status) = 'APPROVED'
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("roles"),
                            rs.getString("status")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
        }

        return null;
    }

    // GET ALL USERS
    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }

        return users;
    }

    // FIND USER BY ID
    public User getUserById(int userId) {

        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("roles"),
                            rs.getString("status")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding user: " + e.getMessage());
        }

        return null;
    }

    // REQUIRED UPDATE METHOD
    public boolean updateUserRoleAndStatus(
            int userId,
            String newRole,
            String newStatus) {

        String sql = """
                UPDATE users
                SET roles = ?, status = ?
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newRole.toUpperCase(Locale.ROOT));
            ps.setString(2, newStatus.toUpperCase(Locale.ROOT));
            ps.setInt(3, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Update Error: " + e.getMessage());
            return false;
        }
    }
}
