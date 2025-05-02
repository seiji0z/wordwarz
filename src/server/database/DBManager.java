package server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBManager {

    public DBManager() {
    }

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
                    // Interpret 'Y' as true
                    return "Y".equalsIgnoreCase(rs.getString("is_admin"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting admin status: " + e.getMessage());
        }
        return false;
    }

    // generate new user_id for new players
    public static int getNextUserId() {
        String query = "SELECT MAX(user_id) AS max_id FROM user";
        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("max_id") + 1;
            }
        } catch (SQLException e) {
            System.out.println("Error generating user ID: " + e.getMessage());
        }
        return 10000; // fallback starting ID
    }

    public static boolean createUser(String username, String password) {

        int userId = getNextUserId();  // Implement this to get the next available user ID

        // Check if the user should be an admin
        String isAdmin = username.equals("admin") ? "Y" : "N";  // Assign 'Y' for admin, 'N' otherwise

        // SQL for inserting into the 'user' table
        String userQuery = "INSERT INTO user (user_id, username, is_admin, wins) VALUES (?, ?, ?, 0)";
        // SQL for inserting into the 'credentials' table
        String credentialsQuery = "INSERT INTO credentials (user_id, username, password) VALUES (?, ?, ?)";

        try {
            // Insert into the 'user' table
            try (PreparedStatement userStmt = DBConnection.con.prepareStatement(userQuery)) {
                userStmt.setInt(1, userId);  // User ID
                userStmt.setString(2, username);  // Username
                userStmt.setString(3, isAdmin);  // 'Y' for admin, 'N' for regular user
                userStmt.executeUpdate();
            }

            // Insert into the 'credentials' table
            try (PreparedStatement credStmt = DBConnection.con.prepareStatement(credentialsQuery)) {
                credStmt.setInt(1, userId);  // User ID
                credStmt.setString(2, username);  // Username
                credStmt.setString(3, password);  // Password
                credStmt.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    public static boolean updatePlayer(String username, String newPassword) throws SQLException {
        // Check if player exists first
        if (!userExists(username)) {
            return false;
        }

        // Update both tables in a transaction
        DBConnection.con.setAutoCommit(false);

        try {
            // Update credentials table
            String credentialsQuery = "UPDATE credentials SET password = ? WHERE username = ?";
            try (PreparedStatement credStmt = DBConnection.con.prepareStatement(credentialsQuery)) {
                credStmt.setString(1, newPassword);
                credStmt.setString(2, username);
                credStmt.executeUpdate();
            }

            // Commit if both updates succeeded
            DBConnection.con.commit();
            return true;
        } catch (SQLException e) {
            DBConnection.con.rollback();
            System.out.println("Error updating player: " + e.getMessage());
            return false;
        } finally {
            DBConnection.con.setAutoCommit(true);
        }
    }

    /**
     * Updates both game configuration settings in a single transaction
     *
     * @param waitingTime   The waiting time in seconds
     * @param roundDuration The round duration in seconds
     * @return true if successful, false otherwise
     */
    public static boolean updateGameConfigurations(int waitingTime, int roundDuration) {
        String query = "INSERT INTO gameconfig (waiting_time, round_duration) VALUES (?, ?)";

        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query)) {
            stmt.setInt(1, waitingTime);
            stmt.setInt(2, roundDuration);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error updating game configurations: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the current game waiting time
     *
     * @return waiting time in seconds, or default 60 if not found
     */
    public static int getGameWaitingTime() {
        String query = "SELECT waiting_time FROM gameconfig WHERE config_id = 1";

        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("waiting_time");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting waiting time: " + e.getMessage());
        }

        return 10; // Default value if not found
    }

    /**
     * Gets the current game round duration
     *
     * @return round duration in seconds
     */
    public static int getGameRoundDuration() {
        String query = "SELECT round_duration FROM gameconfig WHERE config_id = 1";

        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("round_duration");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting round duration: " + e.getMessage());
        }

        return 30;
    }
}


