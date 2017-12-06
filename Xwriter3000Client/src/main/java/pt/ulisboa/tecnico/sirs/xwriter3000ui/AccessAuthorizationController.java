package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import pt.ulisboa.tecnico.sirs.xwriter3000.User;

import java.util.ArrayList;
import java.util.List;

class AccessAuthorizationController {

    //TODO update authorization level
    /**
     * Given a book ID, will retrieve the list of Users (userID and authorization level) that are bound to it.
     * @param bookId the given book ID
     * @return the list of Users that are bound to it
     */
    protected static List<User> createUserListFromGivenBook(String bookId) {
        List<String> userIds = Main.client.getAuthorsFromBook(bookId);
        List<User> users = new ArrayList<>();
        for (String userId : userIds) {
            users.add(new User(userId, 0));
        }
        return users;
    }
}
