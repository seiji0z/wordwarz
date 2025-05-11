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
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueView extends Application {
    private Pane root;
    private Text timerText;
    private Text playerCountText;
    private Pane usernamesPane;
    private Button cancelButton;
    private Text noOpponentText;
    private Text waitingText;
    private Stage stage;
    private Timeline dotsAnimation;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        root = new Pane();
        Font customFont = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 25);
        if (customFont == null) {
            System.out.println("[QueueView] Failed to load custom font, falling back to default.");
            customFont = new Font("System", 30);
        }

        Image backgroundImage = new Image("file:res/images/backgrounds/menu bg.png");
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setFitWidth(1280);
        backgroundView.setFitHeight(760);
        root.getChildren().add(backgroundView);

        Image logoImage = new Image("file:res/images/others/word war z logo.png");
        ImageView logoView = new ImageView(logoImage);
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(300);
        logoView.setX(480);
        logoView.setY(80);
        root.getChildren().add(logoView);

        Rectangle timerRect = new Rectangle(200, 52, Color.BLACK);
        timerRect.setOpacity(0.5);
        timerRect.setX(535);
        timerRect.setY(338);
        root.getChildren().add(timerRect);

        timerText = new Text("00:10");
        timerText.setFont(customFont);
        timerText.setFill(Color.WHITE);
        timerText.setX(570);
        timerText.setY(380);
        root.getChildren().add(timerText);

        Rectangle queueRect = new Rectangle(690, 280, Color.BLACK);
        queueRect.setOpacity(0.5);
        queueRect.setX(300);
        queueRect.setY(400);
        root.getChildren().add(queueRect);

        waitingText = new Text("WAITING FOR PLAYERS.");
        waitingText.setFont(customFont);
        waitingText.setFill(Color.WHITE);
        waitingText.setX(380);
        waitingText.setY(470);
        root.getChildren().add(waitingText);

        dotsAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> waitingText.setText("WAITING FOR PLAYERS.")),
                new KeyFrame(Duration.seconds(1.0), e -> waitingText.setText("WAITING FOR PLAYERS..")),
                new KeyFrame(Duration.seconds(1.5), e -> waitingText.setText("WAITING FOR PLAYERS..."))
        );
        dotsAnimation.setCycleCount(Timeline.INDEFINITE);
        dotsAnimation.play();

        playerCountText = new Text("Player count: 0");
        playerCountText.setFont(customFont);
        playerCountText.setFill(Color.WHITE);
        playerCountText.setX(380);
        playerCountText.setY(520);
        root.getChildren().add(playerCountText);

        cancelButton = new Button("CANCEL QUEUE");
        cancelButton.setFont(customFont);
        cancelButton.setTextFill(Color.WHITE);
        cancelButton.setStyle("-fx-background-color: transparent; -fx-border-color: red; -fx-border-width: 2;");
        cancelButton.setLayoutX(470);
        cancelButton.setLayoutY(630);
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: red; -fx-text-fill: black; -fx-border-color: red; -fx-border-width: 2;"));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: red; -fx-border-width: 2;"));
        cancelButton.setOnMousePressed(e -> cancelButton.setStyle("-fx-background-color: darkred; -fx-text-fill: black; -fx-border-color: red; -fx-border-width: 2;"));
        cancelButton.setOnMouseReleased(e -> cancelButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-border-color: red; -fx-border-width: 2;"));
        root.getChildren().add(cancelButton);

        noOpponentText = new Text("NO OPPONENT FOUND!");
        noOpponentText.setFont(customFont);
        noOpponentText.setFill(Color.RED);
        noOpponentText.setX(380);
        noOpponentText.setY(570);
        noOpponentText.setVisible(false);
        root.getChildren().add(noOpponentText);

        Scene scene = new Scene(root, 1280, 760);
        primaryStage.setTitle("Word War Z - Queue");
        primaryStage.getIcons().add(new Image("file:res/images/others/word war z logo.png"));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        System.out.println("[QueueView] QueueView started");
    }

    public Pane getRoot() {
        return root;
    }

    public void setCancelButtonHandler(EventHandler<ActionEvent> handler) {
        cancelButton.setOnAction(handler);
    }

    public void updateTimer(int secondsLeft) {
        Platform.runLater(() -> {
            String formatted = String.format("00:%02d", secondsLeft);
            timerText.setText(formatted);
            System.out.println("[QueueView] Timer updated to: " + formatted);
        });
    }

    public void updatePlayerCount(int count) {
        Platform.runLater(() -> {
            playerCountText.setText("Player count: " + count);
            System.out.println("[QueueView] Player count updated to: " + count);
        });
    }

    public void showNoOpponentMessage() {
        Platform.runLater(() -> {
            if (dotsAnimation != null) {
                dotsAnimation.stop();
                waitingText.setText("WAITING FOR PLAYERS");
            }

            noOpponentText.setVisible(true);
            System.out.println("[QueueView] Showing 'NO OPPONENT FOUND!' message");
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> Platform.runLater(() -> {
                noOpponentText.setVisible(false);
                System.out.println("[QueueView] Hiding 'NO OPPONENT FOUND!' message");
                if (dotsAnimation != null) {
                    dotsAnimation.play();
                }
            }), 2, TimeUnit.SECONDS);
            scheduler.shutdown();
        });
    }

    public void close() {
        if (stage != null) {
            if (dotsAnimation != null) {
                dotsAnimation.stop();
            }
            // Don't close the stage, just prepare for reuse
            System.out.println("[QueueView] QueueView closed");
        }
    }
}