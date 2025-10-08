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
    public String authenticate(String username, String password) throws InvalidCredentials {
        if (!DBManager.userExists(username)) {
            throw new InvalidCredentials("User not found. Please try again.");
        }

        if (!DBManager.validateCredentials(username, password)) {
            throw new InvalidCredentials("Wrong password. Please try again.");
        }

        boolean isAdmin = DBManager.isAdmin(username);

        // If user is already logged in, remove old session
        if (SessionManager.isUserLoggedIn(username)) {
            String oldToken = SessionManager.getTokenByUsername(username);
            SessionManager.removeSession(oldToken);
            System.out.println("User " + username + " was already logged in. Old session removed.");
        }

        // Create session and generate new token
        String token = SessionManager.createSession(username, isAdmin);
        System.out.println("User " + username + " logged in with new session.");

        return token + ":" + isAdmin;
    }


    @Override
    public void logout(String token) throws NotLoggedIn {
        if (!SessionManager.isTokenValid(token)) {
            throw new NotLoggedIn();
        }
        System.out.println("User " + SessionManager.getSession(token).getUsername() + " logged out.");
        SessionManager.removeSessionWithoutCleanup(token);
    }
}
