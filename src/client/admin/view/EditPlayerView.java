package client.admin.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class EditPlayerView extends VBox {
    private final Font customFont;

    public EditPlayerView(Font customFont) {
        this.customFont = customFont;
        this.setAlignment(Pos.CENTER_RIGHT);
        this.setSpacing(20);
        this.setPadding(new Insets(30));
        this.setPrefWidth(400); // Align toward right side
        this.setMaxWidth(Double.MAX_VALUE);

        buildUI();
    }

    private void buildUI() {
        // Title
        Label titleLabel = new Label("EDIT PLAYER");
        titleLabel.setFont(Font.font(customFont.getFamily(), FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKRED);

        // Top buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        Button createBtn = createStyledButton("CREATE PLAYER");
        Button updateBtn = createStyledButton("UPDATE PLAYER");
        Button deleteBtn = createStyledButton("DELETE PLAYER");
        buttonBox.getChildren().addAll(createBtn, updateBtn, deleteBtn);

        // Username
        VBox usernameBox = new VBox(5);
        Label usernameLabel = new Label("Username:");
        usernameLabel.setTextFill(Color.WHITE);
        usernameLabel.setFont(customFont);
        TextField usernameField = new TextField();
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Password
        VBox passwordBox = new VBox(5);
        Label passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(Color.WHITE);
        passwordLabel.setFont(customFont);
        PasswordField passwordField = new PasswordField();
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        // Confirm Button
        Button confirmBtn = createStyledButton("CREATE PLAYER");

        this.getChildren().addAll(titleLabel, buttonBox, usernameBox, passwordBox, confirmBtn);
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(customFont);
        button.setTextFill(Color.WHITE);
        button.setCursor(Cursor.HAND);
        button.setBackground(new Background(new BackgroundFill(
                Color.rgb(64, 64, 64), new CornerRadii(8), Insets.EMPTY
        )));

        // Set preferred size for the button
        button.setPrefWidth(350);  // Increase width for better fit
        button.setPrefHeight(50);  // Increase height for better button size

        // Add some padding to ensure text is not cut off
        button.setPadding(new Insets(10, 20, 10, 20));

        button.setOnMouseEntered(e -> button.setBackground(new Background(new BackgroundFill(
                Color.rgb(80, 80, 80), new CornerRadii(8), Insets.EMPTY
        ))));
        button.setOnMouseExited(e -> button.setBackground(new Background(new BackgroundFill(
                Color.rgb(64, 64, 64), new CornerRadii(8), Insets.EMPTY
        ))));

        return button;
    }
}
