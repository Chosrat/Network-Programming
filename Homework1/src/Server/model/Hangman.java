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

    boolean hitOrMiss;

    public Hangman(Socket socket, HangmanServer server) {
        super("HangManThread");
        this.socket = socket;
        this.server = server;
        this.guesses = 0;
    }

    public void run() {

        try {
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
            dataOut.writeUTF("Welcome to Hangman game, to play input 'yes' or to exit input 'quit' ");
            dataOut.flush();

            while (true) {
                while (dataIn.available() == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String input = dataIn.readUTF();
                if (input.equalsIgnoreCase("yes")) {
                    startGame();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startGame() {
        hangManWord();
        gameWord = randomWord();
        splitGameWord = gameWord.toCharArray();
        playResult = new char[splitGameWord.length];
        guesses = splitGameWord.length;
        String guess;
        System.out.println(gameWord); //Skriver ut ordet i systemets command line för testa hela ordet.

        try {
            dataOut.writeUTF("Start game: \nYou have " + (splitGameWord.length) + " guesses and the word has " + splitGameWord.length + " letters " +
                    "\nGuess one letter or the entire word: \nYour score is: " + scoreBoard);
            dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while ((guess = dataIn.readUTF()) != null && guesses != 1) {

                if (guess.length() == 1) {

                    playResult = guessChar(guess.charAt(0), playResult, splitGameWord);


                    if (hitOrMiss == false) {
                        guesses--;
                    }

                    dataOut.writeUTF(Arrays.toString(playResult) + "\nYou have " + guesses + " guesses left");
                    dataOut.flush();

                }

                if (guess.length() > 1) {
                    if (guess.equalsIgnoreCase(gameWord)) {
                        scoreBoard++;
                        dataOut.writeUTF("Congratulations you guessed the correct word. " + guess + "\nYour score is: " + scoreBoard);
                        dataOut.flush();

                    }
                    dataOut.writeUTF("Do you want to restart the game write yes or quit to exit");
                    dataOut.flush();
                    if ((guess = dataIn.readUTF()).equalsIgnoreCase("yes")) {
                        startGame();
                    } else {
                        dataOut.writeUTF("FAAAAAIL!!! try again");
                        dataOut.flush();
                        guesses--;
                    }

                }

            }
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


    public void hangManWord() { //Tar filen words.txt och kopierar alla ord till en ordlista
        words = new ArrayList<String>();

        try {
            File file = new File("/Users/Chosrat/Desktop/Nätverksprogrammering/Hangman/Server/words.txt");
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


    public char[] guessChar(char guess, char[] emptyWord, char[] word) {
        hitOrMiss = false;
        for (int i = 0; i < word.length; i++) {
            if (word[i] == guess) {
                hitOrMiss = true;
                emptyWord[i] = guess;
            }

        }
        return emptyWord;
    }

}
