package Client.startup;


import Client.controller.Controller;
import Client.net.ServerConnect;
import Client.view.Interpreter;

/**
 * Created by Chosrat on 2017-11-21.
 */
public class Main {

    public static void main(String[] args){
        Interpreter game = new Interpreter();
        game.start();
    }

}
