package client.player.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.File;

public class MainMenuView extends Application {

    // Media controls
    private MediaPlayer mediaPlayer;
    private boolean isMuted = false;

    // UI Components
    private ImageView soundButtonView;
    private StackPane overlayPane;
    private Button playBtn;
    private Button leaderboardBtn;
    private Button howToPlayBtn;
    private Button quitBtn;
    private Button soundButton;
    private Button closeHowToPlayBtn;

    public MainMenuView() {
        // Initialize the view without showing it
        Platform.runLater(() -> {
            try {
                start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void start(Stage primaryStage) {
        // Main root container
        StackPane root = new StackPane();
        root.setPrefSize(1280, 760);

        // --- MAIN CONTENT ---
        BorderPane mainContent = new BorderPane();
        mainContent.setPrefSize(1280, 760);

        // --- BACKGROUND ---
        Image bg = new Image("file:res/images/backgrounds/menu bg.png");
        BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, false, true);
        mainContent.setBackground(new Background(new BackgroundImage(
                bg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, bgSize
        )));

        // --- SOUND BUTTON ---
        Image soundOnImg = new Image("file:res/images/buttons/menu buttons/sound button.png");
        Image soundOffImg = new Image("file:res/images/buttons/menu buttons/mute button.png");
        soundButtonView = new ImageView(soundOnImg);
        soundButtonView.setPreserveRatio(true);
        soundButtonView.setFitWidth(70);
        soundButton = new Button();
        soundButton.setGraphic(soundButtonView);
        soundButton.setBackground(Background.EMPTY);
        soundButton.setPadding(Insets.EMPTY);

        soundButton.setOnAction(e -> {
            isMuted = !isMuted;
            if (isMuted) {
                soundButtonView.setImage(soundOffImg);
                mediaPlayer.setMute(true);
            } else {
                soundButtonView.setImage(soundOnImg);
                mediaPlayer.setMute(false);
            }
        });

        // --- HOW TO PLAY BUTTON ---
        Image howToPlayImg = new Image("file:res/images/buttons/menu buttons/how to play button.png");
        ImageView howToPlayView = new ImageView(howToPlayImg);
        howToPlayView.setPreserveRatio(true);
        howToPlayView.setFitWidth(70);
        howToPlayBtn = new Button();
        howToPlayBtn.setGraphic(howToPlayView);
        howToPlayBtn.setBackground(Background.EMPTY);
        howToPlayBtn.setPadding(Insets.EMPTY);

        // --- LEADERBOARD BUTTON ---
        Image leaderboardImg = new Image("file:res/images/buttons/menu buttons/Leaderboard button.png");
        ImageView leaderboardView = new ImageView(leaderboardImg);
        leaderboardView.setPreserveRatio(true);
        leaderboardView.setFitWidth(70);
        leaderboardBtn = new Button();
        leaderboardBtn.setGraphic(leaderboardView);
        leaderboardBtn.setBackground(Background.EMPTY);
        leaderboardBtn.setPadding(Insets.EMPTY);
        leaderboardBtn.setOnAction(e -> {
            System.out.println("Leaderboard button clicked!");
            // Implement leaderboard functionality here
        });

        // --- TOP BOX ---
        HBox topBox = new HBox(10, soundButton, howToPlayBtn, leaderboardBtn);
        topBox.setAlignment(Pos.TOP_RIGHT);
        topBox.setPadding(new Insets(10));
        topBox.setBackground(Background.EMPTY);
        mainContent.setTop(topBox);

        // --- LOGO ---
        Image logoImg = new Image("file:res/images/others/word war z logo.png");
        ImageView logoView = new ImageView(logoImg);
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(750);

        // --- PLAY BUTTON ---
        Image playImg = new Image("file:res/images/buttons/menu buttons/play button.png");
        ImageView playView = new ImageView(playImg);
        playView.setPreserveRatio(true);
        playView.setFitWidth(200);
        playBtn = new Button();
        playBtn.setGraphic(playView);
        playBtn.setBackground(Background.EMPTY);
        playBtn.setPadding(Insets.EMPTY);

        // --- QUIT BUTTON ---
        Image quitImg = new Image("file:res/images/buttons/menu buttons/quit button.png");
        ImageView quitView = new ImageView(quitImg);
        quitView.setPreserveRatio(true);
        quitView.setFitWidth(200);
        quitBtn = new Button();
        quitBtn.setGraphic(quitView);
        quitBtn.setBackground(Background.EMPTY);
        quitBtn.setPadding(Insets.EMPTY);
        quitBtn.setOnAction(e -> primaryStage.close());

        VBox centerBox = new VBox(10, logoView, playBtn, quitBtn);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(10, 0, 0, 0));
        mainContent.setCenter(centerBox);

        // --- OVERLAY PANE (How to Play) ---
        overlayPane = new StackPane();
        overlayPane.setBackground(new Background(new BackgroundFill(
                new Color(0, 0, 0, 0.7), CornerRadii.EMPTY, Insets.EMPTY)));
        overlayPane.setVisible(false);

        // How to Play content
        Image howToPlayScreen = new Image("file:res/images/frames/how to play.png");
        ImageView howToPlayFullScreen = new ImageView(howToPlayScreen);
        howToPlayFullScreen.setFitWidth(1000);
        howToPlayFullScreen.setFitHeight(650);
        howToPlayFullScreen.setPreserveRatio(false);

        closeHowToPlayBtn = new Button("Close");
        closeHowToPlayBtn.setStyle("-fx-font-size: 16px; -fx-padding: 8px 16px;");
        closeHowToPlayBtn.setOnAction(ev -> overlayPane.setVisible(false));

        VBox overlayContent = new VBox(20, howToPlayFullScreen, closeHowToPlayBtn);
        overlayContent.setAlignment(Pos.CENTER);
        overlayPane.getChildren().add(overlayContent);

        howToPlayBtn.setOnAction(ev -> overlayPane.setVisible(true));

        // --- PLAY MUSIC ---
        File musicFile = new File("res/music/menu music.mp3");
        Media media = new Media(musicFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        // --- SETUP SCENE ---
        root.getChildren().addAll(mainContent, overlayPane);
        Scene scene = new Scene(root);
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Add these methods to your MainMenuView class:

    // Event handler interfaces
    public interface PlayButtonHandler {
        void handle();
    }

    public interface LeaderboardButtonHandler {
        void handle();
    }

    public interface HowToPlayButtonHandler {
        void handle();
    }

    public interface QuitButtonHandler {
        void handle();
    }

    public interface SoundToggleHandler {
        void handle(boolean isMuted);
    }

    private PlayButtonHandler playButtonHandler;
    private LeaderboardButtonHandler leaderboardButtonHandler;
    private HowToPlayButtonHandler howToPlayButtonHandler;
    private QuitButtonHandler quitButtonHandler;
    private SoundToggleHandler soundToggleHandler;

    public void setPlayButtonHandler(PlayButtonHandler handler) {
        this.playButtonHandler = handler;
        playBtn.setOnAction(e -> handler.handle());
    }

    public void setLeaderboardButtonHandler(LeaderboardButtonHandler handler) {
        this.leaderboardButtonHandler = handler;
        leaderboardBtn.setOnAction(e -> handler.handle());
    }

    public void setHowToPlayButtonHandler(HowToPlayButtonHandler handler) {
        this.howToPlayButtonHandler = handler;
        howToPlayBtn.setOnAction(e -> handler.handle());
    }

    public void setQuitButtonHandler(QuitButtonHandler handler) {
        this.quitButtonHandler = handler;
        quitBtn.setOnAction(e -> handler.handle());
    }

    public void setSoundToggleHandler(SoundToggleHandler handler) {
        this.soundToggleHandler = handler;
        soundButton.setOnAction(e -> {
            isMuted = !isMuted;
            handler.handle(isMuted);
        });
    }

    public void showHowToPlay() {
        overlayPane.setVisible(true);
    }
}
