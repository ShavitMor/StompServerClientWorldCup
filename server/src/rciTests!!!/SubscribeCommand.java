package bgu.spl.net.impl.stomp;

import java.io.Serializable;

import bgu.spl.net.impl.rci.Command;

public class SubscribeCommand  implements Command<Stomp> {

    @Override
    public Serializable execute(Stomp arg) {
        
        arg.Subscribe();

        return null;
    }
    

}