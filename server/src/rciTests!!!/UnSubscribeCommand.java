package bgu.spl.net.impl.stomp;

import java.io.Serializable;

import bgu.spl.net.impl.rci.Command;

public class UnSubscribeCommand  implements Command<Stomp> {

    @Override
    public Serializable execute(Stomp arg) {
        
        arg.UnSubscribe();

        return null;
    }
    

}