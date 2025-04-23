package server;

import WordWarZ.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.CosNaming.*;

public class Server {

    public static void main(String[] args) {
        try {
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servants and register it with the ORB
            GameServant gameServant = new GameServant();
            gameServant.setORB(orb);

            AdminServant adminServant = new AdminServant();
            adminServant.setORB(orb);

            LoginServant loginServant = new LoginServant();
            loginServant.setORB(orb);

            // get object reference from the servant
            org.omg.CORBA.Object refGame = rootpoa.servant_to_reference(gameServant);
            org.omg.CORBA.Object refAdmin = rootpoa.servant_to_reference(adminServant);
            org.omg.CORBA.Object refLogin = rootpoa.servant_to_reference(loginServant);

            GameService hrefGame = GameServiceHelper.narrow(refGame);
            AdminService hrefAdmin = AdminServiceHelper.narrow(refAdmin);
            Login hrefLogin = LoginHelper.narrow(refLogin);

            // get the root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // bind in naming
            ncRef.rebind(ncRef.to_name("GameService"), hrefGame);
            ncRef.rebind(ncRef.to_name("AdminService"), hrefAdmin);
            ncRef.rebind(ncRef.to_name("LoginService"), hrefLogin);

            System.out.println("WordWarZ Server ready and waiting...");

            // wait for invocations
            orb.run();

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("WordWarZ Server Exiting...");
    }
}

