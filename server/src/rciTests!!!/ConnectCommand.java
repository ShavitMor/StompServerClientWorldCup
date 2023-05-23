package bgu.spl.net.impl.stomp;

import java.io.Serializable;

import bgu.spl.net.impl.rci.Command;

public class ConnectCommand implements Command<Stomp> {

    @Override
    public Serializable execute(Stomp arg) {
        
        arg.Connect();

        return null;
    }
    
}
