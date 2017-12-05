package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

class PopupMessage {

    static void initPopupMessageWindow(Stage stage, String title, String message, int height, int width) {

        stage.setTitle(title);
        BorderPane border = new BorderPane();
        Insets insets = new Insets(10);
        border.setPadding(insets);

        Text text = new Text(message);
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

     class PopupChoice{

        private boolean choice;

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
        stage.setMaxWidth(width*3);
        stage.setMaxHeight(height*3);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public boolean getChoice(){
            return choice;
    }

}

class PopupFirstTimeLogin{

    private static int HEIGHT = 200;
    private static int WIDTH = 250;
    private String personalCode;

    protected void initPopupFirstTimeLoginWindow(Stage stage, String title, String message) {

        stage.setTitle(title);
        BorderPane border = new BorderPane();
        Insets insets = new Insets(10);
        border.setPadding(insets);

        Text text = new Text("It seems like you're logging-in for the first time on this machine.\n" +
                " Please fill the following field with your personal code.");
        TextField personalCode = new TextField();

        border.setTop(text);
        BorderPane.setMargin(text, insets);

        border.setCenter(personalCode);
        BorderPane.setMargin(personalCode, insets);

        Button ok = new Button("OK");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().addAll(ok);
        border.setBottom(hbBtn);
        BorderPane.setMargin(hbBtn, insets);

        ok.setOnAction(e -> {
            this.personalCode = personalCode.getText();
            stage.close();
        });

        ok.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                ok.getOnAction();
            }
        });
        ok.setDefaultButton(true);

        Scene scene = new Scene(border, WIDTH, HEIGHT);
        stage.setMinWidth(WIDTH);
        stage.setMinHeight(HEIGHT);
        stage.setMaxWidth(WIDTH*3);
        stage.setMaxHeight(HEIGHT*3);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

    public String getPersonalCode(){
        return personalCode;
    }

}