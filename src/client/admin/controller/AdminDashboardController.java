package client.admin.controller;

import WordWarZ.*;
import client.admin.model.AdminDashboardModel;
import client.admin.model.EditGamePlaySettingsModel;
import client.admin.view.*;
import client.login.controller.LoginController;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.omg.CORBA.ORB;

public class AdminDashboardController {
    private final AdminDashboardView view;
    private final AdminDashboardModel model;
    private final ORB orb;
    private final String token;
    private final Stage adminStage;
    private final LoginController loginController;
    private EditGamePlaySettingsController editGamePlaySettingsController;

    public AdminDashboardController(AdminDashboardView view, AdminDashboardModel model,
                                    ORB orb, String token, Stage adminStage,
                                    LoginController loginController) {
        this.view = view;
        this.model = model;
        this.orb = orb;
        this.token = token;
        this.adminStage = adminStage;
        this.loginController = loginController;

        Platform.runLater(() -> {
            initializeView();
            setupEventHandlers();
        });
    }

    private void initializeView() {
        view.initializeUI(adminStage);

        // Initialize EditGamePlaySettings components
        EditGamePlaySettingsModel gamePlayModel = new EditGamePlaySettingsModel(orb);
        editGamePlaySettingsController = new EditGamePlaySettingsController(
                gamePlayModel,
                view.getEditGamePlaySettingsView(),
                orb,
                token
        );
    }

    private void setupEventHandlers() {
        // Main dashboard buttons
        view.getEditPlayerBtn().setOnAction(e -> view.showEditPlayerView());
        view.getEditGamePlayBtn().setOnAction(e -> handleEditGamePlay());
        view.getQuitBtn().setOnAction(e -> handleQuit());

        // Create Player View handlers
        view.getCreatePlayerView().getCreatePlayerBtn().setOnAction(e -> view.showCreatePlayerView());
        view.getCreatePlayerView().getEditPlayerBtn().setOnAction(e -> view.showEditPlayerView());
        view.getCreatePlayerView().getConfirmBtn().setOnAction(e -> handleCreatePlayer());

        // Edit Player View handlers
        view.getEditPlayerView().getCreatePlayerBtn().setOnAction(e -> view.showCreatePlayerView());
        view.getEditPlayerView().getEditPlayerBtn().setOnAction(e -> view.showEditPlayerView());
        view.getEditPlayerView().getClearBtn().setOnAction(e -> {
            view.getEditPlayerView().getSearchField().clear();
            handleReadAllPlayers();
        });
        view.getEditPlayerView().getSearchField().textProperty().addListener((obs, oldValue, newValue) -> {
            handleSearchPlayers(newValue);
        });
        view.getEditPlayerView().getConfirmDeleteBtn().setOnAction(e -> {
            Player selectedPlayer = view.getEditPlayerView().getPlayerTable()
                    .getSelectionModel()
                    .getSelectedItem();

            if (selectedPlayer != null) {
                handleDeletePlayer(selectedPlayer);
            } else {
                view.getEditPlayerView().showError("No player selected");
            }
        });
        view.getEditPlayerView().getConfirmUpdateBtn().setOnAction(e -> {
            Player selectedPlayer = view.getEditPlayerView().getPlayerTable()
                    .getSelectionModel()
                    .getSelectedItem();
            if (selectedPlayer == null) {
                view.getEditPlayerView().showError("Please select a player to update");
                return;
            }

            String newUsername = view.getEditPlayerView().getUsernameField().getText().trim();
            String newPassword = view.getEditPlayerView().getPasswordField().getText().trim();

            if (view.getEditPlayerView().showUpdateConfirmation(selectedPlayer.username)) {
                handleUpdatePlayer();
            }
        });

        // Load players automatically when view is shown
        view.setOnShowEditPlayerViewListener(this::handleReadAllPlayers);
    }

    private void handleQuit() {
        adminStage.close(); // Close the admin dashboard
        loginController.showLogin(); // Show login screen again
    }

    private void handleEditGamePlay() {
        try {
            view.showEditGameplaySettingsView();
            editGamePlaySettingsController.loadCurrentSettings();
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
            handleReadAllPlayers();
        } catch (NotLoggedIn e) {
            createView.showError("Error: Admin not logged in");
        } catch (UsernameAlreadyExists e) {
            createView.showError("Error: Username already exists");
        }
    }

    private void handleUpdatePlayer() {
        EditPlayerView editView = view.getEditPlayerView();
        Player selectedPlayer = editView.getPlayerTable().getSelectionModel().getSelectedItem();

        if (selectedPlayer == null) {
            editView.showError("Please select a player to update");
            return;
        }
        if (!editView.getUpdateFormContainer().isVisible()) {
            editView.showError("Please select a player first");
            return;
        }

        String currentUsername = selectedPlayer.username;
        String newUsername = editView.getUsernameField().getText().trim();
        String newPassword = editView.getPasswordField().getText().trim();

        if (newUsername.isEmpty() && newPassword.isEmpty()) {
            editView.showError("Fields cannot be empty");
            return;
        }

        if (!newUsername.equals(currentUsername)) {
            // Check if the new username already exists
            try {
                Player[] existingPlayers = model.searchPlayers(newUsername);
                if (existingPlayers != null && existingPlayers.length > 0) {
                    // Username already exists
                    editView.showError("Username already taken");
                    return;
                }
            } catch (NotLoggedIn e) {
                editView.showError("Error: Admin not logged in");
                return;
            } catch (PlayerNotFound ignored) {
                // No player found with the given username - safe to proceed
            } catch (Exception e) {
                editView.showError("Unexpected error during username verification: " + e.getMessage());
                return;
            }
        }

        // Proceed with the update
        try {
            model.updatePlayer(currentUsername, newUsername, newPassword.isEmpty() ? "`" : newPassword);
            editView.showSuccess("Player updated successfully");
            editView.hideUpdateForm();
            handleReadAllPlayers();
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

    private void handleDeletePlayer(Player player) {
        System.out.println("[CONTROLLER] handleDeletePlayer called");
        EditPlayerView editView = view.getEditPlayerView();

        if (player == null) {
            editView.showError("Please select a player to delete");
            return;
        }

        String username = player.username;

        try {
            model.deletePlayer(username);
            editView.showSuccess("Player deleted successfully");
            handleReadAllPlayers();
        } catch (PlayerNotFound e) {
            System.out.println("[CONTROLLER] Player not found - refreshing view");
            editView.showError("Player no longer exists - list refreshed");
            handleReadAllPlayers();
        } catch (NotLoggedIn e) {
            editView.showError("Error: Admin not logged in");
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
