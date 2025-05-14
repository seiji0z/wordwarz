package client.player.model;

import WordWarZ.*;
import util.helpers.CORBAConnector;
import org.omg.CORBA.ORB;

public class MainMenuModel {
    private final String playerToken;
    private GameService gameService;
    private Login loginService;
    private boolean isMuted = false;

    public MainMenuModel(String token, ORB orb) {
        this.playerToken = token;
        initializeCORBAConnection(orb);
    }

    private void initializeCORBAConnection(ORB orb) {
        try {
            CORBAConnector connector = new CORBAConnector(orb);
            gameService = connector.getService("GameService", GameServiceHelper::narrow);
            loginService = connector.getService("Login", LoginHelper::narrow);
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

    public void logout() {
        try {
            loginService.logout(playerToken);
        } catch (Exception e) {
            System.out.println("Error logging out: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isSoundMuted() {
        return isMuted;
    }

    public void setSoundMuted(boolean muted) {
        this.isMuted = muted;
    }

    public GameService getGameService() {
        return gameService;
    }
}