/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ulisboa.tecnico.sirs.databaseconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author josesa
 */
public class ConnectionDB {
    
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/xwritter3000?zeroDateTimeBehavior=convertToNull";

    static final String USER = "sirs";
    static final String PASS = "Sirs2017!";

    public static void main(String[] args) {
        
        Connection conn = null;
        Statement stmt = null;

        try{
          Class.forName("com.mysql.jdbc.Driver");

          conn = DriverManager.getConnection(DB_URL,USER,PASS);

          System.out.println("Creating statement...");
          stmt = conn.createStatement();
          String sql;
          sql = "SELECT userName FROM USER";        //Exemplo para ir buscar o nome de todos os USERS
          ResultSet rs = stmt.executeQuery(sql);

          while(rs.next()){
             String userName = rs.getString("userName");

             System.out.print("userName: " + userName + "\n");
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
}
