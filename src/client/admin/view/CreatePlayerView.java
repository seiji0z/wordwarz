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
    private Button createPlayerBtn;
    private Button editPlayerBtn;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button confirmBtn;

    public CreatePlayerView(Font customFont) {
        this.customFont = customFont;
        initializeUI();
    }

    private void initializeUI() {
        // Main container settings
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
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

        // Navigation buttons container
        HBox navButtonBox = new HBox(20);
        navButtonBox.setAlignment(Pos.CENTER);
        navButtonBox.setPadding(new Insets(20));

        // Create navigation buttons
        createPlayerBtn = createStyledButton("CREATE PLAYER");
        editPlayerBtn = createStyledButton("EDIT PLAYER");

        // Style the active button differently
        createPlayerBtn.setBackground(new Background(new BackgroundFill(
                Color.rgb(80, 80, 80), new CornerRadii(8), Insets.EMPTY
        )));

        navButtonBox.getChildren().addAll(createPlayerBtn, editPlayerBtn);

        // Input fields container
        VBox inputContainer = new VBox(15);
        inputContainer.setAlignment(Pos.CENTER);
        inputContainer.setMaxWidth(600);
        inputContainer.setPadding(new Insets(20));

        // Username field
        VBox usernameBox = new VBox(5);
        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(Font.font(customFont.getFamily(), 14));
        usernameLabel.setTextFill(Color.WHITE);  // White label
        usernameField = new TextField();
        usernameField.setFont(Font.font(customFont.getFamily(), 14));
        usernameField.setPrefWidth(250);
        usernameField.setStyle("-fx-font-family: '" + customFont.getFamily() + "'; -fx-text-fill: black; -fx-background-color: white;");
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Password field
        VBox passwordBox = new VBox(5);
        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font(customFont.getFamily(), 14));
        passwordLabel.setTextFill(Color.WHITE);  // White label
        passwordField = new PasswordField();
        passwordField.setFont(Font.font(customFont.getFamily(), 14));
        passwordField.setPrefWidth(250);
        passwordField.setStyle("-fx-font-family: '" + customFont.getFamily() + "'; -fx-text-fill: black; -fx-background-color: white;");
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        inputContainer.getChildren().addAll(usernameBox, passwordBox);

        // Confirm Button
        confirmBtn = createStyledButton("Create Player");
        confirmBtn.setMaxWidth(300);
        VBox confirmContainer = new VBox(confirmBtn);
        confirmContainer.setAlignment(Pos.CENTER);
        confirmContainer.setPadding(new Insets(20, 0, 0, 0));

        // Add all components to main view
        this.getChildren().addAll(
                titleLabel,
                navButtonBox,
                inputContainer,
                confirmContainer
        );
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
        button.setMinWidth(150);
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
    public Button getCreatePlayerBtn() { return createPlayerBtn; }
    public Button getEditPlayerBtn() { return editPlayerBtn; }
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

        // Apply custom font to alert dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-family: '" + customFont.getFamily() + "';");

        alert.showAndWait();
    }

    // Show Success Alert
    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Apply custom font to alert dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-family: '" + customFont.getFamily() + "';");

        alert.showAndWait();
    }

    // Clear the fields after success
    public void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }
}
