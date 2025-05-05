package client.player.view;

import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GameView extends Application {
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


    private Text timerText;
    private List<ImageView> hearts = new ArrayList<>();
    private Timeline timerTimeline;
    private Stage primaryStage;
    public int remainingGuesses = 5;


    @Override
    public void start(Stage primaryStage) {
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


        // Human (with idle animation)
        humanIdleFrames = new Image[2];
        try {
            humanIdleFrames[0] = new Image("file:res/images/characters/human/idle/human-idle-1.png");
            humanIdleFrames[1] = new Image("file:res/images/characters/human/idle/human-idle-2.png");
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

        // Word Display (static underscores - no functionality - placeholder for now)
        int placeholderWordLength = 6; // Placeholder for a typical word length (e.g., "ZOMBIE")
        double lineWidth = 60;
        double lineHeight = 8;
        double gap = 25;
        double startXWord = (1280 - (placeholderWordLength * lineWidth + (placeholderWordLength - 1) * gap)) / 2.0;

        for (int i = 0; i < placeholderWordLength; i++) {
            Rectangle line = new Rectangle(lineWidth, lineHeight);
            line.setX(startXWord + i * (lineWidth + gap));
            line.setY(470);
            line.setFill(Color.WHITE);
            root.getChildren().add(line);
        }

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

        // --- OVERLAY ---
        overlayPane = new Pane();
        overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlayPane.setPrefSize(1280, 760);

        Text overlayText = new Text("Race against the clock to guess the word! \n\nFirst to 3 wins claims victory!");
        overlayText.setFont(customFont);
        overlayText.setFill(Color.WHITE);
        overlayText.setTextAlignment(TextAlignment.CENTER);
        overlayText.setWrappingWidth(1000);

        // Center the text
        overlayText.setX((1280 - overlayText.getLayoutBounds().getWidth()) / 2);
        overlayText.setY(350);

        overlayPane.getChildren().add(overlayText);
        root.getChildren().add(overlayPane);

        // Remove the overlay after 3 seconds
        Timeline overlayTimer = new Timeline(
                new KeyFrame(Duration.seconds(3),
                        event -> root.getChildren().remove(overlayPane)
                ));
        overlayTimer.play();

        // --- SCENE & STAGE ---
        Scene scene = new Scene(root, 1280, 760); // Explicitly set size
        primaryStage.setTitle("Word War Z");
        primaryStage.getIcons().add(new Image("file:res/images/others/word war z logo.png"));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void setLetterGuessHandler(Consumer<Character> handler) {
        this.letterGuessHandler = handler;
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

    public void startTimer() {
        final int[] timeRemaining = {30};
        timerTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    timeRemaining[0]--;
                    updateTimerDisplay(timeRemaining[0]);
                    if (timeRemaining[0] <= 0) {
                        closeApplication();
                    }
                })
        );
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private void updateTimerDisplay(int seconds) {
        Platform.runLater(() -> {
            int mins = seconds / 60;
            int secs = seconds % 60;
            timerText.setText(String.format("%02d:%02d", mins, secs));
        });
    }

    public void updateHearts() {
        Platform.runLater(() -> {
            for (int i = 0; i < hearts.size(); i++) {
                hearts.get(i).setVisible(i < remainingGuesses);
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
        // Clear existing display
        root.getChildren().removeAll(letterTexts);
        letterTexts.clear();

        double letterWidth = 60;
        double gap = 25;
        double startX = (1280 - (wordLength * letterWidth + (wordLength - 1) * gap)) / 2.0;

        // Create underscore placeholders
        for (int i = 0; i < wordLength; i++) {
            // Underscore line
            Rectangle line = new Rectangle(letterWidth, 8);
            line.setX(startX + i * (letterWidth + gap));
            line.setY(470);
            line.setFill(Color.WHITE);
            root.getChildren().add(line);

            // Invisible text (will be shown when letter is guessed)
            Text letterText = new Text();
            letterText.setFont(Font.loadFont("file:res/PressStart2P-Regular.ttf", 40));
            letterText.setFill(Color.WHITE);
            letterText.setX(startX + i * (letterWidth + gap) + letterWidth/2 - 15);
            letterText.setY(470 + 40);
            letterText.setVisible(false);
            root.getChildren().add(letterText);
            letterTexts.add(letterText);
        }
    }

    private void handleLetterClick(String letter, ImageView button) {
        System.out.println("Letter clicked: " + letter);
        if (!button.isDisable()) {
            button.setDisable(true);
            try {
                Image disabledImage = new Image("file:res/images/buttons/disabled keyboard buttons/" + letter + ".png");
                button.setImage(disabledImage);
            } catch (Exception e) {
                System.out.println("Failed to load disabled image for " + letter + ": " + e.getMessage());
                button.setOpacity(0.5);
            }
        }


        if (letterGuessHandler != null) {
            letterGuessHandler.accept(letter.charAt(0));
        }
    }


    public void updateWordDisplay(char[] wordState) {
        for (int i = 0; i < wordState.length; i++) {
            if (wordState[i] != '_') {
                letterTexts.get(i).setText(String.valueOf(wordState[i]));
                letterTexts.get(i).setVisible(true);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void showErrorMessage(String s) {
    }
}
