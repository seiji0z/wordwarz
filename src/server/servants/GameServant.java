package server.servants;

import WordWarZ.*;
import org.omg.CORBA.ORB;
import server.helpers.QueueManager;
import server.helpers.SessionManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameServant extends GameServicePOA {
    private static final ConcurrentHashMap<String, WordWarZ.ClientCallback> clientCallbacks = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> waitingPlayers = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private String targetWord = "lorraine"; // Hardcoded for testing
    private char[] currentWordState; // Tracks which letters have been guessed
    private List<Character> guessedLetters = new ArrayList<>();


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

    public static void notifyCountdownUpdate(int secondsLeft) {
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
    public int startRound(String token) throws NotLoggedIn, NotInGame, GameNotFound {
        // Reset game state for new round
        currentWordState = null;
        guessedLetters.clear();
        return targetWord.length(); // Return word length
    }

    @Override
    public char[] guessLetter(String token, char letter) throws NotLoggedIn, NotInGame, GameNotFound, CharacterAlreadyGuessed{
        // Convert to lowercase for case-insensitive comparison
        char lowerLetter = Character.toLowerCase(letter);

        // Check if letter was already guessed
        if (guessedLetters.contains(lowerLetter)) {
            throw new CharacterAlreadyGuessed("Letter " + letter + " was already guessed");
        }

        // Add to guessed letters
        guessedLetters.add(lowerLetter);

        // Initialize currentWordState if this is the first guess
        if (currentWordState == null) {
            currentWordState = new char[targetWord.length()];
            Arrays.fill(currentWordState, '_');
        }

        boolean correctGuess = false;

        // Check if letter is in the word
        for (int i = 0; i < targetWord.length(); i++) {
            if (targetWord.charAt(i) == lowerLetter) {
                currentWordState[i] = targetWord.charAt(i);
                correctGuess = true;
            }
        }

        // Return a copy of the current state
        return Arrays.copyOf(currentWordState, currentWordState.length);
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