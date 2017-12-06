package pt.ulisboa.tecnico.sirs.xwriter3000server.domain;

public class Author {
    //fix field
    //private static final AtomicInteger count = new AtomicInteger(1);


    private String name;

    private String password;

    public Author(String name, String password){
        this.name = name;
        this.password = password;
    }


    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
