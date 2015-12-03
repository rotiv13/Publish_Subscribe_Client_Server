import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

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
                BufferedReader streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            String input = streamIn.readLine();
            String splitInput[] = input.split(" ");

            //publisher
            if (splitInput[0].equals("publisher")) {
                broker.addNewPublisher(port, splitInput[1]);

                while((input = streamIn.readLine())!=null){
                    PrintWriter out;
                    LinkedList<Integer> subscribers = broker.streamChannels.get(splitInput[1]).subscribers;
                    for(int clients: subscribers){
                        out = new PrintWriter(broker.getSubscribers().get(clients).socket.getOutputStream(),true);
                        out.println(input);
                    }
                }
               /*
                //keep things running until "Quit!" is typed
                while ((input = streamIn.readLine()) != null) {
                    if (input.equals("Close")) {
                        broker.clientClose(port);
                        break;
                    } else {
                        PrintWriter out;
                        byte[] bytes = new byte[1024];
                        int count = 0;
                        //System.out.println(count);
                        //System.out.println(input);
                        for (int clients : broker.getSubscriptionsPerPub().get(port)) {
                            	out = new PrintWriter(broker.getSubscribers().get(clients).socket.getOutputStream(),true);
                                out.println(input);
                                //System.out.println(input);
                            }
                        }
                    }
                    */
            }

            //subscriber
            if (input.equals("subscriber")) {
                broker.addNewSubscriber(port);
                while ((input = streamIn.readLine()) != null) {
                    splitInput = input.split(" ");
                    if (input.equals("list")) {
                        System.out.println("List");
                        streamOut.println("We have all sorts of products. Let me show you.");

                        for(String stream_name : broker.streamChannels.keySet()){
                            int publisherID = broker.streamChannels.get(stream_name).publisher;
                            streamOut.println("Product: "+stream_name+" | "+ broker.getPublishers().get(publisherID));

                        }
                        streamOut.println("Number of subscribers: " + broker.getSubscribers().size());
                        streamOut.println("...");
                    } else if (splitInput[0].equals("subscribe")) {
                        System.out.println(splitInput.toString());
                        String product = splitInput[1];
                        broker.streamChannels.get(product).subscribers.add(port);
                        System.out.println("Subscriber " + broker.getSubscribers().get(port).socket + " is watching this product --> " + product);
                    } else {
                        streamOut.println("...");
                    }
                }

            }


        }catch (IOException e) {
            broker.clientClose(port);
        }
        catch (NullPointerException e) {
            broker.clientClose(port);
        }
    }
}
