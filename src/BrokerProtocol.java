import java.net.Socket;

/**
 * Created by Vitor Afonso up200908303 and Ricardo Godinho up201003837 on 17/11/2015.
 */
public class BrokerProtocol {
    public String processInput(String in,Broker broker, int port){
        if(in.equals("publisher")){
            broker.addNewPublisher(port);
        }
        return in;
    }
}
