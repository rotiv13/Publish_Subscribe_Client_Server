/**
 * Created by Vitor Afonso up200908303 and Ricardo Godinho up201003837 on 17/11/2015.
 */

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;



class StreamVideo{
    private final String name;
    final Integer publisher;
    final LinkedList<Integer> subscribers;
    final ConcurrentLinkedQueue<OutputStream> outputStreams;
    final byte[][] header;
    StreamVideo(String name, Integer publisher){
        this.name = name;
        this.publisher=publisher;
        subscribers=new LinkedList<>();
        outputStreams=new ConcurrentLinkedQueue<>();
        header=new byte[10][1024*2];
    }
}


public class Broker {
    //Lista de todos os clientes que ainda não se identificar nem como subscribers nem como publishers
    private final Map<Integer,BrokerThread> clients = new HashMap<>();
    //Lista dos publishers
    private final Map<Integer,BrokerThread> publishers = new HashMap<>();
    //Lista  do subscribers
    private final Map<Integer,BrokerThread> subscribers = new HashMap<>();

    final Map<String, StreamVideo> streamChannels = new HashMap<>();


    public Map<Integer, BrokerThread> getSubscribers() {
        return subscribers;
    }

    public Map<Integer, BrokerThread> getPublishers() {
        return publishers;
    }

    public static void main(String[] args) throws IOException {
        int portNumber;
        Broker broker = null;
        //if number of args is not met
        if (args.length != 1) {
            System.err.println("USAGE: java Broker <port number>");
            System.exit(1);
        } else {
            //gets portnumber from the args list
            portNumber = Integer.parseInt(args[0]);
            //intializes broker
            broker = new Broker(portNumber);

        }
    }

    private Broker(int portNumber){
        //creates a new server socket
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                addNewClient(serverSocket.accept());
            }
        } catch (IOException e) {
            System.err.println("COULDN'T LISTEN ON PORT: " + portNumber);
            System.exit(-1);
        }
    }

    /**
     * Creates a new thread and handles client until it closes the connection
     * @param socket
     */
    private void addNewClient(Socket socket) {
        //prints client info
        System.out.println("NEW CONNECTION: "+socket);
        clients.put(socket.getPort(), new BrokerThread(this,socket,socket.getPort()));
        clients.get(socket.getPort()).start();
    }

    /**
     * Client becomes a subscriber transfering him from clients to subscribers.
     * @param clientID
     */
    public void addNewSubscriber(int clientID){
        subscribers.put(clientID,clients.remove(clientID));
        System.out.println("SUBSCRIBER: "+ subscribers.get(clientID).socket+" has entered the building!");
    }

    /**
     * Client becomes a publisher transfering him from clients to publishers.
     * @param clientID
     */
    public void addNewPublisher(int clientID, String stream_name){
        publishers.put(clientID,clients.remove(clientID));
        //subscriptionsPerPub.put(clientID,new LinkedList<Integer>());

        StreamVideo stream = new StreamVideo(stream_name, clientID);
        streamChannels.put(stream_name, stream );

        System.out.println("PUBLISHER: "+ publishers.get(clientID).socket+" has entered the building!");
        System.out.println("PUBLISHER: "+publishers.get(clientID).socket+" created stream "+stream_name);

    }


    /**
     * Closes the clients connection
     * @param clientID
     */
    public void clientClose(String stream,int clientID){
        try{
            BrokerThread close;
            //if client is in the subscribers list
            if(subscribers.containsKey(clientID)){
                close=subscribers.remove(clientID);
                streamChannels.get(stream).subscribers.remove(clientID);
                System.out.println("SUBSCRIBER: "+close.socket+" has left the building!");
                close.socket.close();
            }
            //if client is the publishers list
            else {
                close=publishers.remove(clientID);
                streamChannels.remove(stream);
                System.out.println("PUBLISHER: "+close.socket+" has left the building!");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
