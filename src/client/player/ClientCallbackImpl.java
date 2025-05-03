package client.player;

import WordWarZ.ClientCallbackPOA;
import client.player.view.GameView;
import client.player.view.MainMenuView;
import client.player.view.QueueView;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.omg.CORBA.ORB;

public class ClientCallbackImpl extends ClientCallbackPOA {
    private final MainMenuView menuView;
    private final QueueView queueView;
    private final GameView gameView;
    private final Stage stage;
    private final String playerToken;
    private final ORB orb;


    public ClientCallbackImpl(MainMenuView m, QueueView q, GameView g,
                              Stage stage, String playerToken, ORB orb) {
        this.menuView = m;
        this.queueView = q;
        this.gameView = g;
        this.stage = stage;
        this.playerToken = playerToken;
        this.orb = orb;
    }

    public ClientCallbackImpl(MainMenuView m, Stage stage, String playerToken, ORB orb) {
        this.menuView = m;
        this.gameView = null;
        this.queueView = null;
        this.stage = stage;
        this.playerToken = playerToken;
        this.orb = orb;
    }

    public ClientCallbackImpl(QueueView q, Stage stage, String playerToken, ORB orb) {
        this.menuView = null;
        this.queueView = q;
        this.gameView = null;
        this.stage = stage;
        this.playerToken = playerToken;
        this.orb = orb;
    }

    public ClientCallbackImpl(GameView g, Stage stage, String playerToken, ORB orb) {
        this.menuView = null;
        this.queueView = null;
        this.gameView = g;
        this.stage = stage;
        this.playerToken = playerToken;
        this.orb = orb;
    }

    @Override
    public void onQueueUpdated(int playerCount) {
        System.out.println("Received queue update: " + playerCount + " players");
        if (queueView != null) {
            Platform.runLater(() -> {
                queueView.updatePlayerCount(playerCount);
                System.out.println("Updated QueueView with player count: " + playerCount);
            });
        } else {
            System.out.println("QueueView is null, cannot update player count");
        }
    }

    @Override
    public void onGameCountdown(int secondsLeft) {
        System.out.println("Received countdown update: " + secondsLeft + " seconds");
        if (queueView != null) {
            Platform.runLater(() -> {
                queueView.updateTimer(secondsLeft);
                System.out.println("Updated QueueView with timer: " + secondsLeft);
            });
        } else {
            System.out.println("QueueView is null, cannot update timer");
        }
    }

    @Override
    public void onRoundStarted(String[] wordPlaceholder) {
//        Platform.runLater(() -> {
//            try {
//                // Initialize GameView
//                GameView gameView = new GameView();
//
//                // Create a new scene using GameView's root pane
//                Scene gameScene = new Scene(gameView.getRootPane(), 1280, 760);
//
//                // Set the scene on the existing stage
//                stage.setScene(gameScene);
//                stage.setTitle("Word War Z - Game");
//
//            } catch (Exception e) {
//                System.err.println("Error transitioning to GameView: " + e.getMessage());
//                e.printStackTrace();
//            }
//        });
    }

    @Override
    public void onRoundEnded(String roundResult, String winner) {
        // Future: Handle round end
    }

    @Override
    public void onGameEnded(String winnerUsername) {
        // Future: Handle game end
    }

    @Override
    public void onLeaderboardUpdated(WordWarZ.Player[] leaderboard) {
        // Future: Update leaderboard
    }

    @Override
    public void onForceLogout() {
        // Future: Handle forced logout
    }
}