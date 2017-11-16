package Client.view;

import Client.client.ClientConnection;
import Client.controller.Controller;

/**
 * Created by Chosrat on 2017-11-16.
 */

public class Client {                   //Client class creates a controller
    //Calls for the method which creates the client socket/connection
    ClientConnection cc;                //And the listen for input from the user
    Controller controller;


    public Client() {

        controller = new Controller();
        controller.start();
        controller.ListenForInput();

    }

}
