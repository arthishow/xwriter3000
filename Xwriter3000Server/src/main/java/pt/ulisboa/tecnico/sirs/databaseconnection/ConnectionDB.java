/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ulisboa.tecnico.sirs.databaseconnection;

import pt.ulisboa.tecnico.sirs.xwriter3000server.domain.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ConnectionDB {
    
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/xwriter3000?zeroDateTimeBehavior=convertToNull&useSSL=false";

    static final String USER = "root";
    static final String PASS = "Io8JbOCc";



    public Boolean login(String username, String password){

        String query = "select authorName, authorPass from author " +
                       "where authorName = ? and authorPass = ?";

        try {

            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);


            PreparedStatement login = conn.prepareStatement(query);

            login.setString(1, username);

            login.setString(2, password);

            ResultSet rs = login.executeQuery();

            rs.next();

            String dataUsername = rs.getString("authorName");

            String dataPassword = rs.getString("authorPass");

            conn.close();

            login.close();

            if (username.equals(dataUsername) &&  password.equals(dataPassword)){
                return true;
            }
            else{
                return false;
            }


        } catch(SQLException e){
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean createAuthor(String username, String password){
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection(DB_URL, USER, PASS);


            stmt = conn.createStatement();

            String sql;
            sql = "INSERT INTO author(authorName, authorPass) VALUES ("
                     + "'" + username + "'" + "," + "'" + password + "'" + ")" ;


            int result = stmt.executeUpdate(sql);

            stmt.close();
            conn.close();

            if (result == 1){
                return true;
            } else {
                return false;
            }

        } catch(SQLException e){
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int createBook(Book book, String username){
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection(DB_URL, USER, PASS);


            stmt = conn.createStatement();

            String bookSql;
            bookSql = "INSERT INTO book(bookId, title, content) VALUES ("
                    + book.getBookID() + "," + "'" + book.getTitle() + "'" + "," + "'" + book.getText() + "'" + ")" ;



            String authrizationLevel;
            authrizationLevel = "INSERT INTO userbook(bookId, authorName, authorization) VALUES ("
                            + book.getBookID() + ",'" + username + "',0)";


            int firstResult = stmt.executeUpdate(bookSql);

            int secondResult = stmt.executeUpdate(authrizationLevel);


            stmt.close();
            conn.close();

            //FIXME
            if (firstResult == 1 && secondResult == 1){
                return book.getBookID();

            } else {
                return 0;
            }

        } catch(SQLException e){
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getBook(int bookID, String username){
        Connection conn = null;
        Statement stmt = null;

        try{

            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection(DB_URL, USER, PASS);


            stmt = conn.createStatement();

            String sql;

            //add protection from sqli
            sql = "SELECT authorization FROM userbook WHERE bookId = " + bookID +
                    " AND authorName = '" + username + "'";


            ResultSet rs = stmt.executeQuery(sql);

            rs.next();

            conn.close();

            stmt.close();

            int authorization = rs.getInt("authorization");

            if (authorization <= 1){

                sql = "SELECT content FROM book WHERE bookId = " + bookID;


                rs = stmt.executeQuery(sql);

                rs.next();

                String book = rs.getString("content");

                if(book != null){
                    return book;
                }
                else{
                    return null;
                }

            }
            else {
                return null;
            }

        } catch(SQLException e){
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Book> getBookList(String username){

        try (Connection conn = DriverManager.getConnection(DB_URL,USER,PASS)){
            Class.forName("com.mysql.jdbc.Driver");

            Statement stmt = conn.createStatement();

            String sql = "SELECT DISTINCT B.bookId, title FROM book as B JOIN userbook as U WHERE authorName = '" +
                    username + "' AND authorization <= 1 AND B.bookid = U.bookid"  ;


            ResultSet rs = stmt.executeQuery(sql);

            List<Book> bookList = new ArrayList<>();


            while(rs.next()){


                int bookID = rs.getInt("bookId");

                String title = rs.getString("title");

                Book book = new Book(bookID, title);

                System.out.println(bookID);
                System.out.println(title);

                bookList.add(book);


            }

            conn.close();

            stmt.close();

            return bookList;

        } catch(SQLException e){
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    public Boolean changeBook(String username, int bookID, String content){

        try (Connection conn = DriverManager.getConnection(DB_URL,USER,PASS)){
            Class.forName("com.mysql.jdbc.Driver");

            Statement stmt = conn.createStatement();

            String sql = "SELECT authorization FROM userbook WHERE bookId = " + bookID +
                            " AND authorName = " + username;


            ResultSet rs = stmt.executeQuery(sql);

            rs.next();

            int authorization = rs.getInt("authorization");


            if (authorization <= 1){
                String update = "UPDATE book SET content = '" + content +
                                "' WHERE id=" + bookID;


                int result = stmt.executeUpdate(update);

                conn.close();

                stmt.close();

                return true;

            }

        } catch(SQLException e){
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public Boolean addAuthorAuth(int bookID, String username){

        try (Connection conn = DriverManager.getConnection(DB_URL,USER,PASS)){
            Class.forName("com.mysql.jdbc.Driver");

            Statement stmt = conn.createStatement();

            String update = "INSERT INTO userbook(bookId, authorName, authorization) VALUES ( "
                    + bookID + "," + username + "," + "1";


            int result = stmt.executeUpdate(update);


            if(result == 1){
                return true;
            }

        } catch(SQLException e){
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public Boolean authorExists(String username){
        try (Connection conn = DriverManager.getConnection(DB_URL,USER,PASS)){
            Class.forName("com.mysql.jdbc.Driver");

            Statement stmt = conn.createStatement();

            String sql = "SELECT username FROM author WHERE username = '" + username + "'";

            ResultSet rs = stmt.executeQuery(sql);

            rs.next();


            if(rs.getString("authorName").equals(username)){
                return true;
            }else{
                return false;
            }

        } catch(SQLException e){
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<String> getAuthorsFromBook(String bookID){

        try(Connection conn = DriverManager.getConnection(DB_URL,USER,PASS)){
            Class.forName("com.mysql.jdbc.Driver");

            Statement stmt = conn.createStatement();

            String sql = "SELECT username FROM author JOIN book JOIN userbook WHERE bookID = '" + bookID + "'";

            ResultSet rs = stmt.executeQuery(sql);

            List<String> authors = new ArrayList<>();

            rs.next();

            while(rs.next()){

                String username = rs.getString("username");


                authors.add(username);
            }

            return authors;


        } catch(SQLException e){
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
