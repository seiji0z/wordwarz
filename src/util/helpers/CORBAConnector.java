package util.helpers;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class CORBAConnector {

    private NamingContextExt ncRef;

    public CORBAConnector(ORB orb) throws Exception {
        try {
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            ncRef = NamingContextExtHelper.narrow(objRef);
        } catch (Exception e) {
            throw new Exception("Could not connect to CORBA Naming Service: " + e.getMessage());
        }
    }

    /**
     * Generic method to resolve a CORBA object and narrow it to the desired type.
     *
     * @param <T>        The desired CORBA interface helper type.
     * @param serviceName The name registered in the NameService.
     * @param helper      A functional interface to narrow the object.
     * @return The narrowed CORBA stub.
     * @throws Exception if the lookup fails.
     */
    public <T> T getService(String serviceName, NarrowFunction<T> helper) throws Exception {
        try {
            org.omg.CORBA.Object ref = ncRef.resolve_str(serviceName);
            return helper.narrow(ref);
        } catch (Exception e) {
            throw new Exception("Could not resolve service '" + serviceName + "': " + e.getMessage());
        }
    }

    @FunctionalInterface
    public interface NarrowFunction<T> {
        T narrow(org.omg.CORBA.Object obj);
    }
}