package client.player.controller;
import client.player.model.GameModel;
import client.player.view.GameView;

import java.util.Arrays;

public class GameController {
    private GameModel model;
    private GameView view;
    private int remainingGuesses = 5;
    private char[] previousState;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
        initializeView();
    }

    public void initializeView() {

    }

    public void startGame() {

    }

    public void cancelQueue() {

    }

    // Modify guessLetter method
    public void guessLetter(char letter) {
        try {
            char[] result = model.guessLetter(letter);
            view.updateWordDisplay(result);

            // Check if guess was incorrect
            if (isIncorrectGuess(previousState, result)) {
                remainingGuesses--;
                view.remainingGuesses = remainingGuesses; // Update view state
                view.updateHearts();

                if (remainingGuesses <= 0) {
                    view.showErrorMessage("Game Over! No more guesses!");
                    view.closeApplication();
                }
            }
            previousState = Arrays.copyOf(result, result.length);
        } catch (Exception e) {
            view.showErrorMessage(e.getMessage());
        }
    }

    private boolean isIncorrectGuess(char[] before, char[] after) {
        int countBefore = countRevealedLetters(before);
        int countAfter = countRevealedLetters(after);
        return countAfter == countBefore;
    }

    private int countRevealedLetters(char[] state) {
        int count = 0;
        for (char c : state) {
            if (c != '_') count++;
        }
        return count;
    }

    public void updateLeaderboard() {

    }

    public void startRound() {
        try {
            int wordLength = model.startRound();
            view.initializeWordDisplay(wordLength);
            view.startTimer();
            previousState = new char[wordLength];
            Arrays.fill(previousState, '_');
        } catch (Exception e) {
            view.showErrorMessage("Failed to start round: " + e.getMessage());
        }
    }

    private void handleLetterGuess(char letter) {
        guessLetter(letter);
    }
}