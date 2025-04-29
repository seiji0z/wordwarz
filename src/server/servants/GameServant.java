package server.servants;

import WordWarZ.*;
import org.omg.CORBA.ORB;
import server.helpers.QueueManager;
import server.helpers.SessionManager;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class GameServant extends GameServicePOA {
    private static final ConcurrentHashMap<String, WordWarZ.ClientCallback> clientCallbacks = new ConcurrentHashMap<>();
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
        clientCallbacks.put(token, callback);
        System.out.println("Callback registered for token: " + token + ", username: " + SessionManager.getSession(token).getUsername());
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
        System.out.println("Starting game for token: " + token + ", username: " + username);
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
        System.out.println("Notifying " + clientCallbacks.size() + " clients of queue update: " + playerCount + " players");
        clientCallbacks.forEach((token, callback) -> {
            try {
                callback.onQueueUpdated(playerCount);
                System.out.println("Notified token: " + token + " with player count: " + playerCount);
            } catch (Exception e) {
                System.err.println("Error notifying queue update for token " + token + ": " + e.getMessage());
                clientCallbacks.remove(token);
            }
        });
    }

    public static void notifyCountdownUpdate(int secondsLeft) {
        System.out.println("Notifying " + clientCallbacks.size() + " clients of countdown update: " + secondsLeft + " seconds");
        clientCallbacks.forEach((token, callback) -> {
            try {
                callback.onGameCountdown(secondsLeft);
                System.out.println("Notified token: " + token + " with countdown: " + secondsLeft);
            } catch (Exception e) {
                System.err.println("Error notifying countdown for token " + token + ": " + e.getMessage());
                clientCallbacks.remove(token);
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
