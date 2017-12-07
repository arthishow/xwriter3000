package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

class PopupMessage {

    /**
     * Generate and display a popup window that display a message.
     *
     * @param stage   the container the window will own
     * @param title   the title of the window
     * @param message the message to be displayed
     * @param height  the height of the window
     * @param width   the width of the window
     */
    protected static void initPopupMessageWindow(Stage stage, String title, String message, int height, int width) {

        stage.setTitle(title);
        BorderPane border = new BorderPane();
        Insets insets = new Insets(10);
        border.setPadding(insets);

        Text text = new Text(message);
        text.setTextAlignment(TextAlignment.CENTER);
        border.setCenter(text);
        BorderPane.setMargin(text, insets);

        Button close = new Button("Close");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().add(close);
        border.setBottom(hbBtn);
        BorderPane.setMargin(hbBtn, insets);

        close.setOnAction(e -> stage.close());
        close.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                close.getOnAction();
            }
        });
        close.setDefaultButton(true);

        Scene scene = new Scene(border, width, height);
        stage.setMinWidth(width);
        stage.setMinHeight(height);
        stage.setMinWidth(width * 3);
        stage.setMaxHeight(height * 3);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }
}

class PopupChoice {

    private boolean choice;

    /**
     * Generate and display a popup window with a dialog and yes and no buttons,
     * so that the user has to make a choice. The choice is then saved in the
     * boolean choice and can be access in the function that called this function
     * thanks to the method at the end showAndWait() instead of the usual show()
     *
     * @param stage   the container the window will own
     * @param title   the title of the window
     * @param message the message to be shown (a question generally)
     * @param height  the height of the window
     * @param width   the width of the window
     */
    protected void initPopupChoiceWindow(Stage stage, String title, String message, int height, int width) {

        stage.setTitle(title);
        BorderPane border = new BorderPane();
        Insets insets = new Insets(10);
        border.setPadding(insets);

        Text text = new Text(message);
        border.setCenter(text);
        BorderPane.setMargin(text, insets);

        Button yes = new Button("Yes");
        Button no = new Button("No");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().addAll(yes, no);
        border.setBottom(hbBtn);
        BorderPane.setMargin(hbBtn, insets);

        yes.setOnAction(e -> {
            choice = true;
            stage.close();
        });

        no.setOnAction(e -> {
            choice = false;
            stage.close();
        });
        no.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                no.getOnAction();
            }
        });
        no.setDefaultButton(true);

        Scene scene = new Scene(border, width, height);
        stage.setMinWidth(width);
        stage.setMinHeight(height);
        stage.setMaxWidth(width * 3);
        stage.setMaxHeight(height * 3);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public boolean getChoice() {
        return choice;
    }
}