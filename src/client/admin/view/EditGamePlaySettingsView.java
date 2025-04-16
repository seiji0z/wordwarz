package client.admin.view;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class EditGamePlaySettingsView extends VBox {
    private final Font customFont;


    public EditGamePlaySettingsView(Font customFont) {
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
        Label titleLabel = new Label("EDIT GAMEPLAY SETTINGS");
        titleLabel.setFont(Font.font(customFont.getFamily(), FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKRED);


        // Waiting Time
        VBox waitingBox = new VBox(5);
        Label waitingLabel = new Label("Waiting Time:");
        waitingLabel.setTextFill(Color.WHITE);
        waitingLabel.setFont(customFont);
        TextField waitingField = new TextField();
        waitingBox.getChildren().addAll(waitingLabel, waitingField);


        // Round Duration
        VBox roundBox = new VBox(5);
        Label roundLabel = new Label("Round Duration:");
        roundLabel.setTextFill(Color.WHITE);
        roundLabel.setFont(customFont);
        TextField roundField = new TextField();
        roundBox.getChildren().addAll(roundLabel, roundField);


        // Save Changes button
        Button saveBtn = createStyledButton("SAVE CHANGES");


        this.getChildren().addAll(titleLabel, waitingBox, roundBox, saveBtn);
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
}
