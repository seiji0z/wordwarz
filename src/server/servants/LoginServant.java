package server.servants;

import WordWarZ.*;
import org.omg.CORBA.ORB;
import server.database.DBConnection;
import server.database.DBManager;
import server.helpers.SessionManager;

public class LoginServant extends LoginPOA {
    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
        DBConnection.setCon();
    }

    @Override
    public String login(String username, String password) throws InvalidCredentials, AlreadyLoggedIn {
        if (SessionManager.isUserLoggedIn(username)) {
            throw new AlreadyLoggedIn();
        }

        if (!DBManager.validateCredentials(username, password)) {
            throw new InvalidCredentials();
        }

        boolean isAdmin = DBManager.isAdmin(username);

        // Create session and generate token
        String token = SessionManager.createSession(username, isAdmin);
        return token + ":" + isAdmin;
    }


    @Override
    public void logout(String token) throws NotLoggedIn {
        if (!SessionManager.isTokenValid(token)) {
            throw new NotLoggedIn();
        }
        SessionManager.removeSession(token);
    }
}
