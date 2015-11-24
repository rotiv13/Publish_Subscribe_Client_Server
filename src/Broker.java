import java.io.IOException;
import java.net.InterfaceAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vitor Afonso up200908303 and Ricardo Godinho up201003837 on 17/11/2015.
 */

public class Broker {

    private Map<Integer,BrokerThread> clientQueue = new HashMap<Integer, BrokerThread>();

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java Broker <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                clientQueue.put(new BrokerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
