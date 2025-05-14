package server.servants;

import WordWarZ.*;
import org.omg.CORBA.ORB;
import server.database.DBManager;
import server.objects.GameConfig;

import java.sql.SQLException;
import java.util.List;

public class AdminServant extends AdminServicePOA {

    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    @Override
    public void createPlayer(String username, String password) throws NotLoggedIn, UsernameAlreadyExists {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new NotLoggedIn("Username or Password is empty. You must be logged in as Admin.");
        }

        if (DBManager.userExists(username)) {
            throw new UsernameAlreadyExists("Username already exists. Choose a different username.");
        }

        boolean created = DBManager.createUser(username, password);

        if (!created) {
            throw new NotLoggedIn("Failed to create player. Try again.");
        }

        System.out.println("Player " + username + " created successfully.");
    }

    @Override
    public void updatePlayer(String username, String newUsername, String newPassword)
            throws NotLoggedIn, PlayerNotFound, PlayerCurrentlyLoggedIn {

        if (username == null || username.isEmpty()) {
            throw new NotLoggedIn("Original username is required");
        }
        if (newUsername.isEmpty() && newPassword.isEmpty()) {
            throw new NotLoggedIn("Must update either username or password");
        }

        try {
            boolean updated = DBManager.updatePlayer(username, newUsername, newPassword);
            if (!updated) {
                throw new PlayerNotFound("Failed to update player " + username);
            }
            System.out.println("Player " + username + " updated successfully to " + newUsername);
        } catch (SQLException e) {
            System.out.println("Database error updating player: " + e.getMessage());
            throw new PlayerNotFound("Database error updating player");
        }
    }

    @Override
    public void deletePlayer(String username) throws NotLoggedIn, PlayerNotFound, PlayerCurrentlyLoggedIn {
        System.out.println("Delete request for: " + username);

        if (username == null || username.isEmpty()) {
            throw new NotLoggedIn("Username is required");
        }

        if (!DBManager.userExists(username)) {
            throw new PlayerNotFound("Player not found: " + username);
        }
        try {
            boolean deleted = DBManager.deletePlayer(username);
            if (!deleted) {
                throw new PlayerNotFound("Failed to delete player " + username);
            }
            System.out.println("Player " + username + " deleted successfully");
        } catch (SQLException e) {
            System.out.println("Database error details:");
            System.out.println("Message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
        }
    }

    @Override
    public Player getPlayer(String username) throws NotLoggedIn, PlayerNotFound {
        if (username == null || username.isEmpty()) {
            throw new NotLoggedIn("Original username is required");
        }

        try {
            Player player = DBManager.getPlayer(username);
            if (player == null) {
                throw new PlayerNotFound("Player not found: " + username);
            }
            return player;
        } catch (SQLException e) {
            throw new PlayerNotFound("Database error retrieving player");
        }
    }

    @Override
    public Player[] searchPlayers(String searchQuery) throws NotLoggedIn, PlayerNotFound {
        if (searchQuery == null) {
            throw new NotLoggedIn("Search query cannot be null");
        }

        try {
            List<Player> players;
            if (searchQuery.isEmpty()) {
                players = DBManager.searchPlayers("");
            } else {
                players = DBManager.searchPlayers(searchQuery);
            }

            if (players.isEmpty()) {
                throw new PlayerNotFound("No players found matching: '" + searchQuery + "'");
            }
            return players.toArray(new Player[0]);
        } catch (SQLException e) {
            throw new PlayerNotFound("Database error while searching players");
        }
    }

    @Override
    public void setGameWaitingTime(int seconds) throws NotLoggedIn {
        if (seconds <= 0) throw new IllegalArgumentException("Invalid waiting time value.");
        try {
            boolean updated = DBManager.updateGameConfigurations(seconds, GameConfig.getRoundDuration());
            if (!updated) {
                throw new NotLoggedIn("Failed to update waiting time.");
            }
            GameConfig.updateConfig(seconds, GameConfig.getRoundDuration());
        } catch (SQLException e) {
            throw new NotLoggedIn("Database error while updating waiting time: " + e.getMessage());
        }
    }

    @Override
    public void setGameRoundDuration(int seconds) throws NotLoggedIn {
        if (seconds <= 0) throw new IllegalArgumentException("Invalid round duration value.");
        try {
            boolean updated = DBManager.updateGameConfigurations(GameConfig.getWaitingTime(), seconds);
            if (!updated) {
                throw new NotLoggedIn("Failed to update round duration.");
            }
            GameConfig.updateConfig(GameConfig.getWaitingTime(), seconds);
        } catch (SQLException e) {
            throw new NotLoggedIn("Database error while updating round duration: " + e.getMessage());
        }
    }

    @Override
    public int getGameWaitingTime() throws NotLoggedIn {
        return DBManager.getGameWaitingTime();
    }

    @Override
    public int getGameRoundDuration() throws NotLoggedIn {
        return DBManager.getGameRoundDuration();
    }
}