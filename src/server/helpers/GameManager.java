// server/objects/Game.java
package server.helpers;

import java.util.*;

public class GameManager {
    private List<String> players;
    private List<String> usedWords = new ArrayList<>();
    private String currentWord;
    private Set<Character> guessedLetters = new HashSet<>();
    private int remainingGuesses = 3;
    private int roundDuration;
    private Map<String, Integer> wins = new HashMap<>();

    public GameManager(List<String> players, int roundDuration) {
        this.players = new ArrayList<>(players);
        this.roundDuration = roundDuration;
        for (String player : players) {
            wins.put(player, 0);
        }
    }

    public String startNewRound() {
        currentWord = WordManager.getRandomWord(usedWords);
        usedWords.add(currentWord);
        guessedLetters.clear();
        remainingGuesses = 3;
        return currentWord;
    }

    public char[] guessLetter(char letter) {
        letter = Character.toUpperCase(letter);
        if (guessedLetters.contains(letter)) {
            throw new IllegalArgumentException("Letter already guessed");
        }
        guessedLetters.add(letter);

        if (currentWord.indexOf(letter) == -1) {
            remainingGuesses--;
        }

        return generatePlaceholder();
    }

    private char[] generatePlaceholder() {
        char[] placeholder = new char[currentWord.length()];
        for (int i = 0; i < currentWord.length(); i++) {
            char c = currentWord.charAt(i);
            placeholder[i] = guessedLetters.contains(c) ? c : '_';
        }
        return placeholder;
    }

    public boolean isWordGuessed() {
        return new String(generatePlaceholder()).equals(currentWord);
    }

    public List<String> getPlayers() {
        return players;
    }

    public int getWins(String winner) {
        return wins.getOrDefault(winner, 0);
    }
}