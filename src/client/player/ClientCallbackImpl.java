package client.player;

import WordWarZ.ClientCallbackPOA;
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

    public ClientCallbackImpl(QueueView q, GameController controller, Stage stage, String playerToken, ORB orb) {
        this.queueView = q;
        this.gameController = controller;
        this.stage = stage;
        this.playerToken = playerToken;
        this.orb = orb;
    }

    public ClientCallbackImpl(QueueView q, Stage stage, String playerToken, ORB orb) {
        this.queueView = q;
        this.gameController = null;
        this.stage = stage;
        this.playerToken = playerToken;
        this.orb = orb;
    }

    public ClientCallbackImpl(GameController controller, Stage stage, String playerToken, ORB orb) {
        this.queueView = null;
        this.gameController = controller;
        this.stage = stage;
        this.playerToken = playerToken;
        this.orb = orb;
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
            return; // Prevent multiple initializations
        }
        gameStarted = true;

        System.out.println("Round started callback received");
        Platform.runLater(() -> {
            // Clear the queue view reference to prevent memory leaks
            if (queueView != null) {
                queueView.close();
                queueView = null; // Remove reference
            }

            // Initialize game controller if not already done
            if (gameController == null) {
                gameController = new GameController(playerToken, orb);
            }
            gameController.onGameStart(wordPlaceholder);
        });
    }

    @Override
    public void onRoundLost(String word, String winner) {

    }

    @Override
    public void onRoundWon(String word) {

    }

    @Override
    public void onRoundDrawn(String word) {

    }

    @Override
    public void onGameLost(String winnerUsername) {

    }

    @Override
    public void onGameWon(String winnerUsername) {

    }


    @Override
    public void onLeaderboardUpdated(WordWarZ.Player[] leaderboard) {
        // Optional future: update leaderboard
    }

    @Override
    public void onForceLogout() {
        // Optional future: logout force handling
    }
}
