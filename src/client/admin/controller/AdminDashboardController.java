package client.admin.controller;

import WordWarZ.*;
import client.admin.model.AdminDashboardModel;
import client.admin.view.*;
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
        view.initializeUI(adminStage, model, orb, token);
    }

    private void setupEventHandlers() {
        // Main dashboard buttons
        view.getEditPlayerBtn().setOnAction(e -> view.showEditPlayerView());
        view.getEditGamePlayBtn().setOnAction(e -> handleEditGamePlay());

        // Create Player View handlers
        CreatePlayerView createView = view.getCreatePlayerView();
        createView.getCreatePlayerBtn().setOnAction(e -> view.showCreatePlayerView());
        createView.getEditPlayerBtn().setOnAction(e -> view.showEditPlayerView());

        // Edit Player View handlers
        EditPlayerView editView = view.getEditPlayerView();
        editView.getCreatePlayerBtn().setOnAction(e -> view.showCreatePlayerView());
        editView.getEditPlayerBtn().setOnAction(e -> view.showEditPlayerView());
        editView.getSearchButton().setOnAction(e -> handleSearchPlayers(editView.getSearchField().getText()));
        editView.getClearBtn().setOnAction(e -> editView.getSearchField().clear());
        editView.getConfirmUpdateBtn().setOnAction(e -> handleUpdatePlayer());
        editView.getConfirmDeleteBtn().setOnAction(e -> handleDeletePlayer());

        editView.getSearchField().textProperty().addListener((obs, oldValue, newValue) -> {
            handleSearchPlayers(newValue);
        });

        // Load players automatically when view is shown
        view.setOnShowEditPlayerViewListener(this::handleReadAllPlayers);
    }

    private void handleEditGamePlay() {
        try {
            view.showEditGameplaySettingsView();
            view.getEditGamePlaySettingsView().getController().loadCurrentSettings();
        } catch (Exception e) {
            System.err.println("Error loading gameplay settings: " + e.getMessage());
        }
    }

    private void handleCreatePlayer() {
        CreatePlayerView createView = view.getCreatePlayerView();
        String username = createView.getUsernameField().getText();
        String password = createView.getPasswordField().getText();

        try {
            model.createPlayer(username, password);
            createView.showSuccess("Player created successfully");
            createView.clearFields();
            handleReadAllPlayers(); // Refresh the player list
        } catch (NotLoggedIn e) {
            createView.showError("Error: Admin not logged in");
        } catch (UsernameAlreadyExists e) {
            createView.showError("Error: Username already exists");
        }
    }

    private void handleUpdatePlayer() {
        EditPlayerView editView = view.getEditPlayerView();
        String newUsername = editView.getUsernameField().getText().trim();
        String newPassword = editView.getPasswordField().getText().trim();

        Player selectedPlayer = editView.getPlayerTable().getSelectionModel().getSelectedItem();
        if (selectedPlayer == null) {
            editView.showError("Please select a player to update");
            return;
        }

        String currentUsername = selectedPlayer.username;

        if (newUsername.isEmpty() && newPassword.isEmpty()) {
            editView.showError("Please enter at least one field to update");
            return;
        }

        try {
            if (editView.showUpdateConfirmation(currentUsername)) {
                model.updatePlayer(currentUsername, newUsername, newPassword);
                editView.showSuccess("Player updated successfully");
                editView.getPasswordField().clear();
                editView.getUsernameField().clear();
                editView.hideUpdateForm();
                handleReadAllPlayers(); // Refresh the table
            }
        } catch (NotLoggedIn e) {
            editView.showError("Error: Admin not logged in");
        } catch (PlayerNotFound e) {
            editView.showError("Error: Player not found");
        } catch (PlayerCurrentlyLoggedIn e) {
            editView.showError("Error: Player is currently logged in");
        } catch (Exception e) {
            editView.showError("Unexpected error: " + e.getMessage());
        }
    }

    private void handleDeletePlayer() {
        EditPlayerView editView = view.getEditPlayerView();
        Player selectedPlayer = editView.getPlayerTable().getSelectionModel().getSelectedItem();

        if (selectedPlayer == null) {
            editView.showError("Please select a player to delete");
            return;
        }

        String username = selectedPlayer.username;

        try {
            if (editView.showDeleteConfirmation(username)) {
                model.deletePlayer(username);
                editView.showSuccess("Player deleted successfully");
                editView.hideUpdateForm();
                handleReadAllPlayers(); // Refresh the table
            }
        } catch (NotLoggedIn e) {
            editView.showError("Error: Admin not logged in");
        } catch (PlayerNotFound e) {
            editView.showError("Error: Player not found");
        } catch (PlayerCurrentlyLoggedIn e) {
            editView.showError("Error: Player is currently logged in");
        } catch (Exception e) {
            editView.showError("Unexpected error: " + e.getMessage());
        }
    }

    private void handleReadAllPlayers() {
        EditPlayerView editView = view.getEditPlayerView();
        try {
            Player[] players = model.getAllPlayers();
            editView.displayPlayers(players);
        } catch (NotLoggedIn e) {
            editView.showError("Error: Admin not logged in");
        } catch (PlayerNotFound e) {
            editView.showError("Error: No players found");
        }
    }

    private void handleSearchPlayers(String searchText) {
        EditPlayerView editView = view.getEditPlayerView();
        try {
            Player[] players = model.searchPlayers(searchText.trim());
            editView.displayPlayers(players);
        } catch (NotLoggedIn e) {
            editView.showError("Error: Admin not logged in");
        } catch (PlayerNotFound e) {
            editView.showError("Error: No players found matching the search");
        }
    }
}