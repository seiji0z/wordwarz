package client.player.controller;
import WordWarZ.CharacterAlreadyGuessed;
import client.player.model.GameModel;
import client.player.view.GameView;
import client.player.view.MainMenuView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.omg.CORBA.ORB;

public class GameController {
    private GameModel model;
    private GameView view;
    private final String playerToken;
    private final ORB orb;
    private boolean roundActive = false;

    public GameController(String token, ORB orb, int selectedCharacter) {
        this.orb = orb;
        this.playerToken = token;
        this.model = new GameModel(token, orb);
        this.view = new GameView(new Stage(), selectedCharacter );

        setupEventHandlers();
    }

    private void setupEventHandlers() {
        view.setOnLetterPressed(this::handleLetterGuess);
    }

    public void onGameStart(char[] wordPlaceholder) {
        if (roundActive) return; // Prevent concurrent rounds
        roundActive = true;

        Platform.runLater(() -> {
            view.showOverlayWithTimer(() -> {
                try {
                    model.startRound(); // Ensure proper server round state
                    view.initializeWordDisplay(wordPlaceholder.length);

                    // Start timer and handle timeout
                    view.setOnTimeOut(() -> {
                        try {
                            model.endRound(); // Notify server
                        } catch (Exception e) {
                            view.showErrorMessage("Error ending round: " + e.getMessage());
                        }
                    });

                    view.startTimer(model.getRoundDuration());
                } catch (Exception e) {
                    view.showErrorMessage("Failed to start round: " + e.getMessage());
                } finally {
                    roundActive = false;
                }
            });
        });
    }

    // In GameController
    private void handleLetterGuess(char letter) {
        view.disableLetterButton(letter);

        try {
            char[] result = model.guessLetter(letter);
            // Always update display with the server's response
            view.updateWordDisplay(result);

            if (!isGuessCorrect(result, letter)) {
                handleWrongGuess();
            }
        } catch (CharacterAlreadyGuessed e) {
            view.showErrorMessage("Letter already guessed!");
        } catch (Exception e) {
            view.showErrorMessage("Error processing guess: " + e.getMessage());
        }
    }

    private boolean isGuessCorrect(char[] wordState, char guessedLetter) {
        for (char c : wordState) {
            if (Character.toUpperCase(c) == Character.toUpperCase(guessedLetter)) {
                return true;
            }
        }
        return false;
    }

    private void handleWrongGuess() {
        view.loseHeart();
        if (view.getRemainingGuesses() <= 0) {
            for (char c = 'A'; c <= 'Z'; c++) {
                view.disableLetterButton(c);
            }

            // Notify server this player is out
            try {
                model.notifyPlayerLost();
            } catch (Exception e) {
                view.showErrorMessage("Error notifying server: " + e.getMessage());
            }
        }
    }

    public void handleGameOver(boolean won, String winner) {
        try {
            if (won) {
                view.showEndGameOverlay("You won the game!");

                // sync
                new Timeline(new KeyFrame(Duration.seconds(3), event -> view.createConfetti())).play();
            } else {
                view.showEndGameOverlay(winner + " won the game!");
            }

            // Schedule return to main menu after delay
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(() -> {
                                // Close current game window
                                view.closeApplication();

                                // Return to main menu
                                showMainMenu();
                            });
                        }
                    },
                    5000 // 5-second delay
            );
        } catch (Exception e) {
            view.showErrorMessage("Error handling game over: " + e.getMessage());
        }
    }

    private void showMainMenu() {
        try {
            Stage stage = new Stage();
            MainMenuView mainMenuView = new MainMenuView();
            mainMenuView.initializeUI(stage);
            new MainMenuController(playerToken, orb, mainMenuView, stage);
            stage.setTitle("Word War Z - Main Menu");
            stage.show();
        } catch (Exception e) {
            System.err.println("Error showing main menu: " + e.getMessage());
        }
    }

    public void handleRoundWon(String word) {
        view.showRoundWon(word);
        prepareNextRound();
    }

    public void handleRoundLost(String word, String winner) {
        view.showRoundLost(winner, word);
        prepareNextRound();
    }

    public void handleRoundDrawn(String word) {
        view.showRoundDrawn(word);
        prepareNextRound();
    }

    private void prepareNextRound() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            try {
                                // 1. Reset view first
                                view.resetRound();

                                // 2. Start new round and get word length
                                int wordLength = model.startRound();

                                // 3. Initialize display with new word length
                                view.initializeWordDisplay(wordLength);

                                // 4. Start timer
                                view.startTimer(model.getRoundDuration());
                            } catch (Exception e) {
                                view.showErrorMessage("Error starting new round: " + e.getMessage());
                            }
                        });
                    }
                },
                3000 // 3-second delay
        );
    }
}