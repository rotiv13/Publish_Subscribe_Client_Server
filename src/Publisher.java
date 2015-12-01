/**
 * Created by Vitor Afonso up200908303 and Ricardo Godinho up201003837 on 17/11/2015.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class Publisher {
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java Publisher <hostname> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(new InputStreamReader(System.in));
        ) {

            //tell it is a publisher

            String input;

            while ((input=stdIn.readLine())!=null){
                out.println(input);
            }

        } catch (UnknownHostException e){
            System.err.println("Host unknown "+ hostName);
            System.exit(1);
        }

        catch (IOException e) {
            System.err.println("I/O retrieve not possible "+hostName);
            System.exit(1);
        }
    }
}