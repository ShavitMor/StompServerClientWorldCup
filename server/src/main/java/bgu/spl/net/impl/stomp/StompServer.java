package bgu.spl.net.impl.stomp;

import bgu.spl.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl.net.srv.Server;

public class StompServer {

    public static void main(String[] args) {
      
       

       // you can use any server... 
        Server.threadPerClient(
                7777, //port
                () -> new StompProtocol(), //protocol factory
                LineMessageEncoderDecoder::new //message encoder decoder factory
        ).serve();

       Server.reactor(
               Runtime.getRuntime().availableProcessors(),
               7777, //port
               () -> new StompProtocol(), //protocol factory
               LineMessageEncoderDecoder::new //message encoder decoder factory
       ).serve();

      
    }
}
