package server.servants;

import WordWarZ.*;
import org.omg.CORBA.ORB;
import server.helpers.QueueManager;
import server.helpers.SessionManager;
import server.objects.Game;
import server.objects.GameConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GameServant extends GameServicePOA {
    private static final ConcurrentHashMap<String, WordWarZ.ClientCallback> clientCallbacks = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> waitingPlayers = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Map<String, Game> activeGames = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService roundScheduler = Executors.newScheduledThreadPool(4);
    private static GameServant instance;

    private ORB orb;

    public GameServant() {
        instance = this;
    }

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
        System.out.println("Callback unregistered for token: " + token);
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

    public static void createAndStartGame(List<String> usernames) {
        // Wait until countdown is actually complete
        if (QueueManager.getRemainingTime() > 0) {
            return;
        }

        Game newGame = new Game(usernames);

        // Register game for all players
        for (String username : usernames) {
            String token = SessionManager.getTokenByUsername(username);
            registerActiveGame(token, newGame);
        }

        // Start round only once (for the first player)
        if (!usernames.isEmpty()) {
            String firstPlayerToken = SessionManager.getTokenByUsername(usernames.get(0));
            try {
                instance.startRound(firstPlayerToken);
            } catch (Exception e) {
                System.err.println("Error starting round: " + e.getMessage());
            }
        }
    }

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

    public static void notifyQueueUpdate(int playerCount) {
        System.out.println("Notifying " + clientCallbacks.size() + " clients of queue update: " + playerCount + " players");
        clientCallbacks.entrySet().removeIf(entry -> {
            String token = entry.getKey();
            try {
                if (!SessionManager.isTokenValid(token)) {
                    System.out.println("Removing invalid callback for token: " + token + " (session expired)");
                    return true;
                }
                WordWarZ.ClientCallback callback = entry.getValue();
                callback.onQueueUpdated(playerCount);
                System.out.println("Notified token: " + token + " with player count: " + playerCount);
                return false;
            } catch (Exception e) {
                System.err.println("Error notifying queue update for token " + token + ": " + e.getMessage());
                return true;
            }
        });
    }

    public void notifyCountdownUpdate(int secondsLeft) {
        System.out.println("Notifying " + clientCallbacks.size() + " clients of countdown update: " + secondsLeft + " seconds");

        clientCallbacks.entrySet().removeIf(entry -> {
            String token = entry.getKey();
            try {
                if (!SessionManager.isTokenValid(token)) {
                    System.out.println("Removing invalid callback for token: " + token + " (session expired)");
                    return true;
                }
                WordWarZ.ClientCallback callback = entry.getValue();
                callback.onGameCountdown(secondsLeft);
                System.out.println("Notified token: " + token + " with countdown: " + secondsLeft);
                return false;
            } catch (Exception e) {
                System.err.println("Error notifying countdown for token " + token + ": " + e.getMessage());
                return true;
            }
        });

    }

    public static void clearCallbacksExcept(List<String> currentUsernames) {
        clientCallbacks.entrySet().removeIf(entry -> {
            String token = entry.getKey();
            try {
                String username = SessionManager.getSession(token).getUsername();
                if (!currentUsernames.contains(username)) {
                    System.out.println("Removing stale callback for token: " + token + ", username: " + username);
                    return true;
                }
                return false;
            } catch (NotLoggedIn e) {
                System.out.println("Removing invalid callback for token: " + token + " (session expired)");
                return true;
            }
        });
    }

    @Override
    public Player[] getLeaderboard(String token) throws NotLoggedIn {
        return new Player[0];
    }

    @Override
    public synchronized int startRound(String token) throws NotLoggedIn, NotInGame, GameNotFound {
        if (!SessionManager.isTokenValid(token)) throw new NotLoggedIn();
        String username = SessionManager.getSession(token).getUsername();

        Game session = activeGames.get(username);
        if (session == null || !session.getPlayers().contains(username)) throw new GameNotFound();

        // Only generate new word if this is the first player starting the round
        if (session.getCurrentWord() == null) {
            String word = session.nextWord();
            System.out.println("NEW WORD: " + word);
            String[] placeholder = new String[word.length()];
            Arrays.fill(placeholder, "_");
            System.out.println("PLACEHOLDER: " + Arrays.toString(placeholder));

            // Notify all players
            for (String player : session.getPlayers()) {
                String playerToken = SessionManager.getTokenByUsername(player);
                WordWarZ.ClientCallback cb = clientCallbacks.get(playerToken);
                if (cb != null) {
                    try {
                        cb.onRoundStarted(placeholder);
                    } catch (Exception e) {
                        System.err.println("Failed to notify player " + player + ": " + e.getMessage());
                    }
                }
            }
        }

        return session.getCurrentWord().length();
    }

    @Override
    public char[] guessLetter(String token, char letter) throws NotLoggedIn, NotInGame, GameNotFound, CharacterAlreadyGuessed{
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

    public static void registerActiveGame(String playerToken, Game game) {
        try {
            activeGames.put(SessionManager.getSession(playerToken).getUsername(), game);
            System.out.println("Game registered for player: " + SessionManager.getSession(playerToken).getUsername());
        } catch (NotLoggedIn e) {
            System.out.println(e.getMessage());;
        }
    }

    public static Game getActiveGame(String username) {
        return activeGames.get(username);
    }
}