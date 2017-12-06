package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import java.util.ArrayList;
import java.util.List;

public class AccessAuthorizationController {

    //TODO update authorization level
    protected static List<User> createUserListFromGivenBook(String bookId) {
        List<String> userIds = Main.client.getAuthorsFromBook(bookId);
        List<User> users = new ArrayList<>();
        for (String userId : userIds) {
            users.add(new User(userId, 0));
        }
        return users;
    }
}
