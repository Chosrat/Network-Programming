package Client.view;

import Common.FileCredentials;
import Common.FileServer;
import Common.UserCredentials;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Created by Chosrat on 2017-11-28.
 */
public class Interpreter implements Runnable {

    private FileServer server;
    Scanner input = new Scanner(System.in);
    private UserCredentials user;
    private String userName;
    private String passWord;
    private Socket socket;
    private FileOutputStream fileOutputStream;
    private OutputStream outputStream;
    private byte[] byteArray;
    private FileInputStream file;
    private BufferedInputStream bufferedInputStream;
    private FileCredentials fileCredentials = new FileCredentials();
    private InputStream inputStream;

    public Interpreter() {
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            lookupServerName();
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println("Choose action - type one of the following (login, logout, newuser, deleteuser, listfiles, quit)");

        while (true) {
            String choice = input.nextLine();
            try {
                switch (choice.toLowerCase()) {

                    case "login":
                        login();
                        break;

                    case "logout":
                        break;

                    case "newuser":
                        newUser();
                        break;

                    case "deleteuser":
                        deleteUser();
                        break;

                    case "uploadfile":
                        server.startFileServerSocket();
                        connectToFileServer();
                        server.serverSocketAccept();
                        uploadFile("/Users/Chosrat/Desktop/Nätverksprogrammering/Network-Programming/Homework3/id1212-hw3-2.pdf");
                        fileAttributes();
                        if(server.serverUploadFile(fileCredentials) == true){
                            System.out.println("Filename already exists in databse please try again: \n\n");
                            break;
                        }else System.out.println("File upploaded to database");
                        break;

                    case "downloadfile":
                        System.out.println("NAme of the file you want to download");
                        fileCredentials.setFileName(input.nextLine());
                        fileCredentials.setOwnerId(user.getId());
                        server.startFileServerSocket();
                        connectToFileServer();
                        server.serverSocketAccept();
                        server.downloadFile(fileCredentials);
                        downloadFile();
                        break;

                    case "listfiles":
                        System.out.println(server.listFiles(user));
                        break;

                    case "deletefile":
                        deleteFile();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void deleteFile() throws RemoteException, SQLException {
        System.out.println("Name the file you want to delete");
        fileCredentials.setFileName(input.nextLine());
        fileCredentials.setOwnerId(user.getId());
        System.out.println(server.deleteFile(fileCredentials));
    }

    private void deleteUser() throws RemoteException, SQLException {
        if (user.getStatus() == true) {
            System.out.println("Are you sure you want to delete this user\n YES or NO");
            if (input.nextLine().equalsIgnoreCase("yes")) {
                System.out.println(server.deleteUser(user));
                user.setStatus(false);
            }
        } else System.out.println("You have to be logged in to delete user \nTry another action:");
    }

    public void login() throws RemoteException, SQLException {
        System.out.println("Username: ");
        userName = input.nextLine();
        System.out.println("Password");
        passWord = input.nextLine();
        user = server.login(new UserCredentials(userName, passWord));
        if (user.getStatus() == true) {
            System.out.println("You are logged in, choose action:");
        } else {
            System.out.println("Username and password not found in DB, try again");
            login();
        }
    }

    public void newUser() throws RemoteException, SQLException {
        System.out.println("Choose username: ");
        userName = input.nextLine();
        System.out.println("Choose password: ");
        passWord = input.nextLine();
        user = server.registerNewUser(new UserCredentials(userName, passWord));
        if (user.getStatus() == true) {
            System.out.println("You are now registered and logged in, choose action");
        } else if (user.getStatus() == false) {
            System.out.println("Username already exists, try another username");
            newUser();
        }
    }

    //Letar efter en server med namnet "FILE_SERVER" som är definierat i interface FileServer
    private void lookupServerName() throws RemoteException, NotBoundException, MalformedURLException {
        server = (FileServer) Naming.lookup("//localhost/" + FileServer.SERVER_NAME_IN_REGISTRY);
        System.out.println("Conected to server");
    }

    private void connectToFileServer() throws IOException {
        socket = new Socket("localhost", 3333);
    }

    private void uploadFile(String path) throws IOException, SQLException {
        //fileAttributes();
        File theFile = new File(path);
        byteArray = new byte[(int) theFile.length()];
        file = new FileInputStream(theFile);
        bufferedInputStream = new BufferedInputStream(file);

        //Läser och skriver filen från byte 0 till längden av filen
        bufferedInputStream.read(byteArray, 0, byteArray.length);
        outputStream = socket.getOutputStream();
        //Skickar filen
        outputStream.write(byteArray, 0, byteArray.length);
        outputStream.flush();
        socket.close();
    }

    public void downloadFile() throws IOException {
        System.out.println("Give a name to the file you want to store on your computer" );
        File theFile = new File(input.nextLine() + ".pdf");
        fileOutputStream = new FileOutputStream(theFile);
        inputStream = socket.getInputStream();
        byte[] buffer = new byte[4096];
        while (inputStream.read(buffer) > 0) {
            fileOutputStream.write(buffer);
        }
        socket.close();
    }

    //fixa sista if satsen så att den rättar vid fel
    private void fileAttributes() throws SQLException, RemoteException {
        System.out.println("Name of your file");
      //  fileCredentials = new FileCredentials();
        fileCredentials.setFileName(input.nextLine());
        fileCredentials.setOwnerId(user.getId());
        System.out.println("Do you want the file to be public yes or no?: \n");
        if (input.nextLine().equalsIgnoreCase("yes")) {
            fileCredentials.setPublik(true);
        } else
            fileCredentials.setPublik(false);

    }


}
