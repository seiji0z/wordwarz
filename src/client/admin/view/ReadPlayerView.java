package client.admin.view;


import WordWarZ.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private Button searchBtn;
    private TextField searchField;
    private TableView<Player> playerTable;


    public ReadPlayerView(Font customFont) {
        this.customFont = customFont;
        initializeUI();
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
        Label titleLabel = new Label("Player Details");
        titleLabel.setFont(Font.font(customFont.getFamily(), FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKRED);


        // Button container
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


        // Player table setup (keep this)
        playerTable = new TableView<>();
        playerTable.setStyle("-fx-background-color: rgb(64,64,64);");
        playerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        TableColumn<Player, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setStyle("-fx-text-fill: white; -fx-alignment: CENTER;");


        TableColumn<Player, Integer> winsCol = new TableColumn<>("Wins");
        winsCol.setCellValueFactory(new PropertyValueFactory<>("wins"));
        winsCol.setStyle("-fx-text-fill: white; -fx-alignment: CENTER;");


        playerTable.getColumns().addAll(usernameCol, winsCol);
        playerTable.setPlaceholder(new Label("No players found"));
        playerTable.setPrefHeight(300);


        // Add components to main view (without search container)
        this.getChildren().addAll(
                titleLabel,
                buttonGrid,
                playerTable  // Removed searchContainer from here
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
    public Button getCreateBtn() { return createBtn; }
    public Button getReadBtn() { return readBtn; }
    public Button getUpdateBtn() { return updateBtn; }
    public Button getDeleteBtn() { return deleteBtn; }


    // Getters for fields
    public TableView<Player> getPlayerTable() { return playerTable; }


    // Method to update the table with player data
    public void updatePlayerTable(Player[] players) {
        ObservableList<Player> playerList = FXCollections.observableArrayList(players);
        playerTable.setItems(playerList);
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
