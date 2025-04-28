package client.player.controller;

import client.player.model.QueueModel;
import client.player.view.QueueView;
import org.omg.CORBA.ORB;

public class QueueController {
    private final QueueModel model;
    private final QueueView view;
    private final String playerToken;
    private final ORB orb;

    public QueueController(String token, ORB orb) {
        this.orb = orb;
        this.playerToken = token;
        this.model = new QueueModel(token, orb);
        this.view = new QueueView();


        // When controller starts, join queue
        model.startGame();

        // Initialize listener for future server callbacks (onQueueUpdated, etc.)
        initializeListeners();
    }

    private void initializeListeners() {
        // TODO: Setup callback handling
        // When server sends onQueueUpdated, call view.updatePlayerCount()
        // When server sends onGameCountdown, call view.updateTimer()
        // When server sends onRoundStarted, transition to game screen
    }

}
