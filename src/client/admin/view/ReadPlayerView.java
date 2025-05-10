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

public class ReadPlayerView extends VBox {
    private final Font customFont;
    private Button createBtn;
    private Button readBtn;
    private Button updateBtn;
    private Button deleteBtn;
    private TableView<Player> playerTable;
    private TextField searchField;

    public ReadPlayerView(Font customFont) {
        this.customFont = customFont;
        initializeUI();
        setupTableColumns();
    }

    private void initializeUI() {
        // Main container settings
        this.setAlignment(Pos.CENTER);
        this.setSpacing(30);
        this.setPadding(new Insets(10));
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);

        // Make the view grow with the window
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);

        // Title label
        Label titleLabel = new Label("Read Players");
        titleLabel.setFont(Font.font(customFont.getFamily(), FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKRED);

        // Search bar
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        Label searchLabel = new Label("Search:");
        searchLabel.setFont(Font.font(customFont.getFamily(), FontWeight.BOLD, 14));

        searchField = new TextField();
        searchField.setPromptText("Enter username to search...");
        searchField.setFont(Font.font(customFont.getFamily(), 14));
        searchField.setPrefWidth(200);
        searchField.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 5; " +
                "-fx-padding: 5;");

        searchBox.getChildren().addAll(searchLabel, searchField);

        // Button container
        GridPane buttonGrid = new GridPane();
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setHgap(15);
        buttonGrid.setVgap(15);
        buttonGrid.setPadding(new Insets(20));

        // Create buttons
        createBtn = createStyledButton("CREATE PLAYER");
        readBtn = createStyledButton("READ PLAYERS");
        updateBtn = createStyledButton("UPDATE PLAYER");
        deleteBtn = createStyledButton("DELETE PLAYER");

        // Add buttons to grid
        buttonGrid.add(createBtn, 0, 0);
        buttonGrid.add(readBtn, 1, 0);
        buttonGrid.add(updateBtn, 0, 1);
        buttonGrid.add(deleteBtn, 1, 1);

        // Player table setup
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

        this.getChildren().addAll(
                titleLabel,
                buttonGrid,
                searchBox,
                playerTable
        );
    }

    private void setupTableColumns() {
        playerTable.getColumns().clear();

        // Username Column - 60% width
        TableColumn<Player, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().username));
        usernameCol.setPrefWidth(200);
        usernameCol.setStyle("-fx-alignment: CENTER_LEFT;");

        usernameCol.setCellFactory(column -> new TableCell<Player, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item);
                    setTextFill(Color.BLACK);
                    setStyle("-fx-background-color: white; " +
                            "-fx-font-family: '" + customFont.getFamily() + "'; " +
                            "-fx-font-size: 14px; " +
                            "-fx-border-color: #e0e0e0; " +
                            "-fx-border-width: 0 1 1 0;");
                }
            }
        });

        // Wins Column - 40% width
        TableColumn<Player, Integer> winsCol = new TableColumn<>("Wins");
        winsCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().wins).asObject());
        winsCol.setPrefWidth(150);
        winsCol.setStyle("-fx-alignment: CENTER;");

        winsCol.setCellFactory(column -> new TableCell<Player, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    setTextFill(Color.BLACK);
                    setStyle("-fx-background-color: white; " +
                            "-fx-font-family: '" + customFont.getFamily() + "'; " +
                            "-fx-font-size: 14px; " +
                            "-fx-border-color: #e0e0e0; " +
                            "-fx-border-width: 0 0 1 0;");
                }
            }
        });

        playerTable.getColumns().addAll(usernameCol, winsCol);

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

    // Getters
    public Button getCreateBtn() { return createBtn; }
    public Button getReadBtn() { return readBtn; }
    public Button getUpdateBtn() { return updateBtn; }
    public Button getDeleteBtn() { return deleteBtn; }
    public TableView<Player> getPlayerTable() { return playerTable; }
    public TextField getSearchField() { return searchField; }

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
}