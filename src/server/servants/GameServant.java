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
    public void startGame(String token) throws NotLoggedIn, NoOpponentFound {
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
    public int getRoundDuration(String token) throws NotLoggedIn {
        if (!SessionManager.isTokenValid(token)) {
            throw new NotLoggedIn();
        }
        return GameConfig.getRoundDuration();
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

    public static void notifyNoOpponent(String username) {
        System.out.println("Notifying no opponent for " + username);
        String token = SessionManager.getTokenByUsername(username);
        if (token != null) {
            WordWarZ.ClientCallback callback = clientCallbacks.get(token);
            if (callback != null) {
                try {
                    callback.onQueueUpdated(0);
                    System.out.println("Notified token: " + token + " of no opponent (queue size set to 0)");
                } catch (Exception e) {
                    System.err.println("Error notifying no opponent for token " + token + ": " + e.getMessage());
                }
            }
        }
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
    public synchronized int startRound(String token)
            throws NotLoggedIn, NotInGame, GameNotFound {
        if (!SessionManager.isTokenValid(token)) throw new NotLoggedIn();

        String username = SessionManager.getSession(token).getUsername();
        System.out.println("Starting round for: " + username);

        Game session = activeGames.get(username);
        if (session == null || !session.getPlayers().contains(username)) {
            throw new GameNotFound();
        }

        synchronized (session) {
            if (session.getCurrentWord() == null) {
                String word = session.nextWord();
                System.out.println("New word selected: " + word);
            }

            // Reset player guesses for all players
            for (String player : session.getPlayers()) {
                session.resetPlayerGuesses(player);
            }

            char[] placeholder = session.getGuessedWord();
            for (String player : session.getPlayers()) {
                String playerToken = SessionManager.getTokenByUsername(player);
                WordWarZ.ClientCallback callback = clientCallbacks.get(playerToken);
                if (callback != null) {
                    try {
                        callback.onRoundStarted(placeholder);
                    } catch (Exception e) {
                        System.err.println("Failed to notify player " + player + ": " + e.getMessage());
                    }
                }
            }

            return session.getCurrentWord().length();
        }
    }

    @Override
    public char[] guessLetter(String token, char letter)
            throws NotLoggedIn, NotInGame, GameNotFound, CharacterAlreadyGuessed {

        if (!SessionManager.isTokenValid(token)) throw new NotLoggedIn();
        String username = SessionManager.getSession(token).getUsername();

        Game session = activeGames.get(username);
        if (session == null || !session.getPlayers().contains(username)) throw new GameNotFound();

        // Process the guess and get updated word state
        char[] wordState = session.processGuess(username, letter);

        // Check if the player has won immediately after their guess
        if (session.hasWon(username)) {
            // Handle round win scenario
            try {
                handleRoundWin(session, username);
            } catch (GameNotFinished e) {
                throw new RuntimeException(e);
            }
        }

        return wordState;
    }

    private void handleRoundWin(Game session, String winner) throws GameNotFound, NotLoggedIn, NotInGame, GameNotFinished {
        // Notify all players about the result
        String word = session.getCurrentWord();

        for (String player : session.getPlayers()) {
            String playerToken = SessionManager.getTokenByUsername(player);
            WordWarZ.ClientCallback callback = clientCallbacks.get(playerToken);
            if (callback != null) {
                try {
                    if (player.equals(winner)) {
                        callback.onRoundWon(word);
                    } else {
                        callback.onRoundLost(word, winner);
                    }
                } catch (Exception e) {
                    System.err.println("Error notifying player " + player + ": " + e.getMessage());
                }
            }
        }

        // Increment the winner's score
        session.incrementScore(winner);

        // Check if the game should end
        if (session.getScores().get(winner) >= 3) {
            endGame(SessionManager.getTokenByUsername(winner));
        } else {
            // Reset game state for next round (but don't generate new word yet)
            session.resetForNewRound();

            // Start the next round after a delay
            scheduler.schedule(() -> {
                try {
                    String firstPlayerToken = SessionManager.getTokenByUsername(session.getPlayers().get(0));
                    startRound(firstPlayerToken);
                } catch (Exception e) {
                    System.err.println("Error starting next round: " + e.getMessage());
                }
            }, 3, TimeUnit.SECONDS);
        }
    }

    @Override
    public synchronized void endRound(String token) throws NotLoggedIn, NotInGame, GameNotFound {
        if (!SessionManager.isTokenValid(token)) throw new NotLoggedIn();
        String username = SessionManager.getSession(token).getUsername();

        Game game = activeGames.get(username);
        if (game == null || !game.getPlayers().contains(username)) throw new GameNotFound();

        // Get current word or empty string if null
        String word = game.getCurrentWord();
        String wordToSend = word != null ? word : "";  // Never send null

        boolean wordGuessed = false;

        // Only check for winners if there's a current word
        if (word != null) {
            // Check if any player guessed the word
            for (String player : game.getPlayers()) {
                if (game.hasWon(player)) {
                    wordGuessed = true;
                    break;
                }
            }
        }

        // Notify players based on outcome
        for (String player : game.getPlayers()) {
            String playerToken = SessionManager.getTokenByUsername(player);
            WordWarZ.ClientCallback cb = clientCallbacks.get(playerToken);
            if (cb != null) {
                try {
                    if (wordGuessed) {
                        if (game.hasWon(player)) {
                            cb.onRoundWon(wordToSend);
                        } else {
                            cb.onRoundLost(wordToSend, getWinnerUsername(game));
                        }
                    } else {
                        cb.onRoundDrawn(wordToSend);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to notify player " + player + ": " + e.getMessage());
                }
            }
        }

        if (game.getScores().values().stream().anyMatch(score -> score >= 3)) {
            try {
                endGame(token);
            } catch (GameNotFinished e) {
                throw new RuntimeException(e);
            }
        } else {
            // Reset game state for next round
            game.resetForNewRound();

            // Delay the next round preparation
            scheduler.schedule(() -> {
                try {
                    String firstPlayerToken = SessionManager.getTokenByUsername(game.getPlayers().get(0));
                    startRound(firstPlayerToken);
                } catch (Exception e) {
                    System.err.println("Error starting next round: " + e.getMessage());
                }
            }, 3, TimeUnit.SECONDS);
        }
    }

    // In GameServant.java
    @Override
    public void notifyPlayerLost(String token) throws NotLoggedIn, NotInGame, GameNotFound {
        if (!SessionManager.isTokenValid(token)) throw new NotLoggedIn();
        String username = SessionManager.getSession(token).getUsername();

        Game game = activeGames.get(username);
        if (game == null || !game.getPlayers().contains(username)) throw new GameNotFound();

        // Mark player as eliminated in the game state
        game.markPlayerEliminated(username);

        // Check if all players are eliminated
        if (game.allPlayersEliminated()) {
            endRound(token); // End round if everyone is out
        }
    }

    private String getWinnerUsername(Game game) {
        for (String player : game.getPlayers()) {
            if (game.hasWon(player)) {
                return player;
            }
        }
        return "";
    }

    @Override
    public void endGame(String token) throws NotLoggedIn, NotInGame, GameNotFound, GameNotFinished {
        System.out.println("Game ended. Determining the winner...");

        String username = SessionManager.getSession(token).getUsername();
        Game game = activeGames.get(username);

        if (game == null) throw new GameNotFound();

        String winner = getWinnerUsername(game);
        for (String player : game.getPlayers()) {
            String playerToken = SessionManager.getTokenByUsername(player);
            WordWarZ.ClientCallback callback = clientCallbacks.get(playerToken);
            if (callback != null) {
                try {
                    if (player.equals(winner)) {
                        callback.onGameWon(winner);
                    } else {
                        callback.onGameLost(winner);
                    }
                } catch (Exception e) {
                    System.err.println("Error notifying player " + player + ": " + e.getMessage());
                }
            }
        }

        // Cleanup: remove the game instance
        activeGames.entrySet().removeIf(e -> e.getValue() == game);
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
            System.out.println(e.getMessage());
        }
    }
}