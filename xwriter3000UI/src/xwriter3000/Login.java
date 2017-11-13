package xwriter3000;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Login {

    protected static void initLogInWindow(Stage stage){

        stage.setTitle("Xwriter 3000 - Log-in");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Please log-in");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 6);

        btn.setOnAction(e -> login(userTextField, pwBox, actionTarget, stage));

        btn.setOnKeyPressed(ke -> {
                if (ke.getCode().equals(KeyCode.ENTER))
                {
                   login(userTextField, pwBox, actionTarget, stage);
                }
        });

        btn.setDefaultButton(true);
        stage.setResizable(false);

        Scene scene = new Scene(grid, 300, 275);
        stage.setScene(scene);
    }

    private static void login(TextField userTextField, PasswordField pwBox, Text text, Stage stage){
        if(LoginController.login(userTextField.getText(), pwBox.getText())) {
            text.setFill(Color.GREEN);
            text.setText("Log-in successful.");
            Writing.initTextEditingWindow(stage);
        }else{
            text.setFill(Color.RED);
            text.setText("Error, please retry.");
        }
    }
}
