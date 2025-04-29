package client.player.controller;

import client.player.ClientCallbackImpl;
import client.player.model.QueueModel;
import client.player.view.QueueView;
import WordWarZ.GameService;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import javafx.stage.Stage;

public class QueueController {
    private final QueueModel model;
    private final QueueView view;
    private final String playerToken;
    private final ORB orb;
    private Stage stage;

    public QueueController(String token, ORB orb) {
        this.orb = orb;
        this.playerToken = token;
        this.model = new QueueModel(token, orb);
        this.view = new QueueView();

        // Initialize listener for server callbacks first
        initializeListeners();

        // Then join queue
        model.startGame();
        checkQueueSize();

        // Show the QueueView
        this.stage = new Stage();
        view.start(stage);
    }

    private void initializeListeners() {
        try {
            // Get the root POA
            org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
            POA rootPOA = POAHelper.narrow(obj);
            rootPOA.the_POAManager().activate();

            // Create callback implementation
            ClientCallbackImpl callbackImpl = new ClientCallbackImpl(view, stage, playerToken, orb);

            // Activate the callback object
            org.omg.CORBA.Object callbackObj = rootPOA.servant_to_reference(callbackImpl);

            // Narrow to ClientCallback interface
            WordWarZ.ClientCallback callback = WordWarZ.ClientCallbackHelper.narrow(callbackObj);

            // Get GameService from model
            GameService gameService = model.getGameService();

            // Register callback with the server
            gameService.registerCallback(playerToken, callback);

            System.out.println("Callback registered successfully for token: " + playerToken);

        } catch (Exception e) {
            System.err.println("Error initializing listeners: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkQueueSize() {
        try {
            int size = model.getGameService().getPlayersInQueue(playerToken);
            System.out.println("Queue size after joining: " + size);
        } catch (Exception e) {
            System.err.println("Error checking queue size: " + e.getMessage());
        }
    }

    public Stage getStage() {
        return stage;
    }
}