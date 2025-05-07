package client.player;

import WordWarZ.ClientCallbackPOA;
import WordWarZ.Player;
import client.player.controller.GameController;
import client.player.view.QueueView;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.omg.CORBA.ORB;

public class ClientCallbackImpl extends ClientCallbackPOA {
    private QueueView queueView;
    private GameController gameController;
    private final Stage stage;
    private final String playerToken;
    private final ORB orb;
    private boolean gameStarted = false;
    private int selectedCharacter = 0;

    public ClientCallbackImpl(QueueView q, Stage stage, String playerToken, ORB orb, int selectedCharacter) {
        this.queueView = q;
        this.stage = stage;
        this.playerToken = playerToken;
        this.orb = orb;
        this.selectedCharacter = selectedCharacter; // Store selected character
    }

    @Override
    public void onQueueUpdated(int playerCount) {
        if (queueView != null) {
            queueView.updatePlayerCount(playerCount);
        }
    }

    @Override
    public void onGameCountdown(int secondsLeft) {
        if (queueView != null) {
            // Update the queue view timer display
            queueView.updateTimer(secondsLeft);

            // Check if the countdown is complete
            if (secondsLeft <= 0) {
                System.out.println("Countdown reached 0. Game is ready to start.");
            }
        }
    }

    @Override
    public void onRoundStarted(char[] wordPlaceholder) {
        if (gameStarted) {
            return; // Prevent duplicate initialization
        }
        gameStarted = true;

        System.out.println("Round started callback received");
        Platform.runLater(() -> {
            // Clear the queue view reference to prevent memory leaks
            if (queueView != null) {
                queueView.close();
                queueView = null; // Remove reference
            }

            // Ensure the game controller is only initialized once
            if (gameController == null) {
                gameController = new GameController(playerToken, orb, selectedCharacter);
            }
            gameController.onGameStart(wordPlaceholder);
        });
    }

    // In ClientCallbackImpl.java
    @Override
    public void onRoundLost(String word, String winner) {
        Platform.runLater(() -> {
            if (gameController != null) {
                gameController.handleRoundLost(word, winner);
            }
        });
    }

    @Override
    public void onRoundWon(String word) {
        Platform.runLater(() -> {
            if (gameController != null) {
                gameController.handleRoundWon(word);
            }
        });
    }

    @Override
    public void onRoundDrawn(String word) {
        Platform.runLater(() -> {
            if (gameController != null) {
                gameController.handleRoundDrawn(word);
            }
        });
    }

    @Override
    public void onGameLost(String winnerUsername) {

    }

    @Override
    public void onGameWon(String winnerUsername) {

    }

    @Override
    public void onLeaderboardUpdated(Player[] leaderboard) {
        // Optional future: update leaderboard
    }

    @Override
    public void onForceLogout() {
        // Optional future: logout force handling
    }

}
