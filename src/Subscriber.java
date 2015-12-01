import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Vitor Afonso up200908303 and Ricardo Godinho up201003837 on 17/11/2015.
 */

public class Subscriber {
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println(
                    "Usage: java Subscriber <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(new InputStreamReader(System.in));
        ) {

            String fromServer;
            String fromUser;
            while((fromUser=stdIn.readLine())!=null){
                out.println(fromUser);
                if (fromUser.equals("list")){
                    System.out.println("Listing:");
                    out.println(fromUser);
                    while((fromServer=in.readLine())!=null){
                        if (fromServer.equals("...")){
                            break;
                        }
                        System.out.println(fromServer);
                    }
                }
                else {
                    out.println(fromUser);
                    System.out.println("Listening");
                    while ((fromServer=in.readLine())!=null){
                        if (fromServer.equals("...")){
                            break;
                        }
                        System.out.println("Stopped");
                    }
                }
                System.out.print("Waiting...\n");
            }


        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}
