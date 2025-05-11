package client.player.view;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class GameView {
    private int selectedCharacter;
    private int randomIndex; // Store this to determine melee vs ranged
    private List<Text> letterTexts = new ArrayList<>();
    private Pane root;
    private Pane overlayPane;

    private ImageView humanView;
    private ImageView zombieView;
    private Image[] humanIdleFrames;
    private Image[] zombieIdleFrames;
    private Image[] humanRunFrames;
    private Image[] zombieRunFrames; // Added for zombie run animation
    private Image[] humanAttackFrames;
    private Image[] zombieDeathFrames;
    private Image[] zombieAttackFrames;
    private Image[] humanZombificationFrames;
    private Timeline humanIdleAnimation;
    private Timeline zombieIdleAnimation;
    private Timeline humanRunAnimation;
    private Timeline zombieRunAnimation; // Added for zombie run animation
    private Timeline humanAttackAnimation;
    private Timeline zombieDeathAnimation;
    private Timeline zombieAttackAnimation;
    private Timeline humanZombificationAnimation;
    private TranslateTransition humanRunTransition;
    private TranslateTransition zombieRunTransition;
    private Consumer<Character> letterGuessHandler;

    private int initialRoundDuration;
    private int timeRemaining;
    private Text timerText;
    private List<ImageView> hearts = new ArrayList<>();
    private Timeline timerTimeline;
    private Stage primaryStage;
    public int remainingGuesses = 5;
    private Consumer<Character> onLetterPressed;
    private boolean[] revealedLetters;
    private Runnable timeOutHandler;
    private Pane currentRoundOverlay;
    private boolean isStartingOverlayRemoved = false;
    private boolean isRoundDrawnOverlayShown = false;
    private boolean isGameOver = false;
    private boolean isZombieMoving = true;

    public GameView(Stage primaryStage, int selectedCharacter) {
        this.primaryStage = primaryStage;
        this.selectedCharacter = selectedCharacter;
        initialize();
    }

    public void initialize() {
        root = new Pane();

        Font customFont = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 40);
        if (customFont == null) {
            System.out.println("Failed to load custom font, falling back to default.");
            customFont = new Font("System", 30);
        }

        Image backgroundImage = new Image("file:res/images/backgrounds/game bg.png");
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setFitWidth(1280);
        backgroundView.setFitHeight(760);
        root.getChildren().add(backgroundView);

        for (int i = 0; i < 5; i++) {
            ImageView heart = new ImageView(new Image("file:res/images/others/heart.png"));
            heart.setX(30 + i * 60);
            heart.setY(20);
            heart.setFitWidth(50);
            heart.setFitHeight(50);
            hearts.add(heart);
            root.getChildren().add(heart);
        }

        timerText = new Text("00:30");
        timerText.setFont(customFont);
        timerText.setFill(Color.WHITE);
        timerText.setX(1045);
        timerText.setY(70);
        root.getChildren().add(timerText);

        String[] humanIdleImageFiles = {
                "human1-idle-1.png",
                "human2-idle-1.png",
                "human3-idle-1.png",
                "human4-idle-1.png",
        };

        if (selectedCharacter == 5) {
            randomIndex = new Random().nextInt(humanIdleImageFiles.length);
        } else {
            randomIndex = selectedCharacter - 1;
            randomIndex = Math.max(0, Math.min(randomIndex, humanIdleImageFiles.length - 1));
        }

        String selectedHumanImage = humanIdleImageFiles[randomIndex];

        humanIdleFrames = new Image[2];
        try {
            humanIdleFrames[0] = new Image("file:res/images/characters/human/idle/" + selectedHumanImage);
            humanIdleFrames[1] = new Image("file:res/images/characters/human/idle/" + selectedHumanImage.replace("-1.png", "-2.png"));
        } catch (Exception e) {
            System.out.println("Failed to load human idle animation frames: " + e.getMessage());
        }

        humanRunFrames = new Image[4];
        try {
            for (int i = 1; i <= 4; i++) {
                humanRunFrames[i - 1] = new Image("file:res/images/characters/human/run/human" + (randomIndex + 1) + "-run-" + i + ".png");
            }
        } catch (Exception e) {
            System.out.println("Failed to load human run animation frames: " + e.getMessage());
        }

        humanAttackFrames = new Image[4];
        try {
            for (int i = 1; i <= 4; i++) {
                humanAttackFrames[i - 1] = new Image("file:res/images/characters/human/attack/human" + (randomIndex + 1) + "-attack-" + i + ".png");
            }
        } catch (Exception e) {
            System.out.println("Failed to load human attack animation frames: " + e.getMessage());
        }

        humanZombificationFrames = new Image[8];
        try {
            for (int i = 1; i <= 8; i++) {
                humanZombificationFrames[i - 1] = new Image("file:res/images/characters/human/zombification/human" + (randomIndex + 1) + "-zombification-" + i + ".png");
            }
        } catch (Exception e) {
            System.out.println("Failed to load human zombification animation frames: " + e.getMessage());
        }

        humanView = new ImageView(humanIdleFrames[0]);
        humanView.setX(50);
        humanView.setY(250);
        humanView.setFitWidth(200);
        humanView.setFitHeight(200);
        root.getChildren().add(humanView);
        startHumanIdleAnimation();

        zombieIdleFrames = new Image[2];
        try {
            zombieIdleFrames[0] = new Image("file:res/images/characters/zombie/idle/zombie-idle-1.png");
            zombieIdleFrames[1] = new Image("file:res/images/characters/zombie/idle/zombie-idle-2.png");
        } catch (Exception e) {
            System.out.println("Failed to load zombie idle animation frames: " + e.getMessage());
        }

        // Load zombie run frames
        zombieRunFrames = new Image[4];
        try {
            for (int i = 1; i <= 4; i++) {
                zombieRunFrames[i - 1] = new Image("file:res/images/characters/zombie/run/zombie-run-" + i + ".png");
            }
        } catch (Exception e) {
            System.out.println("Failed to load zombie run animation frames: " + e.getMessage());
        }

        zombieDeathFrames = new Image[4];
        try {
            for (int i = 1; i <= 4; i++) {
                zombieDeathFrames[i - 1] = new Image("file:res/images/characters/zombie/death/zombie-death-" + i + ".png");
            }
        } catch (Exception e) {
            System.out.println("Failed to load zombie death animation frames: " + e.getMessage());
        }

        zombieAttackFrames = new Image[4];
        try {
            for (int i = 1; i <= 4; i++) {
                zombieAttackFrames[i - 1] = new Image("file:res/images/characters/zombie/attack/zombie-attack-" + i + ".png");
            }
        } catch (Exception e) {
            System.out.println("Failed to load zombie attack animation frames: " + e.getMessage());
        }

        zombieView = new ImageView(zombieIdleFrames[0]);
        zombieView.setX(1000);
        zombieView.setY(265);
        zombieView.setFitWidth(190);
        zombieView.setFitHeight(190);
        root.getChildren().add(zombieView);
        startZombieIdleAnimation();

        String[] topRow = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
        double startXTop = (1280 - (10 * 70 + 9 * 15)) / 2.0;
        for (int i = 0; i < topRow.length; i++) {
            ImageView buttonView = new ImageView(new Image("file:res/images/buttons/keyboard buttons/" + topRow[i] + ".png"));
            buttonView.setX(startXTop + i * (70 + 15));
            buttonView.setY(505);
            buttonView.setFitWidth(70);
            buttonView.setFitHeight(70);
            buttonView.setUserData(topRow[i]);
            String letter = topRow[i];
            buttonView.setOnMouseClicked(event -> handleLetterClick(letter, buttonView));
            root.getChildren().add(buttonView);
        }

        String[] middleRow = {"A", "S", "D", "F", "G", "H", "J", "K", "L"};
        double startXMiddle = (1280 - (9 * 70 + 8 * 15)) / 2.0;
        for (int i = 0; i < middleRow.length; i++) {
            ImageView buttonView = new ImageView(new Image("file:res/images/buttons/keyboard buttons/" + middleRow[i] + ".png"));
            buttonView.setX(startXMiddle + i * (70 + 15));
            buttonView.setY(590);
            buttonView.setFitWidth(70);
            buttonView.setFitHeight(70);
            buttonView.setUserData(middleRow[i]);
            String letter = middleRow[i];
            buttonView.setOnMouseClicked(event -> handleLetterClick(letter, buttonView));
            root.getChildren().add(buttonView);
        }

        String[] bottomRow = {"Z", "X", "C", "V", "B", "N", "M"};
        double startXBottom = (1280 - (7 * 70 + 6 * 15)) / 2.0;
        for (int i = 0; i < bottomRow.length; i++) {
            ImageView buttonView = new ImageView(new Image("file:res/images/buttons/keyboard buttons/" + bottomRow[i] + ".png"));
            buttonView.setX(startXBottom + i * (70 + 15));
            buttonView.setY(675);
            buttonView.setFitWidth(70);
            buttonView.setFitHeight(70);
            buttonView.setUserData(bottomRow[i]);
            String letter = bottomRow[i];
            buttonView.setOnMouseClicked(event -> handleLetterClick(letter, buttonView));
            root.getChildren().add(buttonView);
        }

        Scene scene = new Scene(root, 1280, 760);
        scene.setOnKeyPressed(this::handleKeyPress);
        primaryStage.setTitle("Word War Z");
        primaryStage.getIcons().add(new Image("file:res/images/others/word war z logo.png"));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void handleKeyPress(KeyEvent event) {
        if (!isStartingOverlayRemoved) {
            return;
        }

        if (event.getCode().isLetterKey()) {
            char pressedChar = event.getCode().toString().charAt(0);
            handleLetterInput(pressedChar);
        }
    }

    private void handleLetterInput(char letter) {
        letter = Character.toUpperCase(letter);
        ImageView button = findButtonForLetter(letter);
        if (button != null && !button.isDisable()) {
            handleLetterClick(String.valueOf(letter), button);
            animateButtonPress(button);
            if (onLetterPressed != null) {
                onLetterPressed.accept(letter);
            }
        }
    }

    private ImageView findButtonForLetter(char letter) {
        String letterStr = String.valueOf(letter);
        for (Node node : root.getChildren()) {
            if (node instanceof ImageView && node.getUserData() != null) {
                ImageView button = (ImageView) node;
                if (letterStr.equals(button.getUserData())) {
                    return button;
                }
            }
        }
        return null;
    }

    private void animateButtonPress(ImageView button) {
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), button);
        scaleDown.setFromX(1.0);
        scaleDown.setFromY(1.0);
        scaleDown.setToX(0.9);
        scaleDown.setToY(0.9);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), button);
        scaleUp.setFromX(0.9);
        scaleUp.setFromY(0.9);
        scaleUp.setToX(1.0);
        scaleUp.setToY(1.0);

        SequentialTransition pressAnimation = new SequentialTransition(scaleDown, scaleUp);
        pressAnimation.play();
    }

    public void setOnLetterPressed(Consumer<Character> handler) {
        this.onLetterPressed = handler;
        for (Node node : root.getChildren()) {
            if (node instanceof ImageView && node.getUserData() != null) {
                ImageView button = (ImageView) node;
                button.setOnMouseClicked(event -> {
                    String letter = (String) button.getUserData();
                    disableLetterButton(button);
                    if (onLetterPressed != null) {
                        onLetterPressed.accept(letter.charAt(0));
                    }
                });
            }
        }
    }

    public void disableLetterButton(char letter) {
        for (Node node : root.getChildren()) {
            if (node instanceof ImageView && node.getUserData() != null) {
                ImageView button = (ImageView) node;
                if (((String)button.getUserData()).charAt(0) == Character.toUpperCase(letter)) {
                    disableLetterButton(button);
                    break;
                }
            }
        }
    }

    private void disableLetterButton(ImageView button) {
        button.setDisable(true);
        try {
            String letter = (String) button.getUserData();
            Image disabledImage = new Image("file:res/images/buttons/disabled keyboard buttons/" + letter + ".png");
            button.setImage(disabledImage);
        } catch (Exception e) {
            button.setOpacity(0.5);
        }
    }

    public void showOverlayWithTimer(Runnable onOverlayEnd) {
        Platform.runLater(() -> {
            if (overlayPane == null) {
                overlayPane = new Pane();
                overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
                overlayPane.setPrefSize(1280, 760);

                Font customFont = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 40);
                Text overlayText = new Text("Race against the clock to guess the word! \n\nFirst to 3 wins claims victory!");
                overlayText.setFont(customFont);
                overlayText.setFill(Color.WHITE);
                overlayText.setTextAlignment(TextAlignment.CENTER);
                overlayText.setWrappingWidth(1000);
                overlayText.setX((1280 - overlayText.getLayoutBounds().getWidth()) / 2);
                overlayText.setY(350);
                overlayPane.getChildren().add(overlayText);
            }

            if (!root.getChildren().contains(overlayPane)) {
                root.getChildren().add(overlayPane);
            }
            overlayPane.setVisible(true);

            Timeline overlayTimer = new Timeline(
                    new KeyFrame(Duration.seconds(3), event -> {
                        root.getChildren().remove(overlayPane);
                        if (onOverlayEnd != null) {
                            onOverlayEnd.run();
                        }
                        isStartingOverlayRemoved = true;
                    }));
            overlayTimer.play();
        });
    }

    private void startHumanIdleAnimation() {
        if (humanIdleFrames == null || humanIdleFrames.length != 2) {
            System.out.println("Human idle animation frames not loaded properly.");
            return;
        }

        if (humanIdleAnimation != null) {
            humanIdleAnimation.stop();
        }
        if (humanRunAnimation != null) {
            humanRunAnimation.stop();
        }
        if (humanRunTransition != null) {
            humanRunTransition.stop();
        }
        if (humanAttackAnimation != null) {
            humanAttackAnimation.stop();
        }
        if (humanZombificationAnimation != null) {
            humanZombificationAnimation.stop();
        }

        humanView.setX(50);
        humanView.setY(250);
        humanView.setTranslateX(0); // Reset translation
        humanView.setImage(humanIdleFrames[0]);
        humanView.setVisible(true);
        humanView.getTransforms().clear();

        humanIdleAnimation = new Timeline(new KeyFrame(Duration.millis(250), event -> {
            if (humanView.getImage() == humanIdleFrames[0]) {
                humanView.setImage(humanIdleFrames[1]);
            } else {
                humanView.setImage(humanIdleFrames[0]);
            }
        }));
        humanIdleAnimation.setCycleCount(Timeline.INDEFINITE);
        humanIdleAnimation.play();
    }

    private void startZombieIdleAnimation() {
        if (zombieIdleFrames == null || zombieIdleFrames.length != 2) {
            System.out.println("Zombie idle animation frames not loaded properly.");
            return;
        }

        if (zombieIdleAnimation != null) {
            zombieIdleAnimation.stop();
        }
        if (zombieRunAnimation != null) { // Stop zombie run animation
            zombieRunAnimation.stop();
        }
        if (zombieDeathAnimation != null) {
            zombieDeathAnimation.stop();
        }
        if (zombieAttackAnimation != null) {
            zombieAttackAnimation.stop();
        }

        zombieView.setImage(zombieIdleFrames[0]);
        zombieView.setVisible(true);

        zombieIdleAnimation = new Timeline(new KeyFrame(Duration.millis(250), event -> {
            if (zombieView.getImage() == zombieIdleFrames[0]) {
                zombieView.setImage(zombieIdleFrames[1]);
            } else {
                zombieView.setImage(zombieIdleFrames[0]);
            }
        }));
        zombieIdleAnimation.setCycleCount(Timeline.INDEFINITE);
        zombieIdleAnimation.play();
    }

    private void startHumanRunAnimation(double targetX) {
        if (humanRunFrames == null || humanRunFrames.length != 4) {
            System.out.println("Human run animation frames not loaded properly.");
            return;
        }

        if (humanIdleAnimation != null) {
            humanIdleAnimation.stop();
        }
        if (humanRunAnimation != null) {
            humanRunAnimation.stop();
        }
        if (humanRunTransition != null) {
            humanRunTransition.stop();
        }
        if (humanAttackAnimation != null) {
            humanAttackAnimation.stop();
        }
        if (humanZombificationAnimation != null) {
            humanZombificationAnimation.stop();
        }

        humanView.setVisible(true); // Ensure visibility
        humanRunAnimation = new Timeline(new KeyFrame(Duration.millis(150), event -> {
            int frameIndex = (int) (System.currentTimeMillis() / 150 % 4);
            humanView.setImage(humanRunFrames[frameIndex]);
        }));
        humanRunAnimation.setCycleCount(Timeline.INDEFINITE);
        humanRunAnimation.play();

        double currentX = humanView.getX();
        double durationSeconds = Math.abs(targetX - currentX) / 400.0;
        humanRunTransition = new TranslateTransition(Duration.seconds(durationSeconds), humanView);
        humanRunTransition.setFromX(0); // Reset translation
        humanRunTransition.setToX(targetX - currentX); // Relative movement
        humanRunTransition.setOnFinished(e -> {
            humanRunAnimation.stop();
            humanView.setX(targetX);
            humanView.setTranslateX(0); // Reset translation
        });
        humanRunTransition.play();
    }

    private void startZombieRunAnimation(double targetX) {
        if (zombieRunFrames == null || zombieRunFrames.length != 4) {
            System.out.println("Zombie run animation frames not loaded properly.");
            return;
        }

        if (zombieIdleAnimation != null) {
            zombieIdleAnimation.stop();
        }
        if (zombieRunAnimation != null) {
            zombieRunAnimation.stop();
        }
        if (zombieRunTransition != null) {
            zombieRunTransition.stop();
        }

        zombieView.setVisible(true); // Ensure visibility
        zombieRunAnimation = new Timeline(new KeyFrame(Duration.millis(150), event -> {
            int frameIndex = (int) (System.currentTimeMillis() / 150 % 4);
            zombieView.setImage(zombieRunFrames[frameIndex]);
        }));
        zombieRunAnimation.setCycleCount(Timeline.INDEFINITE);
        zombieRunAnimation.play();

        double currentX = zombieView.getX();
        double durationSeconds = Math.abs(targetX - currentX) / 800.0; // Faster run (800 pixels/second)
        zombieRunTransition = new TranslateTransition(Duration.seconds(durationSeconds), zombieView);
        zombieRunTransition.setFromX(0); // Reset translation
        zombieRunTransition.setToX(targetX - currentX); // Relative movement
        zombieRunTransition.setOnFinished(e -> {
            zombieRunAnimation.stop();
            zombieView.setX(targetX);
            zombieView.setTranslateX(0); // Reset translation
            // Play attack and zombification animations after reaching the target
            ParallelTransition attackAnimations = new ParallelTransition(
                    new Timeline(new KeyFrame(Duration.millis(1), ev -> startZombieAttackAnimation())),
                    new Timeline(new KeyFrame(Duration.millis(1), ev -> startHumanZombificationAnimation()))
            );
            attackAnimations.play();
        });
        zombieRunTransition.play();
    }

    private void startHumanAttackAnimation() {
        if (humanAttackFrames == null || humanAttackFrames.length != 4) {
            System.out.println("Human attack animation frames not loaded properly.");
            return;
        }

        if (humanIdleAnimation != null) {
            humanIdleAnimation.stop();
        }
        if (humanRunAnimation != null) {
            humanRunAnimation.stop();
        }
        if (humanRunTransition != null) {
            humanRunTransition.stop();
        }
        if (humanAttackAnimation != null) {
            humanAttackAnimation.stop();
        }
        if (humanZombificationAnimation != null) {
            humanZombificationAnimation.stop();
        }

        humanView.setVisible(true); // Ensure visibility
        humanAttackAnimation = new Timeline(
                new KeyFrame(Duration.millis(0), e -> humanView.setImage(humanAttackFrames[0])),
                new KeyFrame(Duration.millis(150), e -> humanView.setImage(humanAttackFrames[1])),
                new KeyFrame(Duration.millis(300), e -> humanView.setImage(humanAttackFrames[2])),
                new KeyFrame(Duration.millis(450), e -> humanView.setImage(humanAttackFrames[3]))
        );
        humanAttackAnimation.setCycleCount(1);
        humanAttackAnimation.setOnFinished(e -> humanView.setVisible(true)); // Reinforce visibility
        humanAttackAnimation.play();
    }

    private void startZombieDeathAnimation() {
        if (zombieDeathFrames == null || zombieDeathFrames.length != 4) {
            System.out.println("Zombie death animation frames not loaded properly.");
            return;
        }

        if (zombieIdleAnimation != null) {
            zombieIdleAnimation.stop();
        }
        if (zombieRunAnimation != null) {
            zombieRunAnimation.stop();
        }
        if (zombieDeathAnimation != null) {
            zombieDeathAnimation.stop();
        }
        if (zombieAttackAnimation != null) {
            zombieAttackAnimation.stop();
        }

        zombieDeathAnimation = new Timeline(
                new KeyFrame(Duration.millis(0), e -> zombieView.setImage(zombieDeathFrames[0])),
                new KeyFrame(Duration.millis(200), e -> zombieView.setImage(zombieDeathFrames[1])),
                new KeyFrame(Duration.millis(400), e -> zombieView.setImage(zombieDeathFrames[2])),
                new KeyFrame(Duration.millis(600), e -> zombieView.setImage(zombieDeathFrames[3]))
        );
        zombieDeathAnimation.setCycleCount(1);
        zombieDeathAnimation.setOnFinished(e -> zombieView.setImage(zombieDeathFrames[3]));
        zombieDeathAnimation.play();
    }

    private void startZombieAttackAnimation() {
        if (zombieAttackFrames == null || zombieAttackFrames.length != 4) {
            System.out.println("Zombie attack animation frames not loaded properly.");
            return;
        }

        if (zombieIdleAnimation != null) {
            zombieIdleAnimation.stop();
        }
        if (zombieRunAnimation != null) {
            zombieRunAnimation.stop();
        }
        if (zombieAttackAnimation != null) {
            zombieAttackAnimation.stop();
        }
        if (zombieDeathAnimation != null) {
            zombieDeathAnimation.stop();
        }

        zombieView.setVisible(true); // Ensure visibility
        zombieAttackAnimation = new Timeline(
                new KeyFrame(Duration.millis(0), e -> zombieView.setImage(zombieAttackFrames[0])),
                new KeyFrame(Duration.millis(150), e -> zombieView.setImage(zombieAttackFrames[1])),
                new KeyFrame(Duration.millis(300), e -> zombieView.setImage(zombieAttackFrames[2])),
                new KeyFrame(Duration.millis(450), e -> zombieView.setImage(zombieAttackFrames[3]))
        );
        zombieAttackAnimation.setCycleCount(1);
        zombieAttackAnimation.setOnFinished(e -> {
            zombieView.setVisible(true); // Reinforce visibility
            // Removed transition to idle animation as requested
        });
        zombieAttackAnimation.play();
    }

    private void startHumanZombificationAnimation() {
        if (humanZombificationFrames == null || humanZombificationFrames.length != 8) {
            System.out.println("Human zombification animation frames not loaded properly.");
            return;
        }

        if (humanIdleAnimation != null) {
            humanIdleAnimation.stop();
        }
        if (humanRunAnimation != null) {
            humanRunAnimation.stop();
        }
        if (humanRunTransition != null) {
            humanRunTransition.stop();
        }
        if (humanAttackAnimation != null) {
            humanAttackAnimation.stop();
        }
        if (humanZombificationAnimation != null) {
            humanZombificationAnimation.stop();
        }

        humanView.setVisible(true); // Ensure visibility
        humanZombificationAnimation = new Timeline(
                new KeyFrame(Duration.millis(0), e -> humanView.setImage(humanZombificationFrames[0])),
                new KeyFrame(Duration.millis(375), e -> humanView.setImage(humanZombificationFrames[1])),
                new KeyFrame(Duration.millis(750), e -> humanView.setImage(humanZombificationFrames[2])),
                new KeyFrame(Duration.millis(1125), e -> humanView.setImage(humanZombificationFrames[3])),
                new KeyFrame(Duration.millis(1500), e -> humanView.setImage(humanZombificationFrames[4])),
                new KeyFrame(Duration.millis(1875), e -> humanView.setImage(humanZombificationFrames[5])),
                new KeyFrame(Duration.millis(2250), e -> humanView.setImage(humanZombificationFrames[6])),
                new KeyFrame(Duration.millis(2625), e -> humanView.setImage(humanZombificationFrames[7]))
        );
        humanZombificationAnimation.setCycleCount(1);
        humanZombificationAnimation.setOnFinished(e -> {
            humanView.setImage(humanZombificationFrames[7]); // Persist last frame
            humanView.setVisible(true);
        });
        humanZombificationAnimation.play();
    }

    private void updateTimerDisplay(int seconds) {
        Platform.runLater(() -> {
            int mins = seconds / 60;
            int secs = seconds % 60;
            timerText.setText(String.format("%02d:%02d", mins, secs));
        });
    }

    public void startTimer(int duration) {
        if (timerTimeline != null) {
            timerTimeline.stop();
            timerTimeline = null;
        }

        this.initialRoundDuration = duration;
        this.timeRemaining = duration;
        updateTimerDisplay(timeRemaining);
        zombieView.setX(1000);
        isZombieMoving = true;

        timerTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    timeRemaining--;
                    updateTimerDisplay(timeRemaining);
                    if (isZombieMoving) {
                        updateZombiePosition();
                    }

                    if (timeRemaining <= 0) {
                        timerTimeline.stop();
                        handleTimeOut();
                    }
                }
                ));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private void updateZombiePosition() {
        double progress = (double)(initialRoundDuration - timeRemaining) / initialRoundDuration;
        double newX = 1000 - (progress * 800);
        zombieView.setX(Math.max(200, newX));
    }

    public void updateHearts() {
        Platform.runLater(() -> {
            for (int i = 0; i < hearts.size(); i++) {
                ImageView heart = hearts.get(i);
                if (i < remainingGuesses) {
                    try {
                        Image fullHeart = new Image("file:res/images/others/heart.png");
                        heart.setImage(fullHeart);
                    } catch (Exception e) {
                        heart.setOpacity(1.0);
                    }
                    heart.setVisible(true);
                } else {
                    heart.setVisible(false);
                }
            }
        });
    }

    public void initializeWordDisplay(int wordLength) {
        Platform.runLater(() -> {
            revealedLetters = new boolean[wordLength];
            Arrays.fill(revealedLetters, false);

            root.getChildren().removeAll(letterTexts);
            letterTexts.clear();

            root.getChildren().removeIf(node -> node instanceof Rectangle &&
                    node.getUserData() != null && node.getUserData().equals("wordLine"));

            double letterWidth = 60;
            double gap = 25;
            double startX = (1280 - (wordLength * letterWidth + (wordLength - 1) * gap)) / 2.0;

            for (int i = 0; i < wordLength; i++) {
                Rectangle line = new Rectangle(letterWidth, 8);
                line.setX(startX + i * (letterWidth + gap));
                line.setY(470);
                line.setFill(Color.WHITE);
                line.setUserData("wordLine");
                root.getChildren().add(line);

                Text letterText = new Text();
                letterText.setFont(Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 40));
                letterText.setFill(Color.WHITE);
                letterText.setX(startX + i * (letterWidth + gap) + letterWidth / 2 - 15);
                letterText.setY(470 - 16);
                letterText.setVisible(false);
                letterText.setUserData("letterText");
                root.getChildren().add(letterText);
                letterTexts.add(letterText);
            }
        });
    }

    public void updateWordDisplay(char[] wordState) {
        Platform.runLater(() -> {
            for (int i = 0; i < Math.min(wordState.length, letterTexts.size()); i++) {
                if (wordState[i] != '_' && wordState[i] != ' ') {
                    Text letterText = letterTexts.get(i);
                    boolean wasHidden = !revealedLetters[i];

                    letterText.setText(String.valueOf(wordState[i]).toUpperCase());
                    letterText.setVisible(true);
                    revealedLetters[i] = true;

                    if (wasHidden) {
                        animateLetterReveal(letterText);
                    }
                }
            }
        });
    }

    private void animateLetterReveal(Text letterText) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), letterText);
        st.setFromX(0.1);
        st.setFromY(0.1);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    public void loseHeart() {
        if (remainingGuesses <= 0) return;

        Platform.runLater(() -> {
            remainingGuesses--;

            ImageView heart = hearts.get(remainingGuesses);
            try {
                Image depletedHeart = new Image("file:res/images/others/deplted heart.png");
                heart.setImage(depletedHeart);
                animateHeartDepletion(heart);
            } catch (Exception e) {
                System.err.println("Failed to load depleted heart image: " + e.getMessage());
                heart.setOpacity(0.5);
            }

            if (remainingGuesses <= 0) {
                disableAllLetterButtons();
                isZombieMoving = false; // Prevent updateZombiePosition from interfering
                double humanX = humanView.getX();
                double targetX = humanX + 50; // Position 50 pixels away from human
                startZombieRunAnimation(targetX);
                showDeathOverlay(); // Show overlay immediately
            }
        });
    }

    private void disableAllLetterButtons() {
        for (Node node : root.getChildren()) {
            if (node instanceof ImageView && node.getUserData() != null) {
                ImageView button = (ImageView) node;
                disableLetterButton(button);
            }
        }
    }

    private void animateHeartDepletion(ImageView heart) {
        ScaleTransition st1 = new ScaleTransition(Duration.millis(50), heart);
        st1.setFromX(1.0);
        st1.setFromY(1.0);
        st1.setToX(1.3);
        st1.setToY(1.3);

        ScaleTransition st2 = new ScaleTransition(Duration.millis(50), heart);
        st2.setFromX(1.3);
        st2.setFromY(1.3);
        st2.setToX(0.7);
        st2.setToY(0.7);

        ScaleTransition st3 = new ScaleTransition(Duration.millis(50), heart);
        st3.setFromX(0.7);
        st3.setFromY(0.7);
        st3.setToX(1.2);
        st3.setToY(1.2);

        ScaleTransition st4 = new ScaleTransition(Duration.millis(50), heart);
        st4.setFromX(1.2);
        st4.setFromY(1.2);
        st4.setToX(1.0);
        st4.setToY(1.0);

        RotateTransition rt1 = new RotateTransition(Duration.millis(50), heart);
        rt1.setFromAngle(0);
        rt1.setToAngle(15);

        RotateTransition rt2 = new RotateTransition(Duration.millis(50), heart);
        rt2.setFromAngle(15);
        rt2.setToAngle(-15);

        RotateTransition rt3 = new RotateTransition(Duration.millis(50), heart);
        rt3.setFromAngle(-15);
        rt3.setToAngle(0);

        SequentialTransition scaleShake = new SequentialTransition(st1, st2, st3, st4);
        SequentialTransition rotateShake = new SequentialTransition(rt1, rt2, rt3);

        FadeTransition ft = new FadeTransition(Duration.millis(300), heart);
        ft.setFromValue(1.0);
        ft.setToValue(0.7);

        ParallelTransition combined = new ParallelTransition(scaleShake, rotateShake, ft);
        combined.play();
    }

    private void handleLetterClick(String letter, ImageView button) {
        if (button.isDisable()) return;

        button.setDisable(true);
        try {
            Image pressedImage = new Image("file:res/images/buttons/disabled keyboard buttons/" + letter + ".png");
            button.setImage(pressedImage);

            if (letterGuessHandler != null) {
                letterGuessHandler.accept(letter.charAt(0));
            }
        } catch (Exception e) {
            button.setOpacity(0.5);
        }
    }

    public void resetKeyboard() {
        for (Node node : root.getChildren()) {
            if (node instanceof ImageView && node.getUserData() != null) {
                ImageView button = (ImageView) node;
                String letter = (String) button.getUserData();
                try {
                    Image normalImage = new Image("file:res/images/buttons/keyboard buttons/" + letter + ".png");
                    button.setImage(normalImage);
                    button.setDisable(false);
                } catch (Exception e) {
                    button.setOpacity(1.0);
                }
            }
        }
    }

    public void showDeathOverlay() {
        if (isRoundDrawnOverlayShown) {
            return;
        }

        Platform.runLater(() -> {
            Pane deathOverlay = new Pane();
            deathOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
            deathOverlay.setPrefSize(1280, 760);

            Font customFont = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 30);
            Text overlayText = new Text("You died!\nWaiting for the round to end.");
            overlayText.setFont(customFont);
            overlayText.setFill(Color.RED);
            overlayText.setTextAlignment(TextAlignment.CENTER);
            overlayText.setWrappingWidth(1000);
            overlayText.setX((1280 - overlayText.getLayoutBounds().getWidth()) / 2);
            overlayText.setY(350);
            deathOverlay.getChildren().add(overlayText);

            root.getChildren().add(deathOverlay);
            deathOverlay.setUserData("deathOverlay");
        });
    }

    public void removeDeathOverlay() {
        Platform.runLater(() -> {
            root.getChildren().removeIf(node ->
                    node instanceof Pane && "deathOverlay".equals(node.getUserData())
            );
        });
    }

    public int getRemainingGuesses() {
        return this.remainingGuesses;
    }

    public void showErrorMessage(String s) {
    }

    public void showRoundLost(String winner, String word) {
        removeDeathOverlay();
        showRoundEndOverlay("You got eaten by " + winner + "!\nThe word was: " + word, Color.RED);
    }

    public void showRoundWon(String word) {
        isZombieMoving = false;
        startZombieIdleAnimation();

        SequentialTransition attackSequence = new SequentialTransition();

        if (randomIndex != 3) { // Humans 1-3 (melee)
            double zombiePosition = zombieView.getX();
            double targetX = zombiePosition - humanView.getFitWidth() * 0.5; // Move closer to zombie
            startHumanRunAnimation(targetX);

            attackSequence.getChildren().add(
                    new PauseTransition(Duration.seconds(Math.abs(targetX - humanView.getX()) / 400.0))
            );
        }

        attackSequence.getChildren().addAll(
                new ParallelTransition(
                        new Timeline(new KeyFrame(Duration.millis(1), e -> {
                            startHumanAttackAnimation();
                            humanView.setVisible(true); // Ensure visibility during attack
                        })),
                        new Timeline(new KeyFrame(Duration.millis(1), e -> startZombieDeathAnimation()))
                ),
                new PauseTransition(Duration.millis(600)),
                new Timeline(new KeyFrame(Duration.millis(1), e -> {
                    startHumanRunAnimation(1280);
                    humanView.setVisible(true); // Ensure visibility for run
                }))
        );
        attackSequence.play();

        showRoundEndOverlay("You survived!\nThe word was: " + word, Color.GREEN);
    }

    public void showRoundDrawn(String word) {
        isRoundDrawnOverlayShown = true;
        removeDeathOverlay();
        showRoundEndOverlay("Round drawn! No one guessed the word: " + word, Color.YELLOW);

        new Timeline(new KeyFrame(Duration.seconds(3), e -> isRoundDrawnOverlayShown = false)).play();
    }

    private void showRoundEndOverlay(String message, Color color) {
        Platform.runLater(() -> {
            if (currentRoundOverlay != null && root.getChildren().contains(currentRoundOverlay)) {
                return;
            }

            Pane overlay = new Pane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
            overlay.setPrefSize(1280, 760);

            Font font = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 30);
            Text text = new Text(message);
            text.setFont(font);
            text.setFill(color);
            text.setTextAlignment(TextAlignment.CENTER);
            text.setWrappingWidth(1000);
            text.setX((1280 - text.getLayoutBounds().getWidth()) / 2);
            text.setY(380);

            overlay.getChildren().add(text);
            root.getChildren().add(overlay);

            currentRoundOverlay = overlay;

            new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                root.getChildren().remove(overlay);
            })).play();
        });
    }

    private void handleTimeOut() {
        if (timeOutHandler != null) {
            timeOutHandler.run();
        }
        if (!isGameOver) {
            isZombieMoving = false; // Stop position updates
            double humanX = humanView.getX();
            double zombieX = zombieView.getX() - 50; // Move zombie 50 pixels closer to human
            zombieView.setX(zombieX);
            startZombieAttackAnimation();
            startHumanZombificationAnimation();
            showDeathOverlay();
        }
    }

    public void setOnTimeOut(Runnable handler) {
        this.timeOutHandler = handler;
    }

    public void resetRound() {
        if (isGameOver) {
            return;
        }

        Platform.runLater(() -> {
            if (timerTimeline != null) {
                timerTimeline.stop();
                timerTimeline = null;
            }

            resetKeyboard();
            remainingGuesses = 5;
            updateHearts();
            timeRemaining = initialRoundDuration;
            zombieView.setX(1000);
            root.getChildren().removeAll(letterTexts);
            letterTexts.clear();
            revealedLetters = new boolean[0];
            updateTimerDisplay(initialRoundDuration);
            startTimer(initialRoundDuration);

            if (humanRunTransition != null) {
                humanRunTransition.stop();
            }
            if (humanRunAnimation != null) {
                humanRunAnimation.stop();
            }
            if (humanIdleAnimation != null) {
                humanIdleAnimation.stop();
            }
            if (humanAttackAnimation != null) {
                humanAttackAnimation.stop();
            }
            if (humanZombificationAnimation != null) {
                humanZombificationAnimation.stop();
            }
            humanView.setX(50); // Reset position
            humanView.setY(250);
            humanView.setTranslateX(0); // Reset translation
            humanView.setImage(humanIdleFrames[0]);
            humanView.setVisible(true);
            humanView.getTransforms().clear();

            if (zombieRunTransition != null) {
                zombieRunTransition.stop();
            }
            if (zombieRunAnimation != null) {
                zombieRunAnimation.stop();
            }
            if (zombieIdleAnimation != null) {
                zombieIdleAnimation.stop();
            }
            if (zombieAttackAnimation != null) {
                zombieAttackAnimation.stop();
            }
            if (zombieDeathAnimation != null) {
                zombieDeathAnimation.stop();
            }
            zombieView.setX(1000);
            zombieView.setImage(zombieIdleFrames[0]);
            zombieView.setVisible(true);
            startZombieIdleAnimation();

            startHumanIdleAnimation();
            removeDeathOverlay();
        });
    }

    public void showEndGameOverlay(String message) {
        Platform.runLater(() -> {
            isGameOver = true;

            if (message.contains("You won")) {
                isZombieMoving = false;
                startZombieIdleAnimation();

                SequentialTransition attackSequence = new SequentialTransition();

                if (randomIndex != 3) { // Humans 1-3 (melee)
                    double zombiePosition = zombieView.getX();
                    double targetX = zombiePosition - humanView.getFitWidth() * 0.5; // Move closer to zombie
                    startHumanRunAnimation(targetX);

                    attackSequence.getChildren().add(
                            new PauseTransition(Duration.seconds(Math.abs(targetX - humanView.getX()) / 400.0))
                    );
                    // Set position explicitly before attack to prevent reset
                    attackSequence.getChildren().add(
                            new Timeline(new KeyFrame(Duration.millis(1), e -> {
                                humanView.setX(targetX); // Lock position at targetX
                                startHumanAttackAnimation();
                                humanView.setVisible(true); // Ensure visibility during attack
                            }))
                    );
                    attackSequence.getChildren().add(
                            new Timeline(new KeyFrame(Duration.millis(1), e -> startZombieDeathAnimation()))
                    );
                    attackSequence.getChildren().add(
                            new PauseTransition(Duration.millis(600)) // Match attack animation duration
                    );
                    attackSequence.getChildren().add(
                            new Timeline(new KeyFrame(Duration.millis(1), e -> {
                                startHumanRunAnimation(1280);
                                humanView.setVisible(true); // Ensure visibility for run
                            }))
                    );
                }
                attackSequence.play();
            }

            Pane overlay = new Pane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
            overlay.setPrefSize(1280, 760);

            Font font = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 50);
            Text text = new Text(message + "\nReturning to main menu...");
            text.setFont(font);
            text.setFill(Color.GOLD);
            text.setTextAlignment(TextAlignment.CENTER);
            text.setWrappingWidth(1000);
            text.setX((1280 - text.getLayoutBounds().getWidth()) / 2);
            text.setY(350);

            overlay.getChildren().add(text);

            Timeline delayTimeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                root.getChildren().add(overlay);
                zombieView.setVisible(false);
            }));

            delayTimeline.play();
        });
    }

    public void createConfetti() {
        Pane confettiPane = new Pane();
        confettiPane.setPrefSize(1280, 760);

        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            Rectangle confetti = new Rectangle(10, 20);
            confetti.setFill(Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
            confetti.setX(random.nextInt(1280));
            confetti.setY(-random.nextInt(200));

            TranslateTransition fallAnimation = new TranslateTransition(Duration.seconds(3 + random.nextDouble()), confetti);
            fallAnimation.setByY(960);
            fallAnimation.setCycleCount(1);
            fallAnimation.setOnFinished(e -> confettiPane.getChildren().remove(confetti));

            RotateTransition rotateAnimation = new RotateTransition(Duration.seconds(3 + random.nextDouble()), confetti);
            rotateAnimation.setByAngle(360);
            rotateAnimation.setCycleCount(1);

            ParallelTransition confettiAnimation = new ParallelTransition(fallAnimation, rotateAnimation);
            confettiAnimation.play();

            confettiPane.getChildren().add(confetti);
        }

        root.getChildren().add(confettiPane);

        new Timeline(new KeyFrame(Duration.seconds(6), e -> root.getChildren().remove(confettiPane))).play();
    }

    public void closeApplication() {
        Platform.runLater(() -> {
            if (timerTimeline != null) {
                timerTimeline.stop();
            }
            if (humanIdleAnimation != null) {
                humanIdleAnimation.stop();
            }
            if (zombieIdleAnimation != null) {
                zombieIdleAnimation.stop();
            }
            if (humanRunAnimation != null) {
                humanRunAnimation.stop();
            }
            if (zombieRunAnimation != null) {
                zombieRunAnimation.stop();
            }
            if (zombieDeathAnimation != null) {
                zombieDeathAnimation.stop();
            }
            if (humanRunTransition != null) {
                humanRunTransition.stop();
            }
            if (humanAttackAnimation != null) {
                humanAttackAnimation.stop();
            }
            if (zombieAttackAnimation != null) {
                zombieAttackAnimation.stop();
            }
            if (humanZombificationAnimation != null) {
                humanZombificationAnimation.stop();
            }
            if (zombieRunTransition != null) {
                zombieRunTransition.stop();
            }

            zombieView.setVisible(false);

            primaryStage.close();
        });
    }

    public void showForceLogoutMessage() {
        Platform.runLater(() -> {
            // Create a blocking overlay
            Pane overlay = new Pane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9);");
            overlay.setPrefSize(1280, 760);

            Font font = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 30);
            Text text = new Text("You have been logged out\nReturning to login screen...");
            text.setFont(font);
            text.setFill(Color.WHITE);
            text.setTextAlignment(TextAlignment.CENTER);
            text.setWrappingWidth(1000);
            text.setX((1280 - text.getLayoutBounds().getWidth()) / 2);
            text.setY(350);

            overlay.getChildren().add(text);
            root.getChildren().add(overlay);

            new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                closeApplication();
            })).play();
        });
    }

    public void showPlayerDisconnected(String username) {
    }
}