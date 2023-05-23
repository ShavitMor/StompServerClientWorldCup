package bgu.spl.net.impl.stomp;
import bgu.spl.net.srv.Connections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;

public class ConnectionsImpl<T> implements Connections<T>{

//connection handlers
private Map<Integer ,ConnectionHandler<T>> cHandlers;

//list of users
private ConcurrentHashMap<String ,String> users;

//logged in users (connection id's)
private ArrayList<Integer> logggedIn;

//for every channel -> (connection id,subs id)
private Map<String , ConcurrentHashMap<Integer,String>> channelsMap;

private int messageID;



public ConnectionsImpl(){
    cHandlers=new ConcurrentHashMap<Integer ,ConnectionHandler<T>>();
    users=new ConcurrentHashMap<String ,String>();
    //logged in users id list
    logggedIn=new ArrayList<Integer>();
    channelsMap=new HashMap<String, ConcurrentHashMap<Integer,String>>();
    messageID=0;
}




    @Override
    public boolean send(int connectionId, T msg) { // for one client
        
        try {
            System.out.println(msg);
            cHandlers.get(connectionId).send(msg);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void send(String channel, T msg) { //for number of clients
        ConcurrentHashMap<Integer,String> a = channelsMap.get(channel);
        for(Integer id : a.keySet()){
            try {
                cHandlers.get(id).send((T)msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
//        for(ConnectionHandler<T> cH : cHandlers.values()){
//            try {
//                cH.send(msg);
//            } catch (IOException e) {
//
//                e.printStackTrace();
//            }
//        }
        
    }

    @Override
    public void disconnect(int connectionId) { //say bye bye to client

        for(ConcurrentHashMap<Integer,String> a : channelsMap.values()){
            a.remove(connectionId);

        }
        
        try {
            cHandlers.get(connectionId).close();
            if(logggedIn.contains(connectionId))
                logggedIn.remove((Integer) connectionId);
        } catch (IOException e) {
            e.printStackTrace();
        }
       cHandlers.remove(connectionId);

    }


    public void addCH(int connectionId,ConnectionHandler ch){
        cHandlers.put(connectionId, ch);

    }

    
   
    public void addUser(String username, String password){
        users.put(username, password);
    }

    public boolean isUser(String username){
        if(users.containsKey(username)){
            return true;
        }
        else
            return false;
    }

    public void login(int id){
        logggedIn.add(id);

}

public boolean isLoggedIn(int id){
    if(logggedIn.contains(id)){
        return true;
    }
    else
        return false;
}

public boolean isSubscribedToChannel(String channel,int cId){
    ConcurrentHashMap<Integer,String> a = channelsMap.get(channel);
    if(a == null) return false;
    return a.containsKey(cId);
    
    

}

public boolean checkPassword(String user, String password){
    if(users.get(user).equals(password)){
        return true;
    }
    else
        return false;
}




public void subscribe(String destination, int id, String subId) {
    if(!channelsMap.containsKey(destination)){
        ConcurrentHashMap<Integer,String> a=new ConcurrentHashMap<Integer,String>();
        a.put(id, subId);
        channelsMap.put(destination,a);
    }
    else{
        channelsMap.get(destination).put(id, subId);
    }
}
//private Map<String , ConcurrentHashMap<Integer,String>> channelsMap;

public boolean isSubscribed(String subId,int id){
    boolean ans=false;
    for(ConcurrentHashMap<Integer,String> a : channelsMap.values()){
        if(a.containsKey(id) & a.get(id).equals(subId)){
           return true;
        }
    }
    return ans;
}

public void unSubscribe(String subId,int id){
    for(ConcurrentHashMap<Integer,String> a : channelsMap.values()){
        a.remove(id, subId);
    }

}




public String getSubID(String string, int id) {
    return channelsMap.get(string).get(id);
}

public int getMessageID(){
    return messageID++;
}

}