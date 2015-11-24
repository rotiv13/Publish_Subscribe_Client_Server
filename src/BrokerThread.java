import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Vitor Afonso up200908303 and Ricardo Godinho up201003837 on 17/11/2015.
 */

public class BrokerThread extends Thread {
     Socket socket = null;
    private Broker broker = null;
    private int port;
    BrokerThread(Broker broker,Socket socket ,int port) {
        super("BrokerThread");
        this.socket = socket;
        this.broker = broker;
        this.port = port;
    }

    /**
     * Return port of the broker
     * @return
     */
    public int getPort(){return port;}

    /**
     * Return the
     */

    public void run() {

        try (
                PrintWriter streamOut = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader streamIn = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String input=streamIn.readLine();
            String splitInput[] = input.split(" ");
            String whatAreYou = splitInput[0];
            String whereRYouFrom = splitInput[1];
            //publisher
            if(whatAreYou.equals("publish")){
                broker.addNewPublisher(port);
                //keep things running until "Quit!" is typed
                while((input=streamIn.readLine()).equals("Quit!")){
                    PrintWriter stream;
                    //goes to all subscriber of that channel and prints what the publisher wants
                    for(int clients : broker.getSubscriptionsPerPub().get(port)){
                        stream = new PrintWriter(broker.getSubscribers().get(clients).socket.getOutputStream(),true);
                        stream.println(input);
                    }
                }
            }
            //subscriber
            if (whatAreYou.equals("subscribe")){
                broker.addNewSubscriber(port);
                while (true){
                    input = streamIn.readLine();
                    if(input.equals("list")){
                        streamOut.println("We have all sorts of products. Let me show you.");
                        for(int key:broker.getPublishers().keySet()){
                            streamOut.println("Product: "+key+" | "+broker.getPublishers().get(key).socket);
                        }
                        streamOut.println("Number of subscribers: "+broker.getSubscribers().size());
                        streamOut.println("...");
                    }
                    else if (input.substring(0,input.indexOf(' ')).equals("subscribe")){
                        int spaceIndex = input.indexOf(' ');
                        String product = input.substring(0,spaceIndex);
                        int productID = Integer.parseInt(product);
                        System.out.println("Subscriber "+ broker.getSubscribers().get(port).socket+" is watching this product "+productID);
                    }
                    else {
                        streamOut.println("...");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}