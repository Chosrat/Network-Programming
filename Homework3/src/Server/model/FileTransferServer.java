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

    //Startar en socket server för att ta emot och skicka filer.
    public FileTransferServer() {
    }

    public void startFilesServerSocket() throws IOException {
        serverSocket = new ServerSocket(3333);
    }

    public void serverSocketAccept() throws IOException {
        socket = serverSocket.accept();
        System.out.println("Server accepted socket");
    }


    public boolean uploadFile(FileCredentials credentials) throws IOException, ClassNotFoundException, SQLException {
        boolean checkName = true;
        inputStream = socket.getInputStream();
        System.out.println(inputStream.available());
        if (inputStream.available() != 0) {
            System.out.println("innan uppload i run");
           checkName = uploadToDb(inputStream, credentials);
        }
        socket.close();
        serverSocket.close();
        return checkName;
    }

    public void downloadFile(FileCredentials credentials) throws IOException, SQLException, ClassNotFoundException {
        System.out.println("Filestransfet sserver downladfile");
        InputStream theFile = dbHandler.downloadFile(credentials);
        outputStream = socket.getOutputStream();
        byte[] buffer = new byte[4096];
        while (theFile.read(buffer) > 0) {
            System.out.println("FTS if satsen skickar till client");
            outputStream.write(buffer);
        }
        outputStream.flush();
        System.out.println("Efter flush");
        socket.close();
        serverSocket.close();
    }

    private boolean uploadToDb(InputStream inputStream, FileCredentials credentials) throws FileNotFoundException, SQLException, ClassNotFoundException {
        System.out.println("skickar till dbHandler från FTS");
        return dbHandler.uploadFile(inputStream, credentials);
    }

}

