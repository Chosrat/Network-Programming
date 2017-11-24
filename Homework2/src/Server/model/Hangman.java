package Server.model;

/**
 * Created by Chosrat on 2017-11-16.
 */


import Server.net.HangmanServer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.random;

public class Hangman {

    public ArrayList<String> words;
    private int guesses = 0;
    private int scoreBoard;
    public String gameWord;
    public char[] splitGameWord;
    public char[] playResult;
    int step = 0;
    boolean hitOrMiss;
    public HangmanServer.Client client;

    public String guess;

    public Hangman(HangmanServer.Client client) {
        this.guess = null;
        this.client = client;
        startGame(client);
    }

    public void setGuess(String msg) {
        this.guess = msg;
    }

    //Tar emot gissningar från klienten till spelets ord
    public void gameLoop() {

        if (guess.length() == 1) {
            //Om användaren gissat 1 bokstav skickas den iväg tillsammans med spelordet för att se om den får träff eller inte
            playResult = guessChar(guess.charAt(0), playResult, splitGameWord);

            //kollar Om bokstaven som matades skapade resten av ordet
            if (new String(playResult).equalsIgnoreCase(gameWord)) {
                System.out.println("om vi kommer in i step så skriver denna ut detta");
                step = 1;
            }

            switch (step) {
                case 0:
                    //Om bokstaven inte finns i order
                    if (hitOrMiss == false) {
                        guesses--;
                    }

                    client.messageHandler(Arrays.toString(playResult) + "\nYou have " + guesses + " guesses left");
                    break;

                case 1:
                    //Om bokstaven fanns i ordet och ordet blev komplett då avslutas spelet
                    step = 0;
                    scoreBoard++;
                    client.messageHandler("Congratulations you guessed the correct word. \nYour score is: " + scoreBoard +
                            "\nDo you want to restart the game write yes or quit to exit");


                    if ((guess.equalsIgnoreCase("yes"))) {
                        startGame(client);
                    }
                    break;
            }
        }

        if (guess.length() > 1) {   //Om användaren gissar på hela ordet
            if ((guess.equalsIgnoreCase("yes"))) {
                startGame(client);
            } else {
                if (guess.equalsIgnoreCase(gameWord)) {
                    scoreBoard++;
                    client.messageHandler("Congratulations you guessed the correct word. " + guess + "\nYour score is: " + scoreBoard);
                    client.messageHandler("\nDo you want to restart the game write yes or quit to exit");


                } else {
                    guesses--;
                    client.messageHandler("Wrong word, try again");
                    client.messageHandler("\n" + Arrays.toString(playResult) + "\nYou have " + guesses + " guesses left");

                }
            }
        }


        //Startar om eller avslutar spelet
        if (guess != null && guesses <= 0) {
            scoreBoard--;
            client.messageHandler("You have no guesses left \nYour score is: " + scoreBoard + "\nTo restart the game write yes or quit to exit");
            if (guess.equalsIgnoreCase("yes")) {
                startGame(client);
            } else if (guess.equalsIgnoreCase("quit")) {

            }
        }

    }

    //Startar spelet och dess funktioner, tar ut ett slumpmässigt ord från listan
    public void startGame(HangmanServer.Client client) {                            //Hangman spelet
        //HangmanServer hangmanServer = new HangmanServer();
        hangManWord();                                   //Skapar en lista med olika ord
        gameWord = randomWord();                         //Plockar ut ett slumpmässigt ord från listan
        splitGameWord = gameWord.toCharArray();         //Konventerar ordet till en charArray
        playResult = new char[splitGameWord.length];    //Skapar en tom charArray med längden av spelordet där rätt bokstav sparas
        guesses = splitGameWord.length;                 //Antal gissningar
        System.out.println(gameWord);                    //Skriver ut ordet i systemets command line för testa hela ordet.
        client.messageHandler("Hangman Game: \nGuess one letter or the entire word: \nYour score is: " + scoreBoard + "\nThe word has: " + gameWord.length() + " characters\nYou have: " + guesses + " guesses");

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
