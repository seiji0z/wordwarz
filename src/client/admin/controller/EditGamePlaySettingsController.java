package client.admin.controller;

import WordWarZ.NotLoggedIn;
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

        initController();
        loadCurrentSettings();
    }

    private void initController() {
        view.getSaveButton().setOnAction(this::handleSaveSettings);
    }

    public void loadCurrentSettings() {
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

        int waitingTimeValue = Integer.parseInt(waitingTime);
        int roundDurationValue = Integer.parseInt(roundDuration);

        if (waitingTimeValue <= 0 || roundDurationValue <= 0) {
            showAlert("Invalid Input", "Values must be greater than 0.");
            return;
        }

        try {
            boolean waitingSuccess = model.updateGameWaitingTime(waitingTimeValue);
            boolean roundSuccess = model.updateGameRoundDuration(roundDurationValue);

            if (waitingSuccess && roundSuccess) {
                showAlert("Success", "Game configurations updated successfully!");
                loadCurrentSettings();
            } else {
                showAlert("Error", "Failed to update one or more configurations. Please try again.");
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