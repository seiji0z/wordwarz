package server.database;

import WordWarZ.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public static boolean isAdmin(String username) {
        String query = "SELECT is_admin FROM user WHERE username = ?";
        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return "Y".equalsIgnoreCase(rs.getString("is_admin"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting admin status: " + e.getMessage());
        }
        return false;
    }

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
        int userId = getNextUserId();
        String isAdmin = username.equals("admin") ? "Y" : "N";

        String userQuery = "INSERT INTO user (user_id, username, is_admin, wins) VALUES (?, ?, ?, 0)";
        String credentialsQuery = "INSERT INTO credentials (user_id, username, password) VALUES (?, ?, ?)";

        try {
            try (PreparedStatement userStmt = DBConnection.con.prepareStatement(userQuery)) {
                userStmt.setInt(1, userId);
                userStmt.setString(2, username);
                userStmt.setString(3, isAdmin);
                userStmt.executeUpdate();
            }

            try (PreparedStatement credStmt = DBConnection.con.prepareStatement(credentialsQuery)) {
                credStmt.setInt(1, userId);
                credStmt.setString(2, username);
                credStmt.setString(3, password);
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

        DBConnection.con.setAutoCommit(false);

        try {
            int userId = getUserId(username);
            if (userId == -1) {
                throw new SQLException("User ID not found for: " + username);
            }

            String credentialsQuery = "UPDATE credentials SET username = ?, password = ? WHERE user_id = ?";
            try (PreparedStatement credStmt = DBConnection.con.prepareStatement(credentialsQuery)) {
                credStmt.setString(1, newUsername.isEmpty() ? username : newUsername);
                credStmt.setString(2, newPassword.isEmpty() ? getCurrentPassword(username) : newPassword);
                credStmt.setInt(3, userId);
                credStmt.executeUpdate();
            }

            if (!newUsername.isEmpty()) {
                String userQuery = "UPDATE user SET username = ? WHERE user_id = ?";
                try (PreparedStatement userStmt = DBConnection.con.prepareStatement(userQuery)) {
                    userStmt.setString(1, newUsername);
                    userStmt.setInt(2, userId);
                    userStmt.executeUpdate();
                }
            }

            DBConnection.con.commit();
            return true;
        } catch (SQLException e) {
            DBConnection.con.rollback();
            System.out.println("Error updating player: " + e.getMessage());
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
        String query = "SELECT username, wins FROM user WHERE username LIKE ? ORDER BY wins DESC";

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

        try {
            DBConnection.con.setAutoCommit(false);
            try (PreparedStatement stmt = DBConnection.con.prepareStatement(query)) {
                stmt.setInt(1, waitingTime);
                stmt.setInt(2, roundDuration);
                int rowsAffected = stmt.executeUpdate();
                DBConnection.con.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                DBConnection.con.rollback();
                System.out.println("Error updating game configurations: " + e.getMessage());
                return false;
            } finally {
                DBConnection.con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Error setting auto-commit: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the current game waiting time
     *
     * @return waiting time in seconds, or default 10 if not found
     */
    public static int getGameWaitingTime() {
        String query = "SELECT waiting_time FROM gameconfig WHERE config_id = 50001";

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
     * @return round duration in seconds, or default 30 if not found
     */
    public static int getGameRoundDuration() {
        String query = "SELECT round_duration FROM gameconfig WHERE config_id = 50001";

        try (PreparedStatement stmt = DBConnection.con.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("round_duration");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting round duration: " + e.getMessage());
        }

        return 30; // Default value if not found
    }
}