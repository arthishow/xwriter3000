package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

public class Author {

    private int authorID;

    private String name;

    private String password;

    public Author(int authorID, String name, String password){
        this.authorID = authorID;
        this.name = name;
        this.password = password;
    }

}
