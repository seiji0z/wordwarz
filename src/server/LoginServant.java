package server;

import WordWarZ.*;
import org.omg.CORBA.ORB;

public class LoginServant extends LoginPOA {
    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    @Override
    public String login(String username, String password, boolean isAdmin) throws InvalidCredentials, AlreadyLoggedIn {
        return "";
    }

    @Override
    public void logout(String token) throws NotLoggedIn {

    }
}
