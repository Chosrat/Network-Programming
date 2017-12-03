package Common;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Chosrat on 2017-11-28.
 */

//interface som listar funktioner mellan klient och server
public interface FileServer extends Remote {

    public static final String SERVER_NAME_IN_REGISTRY = "FILE_SERVER";


    UserCredentials registerNewUser(UserCredentials user) throws RemoteException, SQLException;

    String deleteUser(UserCredentials user) throws RemoteException, SQLException;

    UserCredentials login(UserCredentials user) throws RemoteException, SQLException;

    String logout(UserCredentials user) throws RemoteException;

    String listFiles(UserCredentials user) throws RemoteException, SQLException;

    void startFileServerSocket() throws IOException;

    void serverSocketAccept() throws IOException;

    boolean serverUploadFile(FileCredentials credentials) throws IOException, SQLException, ClassNotFoundException;

    void downloadFile(FileCredentials credentials) throws IOException, SQLException, ClassNotFoundException;

    //boolean checkFileNAme(FileCredentials fileCredentials) throws SQLException, RemoteException;

    String deleteFile(FileCredentials fileCredentials) throws RemoteException, SQLException;

}
