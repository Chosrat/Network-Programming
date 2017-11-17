package Client.net;

/**
 * Created by Chosrat on 2017-11-16.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnection extends Thread {

    Socket socket;
    DataInputStream dataIn;
    DataOutputStream dataOut;
    boolean shouldRun = true;


    public ClientConnection() {                          //Sets the socket to the adress and port of the server

        try {
            this.socket = new Socket("localhost", 3333);

        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void sendStringToServer(String text) {        //Method which takes input from user and sends it to the server
        CompletableFuture.runAsync(() -> {
            try {

                dataOut.writeUTF(text);                 //Writes to the server through the socket
                dataOut.flush();                        //Forces the data through the stream to clear the stream for further use
            } catch (IOException e) {
                e.printStackTrace();
                close();                                //Calls for the close method if an exception is received
            }
        });
    }

    public void run() {

        try {
            dataIn = new DataInputStream(socket.getInputStream());      //Creates DataInputStream variabel for receiving data from the server through the socket
            dataOut = new DataOutputStream(socket.getOutputStream());   //Creates DataOutputStream variabel for sending data to the server through the socket

            while (true) {
                while (dataIn.available() == 0) {                         //Checks if there is data, if not the thread will go to sleep for 1 millisec
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String reply = dataIn.readUTF();                        //Receives data from server and prints it in the command line for the user
                System.out.println(reply);
            }
        } catch (IOException e) {
            System.out.println("Connection closed, ");
            close();
        }
    }

    public void close() {        //Closes the inputStream, the outputStream and the socket
        try {
            dataIn.close();
            dataOut.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForInput() {           //Receives input from the user and sends it to the server

        Scanner console = new Scanner(System.in);

        while (true) {
            while (!console.hasNextLine()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String input = console.nextLine();
            if (input.toLowerCase().equals("quit")) {
                break;
            }

            sendStringToServer(input);
        }

        close();
    }

}
