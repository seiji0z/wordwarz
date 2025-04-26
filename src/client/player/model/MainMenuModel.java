package client.player.model;

import WordWarZ.GameService;
import WordWarZ.GameServiceHelper;
import WordWarZ.Player;
import util.helpers.CORBAConnector;
import org.omg.CORBA.ORB;

public class MainMenuModel {
    private final String playerToken;
    private GameService gameService;

    public MainMenuModel(String token, ORB orb) {
        this.playerToken = token;
        initializeCORBAConnection(orb);
    }

    private void initializeCORBAConnection(ORB orb) {
        try {
            CORBAConnector connector = new CORBAConnector(orb);
            gameService = connector.getService("GameService", GameServiceHelper::narrow);
        } catch (Exception e) {
            System.out.println("Could not connect to GameService: " + e.getMessage());
        }
    }

    public void startGame() {
        try {
            gameService.startGame(playerToken);
        } catch (Exception e) {
            System.out.println("Error starting game: " + e.getMessage());
        }
    }

    public Player[] getLeaderboard() {
        try {
            return gameService.getLeaderboard(playerToken);
        } catch (Exception e) {
            System.out.println("Error fetching leaderboard: " + e.getMessage());
        }
        return null;
    }
}
