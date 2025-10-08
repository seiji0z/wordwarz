package server.servants;

import WordWarZ.*;
import org.omg.CORBA.ORB;
import server.database.DBManager;
import server.helpers.QueueManager;
import server.helpers.SessionManager;
import server.objects.Game;
import server.objects.GameConfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameServant extends GameServicePOA {
    public static final ConcurrentHashMap<String, WordWarZ.ClientCallback> clientCallbacks = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Map<String, Game> activeGames = new ConcurrentHashMap<>();
    private static final Map<Game, ScheduledFuture<?>> connectivityChecks = new ConcurrentHashMap<>();
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
        if (clientCallbacks.containsKey(token)) {
            unregisterCallback(token); // Cleanup existing callback
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
        activeGames.remove(username);
        System.out.println("Starting game for token: " + token + ", username: " + username);
        QueueManager.joinQueue(username);
    }

    public static void createAndStartGame(List<String> usernames) {
        if (QueueManager.getRemainingTime() > 0) {
            return;
        }

        Game newGame = new Game(usernames);

        // Register game for all players
        for (String username : usernames) {
            String token = SessionManager.getTokenByUsername(username);
            registerActiveGame(token, newGame);
        }

        if (!usernames.isEmpty()) {
            String firstPlayerToken = SessionManager.getTokenByUsername(usernames.get(0));
            try {
                if (!instance.isGameEnding(newGame)) {
                    instance.startRound(firstPlayerToken);
                }
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
                    try {
                        String username = SessionManager.getSession(token).getUsername();
                        if (QueueManager.isInQueue(username)) {
                            QueueManager.leaveQueue(username); // Remove from queue
                            System.out.println("Removed " + username + " from queue due to expired session");
                        }
                    } catch (NotLoggedIn ex) {
                        System.err.println("Session already invalid for token " + token);
                    }
                    return true;
                }
                WordWarZ.ClientCallback callback = entry.getValue();
                callback.onGameCountdown(secondsLeft);
                System.out.println("Notified token: " + token + " with countdown: " + secondsLeft);
                return false;
            } catch (Exception e) {
                System.err.println("Error notifying countdown for token " + token + ": " + e.getMessage());
                try {
                    String username = SessionManager.getSession(token).getUsername();
                    if (QueueManager.isInQueue(username)) {
                        QueueManager.leaveQueue(username); // Remove from queue on disconnect
                        System.out.println("Removed " + username + " from queue due to disconnect");
                    }
                } catch (NotLoggedIn ex) {
                    System.err.println("Session invalid for token " + token + " during error handling");
                }
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
                    System.out.println("Notified token: " + token + " of no opponent");
                } catch (Exception e) {
                    System.err.println("Error notifying no opponent for token " + token + ": " + e.getMessage());
                }
            }
        }
    }

    @Override
    public Player[] getLeaderboard(String token) throws NotLoggedIn {
        if (!SessionManager.isTokenValid(token)) {
            throw new NotLoggedIn();
        }

        List<Player> topPlayers = DBManager.getTopPlayers(5);
        return topPlayers.toArray(new Player[0]);
    }

    @Override
    public synchronized int startRound(String token) throws NotLoggedIn, NotInGame, GameNotFound {
        if (!SessionManager.isTokenValid(token)) {
            throw new NotLoggedIn();
        }

        String username = SessionManager.getSession(token).getUsername();
        System.out.println("startRound called for token: " + token + ", username: " + username);

        Game session = activeGames.get(username);
        if (session == null || !session.getPlayers().contains(username)) {
            throw new GameNotFound("No active game found for user " + username);
        }

        if (isGameEnding(session)) {
            throw new GameNotFound("Game is ending for user " + username);
        }

        // Prevent multiple round starts for the same game
        if (connectivityChecks.containsKey(session)) {
            System.out.println("Round already started for game with players: " + session.getPlayers() + ", skipping");
            return session.getCurrentWord().length();
        }

        System.out.println("Starting round for game with players: " + session.getPlayers() + ", eliminated: " + session.getEliminatedPlayers());

        session.setRoundActive(true);

        synchronized (session) {
            if (session.getCurrentWord() == null) {
                String word = session.nextWord();
                System.out.println("New word selected: " + word);
            }

            for (String player : session.getPlayers()) {
                session.resetPlayerGuesses(player);
            }

            char[] placeholder = session.getGuessedWord();
            for (String player : session.getPlayers()) {
                if (session.getEliminatedPlayers().contains(player)) {
                    System.out.println("Skipping round start notification for eliminated player: " + player);
                    continue;
                }
                String playerToken = SessionManager.getTokenByUsername(player);
                if (playerToken == null || !SessionManager.isTokenValid(playerToken)) {
                    System.out.println("Invalid or missing token for player: " + player + ", token: " + (playerToken != null ? playerToken : "null"));
                    handlePlayerDisconnect(player, session);
                    continue;
                }
                WordWarZ.ClientCallback callback = clientCallbacks.get(playerToken);
                if (callback == null) {
                    System.out.println("No callback found for player: " + player + " (token: " + playerToken + ")");
                    handlePlayerDisconnect(player, session);
                    continue;
                }
                try {
                    callback.onRoundStarted(placeholder);
                } catch (Exception e) {
                    System.err.println("Failed to notify player " + player + " of round start: " + e.getMessage());
                    e.printStackTrace();
                    handlePlayerDisconnect(player, session);
                }
            }

            // Start periodic connectivity check with stability improvements
            AtomicBoolean isFirstCheck = new AtomicBoolean(true);
            ScheduledFuture<?> checkTask = scheduler.scheduleAtFixedRate(() -> {
                synchronized (session) {
                    if (!activeGames.containsValue(session)) {
                        if (isFirstCheck.getAndSet(false)) {
                            System.out.println("Game no longer active, stopping connectivity check for players: " + session.getPlayers());
                        }
                        return;
                    }
                    if (isFirstCheck.getAndSet(false)) {
                        System.out.println("Started periodic connectivity check for game with players: " + session.getPlayers());
                    }
                    for (String player : session.getPlayers()) {
                        if (session.getEliminatedPlayers().contains(player)) {
                            continue;
                        }
                        String playerToken = SessionManager.getTokenByUsername(player);
                        if (playerToken == null || !SessionManager.isTokenValid(playerToken)) {
                            System.out.println("Invalid or missing token for player: " + player + ", token: " + (playerToken != null ? playerToken : "null"));
                            handlePlayerDisconnect(player, session);
                            continue;
                        }
                        WordWarZ.ClientCallback cb = clientCallbacks.get(playerToken);
                        if (cb == null) {
                            System.out.println("No callback found for player: " + player + " (token: " + playerToken + ")");
                            handlePlayerDisconnect(player, session);
                            continue;
                        }
                        try {
                            cb.onGameCountdown(-1); // Lightweight ping to check connectivity
                        } catch (Exception e) {
                            System.err.println("Periodic connectivity check failed for player " + player + ": " + e.getMessage());
                            e.printStackTrace();
                            handlePlayerDisconnect(player, session);
                        }
                    }
                }
            }, 0, 2, TimeUnit.SECONDS); // Check every 2 seconds

            connectivityChecks.put(session, checkTask);
        }

        return session.getCurrentWord().length();
    }

    @Override
    public char[] guessLetter(String token, char letter) throws NotLoggedIn, NotInGame, GameNotFound, CharacterAlreadyGuessed {
        if (!SessionManager.isTokenValid(token)) {
            throw new NotLoggedIn();
        }

        String username = SessionManager.getSession(token).getUsername();

        Game session = activeGames.get(username);
        if (session == null || !session.getPlayers().contains(username)) {
            System.out.println("No game found for username: " + username);
            throw new GameNotFound("No active game found for user " + username);
        }

        if (!session.isRoundActive()) {
            throw new IllegalStateException("Cannot process guesses after the round has ended.");
        }

        char[] wordState = session.processGuess(username, letter);

        if (session.hasWon(username)) {
            System.out.println(username + " has won the round!");
            try {
                handleRoundWin(session, username);
            } catch (GameNotFinished e) {
                System.err.println("Error handling round win for " + username + ": " + e.getMessage());
                throw new RuntimeException(e);
            }
        } else if (session.allPlayersOutOfGuesses()) {
            System.out.println("All players out of guesses, ending round");
            try {
                endRound(token);
            } catch (NotLoggedIn | NotInGame | GameNotFound e) {
                System.err.println("Error ending round for " + username + ": " + e.getMessage());
                throw e;
            }
        }

        return wordState;
    }

    private void handleRoundWin(Game session, String winner) throws GameNotFinished {
        System.out.println("handleRoundWin called for winner: " + winner + ", game players: " + session.getPlayers());
        String word = session.getCurrentWord();
        System.out.println("Current word: " + word + ", scores: " + session.getScores() + ", eliminated: " + session.getEliminatedPlayers());

        boolean gameTerminated = false;
        for (String player : session.getPlayers()) {
            if (session.getEliminatedPlayers().contains(player)) {
                System.out.println("Skipping notification for eliminated player: " + player);
                continue;
            }
            String playerToken = SessionManager.getTokenByUsername(player);
            if (playerToken == null || !SessionManager.isTokenValid(playerToken)) {
                System.out.println("Invalid or missing token for player: " + player + ", token: " + (playerToken != null ? playerToken : "null"));
                handlePlayerDisconnect(player, session);
                gameTerminated = activeGames.get(player) == null;
                continue;
            }
            WordWarZ.ClientCallback callback = clientCallbacks.get(playerToken);
            if (callback == null) {
                System.out.println("No callback found for player: " + player + " (token: " + playerToken + ")");
                handlePlayerDisconnect(player, session);
                gameTerminated = activeGames.get(player) == null;
                continue;
            }
            try {
                if (player.equals(winner)) {
                    callback.onRoundWon(word);
                    System.out.println("Notified " + player + " of round win");
                } else {
                    callback.onRoundLost(word, winner);
                    System.out.println("Notified " + player + " of round loss to " + winner);
                }
            } catch (Exception e) {
                System.err.println("Error notifying player " + player + " in handleRoundWin: " + e.getMessage());
                e.printStackTrace();
                handlePlayerDisconnect(player, session);
                gameTerminated = activeGames.get(player) == null;
            }
        }

        if (gameTerminated) {
            System.out.println("Game terminated due to disconnections, skipping further processing");
            return;
        }

        session.incrementScore(winner);
        System.out.println("Incremented score for " + winner + ": " + session.getScore(winner));

        if (session.getScores().get(winner) >= 3) {
            System.out.println("Game ending, winner: " + winner);
            try {
                endGame(SessionManager.getTokenByUsername(winner));
            } catch (NotLoggedIn | NotInGame | GameNotFound e) {
                System.err.println("Error ending game for " + winner + ": " + e.getMessage());
                throw new GameNotFinished(e.getMessage());
            }
        } else {
            // Cancel existing connectivity check to allow a new round to start
            ScheduledFuture<?> checkTask = connectivityChecks.remove(session);
            if (checkTask != null) {
                checkTask.cancel(false);
                System.out.println("Canceled periodic connectivity check for next round setup for game with players: " + session.getPlayers());
            }
            session.resetForNewRound();
            System.out.println("Reset game for next round");
            scheduler.schedule(() -> {
                try {
                    String firstPlayerToken = SessionManager.getTokenByUsername(session.getPlayers().get(0));
                    if (firstPlayerToken != null && SessionManager.isTokenValid(firstPlayerToken)) {
                        startRound(firstPlayerToken);
                        System.out.println("Scheduled next round start for " + session.getPlayers().get(0));
                    } else {
                        System.out.println("Cannot schedule next round, invalid token for " + session.getPlayers().get(0));
                    }
                } catch (Exception e) {
                    System.err.println("Error scheduling next round: " + e.getMessage());
                }
            }, 3, TimeUnit.SECONDS);
        }
    }

    @Override
    public synchronized void endRound(String token) throws NotLoggedIn, NotInGame, GameNotFound {
        if (!SessionManager.isTokenValid(token)) {
            throw new NotLoggedIn();
        }
        String username = SessionManager.getSession(token).getUsername();
        System.out.println("endRound called for token: " + token + ", username: " + username);

        Game game = activeGames.get(username);
        if (game == null || !game.getPlayers().contains(username)) {
            throw new GameNotFound("No active game found for user " + username);
        }

        game.setRoundActive(false);

        // Cancel the periodic connectivity check
        ScheduledFuture<?> checkTask = connectivityChecks.remove(game);
        if (checkTask != null) {
            checkTask.cancel(false);
            System.out.println("Canceled periodic connectivity check for game with players: " + game.getPlayers());
        }

        System.out.println("Ending round for game with players: " + game.getPlayers() + ", eliminated: " + game.getEliminatedPlayers());
        String word = game.getCurrentWord();
        String wordToSend = word != null ? word : "";
        boolean wordGuessed = false;

        if (word != null) {
            for (String player : game.getPlayers()) {
                if (game.hasWon(player)) {
                    wordGuessed = true;
                    break;
                }
            }
        }
        System.out.println("Word: " + wordToSend + ", wordGuessed: " + wordGuessed);

        // First, check for disconnected players before notifying round outcomes
        boolean gameTerminated = false;
        for (String player : game.getPlayers()) {
            if (game.getEliminatedPlayers().contains(player)) {
                System.out.println("Skipping connectivity check for eliminated player: " + player);
                continue;
            }
            String playerToken = SessionManager.getTokenByUsername(player);
            if (playerToken == null || !SessionManager.isTokenValid(playerToken)) {
                System.out.println("Invalid or missing token for player: " + player + ", token: " + (playerToken != null ? playerToken : "null"));
                handlePlayerDisconnect(player, game);
                gameTerminated = activeGames.get(player) == null;
                continue;
            }
            WordWarZ.ClientCallback cb = clientCallbacks.get(playerToken);
            if (cb == null) {
                System.out.println("No callback found for player: " + player + " (token: " + playerToken + ")");
                handlePlayerDisconnect(player, game);
                gameTerminated = activeGames.get(player) == null;
                continue;
            }
            try {
                cb.onGameCountdown(-1); // Using a lightweight callback to test connectivity
                System.out.println("Connectivity check passed for player: " + player);
            } catch (Exception e) {
                System.err.println("Connectivity check failed for player " + player + ": " + e.getMessage());
                e.printStackTrace();
                handlePlayerDisconnect(player, game);
                gameTerminated = activeGames.get(player) == null;
            }
        }

        if (gameTerminated) {
            System.out.println("Game terminated due to disconnections during connectivity check, skipping further processing");
            return;
        }

        // Now notify players of the round outcome
        for (String player : game.getPlayers()) {
            if (game.getEliminatedPlayers().contains(player)) {
                System.out.println("Skipping notification for eliminated player: " + player);
                continue;
            }
            String playerToken = SessionManager.getTokenByUsername(player);
            WordWarZ.ClientCallback cb = clientCallbacks.get(playerToken);
            try {
                if (wordGuessed) {
                    if (game.hasWon(player)) {
                        cb.onRoundWon(wordToSend);
                        System.out.println("Notified " + player + " of round win");
                    } else {
                        cb.onRoundLost(wordToSend, getWinnerUsername(game));
                        System.out.println("Notified " + player + " of round loss to " + getWinnerUsername(game));
                    }
                } else {
                    cb.onRoundDrawn(wordToSend);
                    System.out.println("Notified " + player + " of round draw");
                }
            } catch (Exception e) {
                System.err.println("Error notifying player " + player + " in endRound after connectivity check: " + e.getMessage());
                e.printStackTrace();
                handlePlayerDisconnect(player, game);
                gameTerminated = activeGames.get(player) == null;
            }
        }

        if (gameTerminated) {
            System.out.println("Game terminated due to disconnections during notifications, skipping further processing");
            return;
        }

        // Check remaining players after notifications
        long remainingPlayers = game.getPlayers().stream()
                .filter(p -> !game.getEliminatedPlayers().contains(p))
                .count();
        System.out.println("Remaining players after notifications: " + remainingPlayers);

        if (remainingPlayers <= 1) {
            String lastPlayer = game.getPlayers().stream()
                    .filter(p -> !game.getEliminatedPlayers().contains(p))
                    .findFirst()
                    .orElse(null);
            if (lastPlayer != null) {
                notifyNoOpponent(lastPlayer);
                for (String player : game.getPlayers()) {
                    activeGames.remove(player);
                    System.out.println("Removed game for player: " + player + " due to no opponents");
                }
                System.out.println("Game terminated due to no opponents");
                return; // Exit early, no need to schedule next round
            }
        }

        if (game.getScores().values().stream().anyMatch(score -> score >= 3)) {
            System.out.println("Game ending due to score >= 3");
            try {
                endGame(token);
            } catch (GameNotFinished e) {
                System.err.println("Error ending game: " + e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            game.resetForNewRound();
            System.out.println("Reset game for next round");
            scheduler.schedule(() -> {
                try {
                    String firstPlayerToken = SessionManager.getTokenByUsername(game.getPlayers().get(0));
                    if (firstPlayerToken != null && SessionManager.isTokenValid(firstPlayerToken)) {
                        startRound(firstPlayerToken);
                        System.out.println("Scheduled next round start for " + game.getPlayers().get(0));
                    } else {
                        System.out.println("Cannot schedule next round, invalid token for " + game.getPlayers().get(0));
                    }
                } catch (Exception e) {
                    System.err.println("Error scheduling next round: " + e.getMessage());
                }
            }, 3, TimeUnit.SECONDS);
        }
    }

    private void handlePlayerDisconnect(String player, Game game) {
        System.out.println("Handling disconnect for player: " + player);
        game.markPlayerEliminated(player);
        System.out.println("Marked " + player + " as eliminated");

        String playerToken = SessionManager.getTokenByUsername(player);
        if (playerToken != null) {
            clientCallbacks.remove(playerToken);
            System.out.println("Removed callback for player: " + player + ", token: " + playerToken);
        }

        for (String otherPlayer : game.getPlayers()) {
            if (!otherPlayer.equals(player) && !game.getEliminatedPlayers().contains(otherPlayer)) {
                String otherPlayerToken = SessionManager.getTokenByUsername(otherPlayer);
                if (otherPlayerToken == null || !SessionManager.isTokenValid(otherPlayerToken)) {
                    System.out.println("Invalid or missing token for player: " + otherPlayer);
                    continue;
                }
                WordWarZ.ClientCallback cb = clientCallbacks.get(otherPlayerToken);
                if (cb == null) {
                    System.out.println("No callback found for player: " + otherPlayer + " (token: " + otherPlayerToken + ")");
                    continue;
                }
                try {
                    cb.onPlayerDisconnected(player);
                    System.out.println("Notified " + otherPlayer + " of " + player + "'s disconnection");
                } catch (Exception e) {
                    System.err.println("Error notifying player " + otherPlayer + " about disconnect: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        long remainingPlayers = game.getPlayers().stream()
                .filter(p -> !game.getEliminatedPlayers().contains(p))
                .count();
        System.out.println("Remaining players after disconnect: " + remainingPlayers);

        if (remainingPlayers == 1) {
            String lastPlayer = game.getPlayers().stream()
                    .filter(p -> !game.getEliminatedPlayers().contains(p))
                    .findFirst()
                    .orElse(null);
            if (lastPlayer != null) {
                notifyNoOpponent(lastPlayer);
                for (String p : game.getPlayers()) {
                    activeGames.remove(p);
                    System.out.println("Removed game for player: " + p + " due to no opponents");
                }
                System.out.println("Game terminated due to no opponents");

                // Cancel the periodic connectivity check
                ScheduledFuture<?> checkTask = connectivityChecks.remove(game);
                if (checkTask != null) {
                    checkTask.cancel(false);
                    System.out.println("Canceled periodic connectivity check due to no opponents for game with players: " + game.getPlayers());
                }
            }
        } else if (remainingPlayers == 0) {
            for (String p : game.getPlayers()) {
                activeGames.remove(p);
                System.out.println("Removed game for player: " + p + " (no players remain)");
            }
            System.out.println("Game terminated as no players remain");

            // Cancel the periodic connectivity check
            ScheduledFuture<?> checkTask = connectivityChecks.remove(game);
            if (checkTask != null) {
                checkTask.cancel(false);
                System.out.println("Canceled periodic connectivity check as no players remain for game with players: " + game.getPlayers());
            }
        }
    }

    private boolean isGameEnding(Game game) {
        return game.getScores().values().stream().anyMatch(score -> score >= 3);
    }

    @Override
    public void notifyPlayerLost(String token) throws NotLoggedIn, NotInGame, GameNotFound {
        if (!SessionManager.isTokenValid(token)) {
            throw new NotLoggedIn();
        }
        String username = SessionManager.getSession(token).getUsername();
        System.out.println("notifyPlayerLost called for token: " + token + ", username: " + username);

        Game game = activeGames.get(username);
        if (game == null || !game.getPlayers().contains(username)) {
            System.out.println("No game found for username: " + username);
            throw new GameNotFound("No active game found for user " + username);
        }

        game.markPlayerEliminated(username);
        System.out.println("Marked " + username + " as eliminated");

        if (game.allPlayersEliminated()) {
            System.out.println("All players eliminated, ending round");
            endRound(token);
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

    private void notifySessionExpired(String token) {
        WordWarZ.ClientCallback callback = clientCallbacks.get(token);
        if (callback != null) {
            try {
                callback.onForceLogout();
                System.out.println("Notified token: " + token + " that session has expired");
            } catch (Exception e) {
                System.err.println("Error notifying session expired for token " + token + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void cleanupExpiredSession(String token) {
        try {
            System.out.println("cleanupExpiredSession called for token: " + token);
            String username = SessionManager.getSession(token).getUsername();
            System.out.println("Username for expired session: " + username);

            instance.notifySessionExpired(token);
            clientCallbacks.remove(token);
            System.out.println("Removed callback for token: " + token + ", remaining callbacks: " + clientCallbacks.keySet());

            Game game = activeGames.get(username);
            if (game != null) {
                System.out.println("Found game for " + username + ", players: " + game.getPlayers() + ", eliminated: " + game.getEliminatedPlayers());
                game.markPlayerEliminated(username);
                System.out.println("Marked " + username + " as eliminated");

                for (String player : game.getPlayers()) {
                    if (!player.equals(username) && !game.getEliminatedPlayers().contains(player)) {
                        String playerToken = SessionManager.getTokenByUsername(player);
                        if (playerToken == null || !SessionManager.isTokenValid(playerToken)) {
                            System.out.println("Invalid or missing token for player: " + player);
                            continue;
                        }
                        WordWarZ.ClientCallback cb = clientCallbacks.get(playerToken);
                        if (cb == null) {
                            System.out.println("No callback found for player: " + player + " (token: " + playerToken + ")");
                            continue;
                        }
                        try {
                            cb.onPlayerDisconnected(username);
                            System.out.println("Notified " + player + " of " + username + "'s disconnection");
                        } catch (Exception e) {
                            System.err.println("Error notifying player " + player + " about disconnect: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }

                long remainingPlayers = game.getPlayers().stream()
                        .filter(p -> !game.getEliminatedPlayers().contains(p))
                        .count();
                System.out.println("Remaining players: " + remainingPlayers);

                if (remainingPlayers == 1) {
                    String lastPlayer = game.getPlayers().stream()
                            .filter(p -> !game.getEliminatedPlayers().contains(p))
                            .findFirst()
                            .orElse(null);
                    if (lastPlayer != null) {
                        String lastPlayerToken = SessionManager.getTokenByUsername(lastPlayer);
                        if (lastPlayerToken == null || !SessionManager.isTokenValid(lastPlayerToken)) {
                            System.out.println("Invalid or missing token for last player: " + lastPlayer);
                        } else {
                            WordWarZ.ClientCallback lastPlayerCallback = clientCallbacks.get(lastPlayerToken);
                            if (lastPlayerCallback != null) {
                                try {
                                    lastPlayerCallback.onPlayerDisconnected("__NoOpponent__");
                                    System.out.println("Notified last player " + lastPlayer + " of no opponents");
                                } catch (Exception e) {
                                    System.err.println("Error notifying last player " + lastPlayer + " of no opponents: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            } else {
                                System.out.println("No callback found for last player: " + lastPlayer + " (token: " + lastPlayerToken + ")");
                            }
                        }
                        for (String player : game.getPlayers()) {
                            activeGames.remove(player);
                            System.out.println("Removed game for player: " + player);
                        }
                        System.out.println("Game terminated due to no opponents");

                        // Cancel the periodic connectivity check
                        ScheduledFuture<?> checkTask = connectivityChecks.remove(game);
                        if (checkTask != null) {
                            checkTask.cancel(false);
                            System.out.println("Canceled periodic connectivity check during session cleanup for game with players: " + game.getPlayers());
                        }
                    }
                } else if (remainingPlayers == 0) {
                    for (String player : game.getPlayers()) {
                        activeGames.remove(player);
                        System.out.println("Removed game for player: " + player + " (no players remain)");
                    }
                    System.out.println("Game terminated as no players remain");

                    // Cancel the periodic connectivity check
                    ScheduledFuture<?> checkTask = connectivityChecks.remove(game);
                    if (checkTask != null) {
                        checkTask.cancel(false);
                        System.out.println("Canceled periodic connectivity check during session cleanup as no players remain for game with players: " + game.getPlayers());
                    }
                } else {
                    if (game.allPlayersEliminated()) {
                        System.out.println("All players eliminated, ending round");
                        try {
                            instance.endRound(token);
                        } catch (Exception e) {
                            System.err.println("Error ending round during session cleanup: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                System.out.println("No game found for username: " + username);
            }

            if (QueueManager.isInQueue(username)) {
                QueueManager.leaveQueue(username);
                notifyQueueUpdate(QueueManager.getQueueSize());
                System.out.println("Removed " + username + " from queue");
            }

            System.out.println("Completed cleanup for expired session: " + token);
        } catch (NotLoggedIn e) {
            System.out.println("Session already invalid during cleanup: " + token);
        }
    }

    @Override
    public void endGame(String token) throws NotLoggedIn, NotInGame, GameNotFound, GameNotFinished {
        System.out.println("endGame called for token: " + token);
        String username = SessionManager.getSession(token).getUsername();
        System.out.println("Ending game for username: " + username);

        Game game = activeGames.get(username);
        if (game == null) {
            System.out.println("No game found for username: " + username);
            throw new GameNotFound("No active game found for user " + username);
        }

        // Cancel the periodic connectivity check
        ScheduledFuture<?> checkTask = connectivityChecks.remove(game);
        if (checkTask != null) {
            checkTask.cancel(false);
            System.out.println("Canceled periodic connectivity check during endGame for game with players: " + game.getPlayers());
        }

        String winner = "";
        int highestScore = -1;
        for (Map.Entry<String, Integer> entry : game.getScores().entrySet()) {
            if (entry.getValue() > highestScore) {
                highestScore = entry.getValue();
                winner = entry.getKey();
            }
        }
        System.out.println("Determined winner: " + winner + " with score: " + highestScore);

        if (!winner.isEmpty()) {
            try {
                DBManager.incrementWinCount(winner);
                System.out.println("Updated win count for winner: " + winner);
            } catch (Exception e) {
                System.err.println("Failed to update win count: " + e.getMessage());
                e.printStackTrace();
            }
        }

        for (String player : game.getPlayers()) {
            if (game.getEliminatedPlayers().contains(player)) {
                System.out.println("Skipping end game notification for eliminated player: " + player);
                continue;
            }
            String playerToken = SessionManager.getTokenByUsername(player);
            if (playerToken == null || !SessionManager.isTokenValid(playerToken)) {
                System.out.println("Invalid or missing token for player: " + player);
                continue;
            }
            WordWarZ.ClientCallback callback = clientCallbacks.get(playerToken);
            if (callback == null) {
                System.out.println("No callback found for player: " + player + " (token: " + playerToken + ")");
                continue;
            }
            try {
                if (player.equals(winner)) {
                    callback.onGameWon(winner);
                    System.out.println("Notified " + player + " of game win");
                } else {
                    callback.onGameLost(winner);
                    System.out.println("Notified " + player + " of game loss to " + winner);
                }
            } catch (Exception e) {
                System.err.println("Error notifying player " + player + " of game end: " + e.getMessage());
                e.printStackTrace();
            }
        }

        scheduler.schedule(() -> {
            for (String player : game.getPlayers()) {
                activeGames.remove(player);
                System.out.println("Removed game for player: " + player + " during endGame cleanup");
            }
            clientCallbacks.entrySet().removeIf(entry -> {
                try {
                    return game.getPlayers().contains(SessionManager.getSession(entry.getKey()).getUsername());
                } catch (NotLoggedIn e) {
                    System.out.println("Removing callback due to invalid session: " + entry.getKey());
                    return true;
                }
            });
            System.out.println("Game cleanup complete for players: " + game.getPlayers() + ", remaining callbacks: " + clientCallbacks.keySet());
        }, 5, TimeUnit.SECONDS);
    }

    @Override
    public int displayWins(String token) throws NotLoggedIn, NotInGame, GameNotFound, RoundNotFinished {
        if (!SessionManager.isTokenValid(token)) {
            throw new NotLoggedIn();
        }

        String username = SessionManager.getSession(token).getUsername();
        Game game = activeGames.get(username);
        if (game == null || !game.getPlayers().contains(username)) {
            throw new GameNotFound("No active game found for user " + username);
        }
        return game.getScore(username);
    }

    public static void registerActiveGame(String playerToken, Game game) {
        try {
            String username = SessionManager.getSession(playerToken).getUsername();
            activeGames.put(username, game);
            System.out.println("Game registered for player: " + username);
        } catch (NotLoggedIn e) {
            System.out.println("Error registering game: " + e.getMessage());
        }
    }
}