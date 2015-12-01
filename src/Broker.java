import java.io.IOException;
import java.net.InterfaceAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Vitor Afonso up200908303 and Ricardo Godinho up201003837 on 17/11/2015.
 */

public class Broker {
    //Lista de todos os clientes que ainda não se identificar nem como subscribers nem como publishers
    private Map<Integer,BrokerThread> clients = new HashMap<Integer, BrokerThread>();
    //Lista dos publishers
    private Map<Integer,BrokerThread> publishers = new HashMap<Integer, BrokerThread>();

    public Map<Integer, BrokerThread> getSubscribers() {
        return subscribers;
    }

    public Map<Integer, BrokerThread> getPublishers() {
        return publishers;
    }

    public Map<Integer, BrokerThread> getClients() {
        return clients;
    }

    //Lista  do subscribers
    private Map<Integer,BrokerThread> subscribers = new HashMap<Integer, BrokerThread>();

    public Map<Integer, LinkedList<Integer>> getSubscriptionsPerPub() {
        return subscriptionsPerPub;
    }

    //Lista que contem os subscritores de cada publisher
    private Map<Integer,LinkedList<Integer>> subscriptionsPerPub = new HashMap<Integer, LinkedList<Integer>>();
    public static void main(String[] args) throws IOException {
        int portNumber = 0;
        Broker broker = null;
        //if number of args is not met
        if (args.length != 1) {
            System.err.println("Usage: java Broker <port number>");
            System.exit(1);
        } else {
            //gets portnumber from the args list
            portNumber = Integer.parseInt(args[0]);
            //intializes broker
            broker = new Broker(portNumber);

        }
    }

    public Broker(int portNumber){
        //creates a new server socket
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                addNewClient(serverSocket.accept());
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }

    /**
     * Creates a new thread and handles client until it closes the connection
     * @param socket
     */
    public void addNewClient(Socket socket) {
        //prints client info
        System.out.println("New connection: "+socket);
        clients.put(socket.getPort(), new BrokerThread(this,socket,socket.getPort()));
        clients.get(socket.getPort()).start();
    }

    /**
     * Client becomes a subscriber transfering him from clients to subscribers.
     * @param clientID
     */
    public void addNewSubscriber(int clientID){
        subscribers.put(clientID,clients.remove(clientID));
        System.out.println("Subscriber: "+ subscribers.get(clientID).socket+" has entered the building!");
    }

    /**
     * Client becomes a publisher trasnfering him from clients to publishers.
     * @param clientID
     */
    public void addNewPublisher(int clientID){
        publishers.put(clientID,clients.remove(clientID));
        subscriptionsPerPub.put(clientID,new LinkedList<>());
        System.out.println("Publisher: "+ publishers.get(clientID).socket+" has entered the building!");
        System.out.println("And is ready for action");

    }

    /**
     * Closes the clients connection
     * @param clientID
     */
    public void clientClose(int clientID){
        try{
            BrokerThread close;
            //if client is in the subscribers list
            if(subscribers.containsKey(clientID)){
                close=subscribers.remove(clientID);
                System.out.println("Subscriber: "+close.socket+" has left the building!");
                close.socket.close();
            }
            //if client is the publishers list
            else {
                close=publishers.remove(clientID);
                System.out.println("Publisher: "+close.socket+" has left the building!");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
