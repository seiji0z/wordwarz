package client.admin.view;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class AdminDashboardView extends Application {


    private static final String BACKGROUND_IMAGE_PATH = "file:res/images/backgrounds/menu bg.png";
    private static final String LOGO_PATH = "file:res/images/others/word war z logo.png";
    private static final String FONT_PATH = "file:res/PressStart2P-Regular.ttf";
    private Font customFont;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Admin Dashboard");


        // Load the custom font
        customFont = Font.loadFont(FONT_PATH, 16);
        if (customFont == null) {
            System.out.println("Failed to load custom font, falling back to default.");
            customFont = Font.font("System", 16);
        }


        StackPane root = new StackPane();


        // Background image
        Image bgImage = new Image(BACKGROUND_IMAGE_PATH);
        BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage backgroundImage = new BackgroundImage(
                bgImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bgSize
        );
        root.setBackground(new Background(backgroundImage));


        // Dark Grey outer panel
        VBox darkGreyPanel = new VBox();
        darkGreyPanel.setAlignment(Pos.CENTER);
        darkGreyPanel.setPadding(new Insets(50));
        darkGreyPanel.setBackground(new Background(new BackgroundFill(
                Color.rgb(50, 50, 50, 0.6), new CornerRadii(12), Insets.EMPTY
        )));


        // Medium Grey panel
        HBox mediumGreyPanel = new HBox();
        mediumGreyPanel.setSpacing(30);
        mediumGreyPanel.setPadding(new Insets(40));
        mediumGreyPanel.setAlignment(Pos.CENTER_LEFT);
        mediumGreyPanel.setBackground(new Background(new BackgroundFill(
                Color.rgb(128, 128, 128, 0.6), new CornerRadii(12), Insets.EMPTY
        )));


        // Light Grey panel (left sidebar)
        VBox lightGreyPanel = new VBox();
        lightGreyPanel.setSpacing(20);
        lightGreyPanel.setPadding(new Insets(30));
        lightGreyPanel.setAlignment(Pos.TOP_CENTER);
        lightGreyPanel.setPrefWidth(300);
        lightGreyPanel.setBackground(new Background(new BackgroundFill(
                Color.rgb(192, 192, 192, 0.85), new CornerRadii(10), Insets.EMPTY
        )));


        // Title
        Label titleLabel = new Label("ADMIN DASHBOARD");
        titleLabel.setFont(Font.font(customFont.getFamily(), FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.MAROON);


        // Buttons
        Button editPlayerBtn = createStyledButton("EDIT PLAYER");
        Button editGamePlayBtn = createStyledButton("EDIT GAME PLAY");
        editPlayerBtn.setPrefSize(250, 100);
        editGamePlayBtn.setPrefSize(250, 100);


        // Logo
        ImageView logoView = new ImageView(new Image(LOGO_PATH));
        logoView.setFitWidth(350);
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);


        // Button container
        VBox buttonContainer = new VBox(50);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(editPlayerBtn, editGamePlayBtn);


        // Spacer to push logo to bottom
        VBox spacer = new VBox();
        spacer.setMinHeight(50);


        lightGreyPanel.getChildren().addAll(titleLabel, buttonContainer, spacer, logoView);


        // Right side content wrapper to keep content aligned right + centered vertically
        StackPane rightWrapper = new StackPane();
        rightWrapper.setAlignment(Pos.CENTER_RIGHT); // This makes views stick to the right
        rightWrapper.setPrefWidth(600); // Or bind it if dynamic


        VBox rightContent = new VBox();
        rightContent.setAlignment(Pos.CENTER); // Center content vertically
        rightContent.setSpacing(20);
        rightContent.setPrefWidth(400); // Fixed width so it's not stretched


        rightWrapper.getChildren().add(rightContent);




        mediumGreyPanel.getChildren().addAll(lightGreyPanel, rightWrapper);
        darkGreyPanel.getChildren().add(mediumGreyPanel);
        root.getChildren().add(darkGreyPanel);


        // Event Handlers
        editPlayerBtn.setOnAction(e -> {
            EditPlayerView editPlayerView = new EditPlayerView(customFont);
            rightContent.getChildren().clear();
            rightContent.getChildren().add(editPlayerView);
        });


        editGamePlayBtn.setOnAction(e -> {
            EditGamePlaySettingsView editGamePlaySettingsView = new EditGamePlaySettingsView(customFont);
            rightContent.getChildren().clear();
            rightContent.getChildren().add(editGamePlaySettingsView);
        });


        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(customFont);
        button.setTextFill(Color.WHITE);
        button.setCursor(Cursor.HAND);
        button.setBackground(new Background(new BackgroundFill(
                Color.rgb(64, 64, 64), new CornerRadii(8), Insets.EMPTY
        )));


        button.setOnMouseEntered(e -> button.setBackground(new Background(new BackgroundFill(
                Color.rgb(80, 80, 80), new CornerRadii(8), Insets.EMPTY
        ))));
        button.setOnMouseExited(e -> button.setBackground(new Background(new BackgroundFill(
                Color.rgb(64, 64, 64), new CornerRadii(8), Insets.EMPTY
        ))));


        return button;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
