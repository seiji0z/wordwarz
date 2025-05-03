package client.player.model;

import WordWarZ.*;
import server.servants.GameServant;

import java.util.Arrays;

public class GameModel {

    private final GameService gameService;
    private final String token;


    public GameModel(GameService gameService, String token){
        this.gameService = gameService;
        this.token = token;
    }


    public void startGame() throws NotLoggedIn, NoOpponentFound {
        gameService.startGame(token);
    }

    public Player[] getLeaderboard() throws NotLoggedIn {
        return gameService.getLeaderboard(token);
    }


    public void cancelQueue() throws NotLoggedIn, PlayerNotInQueue {
        gameService.cancelQueue(token);
    }


    public int getPlayersInQueue() throws NotLoggedIn, PlayerNotInQueue {
        return gameService.getPlayersInQueue(token);
    }


    public int getRemainingGuesses() throws NotLoggedIn, NotInGame {
        return gameService.getRemainingGuesses(token);
    }


    public int startRound() throws GameNotFound, NotLoggedIn, NotInGame {
        int wordLength = gameService.startRound(token);
        // Initialize with all underscores
        char[] initialState = new char[wordLength];
        Arrays.fill(initialState, '_');
        return wordLength;
    }


    public char[] guessLetter(char letter) throws GameNotFound, NotLoggedIn, NotInGame, CharacterAlreadyGuessed {
        return gameService.guessLetter(token, letter);
    }


    public void registerCallback(ClientCallback callback) throws NotLoggedIn {
        gameService.registerCallback(token, callback);
    }


    public void displayWinnerByRound() throws GameNotFound, RoundNotFinished, NotLoggedIn, NotInGame {
        gameService.displayWinnerByRound(token);
    }

    public void displayWinnerByGame() throws GameNotFound, RoundNotFinished, NotLoggedIn, NotInGame {
        gameService.displayWinnerByGame(token);
    }

    public void endGame() throws GameNotFound, NotLoggedIn, NotInGame, GameNotFinished {
        gameService.endGame(token);
    }

    public String displayLoserByTime() throws GameNotFound, RoundNotFinished, NotLoggedIn, NotInGame {
        return gameService.displayLoserByTime(token);
    }

    public String displayWins() throws GameNotFound, RoundNotFinished, NotLoggedIn, NotInGame {
        return gameService.displayWins(token);
    }


}
