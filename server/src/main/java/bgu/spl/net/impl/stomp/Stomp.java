package bgu.spl.net.impl.stomp;

import java.util.HashMap;
import java.util.Map;

public class Stomp {
    
private Map<String ,String> users;

public Stomp(){
users=new HashMap<String ,String>();

}


public void addUser(String username, String password){
    users.put(username, password);
}


public boolean isLoggedIn(String username){
    if(users.containsKey(username)){
        return true;
    }
    else
        return false;
}
}
