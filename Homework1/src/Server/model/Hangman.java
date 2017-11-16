package Server.model;

/**
 * Created by Chosrat on 2017-11-16.
 */


import Server.net.HangmanServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.random;

public class Hangman extends Thread {

    Socket socket;
    HangmanServer server;
    DataInputStream dataIn;
    DataOutputStream dataOut;

    public ArrayList<String> words;
    private int guesses;
    private int scoreBoard;
    public String gameWord;
    public char[] splitGameWord;
    public char[] playResult;
    int step = 0;
    String guess;
    boolean hitOrMiss;

    public Hangman(Socket socket, HangmanServer server) {
        super("HangManThread");
        this.socket = socket;
        this.server = server;
        this.guesses = 0;
    }

    public void run() {

        try {
            dataIn = new DataInputStream(socket.getInputStream());              //Tar input data från klienten
            dataOut = new DataOutputStream(socket.getOutputStream());           // Output för att skriva till klienten
            dataOut.writeUTF("Welcome to Hangman game, to play input 'yes' or to exit input 'quit' ");
            dataOut.flush();

            while (true) {                                                      //Om det inte finns data i dataIn från klient går tråden i sömn
                while (dataIn.available() == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String input = dataIn.readUTF();                        //Startar spelet
                if (input.equalsIgnoreCase("yes")) {
                    startGame();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startGame() {                            //Hangman spelet
        hangManWord();                                   //Skapar en lista med olika ord
        gameWord = randomWord();                         //Plockar ut ett slumpmässigt ord från listan
        splitGameWord = gameWord.toCharArray();         //Konventerar ordet till en charArray
        playResult = new char[splitGameWord.length];    //Skapar en tom charArray med längden av spelordet där rätt bokstav sparas
        guesses = splitGameWord.length;                 //Antal gissningar
        System.out.println(gameWord);                    //Skriver ut ordet i systemets command line för testa hela ordet.

        try {
            dataOut.writeUTF("Start game: \nYou have " + (splitGameWord.length) + " guesses and the word has " + splitGameWord.length + " letters " +
                    "\nGuess one letter or the entire word: \nYour score is: " + scoreBoard);
            dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {   //Om det finns något i dataIn dvs om användaren har matat in ett ord eller bokstad och om det finns gissningar kvar
            while ((guess = dataIn.readUTF()) != null && guesses != 1) {

                if (guess.length() == 1) {
                    //Om användaren gissat 1 bokstav skickas den iväg tillsammans med spelordet för att se om den får träff eller inte
                    playResult = guessChar(guess.charAt(0), playResult, splitGameWord);

                    //kollar Om bokstaven som matades skapade resten av ordet
                    if (new String(playResult).equalsIgnoreCase(gameWord)) {
                        step = 1;
                    }

                    switch (step) {
                        case 0:
                            //Om bokstaven inte finns i order
                            if (hitOrMiss == false) {
                                guesses--;
                            }

                            dataOut.writeUTF(Arrays.toString(playResult) + "\nYou have " + guesses + " guesses left");
                            dataOut.flush();
                            break;

                        case 1:
                            //Om bokstaven fanns i ordet och ordet blev komplett då avslutas spelet
                            step = 0;
                            scoreBoard++;
                            dataOut.writeUTF("Congratulations you guessed the correct word. \nYour score is: " + scoreBoard +
                                    "\nDo you want to restart the game write yes or quit to exit");
                            dataOut.flush();

                            if ((guess = dataIn.readUTF()).equalsIgnoreCase("yes")) {
                                startGame();
                            }
                            break;
                    }
                }

                if (guess.length() > 1) {   //Om användaren gissar på hela ordet
                    if (guess.equalsIgnoreCase(gameWord)) {
                        scoreBoard++;
                        dataOut.writeUTF("Congratulations you guessed the correct word. " + guess + "\nYour score is: " + scoreBoard);
                        dataOut.writeUTF("Do you want to restart the game write yes or quit to exit");
                        dataOut.flush();
                        if ((guess = dataIn.readUTF()).equalsIgnoreCase("yes")) {
                            startGame();
                        }
                    } else {
                        guesses--;
                        dataOut.writeUTF("FAAAAAIL!!! try again");
                        dataOut.writeUTF(Arrays.toString(playResult) + "\nYou have " + guesses + " guesses left");
                        dataOut.flush();
                    }

                }

            }
            //Startar om eller avslutar spelet
            scoreBoard--;
            dataOut.writeUTF("You have no guesses left \nYour score is: " + scoreBoard + "\nTo restart the game write yes or quit to exit");
            dataOut.flush();
            if (guess.equalsIgnoreCase("yes")) {
                startGame();
            } else if (guess.equalsIgnoreCase("quit")) {

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void hangManWord() {         //Tar filen words.txt och kopierar alla ord till en ordlista
        words = new ArrayList<String>();

        try {
            File file = new File("/Users/Chosrat/Desktop/Nätverksprogrammering/Network-Programming/Homework1/words.txt");
            BufferedReader read = new BufferedReader(new FileReader(file));         //BufferedReader.readLine() reads a line of text
            String line;
            try {
                while ((line = read.readLine()) != null) {
                    words.add(line);
                }
                read.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String randomWord() {     //Tar ut ett random ord från ordlistan (words)

        int index = (int) (random() * words.size());
        return words.get(index).toLowerCase();
    }


    public char[] guessChar(char guess, char[] progressionWord, char[] word) {    //Tar in en bookstav och ser om den finns i ordet
        hitOrMiss = false;                                                  //Använder boolean för att minsta på antal gissnings försök
        for (int i = 0; i < word.length; i++) {
            if (word[i] == guess) {
                hitOrMiss = true;
                progressionWord[i] = guess;
            }

        }
        return progressionWord;
    }

}
