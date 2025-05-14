package server.database;

import WordWarZ.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static server.database.DBConnection.con;

public class DBManager {

    public DBManager() {
    }

    public static boolean userExists(String username) {
        String query = "SELECT username FROM credentials WHERE username = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
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

        try (PreparedStatement stmt = con.prepareStatement(query)) {
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

    public static int[] loadGameConfig() {
        String query = "SELECT waiting_time, round_duration FROM gameconfig WHERE config_id = 50001";
        int[] config = new int[2];
        config[0] = 10; // default waiting time
        config[1] = 30; // default round duration

        try (PreparedStatement stmt = con.prepareStatement(query);
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

    public static boolean isAdmin(String username) {
        String query = "SELECT is_admin FROM user WHERE username = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
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
        try (PreparedStatement stmt = con.prepareStatement(query);
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
            try (PreparedStatement userStmt = con.prepareStatement(userQuery)) {
                userStmt.setInt(1, userId);  // User ID
                userStmt.setString(2, username);  // Username
                userStmt.setString(3, isAdmin);  // 'Y' for admin, 'N' for regular user
                userStmt.executeUpdate();
            }

            // Insert into the 'credentials' table
            try (PreparedStatement credStmt = con.prepareStatement(credentialsQuery)) {
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

    public static boolean updatePlayer(String username, String newUsername, String newPassword) throws SQLException {
        if (!userExists(username)) {
            System.out.println("[ERROR] User " + username + " not found");
            return false;
        }

        con.setAutoCommit(false);

        try {
            // Get the user_id first
            int userId = getUserId(username);
            if (userId == -1) {
                throw new SQLException("User ID not found for: " + username);
            }

            // Update credentials table
            String credentialsQuery = "UPDATE credentials SET username = ?, password = ? WHERE user_id = ?";
            try (PreparedStatement credStmt = con.prepareStatement(credentialsQuery)) {
                credStmt.setString(1, newUsername.isEmpty() ? username : newUsername);
                credStmt.setString(2, newPassword.isEmpty() ? getCurrentPassword(username) : newPassword);
                credStmt.setInt(3, userId);
                credStmt.executeUpdate();
            }

            // Update user table if username changed
            if (!newUsername.isEmpty()) {
                String userQuery = "UPDATE user SET username = ? WHERE user_id = ?";
                try (PreparedStatement userStmt = con.prepareStatement(userQuery)) {
                    userStmt.setString(1, newUsername);
                    userStmt.setInt(2, userId);
                    userStmt.executeUpdate();
                }
            }
            con.commit();
            return true;
        } catch (SQLException e) {
            con.rollback();
            System.out.println("Error updating player: " + e.getMessage());
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public static boolean deletePlayer(String username) throws SQLException {
        System.out.println("Attempting to delete player: " + username);

        if (!userExists(username)) {
            System.out.println("[ERROR] User " + username + " not found in credentials table");
            throw new SQLException("User not found");
        }

        // Verify database connection
        if (DBConnection.con == null || DBConnection.con.isClosed()) {
            System.out.println("[ERROR] Database connection is null or closed");
            throw new SQLException("Database connection is not available");
        }

        DBConnection.con.setAutoCommit(false);

        try {
            int userId = getUserId(username);
            if (userId == -1) {
                System.out.println("[ERROR] User ID not found for username: " + username);
                throw new SQLException("User ID not found");
            }
            System.out.println("Found user_id: " + userId + " for username: " + username);

            // First delete from child tables
            String[] childTables = {"credentials", "player_stats", "game_history"}; // Add all child tables
            for (String table : childTables) {
                try {
                    String deleteQuery = "DELETE FROM " + table + " WHERE user_id = ?";
                    System.out.println("Executing query: " + deleteQuery);
                    try (PreparedStatement stmt = DBConnection.con.prepareStatement(deleteQuery)) {
                        stmt.setInt(1, userId);
                        int rows = stmt.executeUpdate();
                        System.out.println("Deleted " + rows + " rows from " + table);
                    }
                } catch (SQLException e) {
                    System.out.println("Warning: Could not delete from " + table +
                            " - " + e.getMessage());
                }
            }

            // Then delete from user table
            String userQuery = "DELETE FROM user WHERE user_id = ?";
            System.out.println("Executing final query: " + userQuery);
            try (PreparedStatement userStmt = DBConnection.con.prepareStatement(userQuery)) {
                userStmt.setInt(1, userId);
                int userRows = userStmt.executeUpdate();
                System.out.println("Rows affected in user: " + userRows);
                if (userRows == 0) {
                    throw new SQLException("No rows deleted from user table");
                }
            }

            DBConnection.con.commit();
            System.out.println("Player " + username + " deleted successfully");
            return true;
        } catch (SQLException e) {
            System.out.println("Error deleting player: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            DBConnection.con.rollback();
            throw e;
        } finally {
            DBConnection.con.setAutoCommit(true);
        }
    }
    private static int getUserId(String username) throws SQLException {
        String query = "SELECT user_id FROM credentials WHERE username = ?";
        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("user_id") : -1;
        }
    }

    private static String getCurrentPassword(String username) throws SQLException {
        String query = "SELECT password FROM credentials WHERE username = ?";
        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("password") : null;
        }
    }

    public static Player getPlayer(String username) throws SQLException {
        String query = "SELECT username, wins FROM user WHERE username = ?";

        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Player(rs.getString("username"), rs.getInt("wins"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    public static List<Player> searchPlayers(String searchQuery) throws SQLException {
        List<Player> players = new ArrayList<>();
        String query = "SELECT username, wins FROM user WHERE username LIKE ? AND is_admin <> 'Y' ORDER BY wins DESC;";

        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query)) {
            stmt.setString(1, "%" + searchQuery + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    players.add(new Player(rs.getString("username"), rs.getInt("wins")));
                }
            }
        }
        return players;
    }

    /**
     * Updates both game configuration settings in a single transaction
     *
     * @param waitingTime   The waiting time in seconds
     * @param roundDuration The round duration in seconds
     * @return true if successful, false otherwise
     */
    public static boolean updateGameConfigurations(int waitingTime, int roundDuration) {
        String query = "UPDATE gameconfig SET waiting_time = ?, round_duration = ? WHERE config_id = 50001";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
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
        String query = "SELECT waiting_time FROM gameconfig WHERE config_id = 50001";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
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
        String query = "SELECT round_duration FROM gameconfig WHERE config_id = 50001";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
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

    /**
     * Increments the win count for a specific player
     * @param username The username of the player who won
     * @return true if successful, false otherwise
     */
    public static boolean incrementWinCount(String username) {
        String query = "UPDATE user SET wins = wins + 1 WHERE username = ?";

        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query)) {
            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error incrementing win count for " + username + ": " + e.getMessage());
            return false;
        }
    }

    public static List<Player> getTopPlayers(int limit) {
        List<Player> players = new ArrayList<>();
        try {
            String sql = "SELECT username, wins FROM user WHERE wins > 0 ORDER BY wins DESC LIMIT ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                Player player = new Player();
                player.username = rs.getString("username");
                player.wins = rs.getInt("wins");
                players.add(player);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching leaderboard: " + e.getMessage());
        }
        return players;
    }
}


