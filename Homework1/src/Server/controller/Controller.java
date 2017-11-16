package Server.controller;

/**
 * Created by Chosrat on 2017-11-16.
 */

import Server.model.Hangman;
import Server.net.HangmanServer;

import java.net.Socket;


public class Controller {

    public void CreateHangman(Socket socket, HangmanServer server) { //

        Hangman hangMan = new Hangman(socket, server);
        hangMan.start();
    }

}
