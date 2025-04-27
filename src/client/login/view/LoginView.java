package client.login.view;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class LoginView {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button playButton;
    private Stage stage; // keep reference to stage
    private Text errorText;

    public LoginView() throws FileNotFoundException {
        this.stage = new Stage();
        initialize(stage);
    }

    public void initialize(Stage primaryStage) throws FileNotFoundException {
        // Main root container
        StackPane root = new StackPane();
        root.setPrefSize(1280, 760);

        // --- BACKGROUND ---
        Image bgImage = new Image("file:res/images/backgrounds/menu bg.png");
        BackgroundImage background = new BackgroundImage(
                bgImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
        );
        root.setBackground(new Background(background));

        // Main grid layout
        GridPane mainGrid = new GridPane();
        mainGrid.setAlignment(Pos.CENTER);
        mainGrid.setHgap(100);
        mainGrid.setVgap(50);

        // Load custom font
        Font pressStartFont = Font.loadFont(new FileInputStream("res/fonts/PressStart2P-Regular.ttf"), 24);
        Font titleFont = Font.loadFont(new FileInputStream("res/fonts/PressStart2P-Regular.ttf"), 36);

        // --- LEFT SIDE ---
        VBox leftSide = new VBox(30);
        leftSide.setAlignment(Pos.TOP_LEFT);
        leftSide.setPadding(new Insets(50, 0, 0, 50));

        // Title Text (WORD WAR Z)
        Text titleText = new Text("LOGIN");
        titleText.setFont(titleFont);
        titleText.setFill(Color.WHITE);

        // Login Panel (rectangle container)
        VBox loginPanel = new VBox(30);
        loginPanel.setAlignment(Pos.TOP_LEFT);
        loginPanel.setPadding(new Insets(30));
        loginPanel.setBackground(new Background(new BackgroundFill(
                new Color(0, 0, 0, 0.5), // Semi-transparent black
                new CornerRadii(10),
                Insets.EMPTY
        )));
        loginPanel.setBorder(new Border(new BorderStroke(
                Color.WHITE,
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(2)
        )));
        loginPanel.setMaxWidth(500);

        // USERNAME Label and Field
        Text usernameLabel = new Text("USERNAME");
        usernameLabel.setFont(pressStartFont);
        usernameLabel.setFill(Color.WHITE);

        usernameField = new TextField();
        usernameField.setFont(pressStartFont);
        usernameField.setPrefHeight(50);
        usernameField.setMinWidth(350);
        usernameField.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2px;");

        // PASSWORD Label and Field
        Text passwordLabel = new Text("PASSWORD");
        passwordLabel.setFont(pressStartFont);
        passwordLabel.setFill(Color.WHITE);

        passwordField = new PasswordField();
        passwordField.setFont(pressStartFont);
        passwordField.setPrefHeight(50);
        passwordField.setMinWidth(350);
        passwordField.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2px;");

        // PLAY Button
        playButton = new Button("PLAY");
        playButton.setFont(pressStartFont);
        playButton.setTextFill(Color.WHITE);
        playButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        playButton.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        playButton.setPadding(new Insets(10, 80, 10, 80));
        playButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        );
        playButton.setOnMouseEntered(e -> playButton.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        playButton.setOnMouseExited(e -> playButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        playButton.setOnMousePressed(e -> playButton.setStyle(
                "-fx-background-color: grey;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        playButton.setOnMouseReleased(e -> playButton.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        playButton.setOnAction(e -> {
            System.out.println("Login attempted with: " + usernameField.getText());
            // Add your login logic here
        });

        errorText = new Text();
        errorText.setFont(Font.loadFont(new FileInputStream("res/fonts/PressStart2P-Regular.ttf"), 12));
        errorText.setFill(Color.RED);
        errorText.setVisible(false);
        errorText.setWrappingWidth(400);

        // Create a container for the error message to prevent layout shifts
        StackPane errorContainer = new StackPane(errorText);
        errorContainer.setPadding(new Insets(0, 0, 0, 0));
        errorContainer.setAlignment(Pos.CENTER);

        // Modify the loginPanel VBox to include the error container
        loginPanel.getChildren().addAll(
                usernameLabel, usernameField,
                passwordLabel, passwordField,
                errorContainer,
                playButton
        );

        leftSide.getChildren().addAll(titleText, loginPanel);

        // --- RIGHT SIDE (Logo) ---
        VBox rightSide = new VBox();
        rightSide.setAlignment(Pos.CENTER);

        Image logoImage = new Image("file:res/images/others/word war z logo.png");
        ImageView logoView = new ImageView(logoImage);
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(600); // Larger logo

        rightSide.getChildren().add(logoView);

        // Add to grid
        mainGrid.add(leftSide, 0, 0);
        mainGrid.add(rightSide, 1, 0);

        root.getChildren().add(mainGrid);

        // --- SCENE SETUP ---
        Scene scene = new Scene(root);
        primaryStage.setTitle("WordWar Z - Login");
        primaryStage.getIcons().add(new Image("file:res/images/others/word war z logo.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void start() {
        stage.show();
    }

    public void close() {
        stage = (Stage) playButton.getScene().getWindow();
        stage.close();
    }

    // Update the showError method
    public void showError(String message) {
        Platform.runLater(() -> {
            errorText.setText(message);
            errorText.setVisible(true);

            // Clear the password field for security
            passwordField.clear();

            // Add pulsing red border effect
            usernameField.setStyle("-fx-background-color: transparent; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: red; " +
                    "-fx-border-width: 2px;");
            passwordField.setStyle("-fx-background-color: transparent; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: red; " +
                    "-fx-border-width: 2px;");

            // Reset the border colors after a short delay
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> {
                usernameField.setStyle("-fx-background-color: transparent; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2px;");
                passwordField.setStyle("-fx-background-color: transparent; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2px;");
                clearError();
            });
            pause.play();
        });
    }

    // Add a method to clear errors
    public void clearError() {
        errorText.setVisible(false);
    }

    // Add these getters at the bottom of the LoginView class:
    public TextField getUsernameField() {
        return usernameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public Button getPlayButton() {
        return playButton;
    }

    public void setOnPlay(Runnable action) {
        playButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Fields cannot be empty.");
            } else {
                clearError();
                action.run();
            }
        });
    }

}
