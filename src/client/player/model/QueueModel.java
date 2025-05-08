package client.player.model;

import WordWarZ.GameService;
import WordWarZ.GameServiceHelper;
import WordWarZ.NoOpponentFound;
import WordWarZ.NotLoggedIn;
import org.omg.CORBA.ORB;
import util.helpers.CORBAConnector;

public class QueueModel {
    private final String playerToken;
    private GameService gameService;

    public QueueModel(String token, ORB orb) {
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

    public void startGame() throws NotLoggedIn, NoOpponentFound {
        try {
            gameService.startGame(playerToken);
        } catch (NotLoggedIn e) {
            System.out.println("Error starting game: Not logged in");
            throw e;
        } catch (NoOpponentFound e) {
            System.out.println("Error starting game: No opponent found");
            throw e;
        } catch (Exception e) {
            System.out.println("Unexpected error starting game: " + e.getMessage());
        }
    }

    public GameService getGameService() {
        return gameService;
    }
}