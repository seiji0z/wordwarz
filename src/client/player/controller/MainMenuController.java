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
    private final Stage stage;

    // Constructor for LoginController (creates new view and stage)
    public MainMenuController(String token, ORB orb) {
        this.orb = orb;
        this.playerToken = token;
        this.model = new MainMenuModel(token, orb);
        this.view = new MainMenuView();
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

    private void startGame() {
        System.out.println("Attempting to start game...");
        new QueueController(playerToken, orb, stage);
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