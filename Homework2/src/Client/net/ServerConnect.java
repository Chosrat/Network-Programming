package Client.net;


import Client.view.Interpreter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
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
public class ServerConnect implements Runnable{

    private SocketChannel socketChannel;
    private Selector selector;
    private InetSocketAddress serverAddress = new InetSocketAddress("localhost", 3333);
    public ByteBuffer messageToServer;
    private boolean write;
    private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();

    public void connect(){
        new Thread(this).start();
    } //STartar en ny tråd för clienten


    @Override
    public void run(){
        try {
            initializeSelector();       //Inställningar för selectorn och anslutning

            while (true) {

                if(write){              //Om det finns meddelande i kö att skickas så ändras SelectorKey till write för att skriva till servern
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    write = false;
                }
                                        //Itererar igenom keys för som finns för att sedan köra rätt metod
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {

                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) {
                        continue;
                    } else if(key.isConnectable()){
                       configureConnection(key);
                    }
                    else if (key.isReadable()) {
                        readFromServer(key);
                    } else if (key.isWritable()) {
                        writeToServer(key);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

        //Läser data som skickas från servern till clienten som sedan tas emot i vyn för att skriva ut till clienten
    private void readFromServer(SelectionKey key) throws IOException{
        ByteBuffer bufferFromServer = ByteBuffer.allocate(256);
        SocketChannel channel = (SocketChannel) key.channel();
        bufferFromServer.clear();
        channel.read(bufferFromServer);
        Executor pool = ForkJoinPool.commonPool();
        pool.execute(()->{

            try {
                bufferFromServer.flip();
                byte[] bytes = new byte[bufferFromServer.remaining()];
                bufferFromServer.get(bytes); //skriver till bytes
                String fromServer = null;
                fromServer = new String(bytes, "UTF-8");
                Interpreter interpreter = new Interpreter();
                interpreter.writeToClient(fromServer);
                key.interestOps(SelectionKey.OP_WRITE);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        });



    }
    //Skriver data från clienten till servern
    private void writeToServer(SelectionKey key) throws IOException{

        while(!messagesToSend.isEmpty()){
            socketChannel.write(messagesToSend.poll());
        }
        key.interestOps(SelectionKey.OP_READ);
    }
    //Skapar uppkoplingen mot servern
    private void configureConnection(SelectionKey key) throws IOException {
        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_READ);
    }
    //Inställningar för selector och socketChannel som behövs för att skapa en uppkoppling till servern
    private void initializeSelector() throws IOException{
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }
    //Tar emot meddelande från klienten och lägger i en meddelande kö som ska skickas till servern
    public  void messageHandler(String msg) {
        messageToServer = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
        messagesToSend.add(messageToServer);
        //System.out.println(msg);
        write = true;
        selector.wakeup();
        //  System.out.println("clienten messageHandler");
    }
    //Avslutar uppkopplingen till servern
    public void disConnect() throws IOException{
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();

    }
}
