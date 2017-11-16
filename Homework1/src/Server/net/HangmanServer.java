package Server.net;

/**
 * Created by Chosrat on 2017-11-16.
 */


import Server.controller.Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class HangmanServer {

    Controller controller;
    ServerSocket socket;
    ArrayList<Controller> clients = new ArrayList<Controller>();

    public static void main(String[] args) {

        new HangmanServer();
    }

    public HangmanServer() {

        try {
            socket = new ServerSocket(3333); //Listening for new connections in port 3333
            while (true) {

                Socket s = socket.accept();
                controller = new Controller();
                controller.CreateHangman(s, this);


                //Hangman game = new Hangman(s, this);
                //game.start();
                clients.add(controller);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
