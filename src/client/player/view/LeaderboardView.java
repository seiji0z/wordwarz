package client.player.view;

import WordWarZ.Player;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;

public class LeaderboardView extends Application {
    private StackPane[] playerPanes = new StackPane[5];
    private Label[] nameLabels = new Label[5];
    private Label[] winLabels = new Label[5];
    private ImageView[] tombViews = new ImageView[5];
    private ImageView[] zombieHandViews = new ImageView[5];
    private boolean initialized = false;
    private Button refreshButton;
    private Button backButton;
    private boolean[] hasPlayer = new boolean[5];

    public LeaderboardView() {
        for (int i = 0; i < 5; i++) {
            nameLabels[i] = new Label();
            winLabels[i] = new Label();
            tombViews[i] = new ImageView();
            zombieHandViews[i] = new ImageView();

            // Main container for each tombstone
            StackPane tombContainer = new StackPane();
            tombContainer.setAlignment(Pos.BOTTOM_CENTER); // Align children to bottom

            // VBox for the labels
            VBox labelBox = new VBox(2);
            labelBox.setAlignment(Pos.CENTER);
            labelBox.getChildren().addAll(nameLabels[i], winLabels[i]);

            // Tombstone and hand to container (order matters)
            tombContainer.getChildren().addAll(tombViews[i], zombieHandViews[i]);

            // Main player pane contains tombstone container and labels
            playerPanes[i] = new StackPane();
            playerPanes[i].getChildren().addAll(tombContainer, labelBox);

            // Initially hide the hand
            zombieHandViews[i].setVisible(false);
            zombieHandViews[i].setTranslateY(20); // Start position below tombstone

            final int index = i;
            playerPanes[i].setOnMouseEntered(e -> {
                // Reset and show hand animation
                if (hasPlayer[index]) {
                    zombieHandViews[index].setImage(null);
                    zombieHandViews[index].setImage(new Image("file:res/images/others/zombie hand.gif"));
                    zombieHandViews[index].setVisible(true);
                    zombieHandViews[index].setTranslateY(20);

                    nameLabels[index].setTextFill(Color.RED);
                    winLabels[index].setTextFill(Color.RED);
                }
            });

            playerPanes[i].setOnMouseExited(e -> {
                // Hide hand
                if (hasPlayer[index]) {
                    zombieHandViews[index].setVisible(false);
                    nameLabels[index].setTextFill(Color.WHITE);
                    winLabels[index].setTextFill(Color.WHITE);
                }
            });
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
    }

    public void initializeUI(Stage primaryStage) {
        Pane root = new Pane();
        root.setPrefSize(1280, 760);

        // Set background
        Image bg = new Image("file:res/images/backgrounds/leaderboard bg.png");
        BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, false, true);
        Background background = new Background(new BackgroundImage(
                bg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, bgSize
        ));
        root.setBackground(background);

        // Load font
        Font pressStartFont = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 12);

        // Define positions for player panes (4 3 1 2 5 order)
        int[] xPositions = {100, 350, 570, 800, 1040};
        int[] yPositions = {550, 500, 450, 500, 550};

        // Load tomb images
        String[] tombImages = {
                "file:res/images/others/4th tomb.png",
                "file:res/images/others/3rd tomb.png",
                "file:res/images/others/1st tomb.png",
                "file:res/images/others/2nd tomb.png",
                "file:res/images/others/5th tomb.png"
        };

        for (int i = 0; i < 5; i++) {
            // Set tomb image
            Image tombImage = new Image(tombImages[i]);
            tombViews[i].setImage(tombImage);
            tombViews[i].setPreserveRatio(true);
            tombViews[i].setFitHeight(200);

            // Configure zombie hand
            zombieHandViews[i].setPreserveRatio(true);
            zombieHandViews[i].setFitHeight(200); // Smaller than tombstone
            zombieHandViews[i].setTranslateY(20); // Position below tombstone

            // Style labels
            nameLabels[i].setFont(pressStartFont);
            nameLabels[i].setTextFill(Color.WHITE);
            nameLabels[i].setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 2, 0, 0, 1);");

            winLabels[i].setFont(pressStartFont);
            winLabels[i].setTextFill(Color.WHITE);
            winLabels[i].setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 2, 0, 0, 1);");

            // Position player panes
            playerPanes[i].setLayoutX(xPositions[i]);
            playerPanes[i].setLayoutY(yPositions[i]);

            root.getChildren().add(playerPanes[i]);
        }

        refreshButton = new Button("REFRESH");
        refreshButton.setFont(pressStartFont);
        refreshButton.setTextFill(Color.WHITE);
        refreshButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        refreshButton.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        refreshButton.setPadding(new Insets(10, 20, 10, 20));
        refreshButton.setLayoutX(1100);
        refreshButton.setLayoutY(50);
        refreshButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        );
        refreshButton.setOnMouseEntered(e -> refreshButton.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        refreshButton.setOnMouseExited(e -> refreshButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));

        // Back Button
        backButton = new Button("BACK");
        backButton.setFont(pressStartFont);
        backButton.setTextFill(Color.WHITE);
        backButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        backButton.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        backButton.setPadding(new Insets(10, 20, 10, 20));
        backButton.setLayoutX(50);
        backButton.setLayoutY(50);
        backButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        );
        backButton.setOnMouseEntered(e -> backButton.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        backButton.setOnMouseExited(e -> backButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));

        root.getChildren().addAll(refreshButton, backButton);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Word War Z - Leaderboard");
        primaryStage.setScene(scene);
        initialized = true;
    }

    public void updateLeaderboard(List<Player> players) {
        if (!initialized) return;

        List<Player> sorted = new java.util.ArrayList<>(players);
        sorted.sort((p1, p2) -> Integer.compare(p2.wins, p1.wins));

        int[] visualOrder = {3, 2, 0, 1, 4};

        Platform.runLater(() -> {
            // Reset hasPlayer array
            Arrays.fill(hasPlayer, false);

            for (int i = 0; i < 5; i++) {
                int sortedIndex = visualOrder[i];
                if (sortedIndex < sorted.size()) {
                    Player p = sorted.get(sortedIndex);
                    nameLabels[i].setText(truncateUsername(p.username));
                    winLabels[i].setText(p.wins + " Wins");
                    hasPlayer[i] = true; // Mark this position as having a player

                    // Hand is hidden initially
                    zombieHandViews[i].setVisible(false);
                } else {
                    nameLabels[i].setText("");
                    winLabels[i].setText("");
                    hasPlayer[i] = false;

                    // Hand is hidden for empty positions
                    zombieHandViews[i].setVisible(false);
                }
            }
        });
    }

    private String truncateUsername(String username) {
        return (username.length() > 9) ? username.substring(0, 6) + "..." : username;
    }

    public void setRefreshButtonHandler(Runnable handler) {
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> handler.run());
        }
    }

    public void setBackButtonHandler(Runnable handler) {
        if (backButton != null) {
            backButton.setOnAction(e -> handler.run());
        }
    }
}