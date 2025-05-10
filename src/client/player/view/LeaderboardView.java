package client.player.view;

import WordWarZ.Player;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import java.util.List;

public class LeaderboardView extends Application {
    private VBox[] playerBoxes = new VBox[5];
    private Label[] nameLabels = new Label[5];
    private Label[] winLabels = new Label[5];
    private boolean initialized = false;

    public LeaderboardView() {
        for (int i = 0; i < 5; i++) {
            nameLabels[i] = new Label();
            winLabels[i] = new Label();
            playerBoxes[i] = new VBox(10); // 10px spacing between name and wins
            playerBoxes[i].setAlignment(Pos.CENTER); // Center content inside VBox
            playerBoxes[i].getChildren().addAll(nameLabels[i], winLabels[i]);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Left blank intentionally — handled externally
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

        // Define positions for VBoxes
        int[] xPositions = {120, 320, 570, 800, 1000};
        int[] yPositions = {500, 500, 500, 500, 500};

        for (int i = 0; i < 5; i++) {
            nameLabels[i].setFont(pressStartFont);
            nameLabels[i].setTextFill(Color.WHITE);

            winLabels[i].setFont(pressStartFont);
            winLabels[i].setTextFill(Color.WHITE);

            playerBoxes[i].setLayoutX(xPositions[i]);
            playerBoxes[i].setLayoutY(yPositions[i]);

            root.getChildren().add(playerBoxes[i]);
        }

        Scene scene = new Scene(root);
        primaryStage.setTitle("Word War Z - Leaderboard");
        primaryStage.setScene(scene);
        initialized = true;
    }

    public void updateLeaderboard(List<Player> players) {
        if (!initialized) return;

        // Sort players by wins in descending order (Java 8 style)
        List<Player> sorted = new java.util.ArrayList<>(players);
        sorted.sort((p1, p2) -> Integer.compare(p2.wins, p1.wins));

        // Visual mapping: [5th, 3rd, 1st, 2nd, 4th]
        int[] visualOrder = {4, 2, 0, 1, 3};

        Platform.runLater(() -> {
            for (int i = 0; i < 5; i++) {
                int sortedIndex = visualOrder[i];
                if (sortedIndex < sorted.size()) {
                    Player p = sorted.get(sortedIndex);
                    nameLabels[i].setText(truncateUsername(p.username));
                    winLabels[i].setText(String.valueOf(p.wins));
                } else {
                    nameLabels[i].setText("");
                    winLabels[i].setText("");
                }
            }
        });
    }

    private String truncateUsername(String username) {
        return (username.length() > 6) ? username.substring(0, 6) + "..." : username;
    }
}
