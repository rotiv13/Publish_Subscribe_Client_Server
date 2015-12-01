import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.*;
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
            out.println("subscriber");
            String fromServer;
            String fromUser;
            while((fromUser=stdIn.readLine())!=null) {
                if (fromUser.equals("list")) {
                    out.println(fromUser);
                    while ((fromServer = in.readLine()) != null) {
                        if (fromServer.equals("...")) {
                            break;
                        }
                        System.out.println(fromServer);
                    }
                } else {
                    out.println(fromUser);
                    System.out.println("Listening");
                    while ((fromServer=in.readLine())!=null) {
                        // File video = new File("/home/rotiv_13/workspace/SD_PUB_SUB/test.avi");
                        // FileOutputStream fos = new FileOutputStream(video);
                        // byte[] data = new byte[1024];
                        // int count = socket.getInputStream().read(data, 0, 1024);
                        // while (count != -1) {
                        //     fos.write(data, 0, count);
                        //     count = socket.getInputStream().read(data, 0, 1024);
                        // }
                        System.out.println(fromServer);
                    }
                }
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
