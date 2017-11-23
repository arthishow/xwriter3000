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

/**
 *
 * @author josesa
 */
public class ConnectionDB {
    
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/xwriter3000?zeroDateTimeBehavior=convertToNull&useSSL=false";

    static final String USER = "root";
    static final String PASS = "Io8JbOCc";

    public void example(String[] args) {
        
        Connection conn = null;
        Statement stmt = null;

        try {
          Class.forName("com.mysql.jdbc.Driver");

          conn = DriverManager.getConnection(DB_URL,USER,PASS);

          System.out.println("Creating statement...");
          stmt = conn.createStatement();
          String sql;
          sql = "SELECT authorName FROM user";        //Exemplo para ir buscar o nome de todos os USERS
          ResultSet rs = stmt.executeQuery(sql);

          while(rs.next()){
             String userName = rs.getString("authorName");

             System.out.print("authorName: " + userName + "\n");
          }
          rs.close();
          stmt.close();
          conn.close();

       }catch(SQLException se){
          se.printStackTrace();
       }catch(Exception e){
          e.printStackTrace();
       }finally{
          try{
             if(stmt!=null)
                stmt.close();
          }catch(SQLException se2){
          }
          try{
             if(conn!=null)
                conn.close();
          }catch(SQLException se){
             se.printStackTrace();
          }
       }
       System.out.println("Goodbye!");
    }

    public int login(String username, String password){
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            System.out.println("Creating statement...");

            stmt = conn.createStatement();

            String sql;

            //add protection from sqli
            sql = "select id, authorName, authorPass from author where authorName = '" + username + "' AND authorPass = '"+ password +"'";

            System.out.println(sql);

            ResultSet rs = stmt.executeQuery(sql);

            rs.next();

            int authorID = rs.getInt("id");

            String dataUsername = rs.getString("authorName");

            String dataPassword = rs.getString("authorPass");

            conn.close();

            stmt.close();

            if (username.equals(dataUsername) &&  password.equals(dataPassword)){
                return authorID;
            }
            else{
                return -1;
            }


        } catch(SQLException e){
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean createAuthor(String username, String password){
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            System.out.println("Creating statement...");

            stmt = conn.createStatement();

            String sql;
            sql = "INSERT INTO author(authorName, authorPass) VALUES ("
                     + "'" + username + "'" + "," + "'" + password + "'" + ")" ;

            System.out.println(sql);

            int result = stmt.executeUpdate(sql);

            System.out.println(result);

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

    public Boolean createBook(Book book, int authorID){
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            System.out.println("Creating statement...");

            stmt = conn.createStatement();

            String bookSql;
            bookSql = "INSERT INTO book(id, title, content) VALUES ("
                    + book.getBookID() + "," + "'" + book.getTitle() + "'" + "," + "'" + book.getText() + "'" + ")" ;

            String authrizationLevel;
            authrizationLevel = "INSERT INTO userbook(bookId, authorId, authorization) VALUES ("
                            + book.getBookID() + "," + authorID + ",0)";


            System.out.println(bookSql);

            int firstResult = stmt.executeUpdate(bookSql);

            int secondResult = stmt.executeUpdate(authrizationLevel);

            System.out.println(firstResult);

            System.out.println(secondResult);

            stmt.close();
            conn.close();

            if (firstResult == 1 && secondResult == 1){
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

    public String getBook(int bookID, int authorID){
        Connection conn = null;
        Statement stmt = null;

        try{

            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            System.out.println("Creating statement...");

            stmt = conn.createStatement();

            String sql;

            //add protection from sqli
            sql = "SELECT authorization FROM userbook WHERE bookId = " + bookID +
                    " AND authorId = " + authorID;

            System.out.println(sql);

            ResultSet rs = stmt.executeQuery(sql);

            rs.next();

            conn.close();

            stmt.close();

            int authorization = rs.getInt("authorization");

            if (authorization <= 1){

                sql = "SELECT content FROM book WHERE bookId = " + bookID;

                System.out.println(sql);

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

    public List<Book> getBookList(int authorID){

        try (Connection conn = DriverManager.getConnection(DB_URL,USER,PASS)){
            Class.forName("com.mysql.jdbc.Driver");

            Statement stmt = conn.createStatement();

            String sql = "SELECT DISTINCT bookId, title FROM book JOIN userbook WHERE authorId = " +
                        authorID + " AND authorization <= 1" ;

            System.out.println(sql);

            ResultSet rs = stmt.executeQuery(sql);

            ArrayList<Book> bookList = new ArrayList<Book>();


            while(rs.next()){


                int bookID = rs.getInt("bookId");

                System.out.println(bookID);

                String title = rs.getString("title");

                Book book = new Book(bookID, title);

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


    public Boolean changeBook(int authorID, int bookID, String content){

        try (Connection conn = DriverManager.getConnection(DB_URL,USER,PASS)){
            Class.forName("com.mysql.jdbc.Driver");

            Statement stmt = conn.createStatement();

            String sql = "SELECT authorization FROM userbook WHERE bookId = " + bookID +
                            " AND authorId = " + authorID;


            ResultSet rs = stmt.executeQuery(sql);

            rs.next();

            int authorization = rs.getInt("authorization");


            if (authorization <= 1){
                String update = "UPDATE book SET content = '" + content +
                                "' WHERE id=" + bookID;

                System.out.println(update);

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
}
