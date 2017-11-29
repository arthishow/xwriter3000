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

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement login = conn.prepareStatement(query)){


            login.setString(1, username);

            login.setString(2, password);

            ResultSet rs = login.executeQuery();

            if(rs.next()){
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

        String insert = "INSERT INTO author(authorName, authorPass) VALUES (?, ?)" ;

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement insertAuthor = conn.prepareStatement(insert)){

            insertAuthor.setString(1, username);

            insertAuthor.setString(2, password);

            int result = insertAuthor.executeUpdate();


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

        String insertBook = "INSERT INTO book(bookId, title, content) VALUES (?, ?, ?)" ;

        String insertAuth = "INSERT INTO userbook(bookId, authorName, authorization) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement insertBookStatement = conn.prepareStatement(insertBook);
             PreparedStatement insertAuthStatement = conn.prepareStatement(insertAuth)){

            insertBookStatement.setInt(1, book.getBookID());

            insertBookStatement.setString(2, book.getTitle());

            insertBookStatement.setString(3, "");

            insertAuthStatement.setInt(1, book.getBookID());

            insertAuthStatement.setString(2, username);

            insertAuthStatement.setInt(3, 0);



            int firstResult = insertBookStatement.executeUpdate();

            int secondResult = insertAuthStatement.executeUpdate();



            //FIXME
            if (firstResult == 1 && secondResult == 1){
                return book.getBookID();

            } else {
                return -1;
            }

        } catch(SQLException e){
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getBook(int bookID, String username){

        String auth = "SELECT authorization FROM userbook WHERE bookId = ? AND authorName = ?";

        String book = "SELECT content FROM book WHERE bookId = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement authStatement = conn.prepareStatement(auth);
             PreparedStatement bookStatement = conn.prepareStatement(book)){

            authStatement.setInt(1, bookID);

            authStatement.setString(2, username);

            ResultSet rs = authStatement.executeQuery();

            if (rs.next()){

                int authorization = rs.getInt("authorization");


                if (authorization <= 2) {

                    bookStatement.setInt(1, bookID);

                    rs = bookStatement.executeQuery();

                    if(rs.next()){
                        String bookContent = rs.getString("content");
                        return bookContent;
                    }
                }

            }

        } catch(SQLException e){
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Book> getBookList(String username){

        String query = "SELECT DISTINCT B.bookId, title FROM book as B JOIN userbook as U " +
                        "WHERE authorName = ? AND authorization <= 1 AND B.bookId = U.bookId";

        try (Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
             PreparedStatement bookStatement = conn.prepareStatement(query)){

            bookStatement.setString(1, username);

            //String sql = "SELECT DISTINCT B.bookId, title FROM book as B JOIN userbook as U WHERE authorName = '" +
            //        username + "' AND authorization <= 1 AND B.bookid = U.bookid"  ;


            ResultSet rs = bookStatement.executeQuery();

            List<Book> bookList = new ArrayList<>();

            while(rs.next()){

                int bookID = rs.getInt("bookId");

                String title = rs.getString("title");

                Book book = new Book(bookID, title);


                bookList.add(book);


            }

            return bookList;

        } catch(SQLException e){
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    public Boolean changeBook(String username, int bookID, String content){

        String checkAuth = "select authorization from userbook where bookId = ? and authorName = ?";

        String update = "update book set content = ? where bookId = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
             PreparedStatement checkAuthStatement = conn.prepareStatement(checkAuth);
             PreparedStatement updateStatement = conn.prepareStatement(update)){

            if(content == null){
                return false;
            }

            checkAuthStatement.setInt(1, bookID);

            checkAuthStatement.setString(2, username);

            ResultSet rs = checkAuthStatement.executeQuery();

            if(rs.next()){

                int authorization = rs.getInt("authorization");


                if (authorization <= 1) {

                    updateStatement.setString(1, content);

                    updateStatement.setInt(2, bookID);

                    int result = updateStatement.executeUpdate();

                    if (result == 1) {
                        return true;
                    }
                }
            }

            return false;

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
