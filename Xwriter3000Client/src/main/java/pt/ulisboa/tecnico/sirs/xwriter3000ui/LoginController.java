package pt.ulisboa.tecnico.sirs.xwriter3000ui;

public class LoginController {

    protected static boolean login(String userName, String password){
        return Main.client.authenticateUser(userName, password);
    }
}
