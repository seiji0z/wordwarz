package client.player.view;

import client.player.controller.MainMenuController;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.util.Random;

public class MainMenuView {
    // Media controls
    private MediaPlayer mediaPlayer;
    private boolean isMuted = false;

    // UI Components
    private StackPane root;
    private StackPane overlayPane;
    private StackPane characterOverlayPane;
    private Button playBtn;
    private Button leaderboardBtn;
    private Button howToPlayBtn;
    private Button quitBtn;
    private Button soundButton;
    private Button creditsBtn;
    private Button closeHowToPlayBtn;
    private Button characterSelectBtn;
    private Button closeCharacterOverlayBtn;
    private ImageView charSelectView;

    // Scene management
    private Scene scene; // Store the scene for reuse

    // Fonts
    private Font pressStartFont;
    private Font pressStartFontLarge;

    // For character selection
    private int selectedCharacterIndex = 1;
    private ImageView[] characterViews;
    private Timeline[] characterAnimations;
    private Image[] characterHeadImages;

    // Random number generator for randomizer
    private Random random = new Random();

    public int getSelectedCharacterIndex() {
        return selectedCharacterIndex;
    }

    public Scene getScene() {
        return scene;
    }

    // Add getters for media control
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean isMuted() {
        return isMuted;
    }

    private void updateGlowEffect() {
        for (int j = 0; j < characterViews.length; j++) {
            if (j + 1 == selectedCharacterIndex) {
                characterViews[j].setEffect(new Glow(0.8));
            } else {
                characterViews[j].setEffect(null);
            }
        }
    }

    private void stopAllAnimations() {
        for (Timeline animation : characterAnimations) {
            if (animation != null) {
                animation.stop();
            }
        }
    }

    private void manageSelectedAnimation() {
        stopAllAnimations();
        if (selectedCharacterIndex >= 1 && selectedCharacterIndex <= 4) {
            characterAnimations[selectedCharacterIndex - 1].play();
        }
    }

    private void updateCharacterSelectButtonImage() {
        charSelectView.setImage(characterHeadImages[selectedCharacterIndex - 1]);
    }

