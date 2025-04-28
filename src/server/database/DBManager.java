package server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBManager {

    public DBManager() {}

    public static boolean userExists(String username) {
        String query = "SELECT username FROM credentials WHERE username = ?";
        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error checking user existence: " + e.getMessage());
            return false;
        }
    }

    public static boolean validateCredentials(String username, String password) {
        if (!userExists(username)) {
            return false;
        }

        String query = "SELECT u.is_admin FROM credentials c NATURAL JOIN user u" +
                " WHERE c.username = ? AND c.password = ?";

        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Error validating credentials: " + e.getMessage());
            return false;
        }
    }

    public static boolean isAdmin(String username) {
        String query = "SELECT is_admin FROM user WHERE username = ?";
        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_admin");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting admin status: " + e.getMessage());
        }
        return false;
    }

    public static int[] loadGameConfig() {
        String query = "SELECT waiting_time, round_duration FROM gameconfig WHERE config_id = 50001";
        int[] config = new int[2];
        config[0] = 10; // default waiting time
        config[1] = 30; // default round duration

        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                config[0] = rs.getInt("waiting_time");
                config[1] = rs.getInt("round_duration");
            }
        } catch (SQLException e) {
            System.out.println("Error loading game config: " + e.getMessage());
        }

        return config;
    }

    public static void updateGameConfig(int newWaitingTime, int newRoundDuration) throws SQLException {
        String query = "UPDATE gameconfig SET waiting_time = ?, round_duration = ? WHERE config_id = 50001";
        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query)) {
            stmt.setInt(1, newWaitingTime);
            stmt.setInt(2, newRoundDuration);
            stmt.executeUpdate();
        }
    }
}
