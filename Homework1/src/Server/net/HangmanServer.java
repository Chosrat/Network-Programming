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
            socket = new ServerSocket(3333); //Kollar efter nya uppkopplingar i porten 3333
            while (true) {

                Socket s = socket.accept();         //Accepterar uppkopplingen
                controller = new Controller();      //Skapar ny kontroller som hantera den klienten
                controller.CreateHangman(s, this);  //Kopplar klienten till spelet

                clients.add(controller);            //Lägger till kopplingen i en lista
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
