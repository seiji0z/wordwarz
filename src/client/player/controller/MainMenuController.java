package client.player.controller;

import WordWarZ.Player;
import client.player.model.MainMenuModel;
import client.player.view.LeaderboardView;
import client.player.view.MainMenuView;
import javafx.application.Platform;
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
    }

    // Constructor for QueueController (uses existing view and stage)
    public MainMenuController(String token, ORB orb, MainMenuView view, Stage stage) {
        this.orb = orb;
        this.playerToken = token;
        this.model = new MainMenuModel(token, orb);
        this.view = view;
        this.stage = stage;
        setupEventHandlers();
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
                leaderboardStage = new Stage();
                leaderboardView = new LeaderboardView();
                leaderboardView.initializeUI(leaderboardStage); // Initialize first

                // Update the leaderboard with current data after initialization
                if (leaderboardData != null) {
                    leaderboardView.updateLeaderboard(Arrays.asList(leaderboardData));
                }

                leaderboardStage.setOnCloseRequest(e -> leaderboardStage = null);
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
}