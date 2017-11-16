/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ulisboa.tecnico.sirs.databaseconnection;

import pt.ulisboa.tecnico.sirs.xwriter3000server.domain.Author;
import pt.ulisboa.tecnico.sirs.xwriter3000server.domain.Book;

import java.sql.*;

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

        try{
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
            sql = "select id from author where authorName = '" + username + "' AND authorPass = '"+ password +"'";

            System.out.println(sql);

            ResultSet rs = stmt.executeQuery(sql);

            rs.next();

            int authorID = rs.getInt("id");


            return authorID;

        } catch(SQLException e){
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Boolean createAuthor(Author author){
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            System.out.println("Creating statement...");

            stmt = conn.createStatement();

            String sql;
            sql = "INSERT INTO author(id, authorName, authorPass) VALUES ("
                    + author.getAuthorID() + "," + "'" + author.getName() + "'" + "," + "'" + author.getPassword() + "'" + ")" ;

            System.out.println(sql);

            int result = stmt.executeUpdate(sql);

            System.out.println(result);

            stmt.close();
            conn.close();

            if (result > 0){
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

    public Boolean createBook(Book book){
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            System.out.println("Creating statement...");

            stmt = conn.createStatement();

            String sql;
            sql = "INSERT INTO book(id, title, content) VALUES ("
                    + book.getBookID() + "," + "'" + book.getTitle() + "'" + "," + "'" + book.getText() + "'" + ")" ;

            System.out.println(sql);

            int result = stmt.executeUpdate(sql);

            System.out.println(result);

            stmt.close();
            conn.close();

            if (result > 0){
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

}
