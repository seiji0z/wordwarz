package server;

import WordWarZ.*;
import org.omg.CORBA.ORB;

public class AdminServant extends AdminServicePOA {

    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    @Override
    public void createPlayer(String username, String password) throws NotLoggedIn, UsernameAlreadyExists {

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
