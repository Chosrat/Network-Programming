package Client.controller;

import Client.client.ClientConnection;

/**
 * Created by Chosrat on 2017-11-16.
 */

public class Controller {

    private final ClientConnection cc = new ClientConnection();     //Controller creates a private connection to the server

    public void start() {                                            //Starts the connection to the server
        cc.start();
    }

    public void ListenForInput() {                                   //Calls the method, listenforinput
        cc.listenForInput();
    }


}
