package Server.startup;

import Server.controller.Controller;
import Server.model.DbHandler;
import Server.model.UserHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by Chosrat on 2017-11-28.
 */
public class Main {


    //Startar RMI servern och regitrera detta i registry
    public static void main(String args[]) throws InstantiationException, IllegalAccessException, SQLException, ClassNotFoundException, IOException {

        try {
            new Main().startRegistry();
            //new DbHandler().accessDb();
            Naming.rebind(Controller.SERVER_NAME_IN_REGISTRY, new Controller());
        } catch (MalformedURLException|RemoteException  e) {
            e.printStackTrace();
        }
    }

    private void startRegistry() throws RemoteException{

        try {
            LocateRegistry.getRegistry().list();
        }catch (RemoteException noRegistryIsRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
    }

    public void list() throws RemoteException {

    }

}
