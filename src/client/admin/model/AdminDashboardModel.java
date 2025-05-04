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


    public Player[] getAllPlayers() throws NotLoggedIn, PlayerNotFound {
        try {
            List<Player> players = DBManager.getAllPlayers();
            if (players.isEmpty()) {
                throw new PlayerNotFound("No players found in database");
            }
            return players.toArray(new Player[0]);
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            throw new PlayerNotFound("Error accessing player database");
        }
    }


    public Player getPlayer(String username) throws NotLoggedIn, PlayerNotFound {
        return adminService.getPlayer(username);
    }


}
