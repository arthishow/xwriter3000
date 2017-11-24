package pt.ulisboa.tecnico.sirs.xwriter3000ui;

public class LoginController {

    protected static User user = new User("123", "Assa");

    //TODO
    protected static boolean login(String userName, String password){
        Boolean success = Main.client.authenticateUser(userName, password);
        return success;
    }
}
