package Server.model;

import Common.FileCredentials;
import Common.UserCredentials;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Chosrat on 2017-11-28.
 */
public class UserHandler {

    private DbHandler dbHandler;

    public UserHandler() throws SQLException, ClassNotFoundException {
        dbHandler = new DbHandler();
        dbHandler.accessDb();
    }

    public UserCredentials registerNewUser(UserCredentials user) throws SQLException {
        user.setStatus(dbHandler.registerNewUser(user));
        if(user.getStatus() == true){
            System.out.println(user.getStatus());
            user.setStatus(true);
        } return user;
    }

    public String deleteUser(UserCredentials user) throws SQLException {
        user.setStatus(dbHandler.deleteUser(user));
        return "User deleted";
    }

    public UserCredentials login(UserCredentials user) throws SQLException {
        user.setId(dbHandler.login(user));
        if(user.getId() > 0){
            user.setStatus(true);
            return user;
        }else
            return user;
    }

    public String logout(UserCredentials user){
        return null;
    }

    public String listFiles(UserCredentials user) throws SQLException {
        return dbHandler.listFiles(user);
    }

    public String deleteFile(FileCredentials fileCredentials) throws SQLException {
        return dbHandler.deleteFile(fileCredentials);
    }

}
