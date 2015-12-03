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
        //String streamName = args[2];

        try{
            Socket socket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            out.println("subscriber");
            String fromServer;
            String fromUser;
            while((fromUser=stdIn.readLine())!=null) {
                /*
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
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while ((fromServer=in.readLine())!=null) {
                        System.out.print(fromServer);
                    }
                }
                */


                out.println(fromUser);
                System.out.println("Listening...");
                in = new BufferedInputStream(socket.getInputStream());
                byte[] data = new byte[1024];
                int bytesread=0;
            	/*
            	Process p;
            	ProcessBuilder pb = new ProcessBuilder("vlc");
            	System.out.println("PASSEI 1");
            	p=pb.start();

            	System.out.println("PASSEI 2");
            	PrintStream vlcOutput = new PrintStream(p.getOutputStream());
            	System.out.println("PASSEI 3");
                while (bytesread!=-1) {
                	System.out.println("PASSEI 4");
                	//bytesread=in.read(data);
                	//vlcOutput.write(data);
                	bytesread=in.read(data,0,data.length);
                	vlcOutput.write(data, 0, data.length);
                	vlcOutput.flush();
                	System.out.println("PASSEI 5");
                }
                System.out.println("PASSEI 6");
                vlcOutput.close();
            	*/
                File tmp = new File("/tmp/video.avi");
                if(tmp.exists()){
                    tmp.delete();
                }
                PrintStream vlcOutput =new PrintStream(tmp);

                while (bytesread!=-1) {
                    System.out.println("PASSEI 4");
                    bytesread=in.read(data);
                    vlcOutput.write(data);
                    vlcOutput.flush();
                    System.out.println("PASSEI 5");
                }
                vlcOutput.close();
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
