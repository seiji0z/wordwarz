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


    }


    @Override
    public Player getPlayer(String username) throws NotLoggedIn, PlayerNotFound {
        if (username == null || username.isEmpty()) {
            throw new NotLoggedIn("Username is required");
        }


        try {
            Player player = DBManager.getPlayer(username);
            if (player == null) {
                throw new PlayerNotFound("Player " + username + " not found");
            }
            return player;
        } catch (SQLException e) {
            System.out.println("Database error getting player: " + e.getMessage());
            throw new PlayerNotFound("Database error getting player");
        }
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

