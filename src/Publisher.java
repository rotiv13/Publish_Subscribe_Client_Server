/**
 * Created by Vitor Afonso up200908303 and Ricardo Godinho up201003837 on 17/11/2015.
 */

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Publisher {
    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            System.err
                    .println("Usage: java Publisher <hostname> <port number> <stream> <videofile>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        String streamName= args[2];
        File video = new File(args[3]);

        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String input=stdIn.readLine();
            if(input.equals("publish")){
                out.println("publisher "+streamName);
            }

            byte[] data = new byte[1024*2];
            FileInputStream videoData = new FileInputStream(video);
            BufferedOutputStream stream = new BufferedOutputStream(socket.getOutputStream());

            int bytesread;
            int totalbytes=0;
            System.out.println("VIDEO LENGTH: "+video.length());
            while ((bytesread = videoData.read(data, 0,data.length)) != -1) {
                stream.write(data,0,data.length);

                totalbytes+=bytesread;
                float percentage=((float)totalbytes/video.length())*100;
                String percentage_string = String.format("%2.02f", percentage);
                System.out.println("Read "+bytesread+" bytes and sent them to Broker\t\t"+percentage_string+"%\t totalbytes: "+totalbytes);
                try{
                    Thread.sleep(5);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Host unknown " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("I/O retrieve not possible " + hostName);

            System.exit(1);
        }
    }
}
