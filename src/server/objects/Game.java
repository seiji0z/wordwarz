package server.objects;

import java.util.*;

public class Game {
    private String gameId;
    private List<String> players;
    private Map<String, Integer> scores;
    private int currentRound;
    private String currentWord;
    private Set<Character> guessedLetters;

    public Game(List<String> players) {
        this.gameId = UUID.randomUUID().toString();
        this.players = new ArrayList<>(players);
        this.scores = new HashMap<>();
        players.forEach(p -> scores.put(p, 0));
        this.currentRound = 0;
    }

    // Add methods for:
    // - Starting new rounds
    // - Processing guesses
    // - Tracking time
    // - Determining winners
}