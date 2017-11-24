package Server.net;

import Server.model.Hangman;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by Chosrat on 2017-11-21.
 */
public class HangmanServer {

    private Selector selector;
    private ServerSocketChannel serverChannel;
    private ServerSocket serverSocket;


    private void run() {
        try {
            initializeSelector();
            initializeSocketChannel();
            while (true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator(); //Itererar igenom tillgängliga nycklar för att köra rätt metod
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isAcceptable()) {
                        clientConnect(key);

                    }
                    if (key.isReadable()) {
                        listenFromClient(key);
                    }
                    if (key.isWritable()) {
                        writeToClient(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Tar in data från spelet och skriver detta till clienten
    private void writeToClient(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        Client client = (Client) key.attachment();
        while (!client.messagesToSend.isEmpty()) {
            channel.write(client.messagesToSend.poll());
        }
        key.interestOps(SelectionKey.OP_READ);
    }

    //Tar emot data från klienten via channel för att skicka detta vidare till spelet
    private void listenFromClient(SelectionKey key) throws IOException {
        ByteBuffer Buffer = ByteBuffer.allocate(256);
        SocketChannel channel = (SocketChannel) key.channel();
        Buffer.clear();
        channel.read(Buffer);
        Executor pool = ForkJoinPool.commonPool();
        pool.execute(() -> {

            try {
                Buffer.flip();
                byte[] bytes = new byte[Buffer.remaining()];
                Buffer.get(bytes); //skriver till bytes
                String input = null;
                input = new String(bytes, "UTF-8");
                Client client = (Client) key.attachment();
                client.hangman.setGuess(input);
                client.hangman.gameLoop();
                key.interestOps(SelectionKey.OP_WRITE);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        });

    }


    //Skapar server och ställer in den till non-blocking och selector key till accept connection.
    private void initializeSocketChannel() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverSocket = serverChannel.socket();
        serverChannel.configureBlocking(false);
        serverSocket.bind(new InetSocketAddress(3333));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

    }


    private void initializeSelector() throws IOException {
        selector = Selector.open();
    }

    //Accepterar klienten och skapar en uppkoppling
    private void clientConnect(SelectionKey key) throws IOException {

        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        Client client = new Client();
        clientChannel.register(selector, SelectionKey.OP_WRITE, client);
    }


    public static void main(String[] args) {
        HangmanServer server = new HangmanServer();
        server.run();
    }

    //Skapar en client för varje uppkoppling för att hantera varje spelare separat
    public class Client {

        public Hangman hangman;
        public final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();

        public Client() {
            hangman = new Hangman(this);
        }

        //Tar in data från spelet för att lägga i kö för att skicka till klienten
        public void messageHandler(String msg) {
            ByteBuffer messageToClient = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
            messagesToSend.add(messageToClient);
            messageToClient.clear();
            selector.wakeup();
        }
    }
}

