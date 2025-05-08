package client.player.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.File;

public class MainMenuView {

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

    // Font
    private Font pressStartFont;

    //for character selection
    private int selectedCharacterIndex = 1; // Default to character 1

    public int getSelectedCharacterIndex() {
        return selectedCharacterIndex;
    }

    // Image fields
    private final Image soundOnImg = new Image("file:res/images/buttons/menu buttons/Sound On.png");
    private final Image soundOffImg = new Image("file:res/images/buttons/menu buttons/Mute.png");

    public void initializeUI(Stage primaryStage) {
        // Load font
        try {
            pressStartFont = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 20);
        } catch (Exception e) {
            System.err.println("Failed to load font: res/fonts/PressStart2P-Regular.ttf. Using default font.");
            pressStartFont = Font.font("System", 20);
        }

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
        soundButtonView = new ImageView(soundOnImg);
        soundButtonView.setPreserveRatio(true);
        soundButtonView.setFitWidth(90);
        soundButton = new Button();
        soundButton.setGraphic(soundButtonView);
        soundButton.setBackground(Background.EMPTY);
        soundButton.setPadding(Insets.EMPTY);

        // --- HOW TO PLAY BUTTON ---
        Image howToPlayImg = new Image("file:res/images/buttons/menu buttons/How to Play.png");
        ImageView howToPlayView = new ImageView(howToPlayImg);
        howToPlayView.setPreserveRatio(true);
        howToPlayView.setFitWidth(90);
        howToPlayBtn = new Button();
        howToPlayBtn.setGraphic(howToPlayView);
        howToPlayBtn.setBackground(Background.EMPTY);
        howToPlayBtn.setPadding(Insets.EMPTY);

        // --- CHARACTER SELECTION ---
        // Create container for character selection section
        VBox characterSelectionContainer = new VBox(5); // 5px spacing between elements
        characterSelectionContainer.setAlignment(Pos.CENTER_LEFT);
        characterSelectionContainer.setPadding(new Insets(10, 0, 0, 20));

        // Create character buttons container
        HBox characterButtonContainer = new HBox(10);
        characterButtonContainer.setAlignment(Pos.CENTER_LEFT);

