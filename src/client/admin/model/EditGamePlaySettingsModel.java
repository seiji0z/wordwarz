package client.admin.model;

import WordWarZ.AdminService;
import WordWarZ.AdminServiceHelper;
import WordWarZ.NotLoggedIn;
import org.omg.CORBA.ORB;
import util.helpers.CORBAConnector;

public class EditGamePlaySettingsModel {
    private AdminService adminService;

    public EditGamePlaySettingsModel(ORB orb) {
        initializeCORBAConnection(orb);
    }

    private void initializeCORBAConnection(ORB orb) {
        try {
            CORBAConnector connector = new CORBAConnector(orb);
            adminService = connector.getService("AdminService", AdminServiceHelper::narrow);
        } catch (Exception e) {
            System.out.println("Could not connect to AdminService: " + e.getMessage());
        }
    }

    public boolean updateGameWaitingTime(int seconds) {
        try {
            adminService.setGameWaitingTime(seconds);
            return true;
        } catch (NotLoggedIn e) {
            System.out.println("Admin not logged in: " + e);
            return false;
        } catch (Exception e) {
            System.out.println("Error updating waiting time: " + e.getMessage());
            return false;
        }
    }

    public boolean updateGameRoundDuration(int seconds) {
        try {
            adminService.setGameRoundDuration(seconds);
            return true;
        } catch (NotLoggedIn e) {
            System.out.println("Admin not logged in: " + e);
            return false;
        } catch (Exception e) {
            System.out.println("Error updating round duration: " + e.getMessage());
            return false;
        }
    }

    public long getCurrentWaitingTime() throws NotLoggedIn {
        return adminService.getGameWaitingTime();
    }

    public long getCurrentRoundDuration() throws NotLoggedIn {
        return adminService.getGameRoundDuration();
    }
}