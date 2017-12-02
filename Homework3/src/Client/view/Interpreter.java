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
        user = new UserCredentials(null, null);
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
        //System.out.println("Choose action - type one of the following (login, logout, newuser, deleteuser, listfiles, quit)");
        System.out.println("File system! \n\nType login to start: ");
        while (true) {
            String choice = input.nextLine();
            try {
                switch (choice.toLowerCase()) {

                    case "login":
                        login();
                        chooseAction();
                        break;

                    case "logout":
                        break;

                    case "newuser":
                        newUser();
                        System.out.println("Choose action - type one of the following (logout, listfiles, downloadfile, uploadfile, deletefile, newuser, deleteuser, quit)");
                        break;

                    case "deleteuser":
                        if(user.getStatus() == true) {
                            deleteUser();
                        }else System.out.println("You have to be logged in to delete user; type login to login: ");
                        break;

                    case "uploadfile":
                        uploadFile();
                        if(server.serverUploadFile(fileCredentials) == true){
                            System.out.println("Filename already exists in databse please try again: \n\n");
                            break;
                        }else System.out.println("File upploaded to database");
                        chooseAction();
                        break;

                    case "downloadfile":
                        if(user.getStatus() == true) {
                            downloadFile();
                        }else System.out.println("You have to be logged in to download files, type login to login: ");
                        chooseAction();
                        break;

                    case "listfiles":
                        if(user.getStatus() == true) {
                            System.out.println(server.listFiles(user));
                        } else System.out.println("You have to be logged in to read the files, type login to login: ");
                        chooseAction();
                        break;

                    case "deletefile":
                        if(user.getStatus() == true) {
                            deleteFile();
                        } else System.out.println("You have to be logged in to delete files, type login to login");
                        chooseAction();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void chooseAction(){
        System.out.println("Choose action - type one of the following (logout, listfiles, downloadfile, uploadfile, deletefile, newuser, deleteuser, quit)");
    }

    private void deleteFile() throws RemoteException, SQLException {
        System.out.println("Name of the file you want to delete");
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

    private void socketConnect() throws IOException {
        server.startFileServerSocket();
        connectToFileServer();
        server.serverSocketAccept();
    }

    private void uploadFile() throws IOException, SQLException {
        socketConnect();
        fileAttributes();
        System.out.println("Enter the path for the file you want to upload");
        File theFile = new File(input.nextLine());
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

    public void downloadFile() throws IOException, SQLException, ClassNotFoundException {
        socketConnect();
        System.out.println("NAme of the file you want to download");
        fileCredentials.setFileName(input.nextLine());
        fileCredentials.setOwnerId(user.getId());
        server.downloadFile(fileCredentials);
        System.out.println("Save as: " );
        File theFile = new File(input.nextLine() );
        fileOutputStream = new FileOutputStream(theFile);
        inputStream = socket.getInputStream();
        byte[] buffer = new byte[4096];
        while (inputStream.read(buffer) > 0) {
            fileOutputStream.write(buffer);
        }
        socket.close();
        System.out.print("Download complete");
    }


    private void fileAttributes() throws SQLException, RemoteException {
        System.out.println("Name of your file");
        fileCredentials.setFileName(input.nextLine());
        fileCredentials.setOwnerId(user.getId());
        System.out.println("Do you want the file to be public yes or no?: \n");
        if (input.nextLine().equalsIgnoreCase("yes")) {
            fileCredentials.setPublik(true);
        } else
            fileCredentials.setPublik(false);

    }


}
