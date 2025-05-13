package client.player;

import WordWarZ.ClientCallbackPOA;
import client.player.controller.GameController;
import client.player.controller.MainMenuController;
import client.player.controller.QueueController;
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
    private final int selectedCharacter;
    private final QueueController queueController;
    private MainMenuController mainMenuController;

    public ClientCallbackImpl(QueueView q, Stage stage, String playerToken, ORB orb, int selectedCharacter, QueueController queueController) {
        this.queueView = q;
        this.stage = stage;
        this.playerToken = playerToken;
        this.orb = orb;
        this.selectedCharacter = selectedCharacter;
        this.queueController = queueController;
        System.out.println("[ClientCallbackImpl] Initialized for token: " + playerToken);
    }

    public void setMainMenuController(MainMenuController controller) {
        this.mainMenuController = controller;
    }

    @Override
    public void onQueueUpdated(int playerCount) {
        System.out.println("[ClientCallbackImpl] Queue updated, player count: " + playerCount + " for token: " + playerToken);
        if (queueView != null) {
            queueView.updatePlayerCount(playerCount);
            if (playerCount == 0 && !gameStarted) {
                System.out.println("[ClientCallbackImpl] No opponents, triggering handleNoOpponentFound for token: " + playerToken);
                queueController.handleNoOpponentFound();
            }
        }
    }

    @Override
    public void onGameCountdown(int secondsLeft) {
        if (secondsLeft == -1) {
            return;
        }
        System.out.println("[ClientCallbackImpl] Countdown updated, seconds left: " + secondsLeft + " for token: " + playerToken);
        if (queueView != null) {
            queueView.updateTimer(secondsLeft);
            if (secondsLeft <= 0) {
                System.out.println("[ClientCallbackImpl] Countdown reached 0 for token: " + playerToken);
            }
        }
    }

    @Override
    public void onRoundStarted(char[] wordPlaceholder) {
        System.out.println("[ClientCallbackImpl] Round started for token: " + playerToken);
        Platform.runLater(() -> {
            if (!gameStarted) {
                // First round: initialize the game
                gameStarted = true;
                if (queueView != null) {
                    queueView.close();
                    queueView = null;
                }
                if (gameController == null) {
                    gameController = new GameController(playerToken, orb, selectedCharacter, stage);
                    System.out.println("[ClientCallbackImpl] GameController initialized for token: " + playerToken);
                }
            }
            gameController.onGameStart(wordPlaceholder);
        });
    }

    @Override
    public void onRoundLost(String word, String winner) {
        System.out.println("[ClientCallbackImpl] Round lost, word: " + word + ", winner: " + winner + " for token: " + playerToken);
        Platform.runLater(() -> {
            if (gameController != null) {
                gameController.handleRoundLost(word, winner);
            }
        });
    }

    @Override
    public void onRoundWon(String word) {
        System.out.println("[ClientCallbackImpl] Round won, word: " + word + " for token: " + playerToken);
        Platform.runLater(() -> {
            if (gameController != null) {
                gameController.handleRoundWon(word);
            }
        });
    }

    @Override
    public void onRoundDrawn(String word) {
        System.out.println("[ClientCallbackImpl] Round drawn, word: " + word + " for token: " + playerToken);
        Platform.runLater(() -> {
            if (gameController != null) {
                gameController.handleRoundDrawn(word);
            }
        });
    }

    @Override
    public void onGameLost(String winnerUsername) {
        System.out.println("[ClientCallbackImpl] Game lost, winner: " + winnerUsername + " for token: " + playerToken);
        Platform.runLater(() -> {
            if (gameController != null) {
                gameController.handleGameOver(false, winnerUsername);
            }
        });
    }

    @Override
    public void onGameWon(String winnerUsername) {
        System.out.println("[ClientCallbackImpl] Game won! for token: " + playerToken);
        Platform.runLater(() -> {
            if (gameController != null) {
                gameController.handleGameOver(true, winnerUsername);
            }
        });
    }

    @Override
    public void onForceLogout() {
        System.out.println("[ClientCallbackImpl] Received force logout for token: " + playerToken);
        if (gameController != null) {
            gameController.handleForceLogout();
        } else if (queueController != null) {
            queueController.handleForceLogout();
        } else if (mainMenuController != null) {
            mainMenuController.handleForceLogout();
        }
    }

    @Override
    public void onPlayerDisconnected(String username) {
        System.out.println("[ClientCallbackImpl] Player disconnected: " + username + " for token: " + playerToken);
        Platform.runLater(() -> {
            if (gameController != null) {
                gameController.handlePlayerDisconnected(username);
            }
        });
    }
}