package client.admin.view;

import client.admin.controller.EditGamePlaySettingsController;
import client.admin.model.AdminDashboardModel;
import client.admin.model.EditGamePlaySettingsModel;
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
import org.omg.CORBA.ORB;

public class AdminDashboardView {
    private static final String BACKGROUND_IMAGE_PATH = "file:res/images/backgrounds/menu bg.png";
    private static final String LOGO_PATH = "file:res/images/others/word war z logo.png";
    private static final String FONT_PATH = "file:res/fonts/PressStart2P-Regular.ttf";

    private Font customFont;
    private VBox rightContent;
    private Button editPlayerBtn;
    private Button editGamePlayBtn;
    private CreatePlayerView createPlayerView;
    private UpdatePlayerView updatePlayerView;
    private ReadPlayerView readPlayerView;
    private DeletePlayerView deletePlayerView;
    private Runnable onShowReadPlayerView;
    private Runnable onShowDeletePlayerView;
    private EditGamePlaySettingsView editGamePlaySettingsView;
    private EditGamePlaySettingsController editGamePlaySettingsController;

    public void initializeUI(Stage primaryStage, AdminDashboardModel adminModel, ORB orb, String token) {
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
        editPlayerBtn = createStyledButton("EDIT PLAYER");
        editGamePlayBtn = createStyledButton("EDIT GAME PLAY");
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

        // Right side content wrapper
        StackPane rightWrapper = new StackPane();
        rightWrapper.setAlignment(Pos.CENTER_RIGHT);
        rightWrapper.setPrefWidth(600);

        rightContent = new VBox();
        rightContent.setAlignment(Pos.CENTER);
        rightContent.setSpacing(20);
        rightContent.setPrefWidth(400);

        rightWrapper.getChildren().add(rightContent);
        mediumGreyPanel.getChildren().addAll(lightGreyPanel, rightWrapper);
        darkGreyPanel.getChildren().add(mediumGreyPanel);
        root.getChildren().add(darkGreyPanel);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize the views
        createPlayerView = new CreatePlayerView(customFont);
        updatePlayerView = new UpdatePlayerView(customFont);
        readPlayerView = new ReadPlayerView(customFont);
        deletePlayerView = new DeletePlayerView(customFont);
        editGamePlaySettingsView = new EditGamePlaySettingsView(customFont);

        // Initialize EditGamePlaySettingsModel
        EditGamePlaySettingsModel gamePlayModel = new EditGamePlaySettingsModel(orb);

        // Initialize the EditGamePlaySettingsController
        editGamePlaySettingsController = new EditGamePlaySettingsController(gamePlayModel, editGamePlaySettingsView, orb, token);
        editGamePlaySettingsView.setController(editGamePlaySettingsController);
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

    public void showCreatePlayerView() {
        rightContent.getChildren().clear();
        rightContent.getChildren().add(createPlayerView);
    }

    public void showUpdatePlayerView() {
        rightContent.getChildren().clear();
        rightContent.getChildren().add(updatePlayerView);
    }

    public void showReadPlayerView() {
        rightContent.getChildren().clear();
        rightContent.getChildren().add(readPlayerView);
        if (onShowReadPlayerView != null) {
            onShowReadPlayerView.run();
        }
    }

    public void showDeletePlayerView() {
        rightContent.getChildren().clear();
        rightContent.getChildren().add(deletePlayerView);
        if (onShowDeletePlayerView != null) {
            onShowDeletePlayerView.run();
        }
    }

    public void setOnShowReadPlayerViewListener(Runnable listener) {
        this.onShowReadPlayerView = listener;
    }

    public void setOnShowDeletePlayerViewListener(Runnable listener) {
        this.onShowDeletePlayerView = listener;
    }

    public void showEditGameplaySettingsView() {
        rightContent.getChildren().clear();
        rightContent.getChildren().add(editGamePlaySettingsView);
    }

    public Button getEditPlayerBtn() {
        return editPlayerBtn;
    }

    public Button getEditGamePlayBtn() {
        return editGamePlayBtn;
    }

    public CreatePlayerView getCreatePlayerView() {
        return createPlayerView;
    }

    public UpdatePlayerView getUpdatePlayerView() {
        return updatePlayerView;
    }

    public ReadPlayerView getReadPlayerView() {
        return readPlayerView;
    }

    public DeletePlayerView getDeletePlayerView() {
        return deletePlayerView;
    }

    public EditGamePlaySettingsView getEditGamePlaySettingsView() {
        return editGamePlaySettingsView;
    }
}