package client.admin.controller;

import WordWarZ.*;
import client.admin.model.AdminDashboardModel;
import client.admin.view.AdminDashboardView;
import client.admin.view.CreatePlayerView;
import client.admin.view.UpdatePlayerView;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.omg.CORBA.ORB;

public class AdminDashboardController {
    private final AdminDashboardView view;
    private final AdminDashboardModel model;
    private final ORB orb;
    private final String token;

    public AdminDashboardController(AdminDashboardView view, AdminDashboardModel model, ORB orb, String token) {
        this.view = view;
        this.model = model;
        this.orb = orb;
        this.token = token;

        Platform.runLater(() -> {
            initializeView();
            setupEventHandlers();
        });
    }

    private void initializeView() {
        Stage adminStage = new Stage();
        view.initializeUI(adminStage);
    }

    private void setupEventHandlers() {
        // Main dashboard buttons
        view.getEditPlayerBtn().setOnAction(e -> view.showCreatePlayerView());
        view.getEditGamePlayBtn().setOnAction(e -> handleEditGamePlay());

        // Create Player View handlers
        CreatePlayerView createView = view.getCreatePlayerView();
        createView.getCreateBtn().setOnAction(e -> view.showCreatePlayerView());
        // createView.getReadBtn().setOnAction(e -> handleReadPlayers());
        createView.getUpdateBtn().setOnAction(e -> view.showUpdatePlayerView());
        createView.getDeleteBtn().setOnAction(e -> view.showUpdatePlayerView());
        createView.getConfirmBtn().setOnAction(e -> handleCreatePlayer());

        // Update Player View handlers
        UpdatePlayerView updateView = view.getUpdatePlayerView();
        updateView.getCreatePlayerBtn().setOnAction(e -> view.showCreatePlayerView());
        // updateView.getReadPlayersBtn().setOnAction(e -> handleReadPlayers());
        updateView.getUpdateButton().setOnAction(e -> view.showUpdatePlayerView());
        // updateView.getDeletePlayerBtn().setOnAction(e -> handleDeletePlayer());
        // updateView.getSearchButton().setOnAction(e -> handleSearchPlayer());
        updateView.getConfirmBtn().setOnAction(e -> handleUpdatePlayer());
    }

    private void handleEditGamePlay() {
        System.out.println("Edit Game Play clicked");
    }

    private void handleCreatePlayer() {
        CreatePlayerView createView = view.getCreatePlayerView();
        String username = createView.getUsernameField().getText();
        String password = createView.getPasswordField().getText();

        try {
            model.createPlayer(username, password);
            createView.showSuccess("Player created successfully");
            createView.clearFields();
        } catch (NotLoggedIn e) {
            createView.showError("Error: Admin not logged in");
        } catch (UsernameAlreadyExists e) {
            createView.showError("Error: Username already exists");
        }
    }

    private void handleUpdatePlayer() {
        UpdatePlayerView updateView = view.getUpdatePlayerView();
        String username = updateView.getSearchField().getText();  // username to update
        String newPassword = updateView.getPasswordField().getText();  // new password only

        try {
            model.updatePlayer(username, newPassword);  // fix: only username and password
            updateView.showSuccess("Player password updated successfully");
        } catch (NotLoggedIn e) {
            updateView.showError("Error: Admin not logged in");
        } catch (PlayerNotFound e) {
            updateView.showError("Error: Player not found");
        } catch (PlayerCurrentlyLoggedIn e) {
            updateView.showError("Error: Player is currently logged in");
        }
    }


//    private void handleDeletePlayer() {
//        UpdatePlayerView updateView = view.getUpdatePlayerView();
//        String username = updateView.getSearchField().getText();
//
//        try {
//            model.deletePlayer(username);
//            updateView.showSuccess("Player deleted successfully");
//        } catch (NotLoggedIn e) {
//            updateView.showError("Error: Admin not logged in");
//        } catch (PlayerNotFound e) {
//            updateView.showError("Error: Player not found");
//        } catch (PlayerCurrentlyLoggedIn e) {
//            updateView.showError("Error: Player is currently logged in");
//        }
//    }

//    private void handleSearchPlayer() {
//        UpdatePlayerView updateView = view.getUpdatePlayerView();
//        String query = updateView.getSearchField().getText();
//
//        try {
//            Player[] players = model.searchPlayers(query);
//            updateView.getUserDropdown().getItems().clear();
//            for (Player player : players) {
//                updateView.getUserDropdown().getItems().add(player.username);
//            }
//        } catch (NotLoggedIn e) {
//            updateView.showError("Error: Admin not logged in");
//        } catch (PlayerNotFound e) {
//            updateView.showError("Error: No players found");
//        }
//    }

//    private void handleReadPlayers() {
//        try {
//            Player[] players = model.searchPlayers(""); // Empty query to get all players
//            // You might want to show these in a dialog or table
//            System.out.println("Players:");
//            for (Player player : players) {
//                System.out.println(player.username);
//            }
//        } catch (NotLoggedIn | PlayerNotFound e) {
//            view.getCreatePlayerView().showError("Error retrieving players");
//        }
//    }
}