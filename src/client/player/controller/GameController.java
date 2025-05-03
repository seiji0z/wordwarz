package client.player.controller;
import client.player.model.GameModel;
import client.player.view.GameView;

public class GameController {
    private GameModel model;
    private GameView view;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view.setLetterGuessHandler(this::handleLetterGuess);


        initializeView();
    }

    public void initializeView() {

    }

    public void startGame() {

    }

    public void cancelQueue() {

    }

    public void guessLetter(char letter) {
        try {
            char[] result = model.guessLetter(letter);
            view.updateWordDisplay(result);
        } catch (Exception e) {
            view.showErrorMessage(e.getMessage());
        }
    }

    public void updateLeaderboard() {

    }

    public void startRound() {
        try {
            int wordLength = model.startRound();
            view.initializeWordDisplay(wordLength);
        } catch (Exception e) {
            view.showErrorMessage("Failed to start round: " + e.getMessage());
        }
    }

    private void handleLetterGuess(char letter) {
        guessLetter(letter);
    }
}