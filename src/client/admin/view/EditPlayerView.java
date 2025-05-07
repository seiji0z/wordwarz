package client.admin.view;

import WordWarZ.Player;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class EditPlayerView extends VBox {
    private final Font customFont;
    private Button createPlayerBtn;
    private Button editPlayerBtn;
    private TableView<Player> playerTable;
    private TextField searchField;
    private Button searchButton;
    private Button clearBtn;
    private VBox updateFormContainer;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button confirmUpdateBtn;
    private Button confirmDeleteBtn;

    public EditPlayerView(Font customFont) {
        this.customFont = customFont;
        initializeUI();
        setupTableColumns();
    }

    private void initializeUI() {
        // Main container settings (same as before)
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        this.setPadding(new Insets(10));
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);

        // Make the view grow with the window
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);

        // Title label
        Label titleLabel = new Label("Edit Players");
        titleLabel.setFont(Font.font(customFont.getFamily(), FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKRED);

        // Button container
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));

        // Create buttons
        createPlayerBtn = createStyledButton("CREATE PLAYER");
        editPlayerBtn = createStyledButton("EDIT PLAYER");

        buttonBox.getChildren().addAll(createPlayerBtn, editPlayerBtn);

        // Search bar (same as before)
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        Label searchLabel = new Label("Search:");
        searchLabel.setFont(Font.font(customFont.getFamily(), FontWeight.BOLD, 14));

        searchField = new TextField();
        searchField.setPromptText("Enter username...");
        searchField.setFont(Font.font(customFont.getFamily(), 14));
        searchField.setPrefWidth(200);
        searchField.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 5; " +
                "-fx-padding: 5;");

        searchButton = new Button("Search");
        searchButton.setFont(Font.font(customFont.getFamily(), 14));
        searchButton.setTextFill(Color.WHITE);
        searchButton.setCursor(Cursor.HAND);
        searchButton.setBackground(new Background(new BackgroundFill(
                Color.rgb(64, 64, 64), new CornerRadii(8), Insets.EMPTY
        )));
        searchButton.setPadding(new Insets(5, 10, 5, 10));
        searchButton.setStyle("-fx-font-size: 12px;");

        // Hover effects for search button
        searchButton.setOnMouseEntered(e -> searchButton.setBackground(new Background(new BackgroundFill(
                Color.rgb(80, 80, 80), new CornerRadii(8), Insets.EMPTY
        ))));
        searchButton.setOnMouseExited(e -> searchButton.setBackground(new Background(new BackgroundFill(
                Color.rgb(64, 64, 64), new CornerRadii(8), Insets.EMPTY
        ))));

        clearBtn = new Button("Clear");
        clearBtn.setFont(Font.font(customFont.getFamily(), 14));
        clearBtn.setTextFill(Color.WHITE);
        clearBtn.setCursor(Cursor.HAND);
        clearBtn.setBackground(new Background(new BackgroundFill(
                Color.rgb(64, 64, 64), new CornerRadii(8), Insets.EMPTY
        )));
        clearBtn.setPadding(new Insets(5, 10, 5, 10));
        clearBtn.setStyle("-fx-font-size: 12px;");

        // Hover effects for clear button
        clearBtn.setOnMouseEntered(e -> clearBtn.setBackground(new Background(new BackgroundFill(
                Color.rgb(80, 80, 80), new CornerRadii(8), Insets.EMPTY
        ))));
        clearBtn.setOnMouseExited(e -> clearBtn.setBackground(new Background(new BackgroundFill(
                Color.rgb(64, 64, 64), new CornerRadii(8), Insets.EMPTY
        ))));

        // Action to clear the search field
        clearBtn.setOnAction(e -> searchField.clear());

        searchBox.getChildren().addAll(searchLabel, searchField, searchButton, clearBtn);

        // Player table setup (same as before)
        playerTable = new TableView<>();
        playerTable.setStyle("-fx-background-color: #f5f5f5; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-padding: 5;");

        // Use fixed cell size to prevent empty rows
        playerTable.setFixedCellSize(35);
        playerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        playerTable.setPlaceholder(new Label("No players found"));
        playerTable.setPrefHeight(300);

        VBox.setVgrow(playerTable, Priority.ALWAYS);
        playerTable.setMinHeight(200);

        // Update form container (initially hidden)
        updateFormContainer = new VBox(15);
        updateFormContainer.setAlignment(Pos.CENTER);
        updateFormContainer.setPadding(new Insets(20));
        updateFormContainer.setVisible(false);

        // Username field
        VBox usernameBox = new VBox(5);
        Label usernameLabel = new Label("New Username:");
        usernameLabel.setFont(Font.font(customFont.getFamily(), 14));
        usernameField = new TextField();
        usernameField.setFont(Font.font(customFont.getFamily(), 14));
        usernameField.setPrefWidth(250);
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Password field
        VBox passwordBox = new VBox(5);
        Label passwordLabel = new Label("New Password:");
        passwordLabel.setFont(Font.font(customFont.getFamily(), 14));
        passwordField = new PasswordField();
        passwordField.setFont(Font.font(customFont.getFamily(), 14));
        passwordField.setPrefWidth(250);
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        // Action buttons
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER);

        confirmUpdateBtn = new Button("Update");
        confirmUpdateBtn.setFont(Font.font(customFont.getFamily(), 14));
        confirmUpdateBtn.setTextFill(Color.WHITE);
        confirmUpdateBtn.setCursor(Cursor.HAND);
        confirmUpdateBtn.setBackground(new Background(new BackgroundFill(
                Color.rgb(50, 120, 50), new CornerRadii(8), Insets.EMPTY
        )));
        confirmUpdateBtn.setPadding(new Insets(8, 15, 8, 15));

        // Add confirmation dialog for update
        confirmUpdateBtn.setOnAction(e -> {
            if (showUpdateConfirmation(usernameField.getText())) {
                // Trigger update action
                confirmUpdateBtn.fire();
            }
        });

        confirmDeleteBtn = new Button("Delete");
        confirmDeleteBtn.setFont(Font.font(customFont.getFamily(), 14));
        confirmDeleteBtn.setTextFill(Color.WHITE);
        confirmDeleteBtn.setCursor(Cursor.HAND);
        confirmDeleteBtn.setBackground(new Background(new BackgroundFill(
                Color.rgb(200, 50, 50), new CornerRadii(8), Insets.EMPTY
        )));
        confirmDeleteBtn.setPadding(new Insets(8, 15, 8, 15));

        actionButtons.getChildren().addAll(confirmUpdateBtn, confirmDeleteBtn);

        updateFormContainer.getChildren().addAll(
               // new Label("Edit Selected Player").setFont(Font.font(customFont.getFamily(), FontWeight.BOLD, 16)),
                usernameBox,
                passwordBox,
                actionButtons
        );

        this.getChildren().addAll(
                titleLabel,
                buttonBox,
                searchBox,
                playerTable,
                updateFormContainer
        );
    }

    private void setupTableColumns() {
        playerTable.getColumns().clear();

        // Username Column
        TableColumn<Player, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().username));
        usernameCol.setPrefWidth(250);
        usernameCol.setStyle("-fx-alignment: CENTER_LEFT;");

        // Wins Column (made thinner)
        TableColumn<Player, Integer> winsCol = new TableColumn<>("Wins");
        winsCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().wins).asObject());
        winsCol.setPrefWidth(100);  // Made thinner
        winsCol.setStyle("-fx-alignment: CENTER;");

        // Action Column
        TableColumn<Player, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(150);
        actionCol.setStyle("-fx-alignment: CENTER;");

        actionCol.setCellFactory(param -> new TableCell<Player, Void>() {
            private final Button updateBtn = new Button("Update");
            private final Button deleteBtn = new Button("Delete");

            {
                // Style update button
                updateBtn.setFont(Font.font(customFont.getFamily(), 12));
                updateBtn.setTextFill(Color.WHITE);
                updateBtn.setBackground(new Background(new BackgroundFill(
                        Color.rgb(50, 120, 50), new CornerRadii(5), Insets.EMPTY
                )));
                updateBtn.setPadding(new Insets(5, 10, 5, 10));
                updateBtn.setOnMouseEntered(e -> updateBtn.setBackground(new Background(new BackgroundFill(
                        Color.rgb(70, 140, 70), new CornerRadii(5), Insets.EMPTY
                ))));
                updateBtn.setOnMouseExited(e -> updateBtn.setBackground(new Background(new BackgroundFill(
                        Color.rgb(50, 120, 50), new CornerRadii(5), Insets.EMPTY
                ))));

                // Style delete button
                deleteBtn.setFont(Font.font(customFont.getFamily(), 12));
                deleteBtn.setTextFill(Color.WHITE);
                deleteBtn.setBackground(new Background(new BackgroundFill(
                        Color.rgb(200, 50, 50), new CornerRadii(5), Insets.EMPTY
                )));
                deleteBtn.setPadding(new Insets(5, 10, 5, 10));
                deleteBtn.setOnMouseEntered(e -> deleteBtn.setBackground(new Background(new BackgroundFill(
                        Color.rgb(220, 70, 70), new CornerRadii(5), Insets.EMPTY
                ))));
                deleteBtn.setOnMouseExited(e -> deleteBtn.setBackground(new Background(new BackgroundFill(
                        Color.rgb(200, 50, 50), new CornerRadii(5), Insets.EMPTY
                ))));

                // Button actions
                updateBtn.setOnAction(event -> {
                    Player player = getTableView().getItems().get(getIndex());
                    showUpdateForm(player);
                });

                deleteBtn.setOnAction(event -> {
                    Player player = getTableView().getItems().get(getIndex());
                    if (showDeleteConfirmation(player.username)) {
                        // Trigger delete action
                        confirmDeleteBtn.fire();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, updateBtn, deleteBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });

        playerTable.getColumns().addAll(usernameCol, winsCol, actionCol);

        // Row factory for alternating colors
        playerTable.setRowFactory(tv -> new TableRow<Player>() {
            @Override
            protected void updateItem(Player item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    if (getIndex() % 2 == 0) {
                        setStyle("-fx-background-color: white;");
                    } else {
                        setStyle("-fx-background-color: #f9f9f9;");
                    }
                }
            }
        });
    }

    public boolean showUpdateConfirmation(String username) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Update");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to update the player '" + username + "'?");

        ButtonType confirmButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        return alert.showAndWait().filter(buttonType -> buttonType == confirmButton).isPresent();
    }

    public boolean showDeleteConfirmation(String username) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the player '" + username + "'? This action cannot be undone.");

        ButtonType confirmButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        return alert.showAndWait().filter(buttonType -> buttonType == confirmButton).isPresent();
    }

    private void showUpdateForm(Player player) {
        usernameField.setText(player.username);
        passwordField.clear();
        updateFormContainer.setVisible(true);
    }

    public void hideUpdateForm() {
        updateFormContainer.setVisible(false);
    }

    public void displayPlayers(Player[] players) {
        Platform.runLater(() -> {
            ObservableList<Player> items = FXCollections.observableArrayList(players);
            playerTable.setItems(items);
        });
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

    public void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public void showSuccess(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    // Getters
    public Button getCreatePlayerBtn() { return createPlayerBtn; }
    public Button getEditPlayerBtn() { return editPlayerBtn; }
    public TableView<Player> getPlayerTable() { return playerTable; }
    public TextField getSearchField() { return searchField; }
    public Button getSearchButton() { return searchButton; }
    public Button getClearBtn() { return clearBtn; }
    public TextField getUsernameField() { return usernameField; }
    public PasswordField getPasswordField() { return passwordField; }
    public Button getConfirmUpdateBtn() { return confirmUpdateBtn; }
    public Button getConfirmDeleteBtn() { return confirmDeleteBtn; }
}