package client.login.model;

import WordWarZ.Login;
import WordWarZ.LoginHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class LoginModel {
    private Login loginStub;

    public LoginModel(ORB orb) {
        try {
            // Resolve the NameService reference
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

            // Narrow the object reference to NamingContextExt
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // Resolve the "Login" object from the NamingContext
            org.omg.CORBA.Object loginRef = ncRef.resolve_str("Login");

            // Narrow the Login reference to the correct type (Login)
            loginStub = LoginHelper.narrow(loginRef);
        } catch (Exception e) {
            System.out.println("Could not connect to Login service: " + e.getMessage());
        }
    }

    public String login(String username, String password) throws Exception {
        return loginStub.login(username, password);
    }

    public void logout(String token) throws Exception {
        loginStub.logout(token);
    }
}