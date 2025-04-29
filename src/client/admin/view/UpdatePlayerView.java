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

public class UpdatePlayerView extends VBox {
    private final Font customFont;
    private TextField searchField;
    private ComboBox<String> userDropdown;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button searchButton;
    private Button updateButton;
    private Button createPlayerBtn;
    private Button deletePlayerBtn;
    private Button readPlayersBtn;
    private Button confirmBtn;

    public UpdatePlayerView(Font customFont) {
        this.customFont = customFont;
        initializeUI();
    }

    private void initializeUI() {
        // Main container settings
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        this.setPadding(new Insets(40));
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);

        // Make the view grow with the window
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);

        // Title label
        Label titleLabel = new Label("Update Player");
        titleLabel.setFont(Font.font(customFont.getFamily(), FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKRED);

        // Button container - using GridPane for better arrangement
        GridPane buttonGrid = new GridPane();
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setHgap(15);
        buttonGrid.setVgap(15);
        buttonGrid.setPadding(new Insets(20));

        // Create buttons with consistent sizing
        createPlayerBtn = createStyledButton("CREATE PLAYER");
        readPlayersBtn = createStyledButton("READ PLAYERS");
        updateButton = createStyledButton("UPDATE PLAYER");
        deletePlayerBtn = createStyledButton("DELETE PLAYER");

        // Add buttons to grid
        buttonGrid.add(createPlayerBtn, 0, 0);
        buttonGrid.add(readPlayersBtn, 1, 0);
        buttonGrid.add(updateButton, 0, 1);
        buttonGrid.add(deletePlayerBtn, 1, 1);

        // Make buttons resize with window
        for (javafx.scene.Node node : buttonGrid.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(button, Priority.ALWAYS);
            }
        }

        // Search container
        VBox searchContainer = new VBox(20);
        searchContainer.setAlignment(Pos.CENTER);
        searchContainer.setMaxWidth(600);
        searchContainer.setPadding(new Insets(20));

        // Search row
        HBox searchRow = new HBox(15);
        searchRow.setAlignment(Pos.CENTER);

        searchField = new TextField();
        searchField.setPromptText("Search username...");
        searchField.setFont(Font.font(customFont.getFamily(), 14));
        searchField.setStyle("-fx-text-fill: white; -fx-background-color: rgb(64,64,64);");
        searchField.setPrefHeight(40);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        userDropdown = new ComboBox<>();
        userDropdown.setPromptText("Select user");
        userDropdown.setStyle("-fx-text-fill: white; -fx-background-color: rgb(64,64,64);");
        userDropdown.setPrefHeight(40);
        userDropdown.setPrefWidth(200);

        searchButton = createStyledButton("SEARCH");
        searchButton.setPrefHeight(40);
        searchButton.setPrefWidth(120);

        searchRow.getChildren().addAll(searchField, userDropdown, searchButton);

        // Input fields container
        VBox inputContainer = new VBox(20);
        inputContainer.setAlignment(Pos.CENTER);
        inputContainer.setMaxWidth(600);
        inputContainer.setPadding(new Insets(20));

        // Username field
        VBox usernameBox = new VBox(8);
        Label usernameLabel = new Label("New Username:");
        usernameLabel.setTextFill(Color.WHITE);
        usernameLabel.setFont(Font.font(customFont.getFamily(), 16));
        usernameField = new TextField();
        usernameField.setFont(Font.font(customFont.getFamily(), 14));
        usernameField.setStyle("-fx-text-fill: white; -fx-background-color: rgb(64,64,64);");
        usernameField.setPrefHeight(40);
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Password field
        VBox passwordBox = new VBox(8);
        Label passwordLabel = new Label("New Password:");
        passwordLabel.setTextFill(Color.WHITE);
        passwordLabel.setFont(Font.font(customFont.getFamily(), 16));
        passwordField = new PasswordField();
        passwordField.setFont(Font.font(customFont.getFamily(), 14));
        passwordField.setStyle("-fx-text-fill: white; -fx-background-color: rgb(64,64,64);");
        passwordField.setPrefHeight(40);
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        inputContainer.getChildren().addAll(usernameBox, passwordBox);

        // Confirm Button - centered with margin
        confirmBtn = createStyledButton("CONFIRM");
        confirmBtn.setMaxWidth(300);
        VBox confirmContainer = new VBox(confirmBtn);
        confirmContainer.setAlignment(Pos.CENTER);
        confirmContainer.setPadding(new Insets(20, 0, 0, 0));

        // Add search row to search container
        searchContainer.getChildren().add(searchRow);

        // Add all components to main view
        this.getChildren().addAll(
                titleLabel,
                buttonGrid,
                searchContainer,
                inputContainer,
                confirmContainer
        );

        // Bind the width of input fields to the container width
        usernameField.prefWidthProperty().bind(inputContainer.widthProperty().subtract(40));
        passwordField.prefWidthProperty().bind(inputContainer.widthProperty().subtract(40));
        searchField.prefWidthProperty().bind(searchContainer.widthProperty().multiply(0.5));
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
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getters for all interactive components
    public TextField getSearchField() { return searchField; }
    public ComboBox<String> getUserDropdown() { return userDropdown; }
    public TextField getUsernameField() { return usernameField; }
    public PasswordField getPasswordField() { return passwordField; }
    public Button getSearchButton() { return searchButton; }
    public Button getUpdateButton() { return updateButton; }
    public Button getCreatePlayerBtn() { return createPlayerBtn; }
    public Button getDeletePlayerBtn() { return deletePlayerBtn; }
    public Button getReadPlayersBtn() { return readPlayersBtn; }
    public Button getConfirmBtn() { return confirmBtn; }
}