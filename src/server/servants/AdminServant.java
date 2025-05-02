package server.servants;

import WordWarZ.*;
import org.omg.CORBA.ORB;
import server.database.DBManager;

import java.sql.SQLException;

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
    public void updatePlayer(String username, String newPassword)
            throws NotLoggedIn, PlayerNotFound, PlayerCurrentlyLoggedIn {

        if (username == null || username.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            throw new NotLoggedIn("Invalid input parameters");
        }

        // Check if player exists
        if (!DBManager.userExists(username)) {
            throw new PlayerNotFound("Player with username " + username + " not found");
        }

        // TODO: Add check if player is currently logged in (if you have session tracking)
        // if (isPlayerLoggedIn(username)) {
        //     throw new PlayerCurrentlyLoggedIn("Player is currently logged in");
        // }

        try {
            boolean updated = DBManager.updatePlayer(username, newPassword);
            if (!updated) {
                throw new PlayerNotFound("Failed to update player " + username);
            }
            System.out.println("Player " + username + " updated successfully");
        } catch (SQLException e) {
            System.out.println("Database error updating player: " + e.getMessage());
            throw new PlayerNotFound("Database error updating player");
        }
    }

    @Override
    public void deletePlayer(String username) throws NotLoggedIn, PlayerNotFound, PlayerCurrentlyLoggedIn {

    }

    @Override
    public Player getPlayer(String username) throws NotLoggedIn, PlayerNotFound {
        return null;
    }

    @Override
    public Player[] searchPlayers(String searchQuery) throws NotLoggedIn, PlayerNotFound {
        return new Player[0];
    }

    @Override
    public void setGameWaitingTime(int seconds) throws NotLoggedIn {
        int currentRoundDuration = DBManager.getGameRoundDuration();
        if (!DBManager.updateGameConfigurations(seconds, currentRoundDuration)) {
            throw new RuntimeException("Failed to update waiting time");
        }
    }

    @Override
    public void setGameRoundDuration(int seconds) throws NotLoggedIn {
        int currentWaitingTime = DBManager.getGameWaitingTime();
        if (!DBManager.updateGameConfigurations(currentWaitingTime, seconds)) {
            throw new RuntimeException("Failed to update round duration");
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
