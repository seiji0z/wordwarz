package client.player.view;

import javafx.application.Application;
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

    private MediaPlayer mediaPlayer;
    private boolean isMuted = false;
    private ImageView soundButtonView;
    private StackPane overlayPane;

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
        Button soundButton = new Button();
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
        Button howToPlayBtn = new Button();
        howToPlayBtn.setGraphic(howToPlayView);
        howToPlayBtn.setBackground(Background.EMPTY);
        howToPlayBtn.setPadding(Insets.EMPTY);

        // --- TOP BOX ---
        HBox topBox = new HBox(10, soundButton, howToPlayBtn);
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
        Button playBtn = new Button();
        playBtn.setGraphic(playView);
        playBtn.setBackground(Background.EMPTY);
        playBtn.setPadding(Insets.EMPTY);

        // --- QUIT BUTTON ---
        Image quitImg = new Image("file:res/images/buttons/menu buttons/quit button.png");
        ImageView quitView = new ImageView(quitImg);
        quitView.setPreserveRatio(true);
        quitView.setFitWidth(200);
        Button quitBtn = new Button();
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

        Button closeHowToPlayBtn = new Button("Close");
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

    public static void main(String[] args) {
        launch(args);
    }
}