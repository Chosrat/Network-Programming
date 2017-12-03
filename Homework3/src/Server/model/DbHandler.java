package Server.model;

import Common.FileCredentials;
import Common.UserCredentials;

import java.io.*;
import java.sql.*;

/**
 * Created by Chosrat on 2017-11-28.
 */

//Klass som hanterar databasen
public class DbHandler {

    private Connection connection;
    private Statement statement;
    private PreparedStatement createUser;
    private PreparedStatement getAllUsers;
    private PreparedStatement deleteUser;
    private PreparedStatement login;
    private PreparedStatement uploadFile;
    private PreparedStatement downloadFile;
    private PreparedStatement getFileName;
    private PreparedStatement listFiles;
    private PreparedStatement deleteFile;
    private boolean fileNameAlreadyExists;

    //skapar anslutning mellan servern och databasen
    public void accessDb() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Homework3", "root", "");
        statement = connection.createStatement();
        preparedStatements(this.connection);
    }

    //Registrerar nya användare i databasen, tittar först om användarnamnet redan existerar eller inte. tar in parametrar genom usercredentials
    public boolean registerNewUser(UserCredentials user) throws SQLException {
        boolean checkIfUserExists = checkUser(user.getUsername());
        if (checkIfUserExists == true) {
            createUser.setString(1, user.getUsername());
            createUser.setString(2, user.getPassword());
            createUser.executeUpdate();
            return true;
        } else
            return false;
    }

    //Tittar om användarnamnen redan finns i databasen
    public boolean checkUser(String username) throws SQLException {
        ResultSet usersInDb = getAllUsers.executeQuery();
        while (usersInDb.next()) {
            if (usersInDb.getString(2).equalsIgnoreCase(username)) {
                return false;
            }
        }
        return true;
    }

    //Tar bort användare från databasen, användare måste vara inloggad för att ta bort sin data
    public boolean deleteUser(UserCredentials user) throws SQLException {
        deleteUser.setString(1, user.getUsername());
        deleteUser.setString(2, user.getPassword());
        deleteUser.executeUpdate();
        return true;
    }

    //Verifierar användarens input för att logga in och skickar tillbaka true om inloggad och false om misslyckad inloggning
    public int login(UserCredentials user) throws SQLException {
        login.setString(1, user.getUsername());
        login.setString(2, user.getPassword());
        ResultSet getId = login.executeQuery();
        while (getId.next()) {
            int id = getId.getInt("id");
            return id;
        }
        return 0;
    }

    //Detta kommer från socket uppkopplingen då skapas en anslutning till databasen och tittar om filnamnet redan finns sparat i databasen  om inte laddas filen upp i databasen
    public boolean uploadFile(InputStream path, FileCredentials credentials) throws FileNotFoundException, SQLException, ClassNotFoundException {
        accessDb();
        fileNameAlreadyExists = checkFileName(credentials);
        if (fileNameAlreadyExists == false) {
            return true;
        }
        uploadFile.setInt(1, credentials.getOwnerId());
        uploadFile.setString(2, credentials.getFileName());
        uploadFile.setInt(3, 123123);
        uploadFile.setBoolean(4, credentials.isPublik());
        uploadFile.setBlob(5, path);
        uploadFile.executeUpdate();
        System.out.println("ExecuteUpdate");
        return false;
    }

    //kontrollerar om filnamnet existerar i databasen eller inte
    public boolean checkFileName(FileCredentials fileCredentials) throws SQLException {
        ResultSet fileNameInDb = getFileName.executeQuery();
        while (fileNameInDb.next()) {
            if (fileNameInDb.getString(2).equalsIgnoreCase(fileCredentials.getFileName())) {
                return false;
            }
        }
        return true;
    }

    //Från socket och skapar först anslutning till databsen för att sedan ladda ner filen om användarid stämmer eller om den är publik.
    //Om filen tillhör en annan användare och den är privat misslyckas nedladdning av filen
    public InputStream downloadFile(FileCredentials credentials) throws IOException, SQLException, ClassNotFoundException {
        accessDb();
        downloadFile.setInt(2, credentials.getOwnerId());
        downloadFile.setString(1, credentials.getFileName());
        ResultSet resultFile = downloadFile.executeQuery();
        InputStream input = null;
        if (resultFile.next()) {
            input = resultFile.getBinaryStream("file");
        }
        return input;
    }

    //Hämtar information om filerna i databasen och skriver ut alla publika filer och de egenägda filerna till användaren
    public String listFiles(UserCredentials user) throws SQLException {
        StringBuilder fileNames = new StringBuilder();
        listFiles.setInt(1, user.getId());
        ResultSet names = listFiles.executeQuery();
        while (names.next()) {
            fileNames.append(names.getString(2) + "\n");
        }
        return fileNames.toString();
    }

    //Låter användare ta bort egna filer eller publika filer
    public String deleteFile(FileCredentials fileCredentials) throws SQLException {
        deleteFile.setString(1, fileCredentials.getFileName());
        deleteFile.setInt(2, fileCredentials.getOwnerId());
        if (checkFileName(fileCredentials) == true) {
            return "Filename not found in database";
        }
        deleteFile.executeUpdate();
        if (checkFileName(fileCredentials) == true) {
            return "File deleted in databse";
        }
        return "File not deleted";
    }

    //SQL statements för databashantering
    private void preparedStatements(Connection connection) throws SQLException {
        createUser = connection.prepareStatement("INSERT INTO user (username, password) VALUES (?, ?)");
        getAllUsers = connection.prepareStatement("SELECT * FROM user ");
        deleteUser = connection.prepareStatement("DELETE FROM user WHERE username =? AND password =?");
        login = connection.prepareStatement("SELECT id FROM user WHERE username = ? AND password = ?");
        uploadFile = connection.prepareStatement("INSERT INTO Files (id, name, size, publik, file) VALUES (?, ?, ?, ?, ?) ");
        listFiles = connection.prepareStatement("SELECT * FROM Files WHERE (publik = 1 OR id = ?)");
        getFileName = connection.prepareStatement("SELECT * FROM Files");
        downloadFile = connection.prepareStatement("SELECT file FROM Files WHERE name = ? AND (publik = 1 OR id = ?)");
        deleteFile = connection.prepareStatement("DELETE FROM Files WHERE name = ? AND (publik = 1 OR id = ?)");
    }


}
