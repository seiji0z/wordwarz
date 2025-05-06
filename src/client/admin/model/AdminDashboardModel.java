package client.admin.model;

import WordWarZ.*;
import org.omg.CORBA.ORB;
import server.database.DBManager;
import util.helpers.CORBAConnector;

import java.sql.SQLException;
import java.util.List;

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

    public void deletePlayer(String username) throws NotLoggedIn, PlayerNotFound, PlayerCurrentlyLoggedIn {
        if (username == null || username.isEmpty()) {
            System.out.println("Client: Invalid username provided: " + username);
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        System.out.println("Client: Deleting player: " + username);
        try {
            adminService.deletePlayer(username);
            System.out.println("DeletePlayer call successful for: " + username);
        } catch (Exception e) {
            System.out.println("Error in deletePlayer: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Player getPlayer(String username) throws NotLoggedIn, PlayerNotFound {
        Player player = adminService.getPlayer(username);
        return player;
    }

    public Player[] getAllPlayers() throws NotLoggedIn, PlayerNotFound {
        Player[] players = adminService.searchPlayers("");
        return players;
    }

    public Player[] searchPlayers(String username) throws NotLoggedIn, PlayerNotFound {
        Player[] players = adminService.searchPlayers(username);
        return players;
    }
}