package client.login.model;

import WordWarZ.Login;
import WordWarZ.LoginHelper;
import server.helpers.SessionManager;
import util.helpers.CORBAConnector;
import org.omg.CORBA.ORB;

public class LoginModel {
    private Login loginStub;

    public LoginModel(ORB orb) {
        try {
            CORBAConnector connector = new CORBAConnector(orb);
            loginStub = connector.getService("Login", LoginHelper::narrow);
        } catch (Exception e) {
            System.out.println("Error connecting to Login service: " + e.getMessage());
        }
    }

    public String login(String username, String password) throws Exception {
        return loginStub.authenticate(username, password);
    }

    public void logout(String token) throws Exception {
        SessionManager.removeSession(token);
        loginStub.logout(token);
    }
}
