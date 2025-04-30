package client.player.controller;

import client.player.ClientCallbackImpl;
import client.player.model.QueueModel;
import client.player.view.MainMenuView;
import client.player.view.QueueView;
import WordWarZ.GameService;
import WordWarZ.NotLoggedIn;
import WordWarZ.PlayerNotInQueue;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class QueueController {
    private final QueueModel model;
    private final QueueView view;
    private final String playerToken;
    private final ORB orb;
    private final Stage stage;

    public QueueController(String token, ORB orb, Stage stage) {
        this.orb = orb;
        this.playerToken = token;
        this.model = new QueueModel(token, orb);
        this.view = new QueueView();
        this.stage = stage;

        // Initialize listener for server callbacks
        initializeListeners();

        // Show the QueueView and close MainMenuView
        view.start(stage);
        stage.show(); // Ensure stage is visible

        // Set cancel button handler after start
        view.setCancelButtonHandler(this::handleCancelQueue);

        // Join queue
        model.startGame();
    }

    private void initializeListeners() {
        try {
            org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
            POA rootPOA = POAHelper.narrow(obj);
            rootPOA.the_POAManager().activate();

            ClientCallbackImpl callbackImpl = new ClientCallbackImpl(view, stage, playerToken, orb);
            org.omg.CORBA.Object callbackObj = rootPOA.servant_to_reference(callbackImpl);
            WordWarZ.ClientCallback callback = WordWarZ.ClientCallbackHelper.narrow(callbackObj);
            GameService gameService = model.getGameService();

            gameService.registerCallback(playerToken, callback);
            System.out.println("Callback registered successfully for token: " + playerToken);

        } catch (Exception e) {
            System.err.println("Error initializing listeners: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleCancelQueue(ActionEvent event) {
        try {
            GameService gameService = model.getGameService();
            gameService.cancelQueue(playerToken);
            System.out.println("Player removed from queue for token: " + playerToken);

            gameService.unregisterCallback(playerToken);
            System.out.println("Callback unregistered for token: " + playerToken);

            // Close QueueView stage
            stage.close();

            // Open MainMenuView in a new stage
            Stage mainMenuStage = new Stage();
            MainMenuView mainMenuView = new MainMenuView();
            MainMenuController mainMenuController = new MainMenuController(playerToken, orb);
            mainMenuView.initializeUI(mainMenuStage);
            mainMenuStage.setTitle("Word War Z - Main Menu");

        } catch (NotLoggedIn e) {
            System.err.println("Error canceling queue: Player not logged in for token: " + playerToken);
            transitionToLoginScreen();
        } catch (PlayerNotInQueue e) {
            System.err.println("Error canceling queue: Player not in queue for token: " + playerToken);
            transitionToMainMenu();
        } catch (Exception e) {
            System.err.println("Unexpected error canceling queue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void transitionToMainMenu() {
        stage.close();
        Stage mainMenuStage = new Stage();
        MainMenuView mainMenuView = new MainMenuView();
        MainMenuController mainMenuController = new MainMenuController(playerToken, orb);
        mainMenuView.initializeUI(mainMenuStage);
        mainMenuStage.setTitle("Word War Z - Main Menu");
    }

    private void transitionToLoginScreen() {
        stage.close();
        System.out.println("Transitioning to login screen (not implemented)");
    }

    public Stage getStage() {
        return stage;
    }
}