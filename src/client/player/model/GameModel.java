package client.player.model;

import WordWarZ.*;
import org.omg.CORBA.ORB;
import util.helpers.CORBAConnector;

public class GameModel {
    private GameService gameService;
    private final String token;

    public GameModel(String token, ORB orb){;
        this.token = token;
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

    public int startRound() throws GameNotFound, NotLoggedIn, NotInGame {
        System.out.println("[MODEL] Starting round");
        return gameService.startRound(token);
    }

    public void endRound() throws  NotLoggedIn, NotInGame, GameNotFound {
        gameService.endRound(token);
    }

    // In GameModel.java
    public void notifyPlayerLost() throws NotLoggedIn, NotInGame, GameNotFound {
        gameService.notifyPlayerLost(token);
    }

    public int getRoundDuration() throws NotLoggedIn, GameNotFound, NotInGame {
        return gameService.getRoundDuration(token);
    }

    public char[] guessLetter(char letter) throws GameNotFound, NotLoggedIn, NotInGame, CharacterAlreadyGuessed {
        return gameService.guessLetter(token, letter);
    }

    public int displayWins() throws GameNotFound, RoundNotFinished, NotLoggedIn, NotInGame {
        return gameService.displayWins(token);
    }
}