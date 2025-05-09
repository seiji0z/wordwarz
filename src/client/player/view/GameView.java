package client.player.view;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.Random;

public class GameView {
    private int selectedCharacter;
    private List<Text> letterTexts = new ArrayList<>();
    private Pane root; // Store the root pane for resetting the game
    private Pane overlayPane; // For the start overlay

    // Animation-related fields for animations
    private ImageView humanView;
    private ImageView zombieView;
    private Image[] humanIdleFrames;
    private Image[] zombieIdleFrames;
    private Timeline humanIdleAnimation;
    private Timeline zombieIdleAnimation;
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

    public GameView(Stage primaryStage, int selectedCharacter) {
        this.primaryStage = primaryStage;
        this.selectedCharacter = selectedCharacter;
        initialize();
    }

    public void initialize() {
        this.primaryStage = primaryStage;
        // --- ROOT LAYOUT ---
        root = new Pane();

        // Load the custom font
        Font customFont = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 40);
        if (customFont == null) {
            System.out.println("Failed to load custom font, falling back to default.");
            customFont = new Font("System", 30); // Fallback font
        }

        // Background
        Image backgroundImage = new Image("file:res/images/backgrounds/game bg.png");
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setFitWidth(1280);
        backgroundView.setFitHeight(760);
        root.getChildren().add(backgroundView);

        // Modify hearts initialization
        for (int i = 0; i < 5; i++) {
            ImageView heart = new ImageView(new Image("file:res/images/others/heart.png"));
            heart.setX(30 + i * 60);
            heart.setY(20);
            heart.setFitWidth(50);
            heart.setFitHeight(50);
            hearts.add(heart);
            root.getChildren().add(heart);
        }

        // Modify timer initialization
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

        // FIXED CHARACTER SELECTION LOGIC
        int randomIndex;
        if (selectedCharacter == 5) {
            randomIndex = new Random().nextInt(humanIdleImageFiles.length);
        } else {
            randomIndex = selectedCharacter - 1;
            randomIndex = Math.max(0, Math.min(randomIndex, humanIdleImageFiles.length - 1));
        }

        String selectedHumanImage = humanIdleImageFiles[randomIndex];

        // Human (with idle animation)
        humanIdleFrames = new Image[2];
        try {
            humanIdleFrames[0] = new Image("file:res/images/characters/human/idle/" + selectedHumanImage);
            humanIdleFrames[1] = new Image("file:res/images/characters/human/idle/" + selectedHumanImage.replace("-1.png", "-2.png"));
        } catch (Exception e) {
            System.out.println("Failed to load human idle animation frames: " + e.getMessage());
        }
        humanView = new ImageView(humanIdleFrames[0]);
        humanView.setX(50);
        humanView.setY(250);
        humanView.setFitWidth(200); // Hard-coded size
        humanView.setFitHeight(200);
        root.getChildren().add(humanView);
        startHumanIdleAnimation();

        // Zombie (with idle animation)
        zombieIdleFrames = new Image[2];
        try {
            zombieIdleFrames[0] = new Image("file:res/images/characters/zombie/idle/zombie-idle-1.png");
            zombieIdleFrames[1] = new Image("file:res/images/characters/zombie/idle/zombie-idle-2.png");
        } catch (Exception e) {
            System.out.println("Failed to load zombie idle animation frames: " + e.getMessage());
        }
        zombieView = new ImageView(zombieIdleFrames[0]);
        zombieView.setX(1000); // Initial position (lives=5)
        zombieView.setY(315);
        zombieView.setFitWidth(90); // Hard-coded size
        zombieView.setFitHeight(140);
        root.getChildren().add(zombieView);
        startZombieIdleAnimation();

        // Keyboard Buttons
        // Top Row: Q to P (Y=505)
        String[] topRow = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
        double startXTop = (1280 - (10 * 70 + 9 * 15)) / 2.0; // Center 10 buttons (70px width, 15px gap)
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

        // Middle Row: A to L (Y=590)
        String[] middleRow = {"A", "S", "D", "F", "G", "H", "J", "K", "L"};
        double startXMiddle = (1280 - (9 * 70 + 8 * 15)) / 2.0; // Center 9 buttons
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

        // Bottom Row: Z to M (Y=675)
        String[] bottomRow = {"Z", "X", "C", "V", "B", "N", "M"};
        double startXBottom = (1280 - (7 * 70 + 6 * 15)) / 2.0; // Center 7 buttons
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

