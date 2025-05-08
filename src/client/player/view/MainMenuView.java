package client.player.view;

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

public class MainMenuView {

    // Media controls
    private MediaPlayer mediaPlayer;
    private boolean isMuted = false;

    // UI Components
    private StackPane overlayPane; // For How to Play
    private StackPane characterOverlayPane; // For Character Selection
    private Button playBtn;
    private Button leaderboardBtn;
    private Button howToPlayBtn;
    private Button quitBtn;
    private Button soundButton;
    private Button creditsBtn;
    private Button closeHowToPlayBtn;
    private Button characterSelectBtn; // New button to open character selection overlay
    private Button closeCharacterOverlayBtn; // Close button for character selection overlay

    // Fonts
    private Font pressStartFont;
    private Font pressStartFontLarge; // Larger font for How to Play button

    // For character selection
    private int selectedCharacterIndex = 1; // Default to character 1
    private ImageView[] characterViews; // To track character ImageViews for glow effect
    private Timeline[] characterAnimations; // To manage animations for each character

    public int getSelectedCharacterIndex() {
        return selectedCharacterIndex;
    }

    // Helper method to update glow effect based on selectedCharacterIndex
    private void updateGlowEffect() {
        for (int j = 0; j < characterViews.length; j++) {
            if (j + 1 == selectedCharacterIndex) {
                characterViews[j].setEffect(new Glow(0.8));
            } else {
                characterViews[j].setEffect(null);
            }
        }
    }

    // Helper method to stop all animations
    private void stopAllAnimations() {
        for (Timeline animation : characterAnimations) {
            if (animation != null) {
                animation.stop();
            }
        }
    }

    // Helper method to start animation for the selected character
    private void manageSelectedAnimation() {
        stopAllAnimations();
        if (selectedCharacterIndex >= 1 && selectedCharacterIndex <= 4) {
            characterAnimations[selectedCharacterIndex - 1].play();
        }
    }

