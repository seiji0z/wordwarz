package client.player.controller;

import client.player.model.MainMenuModel;
import client.player.view.MainMenuView;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.omg.CORBA.ORB;

public class MainMenuController {
    private final MainMenuModel model;
    private final MainMenuView view;
    private final String playerToken;
    private final ORB orb;

    public MainMenuController(String token, ORB orb) {
        this.orb = orb;
        this.playerToken = token;
        this.model = new MainMenuModel(token, orb);
        this.view = new MainMenuView();

        // Initialize the view before setting handlers
        view.initializeUI(new Stage());
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        // Connect the play button to startGame
        view.setPlayButtonHandler(() -> {
            System.out.println("Play button clicked - starting game");
            startGame();
        });

        view.setLeaderboardButtonHandler(this::handleLeaderboard);
        view.setHowToPlayButtonHandler(this::handleHowToPlay);
        view.setQuitButtonHandler(this::handleQuit);
        view.setSoundToggleHandler(this::handleSoundToggle);
    }

    private void startGame() {
        System.out.println("Attempting to start game...");
        new QueueController(playerToken, orb);

    }

    private void handleLeaderboard() {
        model.getLeaderboard();
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
