package bgu.spl.net.impl.stomp;

import java.io.Serializable;

import bgu.spl.net.impl.rci.Command;

public class SendCommand  implements Command<Stomp> {

//maybe need to put here feild of message


    @Override
    public Serializable execute(Stomp arg) {
        arg.Send();

        return null;
    }
    

}