        // --- SCENE & STAGE ---
        Scene scene = new Scene(root, 1280, 760); // Explicitly set size
        primaryStage.setTitle("Word War Z");
        primaryStage.getIcons().add(new Image("file:res/images/others/word war z logo.png"));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void setOnLetterPressed(Consumer<Character> handler) {
        this.onLetterPressed = handler;

        // Setup all keyboard buttons
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
                    disableLetterButton(button);  // This calls the private method below
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
            // Create overlay if it doesn't exist
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

            // Add overlay if not already present
            if (!root.getChildren().contains(overlayPane)) {
                root.getChildren().add(overlayPane);
            }
            overlayPane.setVisible(true);

            // Remove after 3 seconds
            Timeline overlayTimer = new Timeline(
                    new KeyFrame(Duration.seconds(3), event -> {
                        root.getChildren().remove(overlayPane);
                        if (onOverlayEnd != null) {
                            onOverlayEnd.run();
                        }
                    }));
            overlayTimer.play();
        });
    }

    private void startHumanIdleAnimation() {
        if (humanIdleFrames == null || humanIdleFrames.length != 2) {
            System.out.println("Human idle animation frames not loaded properly.");
            return;
        }

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

    private void updateTimerDisplay(int seconds) {
        Platform.runLater(() -> {
            int mins = seconds / 60;
            int secs = seconds % 60;
            timerText.setText(String.format("%02d:%02d", mins, secs));
        });
    }

    public void startTimer(int duration) {
        this.initialRoundDuration = duration;
        this.timeRemaining = duration; // Reset time remaining

        if (timerTimeline != null) {
            timerTimeline.stop();
        }

        timerTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    timeRemaining--;
                    updateTimerDisplay(timeRemaining);
                    updateZombiePosition();

                    if (timeRemaining <= 0) {
                        timerTimeline.stop();
                        handleTimeOut();
                    }
                })
        );
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private void updateZombiePosition() {
        // Calculate progress (0.0 to 1.0)
        double progress = (double)(initialRoundDuration - timeRemaining) / initialRoundDuration;

        // Zombie moves from right (1000) to left (200) based on time elapsed
        double newX = 1000 - (progress * 800);
        zombieView.setX(Math.max(200, newX));
    }

    public void updateHearts() {
        Platform.runLater(() -> {
            for (int i = 0; i < hearts.size(); i++) {
                ImageView heart = hearts.get(i);
                if (i < remainingGuesses) {
                    // Display full heart
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

    public void closeApplication() {
        Platform.runLater(() -> {
            if (timerTimeline != null) {
                timerTimeline.stop();
            }
            primaryStage.close();
        });
    }

    public void initializeWordDisplay(int wordLength) {
        revealedLetters = new boolean[wordLength]; // Initialize the array

        // Clear existing display (both letter texts and underscored lines)
        root.getChildren().removeAll(letterTexts);
        letterTexts.clear();

        // Gather and remove any existing underscores (Rectangles)
        root.getChildren().removeIf(node -> node instanceof Rectangle);

        double letterWidth = 60;
        double gap = 25;
        double startX = (1280 - (wordLength * letterWidth + (wordLength - 1) * gap)) / 2.0;

        // Create new placeholders
        for (int i = 0; i < wordLength; i++) {
            Rectangle line = new Rectangle(letterWidth, 8);
            line.setX(startX + i * (letterWidth + gap));
            line.setY(470);
            line.setFill(Color.WHITE);
            root.getChildren().add(line);

            Text letterText = new Text();
            letterText.setFont(Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 40));
            letterText.setFill(Color.WHITE);
            letterText.setX(startX + i * (letterWidth + gap) + letterWidth / 2 - 15);
            letterText.setY(470 - 16);
            letterText.setVisible(false);
            root.getChildren().add(letterText);
            letterTexts.add(letterText);
        }
    }

    public void updateWordDisplay(char[] wordState) {
        Platform.runLater(() -> {
            for (int i = 0; i < Math.min(wordState.length, letterTexts.size()); i++) {
                if (wordState[i] != '_' && wordState[i] != ' ') {
                    Text letterText = letterTexts.get(i);

                    // Check if this letter was previously hidden
                    boolean wasHidden = !revealedLetters[i];

                    letterText.setText(String.valueOf(wordState[i]).toUpperCase());
                    letterText.setVisible(true);
                    revealedLetters[i] = true; // Mark as revealed

                    // Only animate if this letter was just revealed
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
            remainingGuesses--;  // Move this before the animation

            // Get the heart to deplete (last visible one)
            ImageView heart = hearts.get(remainingGuesses);

            // Change to depleted heart image
            try {
                Image depletedHeart = new Image("file:res/images/others/deplted heart.png");
                heart.setImage(depletedHeart);

                // Add shake animation
                animateHeartDepletion(heart);
            } catch (Exception e) {
                System.err.println("Failed to load depleted heart image: " + e.getMessage());
                heart.setOpacity(0.5);
            }

            // Disable all buttons if no guesses left
            if (remainingGuesses <= 0) {
                disableAllLetterButtons();
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
        // More vigorous shake animation using scale and rotate transitions
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

        // Fade animation
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
            // Visual feedback for pressed key
            Image pressedImage = new Image("file:res/images/buttons/disabled keyboard buttons/" + letter + ".png");
            button.setImage(pressedImage);

            // Notify controller
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

    private void showEndGameOverlay(String message, Color color) {
        Platform.runLater(() -> {
            Pane overlay = new Pane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
            overlay.setPrefSize(1280, 760);

            Font font = Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 50);
            Text text = new Text(message);
            text.setFont(font);
            text.setFill(color);
            text.setTextAlignment(TextAlignment.CENTER);
            text.setWrappingWidth(1000);
            text.setX((1280 - text.getLayoutBounds().getWidth()) / 2);
            text.setY(380);

            overlay.getChildren().add(text);
            root.getChildren().add(overlay);
        });
    }

    public int getRemainingGuesses() {
        return this.remainingGuesses;
    }

    public void showErrorMessage(String s) {
    }

    // In GameView.java
    public void showRoundLost(String winner, String word) {
        showRoundEndOverlay("You got eaten by " + winner + "!\nThe word was: " + word, Color.RED);
    }

    public void showRoundWon(String word) {
        showRoundEndOverlay("You survived!\nThe word was: " + word, Color.GREEN);
    }

    public void showRoundDrawn(String word) {
        showRoundEndOverlay("Time's up! No one guessed the word: " + word, Color.YELLOW);
    }

    public void showWaitingForOthers() {
        showRoundEndOverlay("You got eaten!\nWaiting for round to end...", Color.ORANGE);
    }

    private void showRoundEndOverlay(String message, Color color) {
        Platform.runLater(() -> {
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

            // Remove overlay after 3 seconds
            new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                root.getChildren().remove(overlay);
            })).play();
        });
    }

    private void handleTimeOut() {
        if (timeOutHandler != null) {
            timeOutHandler.run();
        }
    }

    public void setOnTimeOut(Runnable handler) {
        this.timeOutHandler = handler;
    }

    public void resetRound() {
        Platform.runLater(() -> {
            // Stop any existing timer
            if (timerTimeline != null) {
                timerTimeline.stop();
            }

            // Reset the keyboard buttons
            resetKeyboard();

            // Reset hearts to full
            remainingGuesses = 5;
            updateHearts();

            // Reset zombie position and timer state
            timeRemaining = initialRoundDuration;
            zombieView.setX(1000);

            // Clear any displayed letters
            root.getChildren().removeAll(letterTexts);
            letterTexts.clear();
            revealedLetters = new boolean[0]; // Reset the array

            // Reset timer display
            timerText.setText(String.format("%02d:%02d", initialRoundDuration / 60, initialRoundDuration % 60));
        });
    }

    private void showEndGameOverlay(String winner) {
        Platform.runLater(() -> {
            Pane overlay = new Pane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
            overlay.setPrefSize(1280, 760);

            Text message = new Text(winner + " has won the game!\nThank you for playing!");
            message.setFont(Font.loadFont("file:res/fonts/PressStart2P-Regular.ttf", 50));
            message.setFill(Color.GOLD);
            message.setTextAlignment(TextAlignment.CENTER);
            message.setWrappingWidth(1000);
            message.setX((1280 - 1000) / 2);
            message.setY(350);

            overlay.getChildren().add(message);
            root.getChildren().add(overlay);

            new Timeline(new KeyFrame(Duration.seconds(5), e -> Platform.exit())).play();
        });
    }
}