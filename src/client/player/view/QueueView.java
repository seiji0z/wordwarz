package client.player.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class QueueView extends Application {
    private Pane root;
    private Text timerText;
    private Text playerCountText;
    private Pane usernamesPane;
    private Button cancelButton;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        root = new Pane();
        Font customFont = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 25);
        if (customFont == null) {
            System.out.println("Failed to load custom font, falling back to default.");
            customFont = new Font("System", 30);
        }

        Image backgroundImage = new Image("file:res/images/backgrounds/menu bg.png");
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setFitWidth(1280);
        backgroundView.setFitHeight(760);
        root.getChildren().add(backgroundView);

        Image logoImage = new Image("file:res/images/others/word war z logo.png");
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(500);
        logoView.setFitHeight(280);
        logoView.setX(380);
        logoView.setY(30);
        root.getChildren().add(logoView);

        Rectangle timerRect = new Rectangle(200, 52, Color.BLACK);
        timerRect.setOpacity(0.5);
        timerRect.setX(535);
        timerRect.setY(318);
        root.getChildren().add(timerRect);

        timerText = new Text("00:10");
        timerText.setFont(customFont);
        timerText.setFill(Color.WHITE);
        timerText.setX(570);
        timerText.setY(360);
        root.getChildren().add(timerText);

        Rectangle queueRect = new Rectangle(690, 320, Color.BLACK);
        queueRect.setOpacity(0.5);
        queueRect.setX(300);
        queueRect.setY(380);
        root.getChildren().add(queueRect);

        Text waitingText = new Text("WAITING FOR PLAYERS...");
        waitingText.setFont(customFont);
        waitingText.setFill(Color.WHITE);
        waitingText.setX(380);
        waitingText.setY(430);
        root.getChildren().add(waitingText);

        playerCountText = new Text("Player count: 0");
        playerCountText.setFont(customFont);
        playerCountText.setFill(Color.WHITE);
        playerCountText.setX(380);
        playerCountText.setY(480);
        root.getChildren().add(playerCountText);

        usernamesPane = new Pane();
        usernamesPane.setLayoutX(540);
        usernamesPane.setLayoutY(320);
        root.getChildren().add(usernamesPane);

        cancelButton = new Button("CANCEL QUEUE");
        cancelButton.setFont(customFont);
        cancelButton.setTextFill(Color.WHITE);
        cancelButton.setStyle("-fx-background-color: transparent; -fx-border-color: red; -fx-border-width: 2;");
        cancelButton.setLayoutX(470);
        cancelButton.setLayoutY(650);
        root.getChildren().add(cancelButton);

        Scene scene = new Scene(root, 1280, 760);
        primaryStage.setTitle("Word War Z - Queue");
        primaryStage.getIcons().add(new Image("file:res/images/others/word war z logo.png"));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void setCancelButtonHandler(EventHandler<ActionEvent> handler) {
        cancelButton.setOnAction(handler);
    }

    public void updateTimer(int secondsLeft) {
        Platform.runLater(() -> {
            String formatted = String.format("00:%02d", secondsLeft);
            timerText.setText(formatted);
            System.out.println("Timer updated to: " + formatted);
        });
    }

    public void updatePlayerCount(int count) {
        Platform.runLater(() -> {
            playerCountText.setText("Player count: " + count);
            System.out.println("Player count updated to: " + count);
        });
    }

    public void close() {
        if (stage != null) {
            stage.close();
        }
        // Clear any other resources
    }
    }