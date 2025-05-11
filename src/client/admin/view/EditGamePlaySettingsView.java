package client.admin.view;

import client.admin.controller.EditGamePlaySettingsController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class EditGamePlaySettingsView extends VBox {
    private final Font customFont;
    private TextField waitingField, roundField;
    private Button saveBtn;
    private EditGamePlaySettingsController controller;

    public EditGamePlaySettingsView(Font customFont) {
        this.customFont = customFont;
        initializeUI();
    }

    private void initializeUI() {
        // Main container settings - match EditPlayerView
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        this.setPadding(new Insets(30));
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);

        // Title - match EditPlayerView style
        Label titleLabel = new Label("Edit Game Settings");
        titleLabel.setFont(Font.font(customFont.getFamily(), FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKRED);

        // Settings container - removed white background
        VBox settingsContainer = new VBox(20);
        settingsContainer.setAlignment(Pos.CENTER);
        settingsContainer.setPadding(new Insets(20));

        // Waiting Time
        VBox waitingBox = new VBox(5);
        Label waitingLabel = new Label("Waiting Time (seconds):");
        waitingLabel.setFont(Font.font(customFont.getFamily(), 14));
        waitingLabel.setTextFill(Color.WHITE);
        waitingField = new TextField();
        waitingField.setFont(Font.font(customFont.getFamily(), 14));
        waitingField.setPrefWidth(200);
        waitingField.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 5; " +
                "-fx-padding: 5; " +
                "-fx-font-family: '" + customFont.getFamily() + "';");
        waitingBox.getChildren().addAll(waitingLabel, waitingField);

        // Round Duration
        VBox roundBox = new VBox(5);
        Label roundLabel = new Label("Round Duration (seconds):");
        roundLabel.setFont(Font.font(customFont.getFamily(), 14));
        roundLabel.setTextFill(Color.WHITE);
        roundField = new TextField();
        roundField.setFont(Font.font(customFont.getFamily(), 14));
        roundField.setPrefWidth(200);
        roundField.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 5; " +
                "-fx-padding: 5; " +
                "-fx-font-family: '" + customFont.getFamily() + "';");
        roundBox.getChildren().addAll(roundLabel, roundField);

        // Numeric-only input validation
        waitingField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                waitingField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        roundField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                roundField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Save Changes button - use same style as EditPlayerView
        saveBtn = createStyledButton("SAVE CHANGES", 14);

        settingsContainer.getChildren().addAll(waitingBox, roundBox);
        this.getChildren().addAll(titleLabel, settingsContainer, saveBtn);
    }

    private Button createStyledButton(String text, double size) {
        Button button = new Button(text);
        button.setFont(Font.font(customFont.getFamily(), size));
        button.setTextFill(Color.WHITE);
        button.setCursor(Cursor.HAND);
        button.setBackground(new Background(new BackgroundFill(
                Color.rgb(64, 64, 64), new CornerRadii(8), Insets.EMPTY
        )));

        button.setPadding(new Insets(10, 20, 10, 20));

        // Hover effects - match EditPlayerView
        button.setOnMouseEntered(e -> button.setBackground(new Background(new BackgroundFill(
                Color.rgb(80, 80, 80), new CornerRadii(8), Insets.EMPTY
        ))));
        button.setOnMouseExited(e -> button.setBackground(new Background(new BackgroundFill(
                Color.rgb(64, 64, 64), new CornerRadii(8), Insets.EMPTY
        ))));

        return button;
    }


    public TextField getWaitingField() { return waitingField; }
    public TextField getRoundField() { return roundField; }
    public Button getSaveButton() { return saveBtn; }
    public void setController(EditGamePlaySettingsController controller) {
        this.controller = controller;
    }
    public EditGamePlaySettingsController getController() {
        return controller;
    }
}