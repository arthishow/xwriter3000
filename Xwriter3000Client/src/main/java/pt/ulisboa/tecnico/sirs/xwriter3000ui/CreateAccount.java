package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CreateAccount {


    private static int HEIGHT = 180;
    private static int WIDTH = 480;

    protected static void initCreateAccountWindow(Stage stage) {

        stage.setTitle("Create account");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text userid = new Text("User ID: ");
        grid.add(userid, 0, 0);

        TextField author = new TextField();
        grid.add(author, 1, 0);

        Text password = new Text("Password: ");
        grid.add(password, 0, 1);

        PasswordField psw1 = new PasswordField();
        grid.add(psw1, 1, 1);

        Text repeatPassword = new Text("Repeat password: ");
        grid.add(repeatPassword, 0, 2);

        PasswordField psw2 = new PasswordField();
        grid.add(psw2, 1, 2);

        Text actionTextUserId = new Text();
        grid.add(actionTextUserId, 2, 0);

        Text actionTextPassword = new Text();
        grid.add(actionTextPassword, 2, 1);

        Text actionTextRepeatPassword = new Text();
        grid.add(actionTextRepeatPassword, 2, 2);

        Button create = new Button("Create");
        Button cancel = new Button("Cancel");
        grid.add(create, 1, 3);
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(create);
        hbBtn.getChildren().add(cancel);
        grid.add(hbBtn, 1, 3);

        Text actionTextCreateUser = new Text();
        grid.add(actionTextCreateUser, 2, 3);

        author.textProperty().addListener(e -> {
            if (Communication.authorExists(author.getText())) {
                actionTextUserId.setFill(Color.RED);
                actionTextUserId.setText("User ID already taken.");
            } else if (!CreateAccountController.verifyUserId(author.getText())) {
                actionTextUserId.setFill(Color.RED);
                actionTextUserId.setText("User ID too short/long.");
            }else {
                actionTextUserId.setFill(Color.GREEN);
                actionTextUserId.setText("Valid User ID.");
            }
        });

        psw1.textProperty().addListener(e -> {
            if (CreateAccountController.verifyPassword(psw1.getText())) {
                actionTextPassword.setFill(Color.GREEN);
                actionTextPassword.setText("OK.");
            }else{
                actionTextPassword.setFill(Color.RED);
                actionTextPassword.setText("Password is too weak.");
            }

            if(psw1.getText().equals(psw2.getText())) {
                actionTextRepeatPassword.setFill(Color.GREEN);
                actionTextRepeatPassword.setText("OK.");
            }
        });

        psw2.textProperty().addListener(e -> {
            if(psw1.getText().equals(psw2.getText())){
                actionTextRepeatPassword.setFill(Color.GREEN);
                actionTextRepeatPassword.setText("OK.");
            }else{
                actionTextRepeatPassword.setFill(Color.RED);
                actionTextRepeatPassword.setText("Passwords are different.");
            }
        });

        create.setOnAction(e -> {
            if (CreateAccountController.createUser(author.getText(), psw1.getText())) {
                actionTextCreateUser.setFill(Color.GREEN);
                actionTextCreateUser.setText("Author created.");
                PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
                delay.setOnFinished(e2 -> stage.close());
                delay.play();
            } else {
                actionTextCreateUser.setFill(Color.RED);
                actionTextCreateUser.setText("An error has occurred.");
            }
        });
        cancel.setOnAction(e -> stage.close());

        Scene scene = new Scene(grid, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
    }
}
