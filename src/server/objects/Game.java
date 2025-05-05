package server.objects;

import server.helpers.WordManager;

import java.util.*;

public class Game {
    private List<String> players = new ArrayList<>();
    private List<String> usedWords = new ArrayList<>();
    private Map<String, Integer> scores = new HashMap<>();
    private String currentWord;
    private char[] guessedWord;
    private Map<String, Integer> wrongGuesses = new HashMap<>();
    private Set<Character> guessedLetters = new HashSet<>();

    public Game(List<String> players) {
        this.players.addAll(players);
        for (String player : players) {
            scores.put(player, 0);
            wrongGuesses.put(player, 0);
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

    public boolean guessLetter(String player, char letter) {
        letter = Character.toUpperCase(letter);
        if (guessedLetters.contains(letter)) return false;

        guessedLetters.add(letter);
        boolean hit = false;
        for (int i = 0; i < currentWord.length(); i++) {
            if (currentWord.charAt(i) == letter) {
                guessedWord[i] = letter;
                hit = true;
            }
        }

        if (!hit) {
            wrongGuesses.put(player, wrongGuesses.get(player) + 1);
        }

        return hit;
    }

    public boolean hasWon(String player) {
        return new String(guessedWord).equals(currentWord);
    }

    public boolean hasLost(String player) {
        return wrongGuesses.get(player) >= 5;
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

    public Set<Character> getGuessedLetters() { return guessedLetters; }
}
