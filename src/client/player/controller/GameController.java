package client.player.controller;
import client.player.model.GameModel;
import client.player.model.MainMenuModel;
import client.player.view.GameView;
import client.player.view.MainMenuView;
import org.omg.CORBA.ORB;
import server.objects.Game;

import java.util.Arrays;

public class GameController {
    private GameModel model;
    private GameView view;
    private final String playerToken;
    private final ORB orb;

    public GameController(String token, ORB orb) {
        this.orb = orb;
        this.playerToken = token;
        this.model = new GameModel(token, orb);
        this.view = new GameView();
    }
}