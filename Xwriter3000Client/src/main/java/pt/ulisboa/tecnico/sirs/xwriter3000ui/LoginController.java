package pt.ulisboa.tecnico.sirs.xwriter3000ui;

public class LoginController {

    protected static User user = new User("123", "Assa");

    //TODO
    protected static boolean login(String userName, String password){
        Boolean success = Main.client.authenticateUser(userName, password);
        if(success){
            return true;
        }
        else{
            return false;
        }
    }

    //TODO
    protected static boolean verifyPassword(String password){
        return true;
    }

    //TODO
    protected static boolean createUser(String userId, String password){
        Boolean success = Main.client.createUser(userId, password);
        if(success){
            return true;
        }
        else{
            return false;
        }
    }

    //TODO
    protected static boolean authorExists(String userid){
        return false;
    }

}
