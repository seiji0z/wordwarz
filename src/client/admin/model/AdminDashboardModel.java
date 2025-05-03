package client.admin.model;

import WordWarZ.*;
import org.omg.CORBA.ORB;
import util.helpers.CORBAConnector;

public class AdminDashboardModel {
    private AdminService adminService;
    private final String token;

    public AdminDashboardModel(String token, ORB orb) {
        this.token = token;
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

    public void createPlayer(String username, String password)
            throws NotLoggedIn, UsernameAlreadyExists {
        adminService.createPlayer(username, password);
    }

    public void updatePlayer(String username, String newUsername, String newPassword)
            throws NotLoggedIn, PlayerNotFound, PlayerCurrentlyLoggedIn {
        adminService.updatePlayer(username, newUsername, newPassword);
    }

//    public void deletePlayer(String username)
//            throws NotLoggedIn, PlayerNotFound, PlayerCurrentlyLoggedIn {
//        adminService.deletePlayer(token, username);
//    }

//    public Player getPlayer(String username)
//            throws NotLoggedIn, PlayerNotFound {
//        return adminService.getPlayer(token, username);
//    }
//
//    public Player[] searchPlayers(String query)
//            throws NotLoggedIn, PlayerNotFound {
//        Leaderboard leaderboard = adminService.searchPlayers(token, query);
//        return leaderboard.players;
//    }
}