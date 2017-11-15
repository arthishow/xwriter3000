package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

import java.util.concurrent.atomic.AtomicInteger;

public class Author {
    //fix field
    private static final AtomicInteger count = new AtomicInteger(1);

    private int authorID;

    private String name;

    private String password;

    public Author(String name, String password){
        this.authorID = count.incrementAndGet();
        this.name = name;
        this.password = password;
    }

    public int getAuthorID() {
        return authorID;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
