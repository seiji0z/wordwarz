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
        createView.getDeleteBtn().setOnAction(e -> view.showUpdatePlayerView());
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
        readView.getDeleteBtn().setOnAction(e -> view.showUpdatePlayerView());


        // Load players automatically when view is shown
        view.setOnShowReadPlayerViewListener(this::handleReadAllPlayers);
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
            updateView.showError("Please enter at least PRESSone field to update");
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


    private void handleReadAllPlayers() {
        try {
            Player[] players = model.getAllPlayers();
            view.getReadPlayerView().updatePlayerTable(players);
        } catch (NotLoggedIn e) {
            view.getReadPlayerView().showError("Error: Admin not logged in");
        } catch (PlayerNotFound e) {
            view.getReadPlayerView().showError("No players found in database");
        }
    }
}

