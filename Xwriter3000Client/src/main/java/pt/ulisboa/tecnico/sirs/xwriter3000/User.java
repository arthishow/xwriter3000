package pt.ulisboa.tecnico.sirs.xwriter3000;

/**
 * A class that defines a User object.
 * Made to make the displaying of user information easier in the UI.
 */
public class User {

    private String authorId;
    private int authorizationLevel;

    public User(String authorId, int authorizationLevel) {
        this.authorId = authorId;
        this.authorizationLevel = authorizationLevel;
    }

    public String getAuthorId() {
        return authorId;
    }

    public int getAuthorizationLevel() {
        return authorizationLevel;
    }
}
