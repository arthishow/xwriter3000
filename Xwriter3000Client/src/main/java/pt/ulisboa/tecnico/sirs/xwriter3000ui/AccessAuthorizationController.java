package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import javafx.collections.ObservableList;
import pt.ulisboa.tecnico.sirs.xwriter3000.User;

import java.util.ArrayList;
import java.util.List;

class AccessAuthorizationController {

    /**
     * Given a book ID, will retrieve the list of Users (userID and authorization level) that are bound to it.
     *
     * @param bookId the given book ID
     * @return the list of Users that are bound to it
     */
    protected static List<User> createUserListFromGivenBook(String bookId) {
        List<String> userIds = Main.client.getAuthorsFromBook(bookId);
        List<User> users = new ArrayList<>();
        for (String userId : userIds) {
            users.add(new User(userId, Main.client.getAuthFromAuthor(bookId, userId)));
        }
        return users;
    }

    /**
     * Remove from the database the authors that do not feature anymore in the
     * list of authorized users assigned to the given book
     *
     * @param bookId     the given book ID
     * @param newAuthors the new list of authors
     * @return a boolean if the action was completed successfully or not
     */
    protected static boolean removeOldAuthorsFromGivenBook(String bookId, ObservableList<User> newAuthors) {
        List<User> oldAuthors = createUserListFromGivenBook(bookId);
        oldAuthors.removeAll(newAuthors);
        boolean ret = true;
        for (User u : oldAuthors) {
            if (!Main.client.removeAuthor(String.valueOf(bookId), u.getAuthorId())) {
                ret = false;
            }
        }
        return ret;
    }
}
