/**
 * Created by Vitor Afonso up200908303 and Ricardo Godinho up201003837 on 17/11/2015.
 */

import java.io.*;
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
                BufferedReader stdIn =
                        new BufferedReader(new InputStreamReader(System.in));
        ) {
            //tell it is a publisher
            out.println("publisher");
            String input;
            while ((input=stdIn.readLine())!=null){
                if(input.equals("Close")){
                    out.println(input);
                    System.exit(1);
                }
                // else if(input.equals("publish")) {
                //     System.out.println("publish");
                //     InputStream is = new FileInputStream(new File("/home/rotiv_13/workspace/SD_PUB_SUB/1.avi"));
                //     byte[] bytes = new byte[1024];

                //     OutputStream stream = socket.getOutputStream();

                //     int count = is.read(bytes, 0, 1024);
                //     while (count != -1) {
                //         stream.write(bytes, 0, 1024);
                //         count = is.read(bytes, 0, 1024);
                //     }
                // }
                else{
                    out.println(input);
                }
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
