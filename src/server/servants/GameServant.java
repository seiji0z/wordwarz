package server.servants;

import WordWarZ.*;
import org.omg.CORBA.ORB;

public class GameServant extends GameServicePOA {

    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    @Override
    public void startGame(String token) throws NotLoggedIn, NoOpponentFound {

    }

    @Override
    public Player[] getLeaderboard(String token) throws NotLoggedIn {
        return new Player[0];
    }

    @Override
    public void cancelQueue(String token) throws NotLoggedIn, PlayerNotInQueue {

    }

    @Override
    public int getPlayersInQueue(String token) throws NotLoggedIn, PlayerNotInQueue {
        return 0;
    }

    @Override
    public int getTimeUntilGameStart(String token) throws NotLoggedIn, PlayerNotInQueue {
        return 0;
    }

    @Override
    public int startRound(String token) throws NotLoggedIn, NotInGame, GameNotFound {
        return 0;
    }

    @Override
    public char[] guessLetter(String token, char letter) throws NotLoggedIn, NotInGame, GameNotFound {
        return new char[0];
    }

    @Override
    public void displayWinnerByRound(String token) throws NotLoggedIn, NotInGame, GameNotFound, RoundNotFinished {

    }

    @Override
    public void displayWinnerByGame(String token) throws NotLoggedIn, NotInGame, GameNotFound, RoundNotFinished {

    }

    @Override
    public void endGame(String token) throws NotLoggedIn, NotInGame, GameNotFound, GameNotFinished {

    }

    @Override
    public String displayLoserByTime(String token) throws NotLoggedIn, NotInGame, GameNotFound, RoundNotFinished {
        return "";
    }

    @Override
    public String displayLoserByGuess(String token) throws NotLoggedIn, NotInGame, GameNotFound, RoundNotFinished {
        return "";
    }

    @Override
    public String displayWins(String token) throws NotLoggedIn, NotInGame, GameNotFound, RoundNotFinished {
        return "";
    }

    @Override
    public int getRemainingGuesses(String token) throws NotLoggedIn, NotInGame {
        return 0;
    }

    @Override
    public int getRemainingTime(String token) throws NotLoggedIn, NotInGame {
        return 0;
    }

    @Override
    public void registerCallback(String token, ClientCallback callback) throws NotLoggedIn {

    }

    @Override
    public void unregisterCallback(String token) throws NotLoggedIn {

    }
}
