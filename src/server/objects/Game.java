package server.objects;

import WordWarZ.CharacterAlreadyGuessed;
import server.helpers.WordManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Game {
    private List<String> players = new ArrayList<>();
    private List<String> usedWords = new ArrayList<>();
    private Map<String, Integer> scores = new HashMap<>();
    private String currentWord;
    private char[] guessedWord;
    private Map<String, Integer> wrongGuesses = new HashMap<>();
    private Set<Character> guessedLetters = new HashSet<>();
    private Map<String, Set<Character>> playerGuesses = new ConcurrentHashMap<>();
    private Set<String> eliminatedPlayers = new HashSet<>();

    public Game(List<String> players) {
        this.players.addAll(players);
        for (String player : players) {
            scores.put(player, 0);
            wrongGuesses.put(player, 0);
            playerGuesses.put(player, new HashSet<>());
        }
    }

    public String nextWord() {
        currentWord = WordManager.getRandomWord(usedWords);
        usedWords.add(currentWord);
        guessedWord = new char[currentWord.length()];
        Arrays.fill(guessedWord, '_');
        guessedLetters.clear();
        return currentWord;
    }

    public char[] getGuessedWord() {
        return guessedWord.clone();
    }

    public char[] processGuess(String username, char letter) throws CharacterAlreadyGuessed {
        // Track guesses per player
        if (playerGuesses.get(username).contains(letter)) {
            throw new CharacterAlreadyGuessed();
        }

        // Record the guess
        playerGuesses.get(username).add(letter);

        // Build response showing only this player's correct guesses
        char[] result = new char[currentWord.length()];
        for (int i = 0; i < currentWord.length(); i++) {
            char c = currentWord.charAt(i);
            result[i] = playerGuesses.get(username).contains(c) ? c : '_';
        }

        return result;
    }

    public void resetPlayerGuesses(String username) {
        playerGuesses.put(username, new HashSet<>());
    }

    public boolean hasWon(String player) {
        if (currentWord == null) {
            return false;
        }

        playerGuesses.putIfAbsent(player, new HashSet<>());
        Set<Character> guesses = playerGuesses.get(player);

        if (guesses == null) {
            System.err.println("Player guesses not initialized for player: " + player);
            return false;
        }

        for (char c : currentWord.toCharArray()) {
            if (!guesses.contains(c)) {
                return false;
            }
        }
        return true;
    }

    public void incrementScore(String player) {
        scores.put(player, scores.get(player) + 1);
    }

    public int getScore(String player) {
        return scores.getOrDefault(player, 0);
    }

    public List<String> getPlayers() {
        return players;
    }

    public String getCurrentWord() { return currentWord; }

    public Map<String, Integer> getScores() {
        // Return a copy of the scores map to prevent outside modification
        return new HashMap<>(scores);
    }

    public void markPlayerEliminated(String username) {
        eliminatedPlayers.add(username);
    }

    public boolean allPlayersEliminated() {
        return eliminatedPlayers.size() == players.size();
    }

    public void resetForNewRound() {
        this.currentWord = null;
        this.guessedLetters.clear();
        this.eliminatedPlayers.clear();
        for (String player : players) {
            resetPlayerGuesses(player);
            this.wrongGuesses.put(player, 0);
        }
    }
}