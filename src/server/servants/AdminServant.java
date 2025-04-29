package server.servants;

import WordWarZ.*;
import org.omg.CORBA.ORB;
import server.database.DBManager;

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
    public void updatePlayer(String username, String newPassword) throws NotLoggedIn, PlayerNotFound, PlayerCurrentlyLoggedIn {

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

    }

    @Override
    public void setGameRoundDuration(int seconds) throws NotLoggedIn {

    }

    @Override
    public int getGameWaitingTime() throws NotLoggedIn {
        return 0;
    }

    @Override
    public int getGameRoundDuration() throws NotLoggedIn {
        return 0;
    }
}
