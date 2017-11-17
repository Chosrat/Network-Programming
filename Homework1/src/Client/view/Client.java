package Client.view;

import Client.controller.Controller;

/**
 * Created by Chosrat on 2017-11-16.
 */

public class Client {                   //Client class skapar en controller
                                         //Anropar metoden som skapar net socket/connection

    Controller controller;


    public Client() {

        controller = new Controller();
        controller.start();
        controller.ListenForInput();

    }

}
