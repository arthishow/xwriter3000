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

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof User)) return false;
        User otherUser = (User) other;
        return this.authorId.equals(otherUser.authorId) && this.authorizationLevel == otherUser.authorizationLevel;
    }
}
