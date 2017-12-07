/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ulisboa.tecnico.sirs.databaseconnection;

import pt.ulisboa.tecnico.sirs.xwriter3000server.domain.Book;
import pt.ulisboa.tecnico.sirs.xwriter3000server.domain.CypherUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ConnectionDB {

    private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private final String DB_URL = "jdbc:mysql://localhost:3306/xwriter3000?zeroDateTimeBehavior=convertToNull&useSSL=false";

    private final String USER = "root";
    private final String PASS = "Io8JbOCc";

    private CypherUtil cypherUtil;

    public ConnectionDB(CypherUtil cypherUtil) {
        this.cypherUtil = cypherUtil;
    }

    public Boolean login(String username, String password) {

        String query = "select authorName, authorPass from author where authorName = ?";

        String getSalt = "select salt from salt where authorName = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement login = conn.prepareStatement(query);
             PreparedStatement getSaltStatement = conn.prepareStatement(getSalt)) {


            login.setString(1, username);

            getSaltStatement.setString(1, username);

            ResultSet rs = login.executeQuery();

            ResultSet saltResult = getSaltStatement.executeQuery();

            if (rs.next() && saltResult.next()) {
                String passHash = rs.getString("authorPass");

                String salt = saltResult.getString("salt");

                String enteredPass = cypherUtil.hashPass(password, salt);

                if (passHash.equals(enteredPass)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //fixme return false if username exists
    public Boolean createAuthor(String username, String password, String secret, String publicKey) {

        String insert = "INSERT INTO author(authorName, authorPass) VALUES (?, ?)";
        String insertSalt = "INSERT INTO salt(authorName, salt) VALUES (?, ?)";

        String insertSecretKey = "INSERT INTO authorKeys(authorName, secretKey, keyType) VALUES (?, ?, ?)";

        String insertPublicKey = "INSERT INTO authorKeys(authorName, secretKey, keyType) VALUES (?, ?, ?)";

        if (!authorExists(username)) {

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement insertAuthor = conn.prepareStatement(insert);
                 PreparedStatement insertSaltStat = conn.prepareStatement(insertSalt);
                 PreparedStatement insertSecretStatement = conn.prepareStatement(insertSecretKey);
                 PreparedStatement insertPublicStatement = conn.prepareStatement(insertPublicKey)) {

                insertAuthor.setString(1, username);

                insertSaltStat.setString(1, username);

                String salt = cypherUtil.generateSalt();

                String hash = cypherUtil.hashPass(password, salt);


                insertAuthor.setString(2, hash);

                insertSaltStat.setString(2, salt);

                int firstResult = insertAuthor.executeUpdate();

                int secondResult = insertSaltStat.executeUpdate();

                if (firstResult == 1 && secondResult == 1) {

                    insertSecretStatement.setString(1, username);

                    insertSecretStatement.setString(2, secret);

                    insertSecretStatement.setString(3, "secret");

                    insertPublicStatement.setString(1, username);

                    insertPublicStatement.setString(2, publicKey);

                    insertPublicStatement.setString(3, "public");

                    firstResult = insertSecretStatement.executeUpdate();

                    secondResult = insertPublicStatement.executeUpdate();


                    if (firstResult == 1 && secondResult == 1) {
                        return true;
                    }
                }
                return false;

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public int createBook(String title, String username, String secreyKey) {

        String insertBook = "INSERT INTO book(title, content) VALUES (?, ?)";

        String getBookID = "SELECT MAX(bookId) from book";

        String insertAuth = "INSERT INTO userbook(bookId, authorName, authorization) VALUES (?, ?, ?)";

        String insertKey = "INSERT INTO authorSymKeys(authorName, bookId, secretKey) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement insertBookStatement = conn.prepareStatement(insertBook);
             PreparedStatement insertAuthStatement = conn.prepareStatement(insertAuth);
             PreparedStatement insertKeyStatement = conn.prepareStatement(insertKey);
             PreparedStatement getBookIDStat = conn.prepareStatement(getBookID)) {


            insertBookStatement.setString(1, title);

            insertBookStatement.setString(2, "");

            int firstResult = insertBookStatement.executeUpdate();

            ResultSet resultSet = getBookIDStat.executeQuery();

            if (firstResult == 1 && resultSet.next()) {

                int bookID = resultSet.getInt("MAX(bookId)");

                insertAuthStatement.setInt(1, bookID);

                insertAuthStatement.setString(2, username);

                insertAuthStatement.setInt(3, 0);

                insertKeyStatement.setString(1, username);

                insertKeyStatement.setInt(2, bookID);

                insertKeyStatement.setString(3, secreyKey);


                int secondResult = insertAuthStatement.executeUpdate();

                int thirdResult = insertKeyStatement.executeUpdate();


                if (secondResult == 1 && thirdResult == 1) {
                    return bookID;
                } else {
                    return -1;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getBook(int bookID, String username) {

        String auth = "SELECT authorization FROM userbook WHERE bookId = ? AND authorName = ?";

        String book = "SELECT content FROM book WHERE bookId = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement authStatement = conn.prepareStatement(auth);
             PreparedStatement bookStatement = conn.prepareStatement(book)) {

            authStatement.setInt(1, bookID);

            authStatement.setString(2, username);

            ResultSet rs = authStatement.executeQuery();

            if (rs.next()) {

                int authorization = rs.getInt("authorization");


                if (authorization <= 2) {

                    bookStatement.setInt(1, bookID);

                    rs = bookStatement.executeQuery();

                    if (rs.next()) {
                        String bookContent = rs.getString("content");
                        return bookContent;
                    }
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Book> getBookList(String username) {

        String query = "SELECT DISTINCT B.bookId, title FROM book as B JOIN userbook as U " +
                "WHERE authorName = ? AND authorization <= 2 AND B.bookId = U.bookId";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement bookStatement = conn.prepareStatement(query)) {

            bookStatement.setString(1, username);


            ResultSet rs = bookStatement.executeQuery();

            List<Book> bookList = new ArrayList<>();

            while (rs.next()) {

                int bookID = rs.getInt("bookId");

                String title = rs.getString("title");

                Book book = new Book(bookID, title);


                bookList.add(book);


            }

            return bookList;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    public Boolean changeBook(String username, int bookID, String content) {

        String checkAuth = "select authorization from userbook where bookId = ? and authorName = ?";

        String update = "update book set content = ? where bookId = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement checkAuthStatement = conn.prepareStatement(checkAuth);
             PreparedStatement updateStatement = conn.prepareStatement(update)) {

            if (content == null) {
                return false;
            }

            checkAuthStatement.setInt(1, bookID);

            checkAuthStatement.setString(2, username);

            ResultSet rs = checkAuthStatement.executeQuery();

            if (rs.next()) {

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

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public Boolean addAuthorAuth(int bookID, String originalAuthor, String username, int authorization) {

        String checkOriginal = "select authorization from userbook where bookID = ? and authorName = ?";

        String insertAuth = "insert into userbook(bookId, authorName, authorization) values (?, ?, ?)";

        String updateAuth = "update userbook set authorization = ? where bookId = ? and authorName = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement checkAuthStatement = conn.prepareStatement(checkOriginal);
             PreparedStatement insertAuthStatement = conn.prepareStatement(insertAuth);
             PreparedStatement updateAuthStatement = conn.prepareStatement(updateAuth)) {

            checkAuthStatement.setInt(1, bookID);
            checkAuthStatement.setString(2, originalAuthor);

            ResultSet rs = checkAuthStatement.executeQuery();

            if (rs.next()) {


                if (rs.getInt("authorization") == 0) {

                    checkAuthStatement.setString(2, username);

                    rs = checkAuthStatement.executeQuery();

                    if (rs.next()) {

                        if (rs.getInt("authorization") != authorization) {
                            updateAuthStatement.setInt(1, authorization);
                            updateAuthStatement.setInt(2, bookID);
                            updateAuthStatement.setString(3, username);
                            int updateResult = updateAuthStatement.executeUpdate();

                            if (updateResult == 1) {
                                return true;
                            }
                        } else {
                            return true;
                        }

                    } else {
                        insertAuthStatement.setInt(1, bookID);
                        insertAuthStatement.setString(2, username);
                        insertAuthStatement.setInt(3, authorization);

                        int insertResult = insertAuthStatement.executeUpdate();

                        if (insertResult == 1) {
                            return true;
                        }
                    }

                }

            }

            return false;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public Boolean authorExists(String username) {

        String query = "SELECT authorName FROM author WHERE authorName = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement checkAuthStatement = conn.prepareStatement(query)) {

            checkAuthStatement.setString(1, username);

            ResultSet rs = checkAuthStatement.executeQuery();

            if (rs.next()) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public List<String> getAuthorsFromBook(String bookID, String author) {

        String query = "SELECT authorName FROM userbook WHERE bookID = ? ";


        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, Integer.parseInt(bookID));

            ResultSet rs = statement.executeQuery();

            List<String> authors = new ArrayList<>();


            while (rs.next()) {

                String username = rs.getString("authorName");

                if (!author.equals(username)) {
                    authors.add(username);
                }

            }

            return authors;


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPublicKey(String username) {

        String query = "SELECT secretKey FROM authorKeys WHERE authorName = ? and keyType = 'public' ";


        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, username);

            ResultSet rs = statement.executeQuery();


            if (rs.next()) {
                String publicKey = rs.getString("secretKey");
                return publicKey;
            }

            return null;


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPrivateKey(String username) {

        String query = "SELECT secretKey FROM authorKeys WHERE authorName = ? and keyType = 'secret' ";


        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, username);

            ResultSet rs = statement.executeQuery();


            if (rs.next()) {
                String privateKey = rs.getString("secretKey");
                return privateKey;
            }

            return null;


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void storeTempKey(String username, int bookID, String tempKey) {

        String query = "insert into tempSymKeys(authorName, bookId, secretKey) values (?, ?, ?)";


        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, username);

            statement.setInt(2, bookID);

            statement.setString(3, tempKey);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getTempKey(String username, int bookID) {

        String query = "SELECT secretKey FROM tempSymKeys WHERE authorName = ? and bookId = ? ";

        String delete = "DELETE FROM tempSymKeys WHERE authorName = ? and bookId = ? ";


        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = conn.prepareStatement(query);
             PreparedStatement deleteStatement = conn.prepareStatement(delete)) {

            statement.setString(1, username);

            statement.setInt(2, bookID);

            deleteStatement.setString(1, username);

            deleteStatement.setInt(2, bookID);

            ResultSet rs = statement.executeQuery();

            int result = deleteStatement.executeUpdate();

            if (rs.next() || result == 1) {
                String secretKey = rs.getString("secretKey");
                return secretKey;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getSecretKey(String username, int bookID) {

        String query = "SELECT secretKey FROM authorSymKeys WHERE authorName = ? and bookId = ? ";


        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, username);

            statement.setInt(2, bookID);

            ResultSet rs = statement.executeQuery();


            if (rs.next()) {
                String secretKey = rs.getString("secretKey");
                return secretKey;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setSecretKey(String username, int bookID, String symKey) {

        String query = "insert into authorSymKeys(authorName, bookId, secretKey) values (?, ?, ?)";


        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, username);

            statement.setInt(2, bookID);

            statement.setString(3, symKey);

            int result = statement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSecretKey(String username, int bookID, String symKey) {

        String query = "update authorSymKeys set secretKey = ? where bookId = ? and authorName = ?";


        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, symKey);

            statement.setInt(2, bookID);

            statement.setString(3, username);


            int result = statement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Boolean removeUser(String author, int bookID, String remAuthor) {

        String update = "DELETE FROM userbook WHERE authorName = ? and bookId = ? ";

        String checkOriginal = "select authorization from userbook where bookID = ? and authorName = ?";


        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = conn.prepareStatement(update);
             PreparedStatement checkAuth = conn.prepareStatement(checkOriginal)) {

            checkAuth.setInt(1, bookID);

            checkAuth.setString(2, author);

            ResultSet rs = checkAuth.executeQuery();

            if (rs.next()) {


                if (rs.getInt("authorization") == 0) {

                    statement.setString(1, remAuthor);

                    statement.setInt(2, bookID);

                    int result = statement.executeUpdate();

                    if (result == 1) {
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean removeTempKey(int bookID, String remAuthor) {

        String update = "DELETE FROM tempSymKeys WHERE authorName = ? and bookId = ? ";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = conn.prepareStatement(update)) {

            statement.setString(1, remAuthor);

            statement.setInt(2, bookID);

            int result = statement.executeUpdate();

            if (result == 1) {
                return true;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean removeSymKey(int bookID, String remAuthor) {

        String update = "DELETE FROM authorSymKeys WHERE authorName = ? and bookId = ? ";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = conn.prepareStatement(update)) {

            statement.setString(1, remAuthor);

            statement.setInt(2, bookID);

            int result = statement.executeUpdate();

            if (result == 1) {
                return true;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public Boolean checkTempKey(int bookID, String remAuthor) {

        String query = "Select * FROM tempSymKeys WHERE authorName = ? and bookId = ? ";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, remAuthor);

            statement.setInt(2, bookID);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return true;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean checkSymKey(int bookID, String remAuthor) {

        String query = "Select * FROM authorSymKeys WHERE authorName = ? and bookId = ? ";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, remAuthor);

            statement.setInt(2, bookID);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Integer getAuthFromBook(int bookID, String username) {

        String update = "Select authorization FROM userbook WHERE authorName = ? and bookId = ? ";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement statement = conn.prepareStatement(update)) {

            statement.setString(1, username);

            statement.setInt(2, bookID);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getInt("authorization");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


}
