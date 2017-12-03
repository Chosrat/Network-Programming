package Server.controller;

import Common.FileCredentials;
import Common.FileServer;
import Common.UserCredentials;
import Server.model.DbHandler;
import Server.model.FileTransferServer;
import Server.model.UserHandler;
import Server.startup.Main;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Chosrat on 2017-11-28.
 */
public class Controller extends UnicastRemoteObject implements FileServer {

    private UserHandler userHandler = new UserHandler();
    private FileTransferServer fileTransferServer = new FileTransferServer();

    public Controller() throws RemoteException, SQLException, ClassNotFoundException {
    }

    @Override
    public UserCredentials registerNewUser(UserCredentials user) throws RemoteException, SQLException {
        return userHandler.registerNewUser(user);
    }

    @Override
    public String deleteUser(UserCredentials user) throws RemoteException, SQLException {
        return userHandler.deleteUser(user);
    }

    @Override
    public UserCredentials login(UserCredentials user) throws RemoteException, SQLException {
        return userHandler.login(user);
    }

    @Override
    public String logout(UserCredentials user) throws RemoteException {
        return null;
    }

    @Override
    public String listFiles(UserCredentials user) throws RemoteException, SQLException {
        return userHandler.listFiles(user);
    }

    @Override
    public void startFileServerSocket() throws IOException {
        fileTransferServer.startFilesServerSocket();
    }

    @Override
    public void serverSocketAccept() throws IOException {
        fileTransferServer.serverSocketAccept();
    }

    @Override
    public boolean serverUploadFile(FileCredentials credentials) throws IOException, SQLException, ClassNotFoundException {
        return fileTransferServer.uploadFile(credentials);
    }

    @Override
    public void downloadFile(FileCredentials credentials) throws IOException, SQLException, ClassNotFoundException {
        fileTransferServer.downloadFile(credentials);
    }

    @Override
    public String deleteFile(FileCredentials fileCredentials) throws RemoteException, SQLException {
        return userHandler.deleteFile(fileCredentials);
    }


}
