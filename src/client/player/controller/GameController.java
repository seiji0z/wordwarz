package client.player.controller;
import client.player.model.GameModel;
import client.player.model.MainMenuModel;
import client.player.view.GameView;
import client.player.view.MainMenuView;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.omg.CORBA.ORB;
import server.objects.Game;

import java.util.Arrays;

public class GameController {
    private GameModel model;
    private GameView view;
    private final String playerToken;
    private final ORB orb;
    private boolean roundActive = false;

    public GameController(String token, ORB orb) {
        this.orb = orb;
        this.playerToken = token;
        this.model = new GameModel(token, orb);
        this.view = new GameView(new Stage());
    }

    public void onGameStart(String[] wordPlaceholder) {
        if (roundActive) return;
        roundActive = true;

        Platform.runLater(() -> {
            view.showOverlayWithTimer(() -> {
                try {
                    view.initializeWordDisplay(wordPlaceholder.length);
                    model.startRound();
                    view.startTimer(model.getRoundDuration());
                } catch (Exception e) {
                    view.showErrorMessage("Failed to start round: " + e.getMessage());
                } finally {
                    roundActive = false;
                }
            });
        });
    }
}