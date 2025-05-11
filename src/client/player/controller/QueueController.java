package client.player.controller;

import client.login.controller.LoginController;
import client.player.ClientCallbackImpl;
import client.player.model.QueueModel;
import client.player.view.MainMenuView;
import client.player.view.QueueView;
import WordWarZ.GameService;
import WordWarZ.NoOpponentFound;
import WordWarZ.NotLoggedIn;
import WordWarZ.PlayerNotInQueue;
import javafx.scene.control.Alert;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueController {
    private final QueueModel model;
    private final QueueView view;
    private final String playerToken;
    private final ORB orb;
    private final Stage stage;
    private final int selectedCharacter;
    private ClientCallbackImpl callbackImpl;

    public QueueController(String playerToken, ORB orb, Stage stage, int selectedCharacter) {
        this.orb = orb;
        this.playerToken = playerToken;
        this.model = new QueueModel(playerToken, orb);
        this.view = new QueueView();
        this.stage = stage;
        this.selectedCharacter = selectedCharacter;
        System.out.println("[QueueController] Initializing for token: " + playerToken);

        initializeListeners();
        view.start(stage);
        stage.show();
        view.setCancelButtonHandler(this::handleCancelQueue);

        try {
            model.startGame();
        } catch (NotLoggedIn e) {
            System.err.println("[QueueController] Not logged in for token: " + playerToken + ": " + e.getMessage());
            transitionToLoginScreen();
        } catch (Exception e) {
            System.err.println("[QueueController] Unexpected error starting game for token: " + playerToken + ": " + e.getMessage());
        }
    }

    private void initializeListeners() {
        try {
            org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
            POA rootPOA = POAHelper.narrow(obj);
            rootPOA.the_POAManager().activate();

            callbackImpl = new ClientCallbackImpl(view, stage, playerToken, orb, selectedCharacter, this);
            org.omg.CORBA.Object callbackObj = rootPOA.servant_to_reference(callbackImpl);
            WordWarZ.ClientCallback callback = WordWarZ.ClientCallbackHelper.narrow(callbackObj);
            GameService gameService = model.getGameService();

            gameService.registerCallback(playerToken, callback);
            System.out.println("[QueueController] Callback registered for token: " + playerToken);
        } catch (Exception e) {
            System.err.println("[QueueController] Error initializing listeners for token: " + playerToken + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleCancelQueue(ActionEvent event) {
        System.out.println("[QueueController] Cancel queue requested for token: " + playerToken);
        try {
            GameService gameService = model.getGameService();
            gameService.cancelQueue(playerToken);
            System.out.println("[QueueController] Player removed from queue for token: " + playerToken);

            gameService.unregisterCallback(playerToken);
            System.out.println("[QueueController] Callback unregistered for token: " + playerToken);

            transitionToMainMenu();
        } catch (NotLoggedIn e) {
            System.err.println("[QueueController] Not logged in when canceling queue for token: " + playerToken + ": " + e.getMessage());
            transitionToLoginScreen();
        } catch (PlayerNotInQueue e) {
            System.err.println("[QueueController] Player not in queue for token: " + playerToken + ": " + e.getMessage());
            transitionToMainMenu();
        } catch (Exception e) {
            System.err.println("[QueueController] Unexpected error canceling queue for token: " + playerToken + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleNoOpponentFound() {
        System.out.println("[QueueController] Handling no opponent found for token: " + playerToken);
        Platform.runLater(() -> {
            view.showNoOpponentMessage();
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> Platform.runLater(() -> {
                System.out.println("[QueueController] Transitioning to main menu after no opponent for token: " + playerToken);
                transitionToMainMenu();
            }), 2, TimeUnit.SECONDS);
            scheduler.shutdown();
        });
    }

    private void transitionToMainMenu() {
        System.out.println("[QueueController] Transitioning to MainMenuView for token: " + playerToken);
        Platform.runLater(() -> {
            try {
                view.close();
                System.out.println("[QueueController] QueueView closed for token: " + playerToken);
                MainMenuView mainMenuView = new MainMenuView();
                mainMenuView.initializeUI(stage);
                MainMenuController mainMenuController = new MainMenuController(playerToken, orb, mainMenuView, stage);

                if (callbackImpl != null) {
                    callbackImpl.setMainMenuController(mainMenuController);
                }

                stage.setTitle("Word War Z - Main Menu");
                stage.show();
                System.out.println("[QueueController] MainMenuView opened for token: " + playerToken);
            } catch (Exception e) {
                System.err.println("[QueueController] Error transitioning to main menu: " + e.getMessage());
            }
        });
    }

    private void transitionToLoginScreen() {
        System.out.println("[QueueController] Transitioning to login screen for token: " + playerToken);
        Platform.runLater(() -> {
            view.close();
            stage.close();
            System.out.println("[QueueController] Login screen transition completed for token: " + playerToken);
        });
    }

    public void handleForceLogout() {
        Platform.runLater(() -> {
            try {
                // Close current queue window
                if (view != null) {
                    view.close();
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

    public Stage getStage() {
        return stage;
    }
}