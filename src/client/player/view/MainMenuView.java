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
import javafx.stage.Stage;

public class MainMenuView extends Application {

    @Override
    public void start(Stage primaryStage) {
        // --- ROOT LAYOUT ---
        BorderPane root = new BorderPane();
        root.setPrefSize(1280, 760);

        // Background (cover entire window)
        Image bg = new Image("file:res/Menu BG.png");
        BackgroundSize bgSize = new BackgroundSize(
                100, 100, true, true, false, true
        );
        root.setBackground(new Background(new BackgroundImage(
                bg,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bgSize
        )));

        // --- TOP‑RIGHT BUTTONS ---
        Button soundBtn     = createImageButton("Sound Button.png", 110);
        Button howToPlayBtn = createImageButton("How to Play Button.png", 110);
        soundBtn.setOnAction(e -> toggleSound());
        howToPlayBtn.setOnAction(e -> showHowToPlay());

        HBox topRight = new HBox(10, soundBtn, howToPlayBtn);
        topRight.setAlignment(Pos.CENTER_RIGHT);
        topRight.setPadding(new Insets(5, 5, 0, 0));
        root.setTop(topRight);

        // --- CENTER: BIGGER LOGO + MAIN BUTTONS
        ImageView logo = new ImageView(new Image("file:res/Word War Z Logo.png"));
        logo.setPreserveRatio(true);
        logo.setFitWidth(800);

        Button playBtn = createImageButton("Play Button.png", 250);
        Button quitBtn = createImageButton("Quit Button.png", 250);
        playBtn.setOnAction(e -> startGame());
        quitBtn.setOnAction(e -> Platform.exit());

        VBox centerBox = new VBox(20, logo, playBtn, quitBtn);
        centerBox.setAlignment(Pos.CENTER);

        centerBox.setTranslateY(-60);
        BorderPane.setAlignment(centerBox, Pos.CENTER);
        root.setCenter(centerBox);

        // --- SCENE & STAGE ---
        Scene scene = new Scene(root);
        primaryStage.setTitle("Word War Z");
        primaryStage.getIcons().add(new Image("file:res/Word War Z Logo.png"));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    private Button createImageButton(String imageName, double fitWidth) {
        ImageView iv = new ImageView(new Image("file:res/" + imageName));
        iv.setPreserveRatio(true);
        iv.setFitWidth(fitWidth);

        Button btn = new Button();
        btn.setGraphic(iv);
        btn.setBackground(Background.EMPTY);
        btn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Hover effect
        btn.setOnMouseEntered(e ->
                btn.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 0, 0, 0, 0)")
        );
        btn.setOnMouseExited(e ->
                btn.setStyle("-fx-cursor: default; -fx-effect: null")
        );

        btn.setOnMousePressed(e ->
                btn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;")
        );
        btn.setOnMouseReleased(e ->
                btn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;")
        );

        return btn;
    }

    private void startGame() {
        System.out.println("Launching Word War Z...");
    }

    private void showHowToPlay() {
        System.out.println("Showing how to play...");
    }

    private void toggleSound() {
        System.out.println("Toggling sound...");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