        // Create 5 character buttons with frames
        for (int i = 1; i <= 5; i++) {
            // Load character image (use placeholder if not available)
            Image charImage;
            try {
                charImage = new Image("file:res/images/buttons/menu buttons/char" + i + ".png");
            } catch (Exception e) {
                charImage = new Image("file:res/images/buttons/menu buttons/placeholder.png");
            }


            // Create image view with uniform sizing
            ImageView charView = new ImageView(charImage);
            charView.setPreserveRatio(true);
            charView.setFitWidth(80);
            charView.setFitHeight(80);

            // Create frame background
            Image frameImage = new Image("file:res/images/frames/character_frame.png");
            ImageView frameView = new ImageView(frameImage);
            frameView.setPreserveRatio(true);
            frameView.setFitWidth(90);
            frameView.setFitHeight(90);

            // Stack frame and character image
            StackPane framedCharacter = new StackPane();
            framedCharacter.getChildren().addAll(frameView, charView);

            // Create button with identical style to sound/how-to-play
            Button charButton = new Button();
            charButton.setGraphic(framedCharacter);
            charButton.setBackground(Background.EMPTY);
            charButton.setPadding(Insets.EMPTY);

            // Add hover effect
            charButton.setOnMouseEntered(e -> {
                charButton.setCursor(Cursor.HAND);
                charView.setEffect(new Glow(0.5));
            });
            charButton.setOnMouseExited(e -> {
                charView.setEffect(null);
            });

            // Add selection handler
            final int charIndex = i;
            charButton.setOnAction(e -> {
                selectedCharacterIndex = charIndex;
                System.out.println("Character " + charIndex + " selected");
                // Add visual feedback for selected character
                for (Node node : characterButtonContainer.getChildren()) {
                    if (node instanceof Button) {
                        Button btn = (Button) node;
                        if (btn == charButton) {
                            btn.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(255,255,0,0.8), 10, 0, 0, 0);");
                        } else {
                            btn.setStyle("");
                        }
                    }
                }
            });
            characterButtonContainer.getChildren().add(charButton);
        }

        // Create "CHARACTER SELECT" label with custom font
        javafx.scene.control.Label characterSelectLabel = new javafx.scene.control.Label("CHARACTER SELECT");
        characterSelectLabel.setFont(pressStartFont);
        characterSelectLabel.setTextFill(Color.WHITE);
        characterSelectLabel.setStyle("-fx-font-size: 12px;"); // Adjust size as needed

        // Add components to character selection container
        characterSelectionContainer.getChildren().addAll(characterButtonContainer, characterSelectLabel);

        // --- TOP RIGHT BOX (existing buttons) ---
        HBox topRightBox = new HBox(10, soundButton, howToPlayBtn);
        topRightBox.setAlignment(Pos.TOP_RIGHT);
        topRightBox.setPadding(new Insets(10));
        topRightBox.setBackground(Background.EMPTY);

        // --- COMBINED TOP SECTION ---
        BorderPane topRegion = new BorderPane();
        topRegion.setLeft(characterSelectionContainer);
        topRegion.setRight(topRightBox);
        mainContent.setTop(topRegion);



        // --- LOGO ---
        Image logoImg = new Image("file:res/images/others/word war z logo.png");
        ImageView logoView = new ImageView(logoImg);
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(300);

        // --- PLAY BUTTON ---
        playBtn = new Button("PLAY");
        playBtn.setFont(pressStartFont);
        playBtn.setTextFill(Color.WHITE);
        playBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        playBtn.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        playBtn.setPadding(new Insets(10, 80, 10, 80));
        playBtn.setPrefWidth(450);
        playBtn.setPrefHeight(60);
        playBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        );
        playBtn.setOnMouseEntered(e -> playBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        playBtn.setOnMouseExited(e -> playBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        playBtn.setOnMousePressed(e -> playBtn.setStyle(
                "-fx-background-color: grey;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        playBtn.setOnMouseReleased(e -> playBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));

        // --- LEADERBOARD BUTTON ---
        leaderboardBtn = new Button("LEADERBOARD");
        leaderboardBtn.setFont(pressStartFont);
        leaderboardBtn.setTextFill(Color.WHITE);
        leaderboardBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        leaderboardBtn.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        leaderboardBtn.setPadding(new Insets(10, 80, 10, 80));
        leaderboardBtn.setPrefWidth(450);
        leaderboardBtn.setPrefHeight(60);
        leaderboardBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        );
        leaderboardBtn.setOnMouseEntered(e -> leaderboardBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        leaderboardBtn.setOnMouseExited(e -> leaderboardBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        leaderboardBtn.setOnMousePressed(e -> leaderboardBtn.setStyle(
                "-fx-background-color: grey;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        leaderboardBtn.setOnMouseReleased(e -> leaderboardBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));

        // --- QUIT BUTTON ---
        quitBtn = new Button("QUIT");
        quitBtn.setFont(pressStartFont);
        quitBtn.setTextFill(Color.WHITE);
        quitBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        quitBtn.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        quitBtn.setPadding(new Insets(10, 80, 10, 80));
        quitBtn.setPrefWidth(450);
        quitBtn.setPrefHeight(60);
        quitBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        );
        quitBtn.setOnMouseEntered(e -> quitBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        quitBtn.setOnMouseExited(e -> quitBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        quitBtn.setOnMousePressed(e -> quitBtn.setStyle(
                "-fx-background-color: grey;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        quitBtn.setOnMouseReleased(e -> quitBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        quitBtn.setOnAction(e -> {
            if (quitButtonHandler != null) {
                quitButtonHandler.handle();
            }
            Stage stage = (Stage) quitBtn.getScene().getWindow();
            stage.close();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });

        // --- CENTER BOX ---
        VBox centerBox = new VBox(20, logoView, playBtn, leaderboardBtn, quitBtn);
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

        //CLOSE HOW TO PLAY BUTTON
        closeHowToPlayBtn = new Button("CLOSE");
        closeHowToPlayBtn.setFont(pressStartFont);
        closeHowToPlayBtn.setTextFill(Color.WHITE);
        closeHowToPlayBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        closeHowToPlayBtn.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        closeHowToPlayBtn.setPadding(new Insets(10, 80, 10, 80));
        closeHowToPlayBtn.setPrefWidth(450);
        closeHowToPlayBtn.setPrefHeight(60);
        closeHowToPlayBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        );


        closeHowToPlayBtn.setOnMouseEntered(e -> closeHowToPlayBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));

        closeHowToPlayBtn.setOnMouseExited(e -> closeHowToPlayBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));

        closeHowToPlayBtn.setOnMousePressed(e -> closeHowToPlayBtn.setStyle(
                "-fx-background-color: grey;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));

        closeHowToPlayBtn.setOnMouseReleased(e -> closeHowToPlayBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));

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
        primaryStage.setTitle("Word War Z - Main Menu");
        primaryStage.getIcons().add(new Image("file:res/images/others/word war z logo.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

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
        playBtn.setOnAction(e -> {
            System.out.println("Play button clicked!");
            handler.handle();
        });
    }

    public void setLeaderboardButtonHandler(LeaderboardButtonHandler handler) {
        this.leaderboardButtonHandler = handler;
        leaderboardBtn.setOnAction(e -> {
            System.out.println("Leaderboard button clicked!");
            handler.handle();
        });
    }

    public void setHowToPlayButtonHandler(HowToPlayButtonHandler handler) {
        this.howToPlayButtonHandler = handler;
        howToPlayBtn.setOnAction(e -> {
            System.out.println("How to Play button clicked!");
            overlayPane.setVisible(true);
            handler.handle();
        });
    }

    public void setQuitButtonHandler(QuitButtonHandler handler) {
        this.quitButtonHandler = handler;
        quitBtn.setOnAction(e -> {
            System.out.println("Quit button clicked!");
            handler.handle();
            Stage stage = (Stage) quitBtn.getScene().getWindow();
            stage.close();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });
    }

    public void setSoundToggleHandler(SoundToggleHandler handler) {
        this.soundToggleHandler = handler;
        soundButton.setOnAction(e -> {
            System.out.println("Sound button clicked! Muted: " + !isMuted);
            isMuted = !isMuted;
            mediaPlayer.setMute(isMuted);
            soundButtonView.setImage(isMuted ? soundOffImg : soundOnImg);
            if (handler != null) {
                handler.handle(isMuted);
            }
        });
    }

    public void showHowToPlay() {
        overlayPane.setVisible(true);
    }
}