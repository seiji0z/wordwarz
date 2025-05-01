package client.admin.controller;

import WordWarZ.*;
import client.admin.model.EditGamePlaySettingsModel;
import client.admin.view.EditGamePlaySettingsView;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import org.omg.CORBA.ORB;

public class EditGamePlaySettingsController {
    private final EditGamePlaySettingsModel model;
    private final EditGamePlaySettingsView view;
    private final ORB orb;
    private final String token;

    public EditGamePlaySettingsController(EditGamePlaySettingsModel model, EditGamePlaySettingsView view, ORB orb, String token) {
        this.model = model;
        this.view = view;
        this.orb = orb;
        this.token = token;

        initializeView();
        initController();
        loadCurrentSettings();
    }

    private void initializeView() {
        view.buildUI();
    }

    private void initController() {
        view.getSaveButton().setOnAction(this::handleSaveSettings);
    }

    private void loadCurrentSettings() {
        try {
            long waitingTime = model.getCurrentWaitingTime();
            long roundDuration = model.getCurrentRoundDuration();

            view.getWaitingField().setText(String.valueOf(waitingTime));
            view.getRoundField().setText(String.valueOf(roundDuration));
        } catch (NotLoggedIn e) {
            showAlert("Session Expired", "You are not logged in. Please log in again.");
        } catch (Exception e) {
            showAlert("Error", "Failed to load current settings: " + e.getMessage());
        }
    }

    private void handleSaveSettings(ActionEvent event) {
        String waitingTime = view.getWaitingField().getText();
        String roundDuration = view.getRoundField().getText();

        if (!waitingTime.matches("\\d+") || !roundDuration.matches("\\d+")) {
            showAlert("Invalid Input", "Please enter valid numeric values for both fields.");
            return;
        }

        try {
            boolean waitingSuccess = model.updateGameWaitingTime(Integer.parseInt(waitingTime));
            boolean roundSuccess = model.updateGameRoundDuration(Integer.parseInt(roundDuration));

            if (waitingSuccess && roundSuccess) {
                showAlert("Success", "Game configurations updated successfully!");
            } else {
                showAlert("Partial Success", "Some configurations might not have been updated.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred while updating configurations: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}