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
        view.getEditPlayerBtn().setOnAction(e -> view.showCreatePlayerView());
        view.getEditGamePlayBtn().setOnAction(e -> handleEditGamePlay());

        // Create Player View handlers
        CreatePlayerView createView = view.getCreatePlayerView();
        createView.getCreateBtn().setOnAction(e -> view.showCreatePlayerView());
        createView.getReadBtn().setOnAction(e -> view.showReadPlayerView());
        createView.getUpdateBtn().setOnAction(e -> view.showUpdatePlayerView());
        createView.getDeleteBtn().setOnAction(e -> view.showDeletePlayerView());
        createView.getConfirmBtn().setOnAction(e -> handleCreatePlayer());

        // Update Player View handlers
        UpdatePlayerView updateView = view.getUpdatePlayerView();
        updateView.getCreatePlayerBtn().setOnAction(e -> view.showCreatePlayerView());
        updateView.getReadPlayersBtn().setOnAction(e -> view.showReadPlayerView());
        updateView.getUpdateButton().setOnAction(e -> view.showUpdatePlayerView());
        updateView.getConfirmBtn().setOnAction(e -> handleUpdatePlayer());

        // Read Player View handlers
        ReadPlayerView readView = view.getReadPlayerView();
        readView.getCreateBtn().setOnAction(e -> view.showCreatePlayerView());
        readView.getReadBtn().setOnAction(e -> handleReadAllPlayers());
        readView.getUpdateBtn().setOnAction(e -> view.showUpdatePlayerView());
        readView.getDeleteBtn().setOnAction(e -> view.showDeletePlayerView());

        // Delete Player View handlers
        DeletePlayerView deleteView = view.getDeletePlayerView();
        deleteView.getCreateBtn().setOnAction(e -> view.showCreatePlayerView());
        deleteView.getReadBtn().setOnAction(e -> view.showReadPlayerView());
        deleteView.getUpdateBtn().setOnAction(e -> view.showUpdatePlayerView());
        deleteView.getDeleteBtn().setOnAction(e -> view.showDeletePlayerView());
        deleteView.getPlayerTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                deleteView.getSearchField().setText(newSelection.username);
            }
        });
        deleteView.getSearchDeleteBtn().setOnAction(e -> {
            String username = deleteView.getSearchField().getText().trim();
            if (!username.isEmpty() && deleteView.showDeleteConfirmation(username)) {
                handleDeletePlayer();
            }
        });
        deleteView.getSearchField().textProperty().addListener((obs, oldValue, newValue) -> {
            handleSearchPlayers(newValue);
        });

        // Load players automatically when view is shown
        view.setOnShowReadPlayerViewListener(this::handleReadAllPlayers);
        view.setOnShowDeletePlayerViewListener(this::handleReadAllPlayers);
    }

    private void handleEditGamePlay() {
        try {
            // Show the EditGamePlaySettingsView
            view.showEditGameplaySettingsView();
            // Load the current settings
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

        if (username.isEmpty()) {
            updateView.showError("Please enter a username to update");
            return;
        }
        if (newUsername.isEmpty() && newPassword.isEmpty()) {
            updateView.showError("Please enter at least one field to update");
            return;
        }

        try {
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

    private void handleDeletePlayer() {
        DeletePlayerView deleteView = view.getDeletePlayerView();
        String username = deleteView.getSearchField().getText().trim();

        if (username.isEmpty()) {
            deleteView.showError("Please enter a username to delete");
            return;
        }

        try {
            model.deletePlayer(username);
            deleteView.showSuccess("Player deleted successfully");
            deleteView.getSearchField().clear();
            handleReadAllPlayers(); // Refresh the table
        } catch (NotLoggedIn e) {
            deleteView.showError("Error: Admin not logged in");
        } catch (PlayerNotFound e) {
            deleteView.showError("Error: Player not found");
        } catch (PlayerCurrentlyLoggedIn e) {
            deleteView.showError("Error: Player is currently logged in");
        } catch (Exception e) {
            deleteView.showError("Unexpected error: " + e.getMessage());
        }
    }

    private void handleReadAllPlayers() {
        ReadPlayerView readView = view.getReadPlayerView();
        try {
            Player[] players = model.getAllPlayers();
            readView.displayPlayers(players);
        } catch (NotLoggedIn e) {
            readView.showError("Error: Admin not logged in");
        } catch (PlayerNotFound e) {
            readView.showError("Error: No players found");
        }
    }
    private void handleSearchPlayers(String searchText) {
        ReadPlayerView readView = view.getReadPlayerView();
        DeletePlayerView deleteView = view.getDeletePlayerView();
        try {
            Player[] players = model.searchPlayers(searchText.trim());
            readView.displayPlayers(players);
            deleteView.displayPlayers(players);
        } catch (NotLoggedIn e) {
            readView.showError("Error: Admin not logged in");
            deleteView.showError("Error: Admin not logged in");
        } catch (PlayerNotFound e) {
            readView.showError("Error: No players found matching the search");
            deleteView.showError("Error: No players found matching the search");
        }
    }
}