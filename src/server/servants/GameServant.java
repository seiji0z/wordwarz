package server.servants;

import WordWarZ.*;
import org.omg.CORBA.ORB;
import server.helpers.QueueManager;
import server.helpers.SessionManager;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class GameServant extends GameServicePOA {
    private static final ConcurrentHashMap<String, ClientCallback> clientCallbacks = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> waitingPlayers = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    @Override
    public void registerCallback(String token, WordWarZ.ClientCallback callback) throws NotLoggedIn {
        if (!SessionManager.isTokenValid(token)) {
            throw new NotLoggedIn();
        }
        clientCallbacks.put(token, (ClientCallback) callback);
        System.out.println("Callback registered for " + SessionManager.getSession(token).getUsername());
    }

    @Override
    public void unregisterCallback(String token) throws NotLoggedIn {
        if (!SessionManager.isTokenValid(token)) {
            throw new NotLoggedIn();
        }
        clientCallbacks.remove(token);
    }

    private void notifyAllCallbacks(Runnable notification) {
        clientCallbacks.forEach((token, callback) -> {
            try {
                notification.run();
            } catch (Exception e) {
                System.err.println("Error notifying callback for token " + token + ": " + e.getMessage());
                clientCallbacks.remove(token);
            }
        });
    }

    @Override
    public void startGame(String token) throws NotLoggedIn {
        if (!SessionManager.isTokenValid(token)) {
            throw new NotLoggedIn();
        }

        String username = SessionManager.getSession(token).getUsername();
        QueueManager.joinQueue(username);
    }

    @Override
    public Player[] getLeaderboard(String token) throws NotLoggedIn {
        return new Player[0];
    }

    // Add these methods to the GameServant class
    @Override
    public void cancelQueue(String token) throws NotLoggedIn, PlayerNotInQueue {
        if (!SessionManager.isTokenValid(token)) {
            throw new NotLoggedIn();
        }

        String username = SessionManager.getSession(token).getUsername();
        if (!QueueManager.isInQueue(username)) {
            throw new PlayerNotInQueue();
        }

        QueueManager.leaveQueue(username);
    }

    @Override
    public int getPlayersInQueue(String token) throws NotLoggedIn, PlayerNotInQueue {
        if (!SessionManager.isTokenValid(token)) {
            throw new NotLoggedIn();
        }

        String username = SessionManager.getSession(token).getUsername();
        if (!QueueManager.isInQueue(username)) {
            throw new PlayerNotInQueue();
        }

        return QueueManager.getQueueSize();
    }

    @Override
    public int getTimeUntilGameStart(String token) throws NotLoggedIn, PlayerNotInQueue {
        if (!SessionManager.isTokenValid(token)) {
            throw new NotLoggedIn();
        }

        String username = SessionManager.getSession(token).getUsername();
        if (!QueueManager.isInQueue(username)) {
            throw new PlayerNotInQueue();
        }

        return QueueManager.getRemainingTime();
    }

    // Add these helper methods to GameServant
    public static void notifyQueueUpdate(int playerCount) {
        clientCallbacks.forEach((token, callback) -> {
            try {
                callback.onQueueUpdated(playerCount);
            } catch (Exception e) {
                System.err.println("Error notifying queue update: " + e.getMessage());
            }
        });
    }

    public static void notifyCountdownUpdate(int secondsLeft) {
        clientCallbacks.forEach((token, callback) -> {
            try {
                callback.onGameCountdown(secondsLeft);
            } catch (Exception e) {
                System.err.println("Error notifying countdown: " + e.getMessage());
            }
        });
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

}