    public void initializeUI(Stage primaryStage) {
        // Load PressStart2P font
        try {
            pressStartFont = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 20);
            pressStartFontLarge = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 30); // Larger font size for How to Play button
        } catch (Exception e) {
            System.err.println("Failed to load font: res/fonts/PressStart2P-Regular.ttf. Using default font.");
            pressStartFont = Font.font("System", 20);
            pressStartFontLarge = Font.font("System", 30);
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
        soundButton = new Button();
        Image soundOnDefaultImg = new Image("file:res/images/buttons/menu buttons/sound on-1.png");
        Image soundOnHoverImg = new Image("file:res/images/buttons/menu buttons/sound on-2.png");
        Image soundOffDefaultImg = new Image("file:res/images/buttons/menu buttons/mute-1.png");
        Image soundOffHoverImg = new Image("file:res/images/buttons/menu buttons/mute-2.png");

        ImageView soundView = new ImageView(!isMuted ? soundOnDefaultImg : soundOffDefaultImg);
        soundView.setPreserveRatio(true);
        soundView.setFitHeight(50); // Reduced height to make image and button smaller
        soundButton.setGraphic(soundView);
        soundButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        soundButton.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        soundButton.setPadding(new Insets(10, 20, 10, 20));
        soundButton.setPrefWidth(-1); // Auto-size to image width
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
        soundButton.setOnAction(e -> {
            boolean newMutedState = !isMuted;
            System.out.println("Sound button clicked! Muted: " + newMutedState);
            isMuted = newMutedState;
            mediaPlayer.setMute(isMuted);
            soundView.setImage(isMuted ? soundOffDefaultImg : soundOnDefaultImg);
            if (soundToggleHandler != null) {
                soundToggleHandler.handle(isMuted);
            }
        });

        // --- HOW TO PLAY BUTTON ---
        howToPlayBtn = new Button("?");
        howToPlayBtn.setFont(pressStartFontLarge); // Use larger font
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
        Image charSelectImg = new Image("file:res/images/buttons/menu buttons/char1.png");
        ImageView charSelectView = new ImageView(charSelectImg);
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
            manageSelectedAnimation(); // Ensure selected character's animation plays when overlay opens
        });

        // --- CHARACTER SELECTION OVERLAY ---
        characterOverlayPane = new StackPane();
        characterOverlayPane.setBackground(new Background(new BackgroundFill(
                new Color(0, 0, 0, 0.7), CornerRadii.EMPTY, Insets.EMPTY)));
        characterOverlayPane.setVisible(false);

        // Initialize character views and animations arrays
        characterViews = new ImageView[5]; // 4 characters + 1 randomizer
        characterAnimations = new Timeline[4]; // Only for the 4 animated characters

        // Create HBox for characters
        HBox characterContainer = new HBox(20);
        characterContainer.setAlignment(Pos.CENTER);

        // Add 4 characters (human1 to human4)
        for (int i = 1; i <= 4; i++) {
            final int index = i; // Final variable for lambda
            Image frame1 = new Image("file:res/images/characters/human/idle/human" + index + "-idle-1.png");
            Image frame2 = new Image("file:res/images/characters/human/idle/human" + index + "-idle-2.png");
            final Image[] frames = new Image[]{frame1, frame2};
            ImageView charView = new ImageView(frame1);
            charView.setFitWidth(200);
            charView.setFitHeight(200);
            characterViews[index - 1] = charView;

            // Create animation for this character
            Timeline animation = new Timeline(new KeyFrame(Duration.millis(250), event -> {
                if (charView.getImage() == frames[0]) {
                    charView.setImage(frames[1]);
                } else {
                    charView.setImage(frames[0]);
                }
            }));
            animation.setCycleCount(Timeline.INDEFINITE);
            characterAnimations[index - 1] = animation;

            // Create button for the character
            Button charButton = new Button();
            charButton.setGraphic(charView);
            charButton.setBackground(Background.EMPTY);
            charButton.setPadding(Insets.EMPTY);

            // Hover effects
            charButton.setOnMouseEntered(e -> {
                charView.setEffect(new Glow(0.5));
                if (selectedCharacterIndex != index) {
                    animation.play();
                }
            });
            charButton.setOnMouseExited(e -> {
                updateGlowEffect();
                if (selectedCharacterIndex != index) {
                    animation.stop();
                    charView.setImage(frame1); // Reset to first frame if not selected
                }
            });

            // Selection logic
            charButton.setOnAction(e -> {
                selectedCharacterIndex = index;
                System.out.println("Character " + index + " selected");
                updateGlowEffect();
                manageSelectedAnimation();
            });

            characterContainer.getChildren().add(charButton);
        }

        // Add Randomizer (char5.png)
        Image randomizerImg = new Image("file:res/images/buttons/menu buttons/char5.png");
        ImageView randomizerView = new ImageView(randomizerImg);
        randomizerView.setFitWidth(75);
        randomizerView.setFitHeight(75);
        characterViews[4] = randomizerView;

        Button randomizerButton = new Button();
        randomizerButton.setGraphic(randomizerView);
        randomizerButton.setBackground(Background.EMPTY);
        randomizerButton.setPadding(Insets.EMPTY);

        // Hover effects for randomizer
        randomizerButton.setOnMouseEntered(e -> randomizerView.setEffect(new Glow(0.5)));
        randomizerButton.setOnMouseExited(e -> updateGlowEffect());

        // Selection logic for randomizer
        randomizerButton.setOnAction(e -> {
            selectedCharacterIndex = 5;
            System.out.println("Randomizer (Character 5) selected");
            updateGlowEffect();
            stopAllAnimations(); // Randomizer has no animation
        });

        characterContainer.getChildren().add(randomizerButton);

        // --- CLOSE BUTTON FOR CHARACTER OVERLAY ---
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
        });
        VBox.setMargin(closeCharacterOverlayBtn, new Insets(-30, 0, 0, 0));

        // --- ADD CHARACTER SELECT TEXT ---
        Label characterSelectText = new Label("CHARACTER SELECT");
        characterSelectText.setFont(pressStartFont);
        characterSelectText.setTextFill(Color.WHITE);
        characterSelectText.setAlignment(Pos.CENTER);

        // Add character container, text, and close button to overlay
        VBox characterOverlayContent = new VBox(50, characterSelectText, characterContainer, closeCharacterOverlayBtn);
        characterOverlayContent.setAlignment(Pos.CENTER);
        characterOverlayPane.getChildren().add(characterOverlayContent);

        // --- TOP LEFT: CHARACTER SELECT BUTTON ---
        VBox characterSelectContainer = new VBox(5);
        characterSelectContainer.setAlignment(Pos.CENTER_LEFT);
        characterSelectContainer.setPadding(new Insets(10, 0, 0, 20));
        characterSelectContainer.getChildren().add(characterSelectBtn);

        // --- TOP RIGHT BOX ---
        HBox topRightBox = new HBox(10, soundButton, howToPlayBtn);
        topRightBox.setAlignment(Pos.TOP_RIGHT);
        topRightBox.setPadding(new Insets(10));
        topRightBox.setBackground(Background.EMPTY);

        // --- COMBINED TOP SECTION ---
        BorderPane topRegion = new BorderPane();
        topRegion.setLeft(characterSelectContainer);
        topRegion.setRight(topRightBox);
        mainContent.setTop(topRegion);

        // --- LOGO ---
        Image logoImg = new Image("file:res/images/others/word war z logo.png");
        ImageView logoView = new ImageView(logoImg);
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(300);
        logoView.setY(150);

        // --- PLAY BUTTON ---
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

        // --- LEADERBOARD BUTTON ---
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

        // --- QUIT BUTTON ---
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
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });

        // --- CREDITS BUTTON ---
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

        // --- CENTER BOX ---
        VBox centerBox = new VBox(20);
        centerBox.getChildren().addAll(logoView, playBtn, leaderboardBtn, creditsBtn, quitBtn);
        centerBox.setAlignment(Pos.TOP_CENTER);
        mainContent.setCenter(centerBox);
        BorderPane.setMargin(centerBox, new Insets(-25, 0, 0, 0));

        // --- OVERLAY PANE (How to Play) ---
        overlayPane = new StackPane();
        overlayPane.setBackground(new Background(new BackgroundFill(
                new Color(0, 0, 0, 0.7), CornerRadii.EMPTY, Insets.EMPTY)));
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

        // --- PLAY MUSIC ---
        File musicFile = new File("res/music/menu music.mp3");
        Media media = new Media(musicFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        // --- SETUP SCENE ---
        root.getChildren().addAll(mainContent, overlayPane, characterOverlayPane);
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
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });
    }

    public void setSoundToggleHandler(SoundToggleHandler handler) {
        this.soundToggleHandler = handler;
        soundButton.setOnAction(e -> {
            boolean newMutedState = !isMuted;
            System.out.println("Sound button clicked! Muted: " + newMutedState);
            isMuted = newMutedState;
            ((ImageView) soundButton.getGraphic()).setImage(isMuted ? new Image("file:res/images/buttons/menu buttons/mute-1.png") : new Image("file:res/images/buttons/menu buttons/sound on-1.png"));
            mediaPlayer.setMute(isMuted);
            if (handler != null) {
                soundToggleHandler.handle(isMuted);
            }
        });
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
}