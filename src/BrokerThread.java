import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by rotiv_13 on 17-11-2015.
 */

public class BrokerThread extends Thread {
    private Socket socket = null;

    public BrokerThread(Socket socket) {
        super("BrokerThread");
        this.socket = socket;
    }

    public void run() {

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            BrokerProtocol kkp = new BrokerProtocol();
            outputLine = kkp.processInput(""+socket.getPort());
            out.println(outputLine);
            System.out.println(outputLine);
            while ((inputLine = in.readLine()) != null) {
                outputLine = kkp.processInput(inputLine);
                out.println(outputLine);
                System.out.println(outputLine);
                if (outputLine.equals("Bye"))
                    break;
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}