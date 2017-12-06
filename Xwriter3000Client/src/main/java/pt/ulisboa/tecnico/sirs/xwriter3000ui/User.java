package pt.ulisboa.tecnico.sirs.xwriter3000ui;

public class User {

    private String authorId;
    private int authorizationLevel;

    protected User(String authorId, int authorizationLevel) {
        this.authorId = authorId;
        this.authorizationLevel = authorizationLevel;
    }

    protected String getAuthorId() {
        return authorId;
    }

    protected int getAuthorizationLevel() {
        return authorizationLevel;
    }
}
