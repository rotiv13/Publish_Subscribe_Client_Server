/**
 * Created by Vitor Afonso up200908303 and Ricardo Godinho up201003837 on 17/11/2015.
 */

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;



public class BrokerThread extends Thread {
    Socket socket = null;
    private Broker broker = null;
    private final int port;
    BrokerThread(Broker broker,Socket socket ,int port) {
        super("BrokerThread");
        this.socket = socket;
        this.broker = broker;
        this.port = port;
    }

    /**
     * Return the
     */

    public void run() {
        String stream_name="";
        try (
                PrintWriter streamOut = new PrintWriter(socket.getOutputStream(),true);
                BufferedReader streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedInputStream in = new BufferedInputStream(socket.getInputStream())
        ) {
            String input = streamIn.readLine();
            String splitInput[] = input.split(" ");
            stream_name=splitInput[1];
            //publisher
            Map<String, StreamVideo> streamChannels = broker.streamChannels;
            try {

                if (splitInput[0].equals("publisher")) {
                    broker.addNewPublisher(port, stream_name);

                    int byteread = 0;
                    int countHeader = 10; //32kb
                    int i = 0;
                    StreamVideo stream = streamChannels.get(splitInput[1]);
                    while (byteread != -1) {
                        byte[] data = new byte[1024 * 2];
                        byteread = in.read(data, 0, data.length);
                        if (i < countHeader) {
                            stream.header[i] = data;
                        }
                        ConcurrentLinkedQueue<OutputStream> subscribers = stream.outputStreams;

                        for (OutputStream clients : subscribers) {

                            byte[] dataClone = data.clone();
                            clients.write(dataClone, 0, dataClone.length);
                            clients.flush();
                        }
                        i++;
                    }

                }
            }catch (IOException e) {
                broker.clientClose(stream_name,port);
            }

            //subscriber

            try {

                if (input.equals("subscriber")) {
                    broker.addNewSubscriber(port);
                    while ((input = streamIn.readLine()) != null) {
                        splitInput = input.split(" ");
                        Map<Integer, BrokerThread> subscribers = broker.getSubscribers();
                        if (input.equals("list")) {
                            streamOut.println("We have all sorts of products. Let me show you.");

                            for (String name_stream : streamChannels.keySet()) {
                                int publisherID = streamChannels.get(name_stream).publisher;
                                BrokerThread publisher = broker.getPublishers().get(publisherID);
                                System.out.println("Product: " + name_stream + " | " + publisher);
                                streamOut.println("Product: " + name_stream + " | " + publisher);

                            }
                            streamOut.println("Number of subscribers: " + subscribers.size());
                            streamOut.println("...");
                        } else if (splitInput[0].equals("subscribe")) {
                            String product = splitInput[1];
                            stream_name= product;
                            BufferedOutputStream out;
                            StreamVideo channels = streamChannels.get(product);
                            Socket subscriberSocket = subscribers.get(port).socket;
                            OutputStream outputStreamSubscribers = subscriberSocket.getOutputStream();
                            for (byte[] data : channels.header) {
                                out = new BufferedOutputStream(outputStreamSubscribers);
                                out.write(data);
                                out.flush();
                            }
                            channels.subscribers.add(port);
                            channels.outputStreams.add(new BufferedOutputStream(outputStreamSubscribers));
                            System.out.println("Subscriber " + subscriberSocket + " is watching this product --> " + product);


                        }
                    }

                }
            }catch (IOException e) {
                broker.clientClose(stream_name,port);
            }

        }catch (IOException e) {
            broker.clientClose(stream_name,port);
        }
    }
}
