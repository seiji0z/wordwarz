package client.admin.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class CreatePlayerView extends VBox {
    private final Font customFont;
    private Button createBtn;
    private Button readBtn;
    private Button updateBtn;
    private Button deleteBtn;
    private Button confirmBtn;
    private TextField usernameField;
    private PasswordField passwordField;

    public CreatePlayerView(Font customFont) {
        this.customFont = customFont;
        initializeUI();
    }

    private void initializeUI() {
        // Main container settings
        this.setAlignment(Pos.CENTER);
        this.setSpacing(30); // Increased spacing for better visual hierarchy
        this.setPadding(new Insets(10));
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);

        // Make the view grow with the window
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);

        // Title label
        Label titleLabel = new Label("Create Player");
        titleLabel.setFont(Font.font(customFont.getFamily(), FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKRED);

        // Button container - now using GridPane for better button arrangement
        GridPane buttonGrid = new GridPane();
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setHgap(15);
        buttonGrid.setVgap(15);
        buttonGrid.setPadding(new Insets(20));

        // Create buttons with consistent sizing
        createBtn = createStyledButton("CREATE PLAYER");
        readBtn = createStyledButton("READ PLAYERS");
        updateBtn = createStyledButton("UPDATE PLAYER");
        deleteBtn = createStyledButton("DELETE PLAYER");

        // Add buttons to grid
        buttonGrid.add(createBtn, 0, 0);
        buttonGrid.add(readBtn, 1, 0);
        buttonGrid.add(updateBtn, 0, 1);
        buttonGrid.add(deleteBtn, 1, 1);

        // Make buttons resize with window
        for (javafx.scene.Node node : buttonGrid.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(button, Priority.ALWAYS);
            }
        }

        // Input fields container
        VBox inputContainer = new VBox(20);
        inputContainer.setAlignment(Pos.CENTER);
        inputContainer.setMaxWidth(600); // Limit width but allow some flexibility
        inputContainer.setPadding(new Insets(20));

        // Username field
        VBox usernameBox = new VBox(8);
        Label usernameLabel = new Label("Username:");
        usernameLabel.setTextFill(Color.WHITE);
        usernameLabel.setFont(Font.font(customFont.getFamily(), 16));
        usernameField = new TextField();
        usernameField.setFont(customFont);
        usernameField.setStyle("-fx-text-fill: white; -fx-background-color: rgb(64,64,64);");
        usernameField.setPrefHeight(40);
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Password field
        VBox passwordBox = new VBox(8);
        Label passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(Color.WHITE);
        passwordLabel.setFont(Font.font(customFont.getFamily(), 16));
        passwordField = new PasswordField();
        passwordField.setFont(customFont);
        passwordField.setStyle("-fx-text-fill: white; -fx-background-color: rgb(64,64,64);");
        passwordField.setPrefHeight(40);
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        inputContainer.getChildren().addAll(usernameBox, passwordBox);

        // Confirm Button - centered and with some margin
        confirmBtn = createStyledButton("CONFIRM");
        confirmBtn.setMaxWidth(300);
        VBox confirmContainer = new VBox(confirmBtn);
        confirmContainer.setAlignment(Pos.CENTER);
        confirmContainer.setPadding(new Insets(20, 0, 0, 0));

        // Add all components to main view
        this.getChildren().addAll(
                titleLabel,
                buttonGrid,
                inputContainer,
                confirmContainer
        );

        // Bind the width of input fields to the container width
        usernameField.prefWidthProperty().bind(inputContainer.widthProperty().subtract(40));
        passwordField.prefWidthProperty().bind(inputContainer.widthProperty().subtract(40));
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font(customFont.getFamily(), 14));
        button.setTextFill(Color.WHITE);
        button.setCursor(Cursor.HAND);
        button.setBackground(new Background(new BackgroundFill(
                Color.rgb(64, 64, 64), new CornerRadii(8), Insets.EMPTY
        )));
        button.setPrefHeight(50);
        button.setMinWidth(150); // Minimum width to prevent text clipping
        button.setPadding(new Insets(10, 20, 10, 20));

        // Hover effects
        button.setOnMouseEntered(e -> button.setBackground(new Background(new BackgroundFill(
                Color.rgb(80, 80, 80), new CornerRadii(8), Insets.EMPTY
        ))));
        button.setOnMouseExited(e -> button.setBackground(new Background(new BackgroundFill(
                Color.rgb(64, 64, 64), new CornerRadii(8), Insets.EMPTY
        ))));

        return button;
    }

    // Getters for buttons
    public Button getCreateBtn() { return createBtn; }
    public Button getReadBtn() { return readBtn; }
    public Button getUpdateBtn() { return updateBtn; }
    public Button getDeleteBtn() { return deleteBtn; }
    public Button getConfirmBtn() { return confirmBtn; }

    // Getters for fields
    public TextField getUsernameField() { return usernameField; }
    public PasswordField getPasswordField() { return passwordField; }

    // Show Error Alert
    public void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Show Success Alert
    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Clear the fields after success
    public void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }
}