package client.player.model;

import WordWarZ.*;
import org.omg.CORBA.ORB;
import util.helpers.CORBAConnector;

public class GameModel {
    private final String playerToken;
    private GameService gameService;

    public GameModel(String token, ORB orb) {
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
}
