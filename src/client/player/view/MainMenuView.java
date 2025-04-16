package client.player.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainMenuView extends Application {

    @Override
    public void start(Stage primaryStage) {
        // --- ROOT LAYOUT ---
        BorderPane root = new BorderPane();
        root.setPrefSize(1280, 760);

        // --- BACKGROUND (cover entire window) ---
        Image bg = new Image("file:res/images/backgrounds/menu bg.png");
        BackgroundSize bgSize = new BackgroundSize(
                100, 100, true, true, false, true
        );
        root.setBackground(new Background(new BackgroundImage(
                bg,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bgSize
        )));

        // --- TOP‑RIGHT IMAGES ---
        // Replace "top1.png" and "top2.png" with your actual filenames
        Image topImg1 = new Image("file:res/images/buttons/menu buttons/mute button.png");
        ImageView topView1 = new ImageView(topImg1);
        topView1.setPreserveRatio(true);
        topView1.setFitWidth(70);

        Image topImg2 = new Image("file:res/images/buttons/menu buttons/how to play button.png");
        ImageView topView2 = new ImageView(topImg2);
        topView2.setPreserveRatio(true);
        topView2.setFitWidth(70);

        HBox topBox = new HBox(10, topView1, topView2);
        topBox.setAlignment(Pos.TOP_RIGHT);
        topBox.setPadding(new Insets(10));
        topBox.setBackground(Background.EMPTY);
        root.setTop(topBox);

        // --- LOGO IMAGE (CENTER) ---
        Image logoImg = new Image("file:res/images/others/word war z logo.png");
        ImageView logoView = new ImageView(logoImg);
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(800);

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

        // --- COMBINE LOGO & MAIN BUTTONS ---
        VBox centerBox = new VBox(10, logoView, playBtn, quitBtn);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(40, 0, 0, 0));
        root.setCenter(centerBox);

        // --- FINISH SETUP ---
        Scene scene = new Scene(root);
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