    public StackPane initializeUI(Stage primaryStage) {
        // If the root already exists, just return it (avoid rebuilding the UI)
        if (root != null) {
            // Resume media if it exists
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
            return root;
        }

        // Load PressStart2P font
        try {
            pressStartFont = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 20);
            pressStartFontLarge = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 30);
        } catch (Exception e) {
            System.err.println("Failed to load font: res/fonts/PressStart2P-Regular.ttf. Using default font.");
            pressStartFont = Font.font("System", 20);
            pressStartFontLarge = Font.font("System", 30);
        }

        // Preload character head images (char1.png to char4.png)
        characterHeadImages = new Image[4];
        for (int i = 1; i <= 4; i++) {
            characterHeadImages[i - 1] = new Image("file:res/images/buttons/menu buttons/char" + i + ".png");
        }

        // Main root container
        root = new StackPane();
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
        soundButton = new Button();
        Image soundOnDefaultImg = new Image("file:res/images/buttons/menu buttons/sound on-1.png");
        Image soundOnHoverImg = new Image("file:res/images/buttons/menu buttons/sound on-2.png");
        Image soundOffDefaultImg = new Image("file:res/images/buttons/menu buttons/mute-1.png");
        Image soundOffHoverImg = new Image("file:res/images/buttons/menu buttons/mute-2.png");

        ImageView soundView = new ImageView(isMuted ? soundOffDefaultImg : soundOnDefaultImg);
        soundView.setPreserveRatio(true);
        soundView.setFitHeight(50);
        soundButton.setGraphic(soundView);
        soundButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        soundButton.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        soundButton.setPadding(new Insets(10, 20, 10, 20));
        soundButton.setPrefWidth(-1);
        soundButton.setPrefHeight(70);
        soundButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        );
        soundButton.setOnMouseEntered(e -> {
            soundView.setImage(isMuted ? soundOffHoverImg : soundOnHoverImg);
            soundButton.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-border-color: white;" +
                            "-fx-border-width: 2px;" +
                            "-fx-cursor: hand;"
            );
        });
        soundButton.setOnMouseExited(e -> {
            soundView.setImage(isMuted ? soundOffDefaultImg : soundOnDefaultImg);
            soundButton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-border-color: white;" +
                            "-fx-border-width: 2px;" +
                            "-fx-cursor: hand;"
            );
        });
        soundButton.setOnMousePressed(e -> {
            soundView.setImage(isMuted ? soundOffHoverImg : soundOnHoverImg);
            soundButton.setStyle(
                    "-fx-background-color: grey;" +
                            "-fx-border-color: white;" +
                            "-fx-border-width: 2px;" +
                            "-fx-cursor: hand;"
            );
        });
        soundButton.setOnMouseReleased(e -> {
            soundView.setImage(isMuted ? soundOffDefaultImg : soundOnDefaultImg);
            soundButton.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-border-color: white;" +
                            "-fx-border-width: 2px;" +
                            "-fx-cursor: hand;"
            );
        });

        // --- HOW TO PLAY BUTTON ---
        howToPlayBtn = new Button("?");
        howToPlayBtn.setFont(pressStartFontLarge);
        howToPlayBtn.setTextFill(Color.WHITE);
        howToPlayBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        howToPlayBtn.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        howToPlayBtn.setPadding(new Insets(10, 20, 10, 20));
        howToPlayBtn.setPrefWidth(100);
        howToPlayBtn.setPrefHeight(74);
        howToPlayBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        );
        howToPlayBtn.setOnMouseEntered(e -> howToPlayBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        howToPlayBtn.setOnMouseExited(e -> howToPlayBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        howToPlayBtn.setOnMousePressed(e -> howToPlayBtn.setStyle(
                "-fx-background-color: grey;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        howToPlayBtn.setOnMouseReleased(e -> howToPlayBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        howToPlayBtn.setOnAction(ev -> overlayPane.setVisible(true));

        // --- CHARACTER SELECT BUTTON ---
        characterSelectBtn = new Button();
        charSelectView = new ImageView(characterHeadImages[0]);
        charSelectView.setPreserveRatio(true);
        charSelectView.setFitWidth(60);
        charSelectView.setFitHeight(60);
        characterSelectBtn.setGraphic(charSelectView);
        characterSelectBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        characterSelectBtn.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        characterSelectBtn.setPadding(new Insets(10, 20, 10, 20));
        characterSelectBtn.setPrefWidth(80);
        characterSelectBtn.setPrefHeight(54);
        characterSelectBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        );
        characterSelectBtn.setOnMouseEntered(e -> characterSelectBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        characterSelectBtn.setOnMouseExited(e -> characterSelectBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        characterSelectBtn.setOnMousePressed(e -> characterSelectBtn.setStyle(
                "-fx-background-color: grey;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        characterSelectBtn.setOnMouseReleased(e -> characterSelectBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        characterSelectBtn.setOnAction(e -> {
            characterOverlayPane.setVisible(true);
            manageSelectedAnimation();
        });

        // --- CHARACTER SELECTION OVERLAY ---
        characterOverlayPane = new StackPane();
        characterOverlayPane.setBackground(new Background(new BackgroundFill(
                new Color(0, 0, 0, 0.8), CornerRadii.EMPTY, Insets.EMPTY)));
        characterOverlayPane.setVisible(false);

        characterViews = new ImageView[5];
        characterAnimations = new Timeline[4];

        HBox characterContainer = new HBox(20);
        characterContainer.setAlignment(Pos.CENTER);

        for (int i = 1; i <= 4; i++) {
            final int index = i;
            Image frame1 = new Image("file:res/images/characters/human/idle/human" + index + "-idle-1.png");
            Image frame2 = new Image("file:res/images/characters/human/idle/human" + index + "-idle-2.png");
            final Image[] frames = new Image[]{frame1, frame2};
            ImageView charView = new ImageView(frame1);
            charView.setFitWidth(200);
            charView.setFitHeight(200);
            characterViews[index - 1] = charView;

            Timeline animation = new Timeline(new KeyFrame(Duration.millis(250), event -> {
                if (charView.getImage() == frames[0]) {
                    charView.setImage(frames[1]);
                } else {
                    charView.setImage(frames[0]);
                }
            }));
            animation.setCycleCount(Timeline.INDEFINITE);
            characterAnimations[index - 1] = animation;

            Button charButton = new Button();
            charButton.setGraphic(charView);
            charButton.setBackground(Background.EMPTY);
            charButton.setPadding(Insets.EMPTY);

            charButton.setOnMouseEntered(e -> charView.setEffect(new Glow(0.5)));
            charButton.setOnMouseExited(e -> {
                updateGlowEffect();
                if (selectedCharacterIndex != index) {
                    charView.setImage(frame1);
                }
            });

            charButton.setOnAction(e -> {
                selectedCharacterIndex = index;
                System.out.println("Character " + index + " selected");
                updateGlowEffect();
                manageSelectedAnimation();
                updateCharacterSelectButtonImage();
            });

            characterContainer.getChildren().add(charButton);
        }

        Image randomizerImg = new Image("file:res/images/buttons/menu buttons/randomizer.png");
        ImageView randomizerView = new ImageView(randomizerImg);
        randomizerView.setFitWidth(75);
        randomizerView.setFitHeight(75);
        characterViews[4] = randomizerView;

        Button randomizerButton = new Button();
        randomizerButton.setGraphic(randomizerView);
        randomizerButton.setBackground(Background.EMPTY);
        randomizerButton.setPadding(Insets.EMPTY);

        randomizerButton.setOnMouseEntered(e -> randomizerView.setEffect(new Glow(0.5)));
        randomizerButton.setOnMouseExited(e -> updateGlowEffect());

        randomizerButton.setOnAction(e -> {
            selectedCharacterIndex = random.nextInt(4) + 1;
            System.out.println("Random character selected: Character " + selectedCharacterIndex);
            updateGlowEffect();
            manageSelectedAnimation();
            updateCharacterSelectButtonImage();
        });

        characterContainer.getChildren().add(randomizerButton);

        closeCharacterOverlayBtn = new Button("CLOSE");
        closeCharacterOverlayBtn.setFont(pressStartFont);
        closeCharacterOverlayBtn.setTextFill(Color.WHITE);
        closeCharacterOverlayBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        closeCharacterOverlayBtn.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        closeCharacterOverlayBtn.setPadding(new Insets(10, 80, 10, 80));
        closeCharacterOverlayBtn.setPrefWidth(450);
        closeCharacterOverlayBtn.setPrefHeight(60);
        closeCharacterOverlayBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        );
        closeCharacterOverlayBtn.setOnMouseEntered(e -> closeCharacterOverlayBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        closeCharacterOverlayBtn.setOnMouseExited(e -> closeCharacterOverlayBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        closeCharacterOverlayBtn.setOnMousePressed(e -> closeCharacterOverlayBtn.setStyle(
                "-fx-background-color: grey;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        closeCharacterOverlayBtn.setOnMouseReleased(e -> closeCharacterOverlayBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        closeCharacterOverlayBtn.setOnAction(e -> {
            characterOverlayPane.setVisible(false);
            stopAllAnimations();
            updateCharacterSelectButtonImage();
        });
        VBox.setMargin(closeCharacterOverlayBtn, new Insets(20, 0, 0, 0));

        Label characterSelectText = new Label("CHARACTER SELECT");
        characterSelectText.setFont(Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 40));
        characterSelectText.setTextFill(Color.WHITE);
        characterSelectText.setAlignment(Pos.CENTER);

        VBox characterOverlayContent = new VBox(50, characterSelectText, characterContainer, closeCharacterOverlayBtn);
        characterOverlayContent.setAlignment(Pos.CENTER);
        characterOverlayPane.getChildren().add(characterOverlayContent);

        VBox characterSelectContainer = new VBox(5);
        characterSelectContainer.setAlignment(Pos.CENTER_LEFT);
        characterSelectContainer.setPadding(new Insets(10, 0, 0, 20));
        characterSelectContainer.getChildren().add(characterSelectBtn);

        HBox topRightBox = new HBox(10, soundButton, howToPlayBtn);
        topRightBox.setAlignment(Pos.TOP_RIGHT);
        topRightBox.setPadding(new Insets(10));
        topRightBox.setBackground(Background.EMPTY);

        BorderPane topRegion = new BorderPane();
        topRegion.setLeft(characterSelectContainer);
        topRegion.setRight(topRightBox);
        mainContent.setTop(topRegion);

        Image logoImg = new Image("file:res/images/others/word war z logo.png");
        ImageView logoView = new ImageView(logoImg);
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(300);
        logoView.setY(150);

        playBtn = new Button("PLAY");
        playBtn.setFont(pressStartFont);
        playBtn.setTextFill(Color.WHITE);
        playBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        playBtn.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        playBtn.setPadding(new Insets(10, 80, 10, 80));
        playBtn.setPrefWidth(450);
        playBtn.setPrefHeight(70);
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

        leaderboardBtn = new Button("LEADERBOARD");
        leaderboardBtn.setFont(pressStartFont);
        leaderboardBtn.setTextFill(Color.WHITE);
        leaderboardBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        leaderboardBtn.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        leaderboardBtn.setPadding(new Insets(10, 80, 10, 80));
        leaderboardBtn.setPrefWidth(450);
        leaderboardBtn.setPrefHeight(70);
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

        quitBtn = new Button("QUIT");
        quitBtn.setFont(pressStartFont);
        quitBtn.setTextFill(Color.WHITE);
        quitBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        quitBtn.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        quitBtn.setPadding(new Insets(10, 80, 10, 80));
        quitBtn.setPrefWidth(450);
        quitBtn.setPrefHeight(70);
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
            close();
        });

        creditsBtn = new Button("CREDITS");
        creditsBtn.setFont(pressStartFont);
        creditsBtn.setTextFill(Color.WHITE);
        creditsBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        creditsBtn.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        creditsBtn.setPadding(new Insets(10, 80, 10, 80));
        creditsBtn.setPrefWidth(450);
        creditsBtn.setPrefHeight(70);
        creditsBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        );
        creditsBtn.setOnMouseEntered(e -> creditsBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        creditsBtn.setOnMouseExited(e -> creditsBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        creditsBtn.setOnMousePressed(e -> creditsBtn.setStyle(
                "-fx-background-color: grey;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        creditsBtn.setOnMouseReleased(e -> creditsBtn.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        creditsBtn.setOnAction(e -> {
            System.out.println("Credits button clicked!");
            if (creditsButtonHandler != null) {
                creditsButtonHandler.handle();
            }
        });

        VBox centerBox = new VBox(20);
        centerBox.getChildren().addAll(logoView, playBtn, leaderboardBtn, creditsBtn, quitBtn);
        centerBox.setAlignment(Pos.TOP_CENTER);
        mainContent.setCenter(centerBox);
        BorderPane.setMargin(centerBox, new Insets(-25, 0, 0, 0));

        overlayPane = new StackPane();
        overlayPane.setBackground(new Background(new BackgroundFill(
                new Color(0, 0, 0, 0.8), CornerRadii.EMPTY, Insets.EMPTY)));
        overlayPane.setVisible(false);

        Image howToPlayScreen = new Image("file:res/images/frames/how to play.png");
        ImageView howToPlayFullScreen = new ImageView(howToPlayScreen);
        howToPlayFullScreen.setFitWidth(1000);
        howToPlayFullScreen.setFitHeight(650);
        howToPlayFullScreen.setPreserveRatio(false);

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
        closeHowToPlayBtn.setOnAction(e -> overlayPane.setVisible(false));
        VBox.setMargin(closeHowToPlayBtn, new Insets(-30, 0, 0, 0));

        VBox overlayContent = new VBox(5, howToPlayFullScreen, closeHowToPlayBtn);
        overlayContent.setAlignment(Pos.CENTER);
        overlayPane.getChildren().add(overlayContent);

        // Initialize MediaPlayer only once
        if (mediaPlayer == null) {
            File musicFile = new File("res/music/menu music.mp3");
            Media media = new Media(musicFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setMute(isMuted); // Set initial mute state
            mediaPlayer.play();
        }

        root.getChildren().addAll(mainContent, overlayPane, characterOverlayPane);

        // Create the scene only once
        if (scene == null) {
            scene = new Scene(root, 1280, 760);
            primaryStage.setTitle("Word War Z - Main Menu");
            primaryStage.getIcons().add(new Image("file:res/images/others/word war z logo.png"));
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        }

        return root;
    }

    public void pauseMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void close() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
        stopAllAnimations();
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

    public interface CreditsButtonHandler {
        void handle();
    }

    private PlayButtonHandler playButtonHandler;
    private LeaderboardButtonHandler leaderboardButtonHandler;
    private HowToPlayButtonHandler howToPlayButtonHandler;
    private QuitButtonHandler quitButtonHandler;
    private SoundToggleHandler soundToggleHandler;
    private CreditsButtonHandler creditsButtonHandler;

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
            close();
        });
    }

    public void setSoundToggleHandler(SoundToggleHandler handler) {
        this.soundToggleHandler = handler;
        soundButton.setOnAction(null); // Clear previous handler
        soundButton.setOnAction(e -> {
            boolean newMutedState = !isMuted;
            System.out.println("Sound button clicked! Muted: " + newMutedState);
            isMuted = newMutedState;
            if (mediaPlayer != null) {
                mediaPlayer.setMute(isMuted);
            }
            ((ImageView) soundButton.getGraphic()).setImage(isMuted ? new Image("file:res/images/buttons/menu buttons/mute-1.png") : new Image("file:res/images/buttons/menu buttons/sound on-1.png"));
            if (handler != null) {
                soundToggleHandler.handle(isMuted);
            }
        });
        // Update button state based on model (called by controller)
        if (handler instanceof MainMenuController) {
            isMuted = ((MainMenuController) handler).getModel().isSoundMuted();
            if (mediaPlayer != null) {
                mediaPlayer.setMute(isMuted);
            }
            ((ImageView) soundButton.getGraphic()).setImage(isMuted ? new Image("file:res/images/buttons/menu buttons/mute-1.png") : new Image("file:res/images/buttons/menu buttons/sound on-1.png"));
        }
    }

    public void setCreditsButtonHandler(CreditsButtonHandler handler) {
        this.creditsButtonHandler = handler;
        creditsBtn.setOnAction(e -> {
            System.out.println("Credits button clicked!");
            handler.handle();
        });
    }

    public void showHowToPlay() {
        overlayPane.setVisible(true);
    }

    public void closeApplication() {
        // Stop and clean up the MediaPlayer if it exists
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        // Stop all ongoing animations
        stopAllAnimations();

        // Hide overlays
        if (overlayPane != null) {
            overlayPane.setVisible(false);
        }
        if (characterOverlayPane != null) {
            characterOverlayPane.setVisible(false);
        }

        // Close the window (stage)
        if (playBtn != null && playBtn.getScene() != null) {
            Stage stage = (Stage) playBtn.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }
}