/**
 * Created by Vitor Afonso up200908303 and Ricardo Godinho up201003837 on 17/11/2015.
 */

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;



public class Subscriber {
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println(
                    "Usage: java Subscriber <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        //String streamName = args[2];

        try (
            //socket
            final Socket socket = new Socket(hostName, portNumber);
            //sends text to the broker
            PrintWriter outputToServer = new PrintWriter(socket.getOutputStream(), true);
            // to recieve the text response from the server
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // to recieve the text from the stdIn
            BufferedReader terminalInput = new BufferedReader(new InputStreamReader(System.in));
            // to recieve the stream of byte
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream())
        ){
            // notify server that this is a subscriber
            outputToServer.println("subscriber");
            String fromServer;
            String fromUser;
            try {
                while ((fromUser = terminalInput.readLine()) != null) {
                    //subscriber wants to see the list of publishers
                    if (fromUser.equals("list")) {
                        outputToServer.println(fromUser);
                        while ((fromServer = serverInput.readLine()) != null) {
                            if (fromServer.equals("...")) {
                                break;
                            }
                            System.out.println(fromServer);
                        }
                    }
                    if (fromUser.contains("subscribe")) {
                        outputToServer.println(fromUser);
                        System.out.println("entrei");
                        int bytesread = 0;

                        Process p = Runtime.getRuntime().exec("/usr/bin/vlc -");

                        BufferedOutputStream vlcOutput = new BufferedOutputStream(p.getOutputStream());
                        while (bytesread != -1) {
                            byte[] data = new byte[1024 * 2];
                            bytesread = in.read(data, 0, data.length);

                            vlcOutput.write(data, 0, data.length);
                            try {
                                Thread.sleep(5);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " + hostName);
                outputToServer.println("unsubscribe");
            }

        }catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        }
    }
}
