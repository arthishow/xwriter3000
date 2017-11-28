package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Login {

    private static int HEIGHT = 280;
    private static int WIDTH = 320;

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

        Label userName = new Label("User Name: ");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password: ");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button signIn = new Button("Sign in");
        Button createAccount = new Button("Create account");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(createAccount);
        hbBtn.getChildren().add(signIn);
        grid.add(hbBtn, 1, 4);

        Text actionTarget = new Text();
        grid.add(actionTarget, 1, 6);

        createAccount.setOnAction(e -> CreateAccount.initCreateAccountWindow(new Stage()));
        signIn.setOnAction(e -> {
            if (Main.client.authenticateUser(userTextField.getText(), pwBox.getText())) {
                actionTarget.setFill(Color.GREEN);
                actionTarget.setText("Log-in successful.");
                SelectBook.initSelectBookWindow(stage);
            } else {
                pwBox.setText("");
                actionTarget.setFill(Color.RED);
                actionTarget.setText("Error, please retry.");
            }
        });

        signIn.setOnKeyPressed(ke -> {
                if (ke.getCode().equals(KeyCode.ENTER))
                {
                   signIn.getOnAction();
                }
        });

        signIn.setDefaultButton(true);
        stage.setResizable(false);

        Scene scene = new Scene(grid, WIDTH, HEIGHT);
        stage.setScene(scene);
    }
}
