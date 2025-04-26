package client.player.controller;

import client.player.model.MainMenuModel;
import client.player.view.MainMenuView;
import javafx.application.Platform;
import org.omg.CORBA.ORB;
import server.helpers.SessionManager;

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

        Platform.runLater(() -> {
            initializeView();
            setupEventHandlers();
        });
    }

    private void initializeView() {
    }

    private void setupEventHandlers() {
        view.setPlayButtonHandler(this::startGame);
        view.setLeaderboardButtonHandler(this::handleLeaderboard);
        view.setHowToPlayButtonHandler(this::handleHowToPlay);
        view.setQuitButtonHandler(this::handleQuit);
        view.setSoundToggleHandler(this::handleSoundToggle);
    }

    private void startGame() {
        // implement saenz
    }

    private void handleLeaderboard() {
        model.getLeaderboard();
    }

    private void handleHowToPlay() {
        view.showHowToPlay();
    }

    private void handleQuit() {
        SessionManager.removeSession(playerToken);
        System.exit(0);
    }

    private void handleSoundToggle(boolean isMuted) {
        System.out.println("Sound is now " + (isMuted ? "muted" : "unmuted"));
    }
}
