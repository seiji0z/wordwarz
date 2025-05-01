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
    TextField waitingField, roundField;
    Button saveBtn;


    public EditGamePlaySettingsView(Font customFont) {
        this.customFont = customFont;
        this.setAlignment(Pos.CENTER_RIGHT);
        this.setSpacing(20);
        this.setPadding(new Insets(30));
        this.setPrefWidth(400); // Align toward right side
        this.setMaxWidth(Double.MAX_VALUE);


        buildUI();
    }


    public void buildUI() {
        // Title
        Label titleLabel = new Label("EDIT GAMEPLAY SETTINGS");
        titleLabel.setFont(Font.font(customFont.getFamily(), FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKRED);


        // Waiting Time
        VBox waitingBox = new VBox(5);
        Label waitingLabel = new Label("Waiting Time:");
        waitingLabel.setTextFill(Color.WHITE);
        waitingLabel.setFont(customFont);
        waitingField = new TextField();
        waitingBox.getChildren().addAll(waitingLabel, waitingField);


        // Round Duration
        VBox roundBox = new VBox(5);
        Label roundLabel = new Label("Round Duration:");
        roundLabel.setTextFill(Color.WHITE);
        roundLabel.setFont(customFont);
        roundField = new TextField();
        roundBox.getChildren().addAll(roundLabel, roundField);

        // Add input validation and tooltips
        waitingField.setPromptText("Enter waiting time in seconds");
        roundField.setPromptText("Enter round duration in seconds");

        // Add tooltips
        waitingField.setTooltip(new Tooltip("Enter the waiting time between games in seconds"));
        roundField.setTooltip(new Tooltip("Enter the duration for each game round in seconds"));

        // Numeric-only input validation
        waitingField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                waitingField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        roundField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                roundField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Save Changes button
        saveBtn = createStyledButton("SAVE CHANGES");


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

    public TextField getWaitingField() { return waitingField;}
    public TextField getRoundField(){ return roundField;}
    public Button getSaveButton() { return saveBtn; }


}
