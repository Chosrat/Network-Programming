package Client.view;

import Client.controller.Controller;
import Client.net.ServerConnect;

import java.io.IOException;
import java.util.Scanner;

public class Interpreter implements Runnable{

    private final ServerConnect client = new ServerConnect();


    public void start(){
        new Thread(this).start();
    } //Skapar en från som hanterar vyn, tar emot input från användaren och data från servern som ska skrivas ut till användaren

    Scanner console = new Scanner(System.in);

   @Override
    public void run(){
        client.connect();

        while(true){
            while(!console.hasNextLine()){
                try{
                    Thread.sleep(1);
                } catch (InterruptedException e){
                    System.out.println("Problem vid läsning av text");
                    e.printStackTrace();
                }
            }
            String input = console.nextLine();
            if(input.toLowerCase().equalsIgnoreCase("quit")){
                break;
            }
            client.messageHandler(input);
        }
       try {
           client.disConnect();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   public void writeToClient(String msg){
        System.out.println(msg);
   }

}
