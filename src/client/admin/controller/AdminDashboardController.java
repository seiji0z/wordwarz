package client.admin.controller;

import WordWarZ.*;
import client.admin.model.AdminDashboardModel;
import client.admin.view.AdminDashboardView;
import client.admin.view.CreatePlayerView;
import client.admin.view.EditGamePlaySettingsView;
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
        view.getEditGamePlayBtn().setOnAction(e -> view.showEditGameplaySettingsView());

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
        System.out.println("handleEditGamePlay");
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
        String username = updateView.getSearchField().getText().trim();
        String newPassword = updateView.getPasswordField().getText().trim();
        String newUsername = updateView.getUsernameField().getText().trim();

        // Input validation
        if (username.isEmpty()) {
            updateView.showError("Please enter a username to update");
            return;
        }
        if (newUsername.isEmpty() && newPassword.isEmpty()) {
            updateView.showError("Please enter at least one field to update");
            return;
        }

        try {
            // Correct parameter order
            model.updatePlayer(username, newUsername, newPassword);
            updateView.showSuccess("Player updated successfully");
            updateView.getPasswordField().clear();
            updateView.getUsernameField().clear();
        } catch (NotLoggedIn e) {
            updateView.showError("Error: Admin not logged in");
        } catch (PlayerNotFound e) {
            updateView.showError("Error: Player not found");
        } catch (PlayerCurrentlyLoggedIn e) {
            updateView.showError("Error: Player is currently logged in");
        } catch (Exception e) {
            updateView.showError("Unexpected error: " + e.getMessage());
        }
    }
}