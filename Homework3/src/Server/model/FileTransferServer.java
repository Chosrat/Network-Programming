package Server.model;

import Common.FileCredentials;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

/**
 * Created by Chosrat on 2017-11-30.
 */
public class FileTransferServer {

    ServerSocket serverSocket;
    Socket socket = null;
    InputStream inputStream;
    DbHandler dbHandler = new DbHandler();
    OutputStream outputStream;


    public FileTransferServer() {
    }

    //Startar en socket server för att ta emot och skicka filer.
    public void startFilesServerSocket() throws IOException {
        serverSocket = new ServerSocket(3333);
    }

    //Tar emot anslutningsförfrågan från klienten och skapar anslutning
    public void serverSocketAccept() throws IOException {
        socket = serverSocket.accept();
        System.out.println("Server accepted socket");
    }

    //Tar emot filen som skickades från klienten och skickar den vidare till databashanteraren
    public boolean uploadFile(FileCredentials credentials) throws IOException, ClassNotFoundException, SQLException {
        boolean checkName = true;
        inputStream = socket.getInputStream();
        System.out.println(inputStream.available());
        if (inputStream.available() != 0) {
            checkName = uploadToDb(inputStream, credentials);
        }
        socket.close();
        serverSocket.close();
        return checkName;
    }

    //Tar emot filen från databashanteraren och skickar den vidare till klienten
    public void downloadFile(FileCredentials credentials) throws IOException, SQLException, ClassNotFoundException {
        InputStream theFile = dbHandler.downloadFile(credentials);
        outputStream = socket.getOutputStream();
        byte[] buffer = new byte[4096];
        while (theFile.read(buffer) > 0) {
            outputStream.write(buffer);
        }
        outputStream.flush();
        socket.close();
        serverSocket.close();
    }

    //Tar emot filecredentials och filen från klienten och skickar detta vidare till databashanteraren
    private boolean uploadToDb(InputStream inputStream, FileCredentials credentials) throws FileNotFoundException, SQLException, ClassNotFoundException {
        return dbHandler.uploadFile(inputStream, credentials);
    }

}

