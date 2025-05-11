package client.player.controller;

import WordWarZ.Player;
import client.login.controller.LoginController;
import client.player.ClientCallbackImpl;
import client.player.model.MainMenuModel;
import client.player.view.LeaderboardView;
import client.player.view.MainMenuView;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.omg.CORBA.ORB;

import java.util.Arrays;

public class MainMenuController {
    private final MainMenuModel model;
    private final MainMenuView view;
    private final String playerToken;
    private final ORB orb;
    private final Stage stage;
    private Stage leaderboardStage;
    private LeaderboardView leaderboardView;
    private ClientCallbackImpl callbackImpl;

    // Constructor for LoginController (creates new view and stage)
    public MainMenuController(String token, ORB orb) {
        this.orb = orb;
        this.playerToken = token;
        this.model = new MainMenuModel(token, orb);
        this.view = new MainMenuView();
        this.leaderboardView = new LeaderboardView();
        this.stage = new Stage();
        view.initializeUI(stage);
        setupEventHandlers();
        registerCallback();
    }

    // Constructor for QueueController (uses existing view and stage)
    public MainMenuController(String token, ORB orb, MainMenuView view, Stage stage) {
        this.orb = orb;
        this.playerToken = token;
        this.model = new MainMenuModel(token, orb);
        this.view = view;
        this.stage = stage;
        setupEventHandlers();
        registerCallback();
    }


    private void registerCallback() {
        try {
            org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
            org.omg.PortableServer.POA rootPOA = org.omg.PortableServer.POAHelper.narrow(obj);
            rootPOA.the_POAManager().activate();

            callbackImpl = new ClientCallbackImpl(null, stage, playerToken, orb, 0, null);
            callbackImpl.setMainMenuController(this);

            org.omg.CORBA.Object callbackObj = rootPOA.servant_to_reference(callbackImpl);
            WordWarZ.ClientCallback callback = WordWarZ.ClientCallbackHelper.narrow(callbackObj);
            model.getGameService().registerCallback(playerToken, callback);
            System.out.println("[MainMenuController] Callback registered for token: " + playerToken);
        } catch (Exception e) {
            System.err.println("[MainMenuController] Error registering callback: " + e.getMessage());
        }
    }

    private void setupEventHandlers() {
        view.setPlayButtonHandler(() -> {
            System.out.println("Play button clicked - starting game");
            startGame();
        });

        view.setLeaderboardButtonHandler(this::handleLeaderboard);
        view.setHowToPlayButtonHandler(this::handleHowToPlay);
        view.setQuitButtonHandler(this::handleQuit);
        view.setSoundToggleHandler(this::handleSoundToggle);
    }

    // In MainMenuController.java
    private void startGame() {
        System.out.println("Attempting to start game...");
        int selectedChar = view.getSelectedCharacterIndex();
        new QueueController(playerToken, orb, stage, selectedChar);
    }


    private void handleLeaderboard() {
        // Get leaderboard data from model
        Player[] leaderboardData = model.getLeaderboard();

        Platform.runLater(() -> {
            if (leaderboardStage == null) {
                leaderboardStage = stage; // Use the same stage as main menu
                leaderboardView = new LeaderboardView();
                leaderboardView.initializeUI(leaderboardStage);

                // Set the refresh button handler
                leaderboardView.setRefreshButtonHandler(() -> {
                    Player[] refreshedData = model.getLeaderboard();
                    if (refreshedData != null) {
                        leaderboardView.updateLeaderboard(Arrays.asList(refreshedData));
                    }
                });

                if (leaderboardData != null) {
                    leaderboardView.updateLeaderboard(Arrays.asList(leaderboardData));
                }

                leaderboardStage.setOnCloseRequest(e -> {
                    leaderboardStage.hide(); // Hide instead of null to reuse
                    leaderboardStage.setScene(null); // Clear the scene
                });

                // Store the leaderboard scene to reuse
                Scene leaderboardScene = leaderboardStage.getScene();
                leaderboardStage.setScene(leaderboardScene);
                leaderboardStage.show();
            } else {
                // If stage already exists, just update the data
                if (leaderboardData != null) {
                    leaderboardView.updateLeaderboard(Arrays.asList(leaderboardData));
                }
                leaderboardStage.show();
            }
        });
    }

    private void handleHowToPlay() {
        view.showHowToPlay();
    }

    private void handleQuit() {
        try {
            model.logout();
            Platform.exit();
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            Platform.exit();
        }
    }

    private void handleSoundToggle(boolean isMuted) {
        System.out.println("Sound is now " + (isMuted ? "muted" : "unmuted"));
    }

    public void handleForceLogout() {
        Platform.runLater(() -> {
            try {
                // Close current game window
                if (view != null) {
                    view.closeApplication();
                }

                // Close leaderboard if open
                if (leaderboardStage != null) {
                    leaderboardStage.close();
                }

                new LoginController(orb);

                // Show alert message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Session Expired");
                alert.setHeaderText("You have been logged out");
                alert.setContentText("Your session has expired or you were logged out from another device.");
                alert.showAndWait();
            } catch (Exception e) {
                System.err.println("Error handling force logout: " + e.getMessage());
            }
        });
    }
}