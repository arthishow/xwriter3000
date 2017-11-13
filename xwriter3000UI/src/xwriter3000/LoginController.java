package xwriter3000;

public class LoginController {

    protected static User user;

    //TODO
    protected static boolean login(String userName, String password){
        user = new User("abc123456", "Assa");
        return userName.equals("test") && password.equals("test");
    }
}
