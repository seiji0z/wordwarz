package client.player.model;

import WordWarZ.*;
import server.GameServant;

public class GameModel {

    private final GameServant gameServant;
    private final String token;


    public GameModel(GameServant gameServant, String token){
        this.gameServant = gameServant;
        this.token = token;
    }


    public void startGame() throws NotLoggedIn, NoOpponentFound {
        gameServant.startGame(token);
    }

    public Player[] getLeaderboard() throws NotLoggedIn {
        return gameServant.getLeaderboard(token);
    }


    public void cancelQueue() throws NotLoggedIn, PlayerNotInQueue {
        gameServant.cancelQueue(token);
    }


    public int getPlayersInQueue() throws NotLoggedIn, PlayerNotInQueue {
        return gameServant.getPlayersInQueue(token);
    }


    public int getRemainingGuesses() throws NotLoggedIn, NotInGame {
        return gameServant.getRemainingGuesses(token);
    }


    public int startRound() throws GameNotFound, NotLoggedIn, NotInGame {
        return gameServant.startRound(token);
    }


    public char[] guessLetter(char letter) throws GameNotFound, NotLoggedIn, NotInGame {
        return gameServant.guessLetter(token, letter);
    }


    public void registerCallback(ClientCallback callback) throws NotLoggedIn {
        gameServant.registerCallback(token, callback);
    }


    public void displayWinnerByRound() throws GameNotFound, RoundNotFinished, NotLoggedIn, NotInGame {
        gameServant.displayWinnerByRound(token);
    }

    public void displayWinnerByGame() throws GameNotFound, RoundNotFinished, NotLoggedIn, NotInGame {
        gameServant.displayWinnerByGame(token);
    }

    public void endGame() throws GameNotFound, NotLoggedIn, NotInGame, GameNotFinished {
        gameServant.endGame(token);
    }

    public String displayLoserByTime() throws GameNotFound, RoundNotFinished, NotLoggedIn, NotInGame {
        return gameServant.displayLoserByTime(token);
    }

    public String displayWins() throws GameNotFound, RoundNotFinished, NotLoggedIn, NotInGame {
        return gameServant.displayWins(token);
    }
}
