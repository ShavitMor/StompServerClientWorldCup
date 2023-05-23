package bgu.spl.net.impl.stomp;
import java.util.ArrayList;

import javax.print.attribute.standard.Destination;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.Connections;




public class StompProtocol<T> implements StompMessagingProtocol<T>{

    //pointer to connections
    private ConnectionsImpl<T> connections;
    private int id;
    

    private boolean shouldTerminate;
    
    

    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.connections=(ConnectionsImpl)connections;
        this.id=connectionId; 
        shouldTerminate=false;
    }
    

    @Override
    public void process(T message) {
        
        
        String msg=(String)message;

        String split[] = msg.split("\n");
        runFrame(split[0],msg);


    }

    


    private void runFrame(String string,String msg) {

        switch(string){
            case "CONNECT":
                login(msg);
                break;
            case "SEND":
                report(msg);
                break;
            case "SUBSCRIBE":
                join(msg);
                break;
            case "UNSUBSCRIBE":
                exit(msg);
                break;
            case "DISCONNECT":
                logout(msg);
                break;
        }
    }

    private void login(String msg){

        System.out.println("This is the message to login: "+msg);
        String split[] = msg.split("\n");
        String loginSplit[] = split[3].split(":");
        String passcodeSplit[] = split[4].split(":");
        ArrayList<String> a =new ArrayList<>();
        a.add("accept-version:1.2");
        a.add("host:stomp.cs.bgu.ac.il");
        a.add("login");
        a.add("passcode");

        
        a.remove(split[1]);
        a.remove(split[2]);
        a.remove(loginSplit[0]);
        a.remove(passcodeSplit[0]);

        if(!a.isEmpty()){
            String headersMissing="";
            for(String s : a){
                headersMissing=s+" ";
            }
            sendError(null,"malformed frame recived",msg,
            "Headers missing"+headersMissing);
        }
        System.out.println("here "+ msg);
        //new user
        if(!shouldTerminate()){
            System.out.println(loginSplit[1]);
        String username=loginSplit[1];
        String password=passcodeSplit[1];
        if(!connections.isUser(username) ){

            connections.addUser(username,password);
            //wrong password
             if(!connections.checkPassword(username, password)){
                sendError(null, "Wrong password", msg, "");
             }
            else{
                connections.login(id);
                sendConnected();
            }
        }
        else{
            if(connections.isLoggedIn(id)){
            sendError(null, "User is already logged in", msg, "");
             }
             else{
                 //wrong password
             if(!connections.checkPassword(username, password)){
                sendError(null, "Wrong password", msg, "");
             }
            else{
                connections.login(id);
                sendConnected();
            }
             }
        }
    
    }



    }
    public void report(String msg){
        String split[] = msg.split("\n");
        String[] destinationH=split[1].split(":");
       
        String destinationHeader=destinationH[0];

        ArrayList<String> a =new ArrayList<>();
        a.add("destination");      
        a.remove(destinationHeader);
      
        if(!a.isEmpty()){
            String headersMissing="";
            for(String s : a){
                headersMissing=s+" ";
            }
            sendError(null,"malformed frame recived",msg,
            "Headers missing"+headersMissing);
        }


        //check logged in, check subscribed, 
        if(!shouldTerminate()){
            if(!connections.isSubscribedToChannel(destinationH[1], id))
                sendError(null, "send to unsbscribed channel", msg, "");
            else if(!connections.isLoggedIn(id)){
                sendError(null, "user not logged in", msg, "");
            }
        

            String subID= connections.getSubID(destinationH[1],id);
            
            sendMessage(msg, subID,connections.getMessageID(),destinationH[1]);
        }

    
    }
    public void exit(String msg){

        String split[] = msg.split("\n");
        String idHeader=split[1].split(":")[0];
        

        String subId=split[1].split(":")[1];
        String receiptHeader=split[2].split(":")[0];
        String receiptId=split[2].split(":")[1];
        ArrayList<String> a =new ArrayList<>();
        a.add("id");
        a.add("receipt");
   
        a.remove(idHeader);
        a.remove(receiptHeader);
        

        if(!a.isEmpty()){
            String headersMissing="";
            for(String s : a){
                headersMissing=s+" ";
            }
            sendError(null,"malformed frame recived",msg,
            "Headers missing"+headersMissing);
        }

        if(!shouldTerminate()){

            if(!connections.isLoggedIn(id)){
                sendError(receiptId, "user isnt logged in", msg, "");
            }else
            if(!connections.isSubscribed(subId, id)){
                sendError(receiptId, "wasnt subscribed", msg, "");
            }
            else{
                connections.unSubscribe(subId,id);
                sendReceipt(receiptId);
            }

        }

        
        
    }

    public void logout(String msg){
        System.out.println(msg);
        String split[]=msg.split("\n");
        String receiptHeader=split[1].split(":")[0];
        String receiptId=split[1].split(":")[1];

        ArrayList<String> a =new ArrayList<>();
        a.add("receipt");
        
        a.remove(receiptHeader);
        
        if(!a.isEmpty()){
            String headersMissing="";
            for(String s : a){
                headersMissing=s+" ";
            }
            sendError(null,"malformed frame recived",msg,
            "Headers missing"+headersMissing);
        }

        if(!shouldTerminate()){
            if(!connections.isLoggedIn(id)){
                sendError(receiptId, "cant logout before login", msg, "");
            }else{
            sendReceipt(receiptId);
            connections.disconnect(id);
        }
        }
            

    }

    public void join(String msg){
        System.out.println(msg);
        String split[] = msg.split("\n");
        String dest[]=split[1].split(":");
        String recipt[]=split[3].split(":");
        String idHead[]=split[2].split(":");

        String reciptId=recipt[1];
        String subId=idHead[1];
        String destination=dest[1];

        ArrayList<String> a =new ArrayList<>();
        a.add("destination");
        a.add("id");
        a.add("receipt");
        System.out.println(a);
        System.out.println(recipt[0]+" ine ze ");
        a.remove(dest[0]);
        a.remove(idHead[0]);
       a.remove(recipt[0]);
       System.out.println(a);
        if(!a.isEmpty()){
            System.out.println(a +"whatefuk");
            String headersMissing="";
            for(String s : a){
                headersMissing=s+" ";
            }
            sendError(null,"malformed frame recived",msg,
            "Headers missing "+headersMissing);
        }

       
        if(!shouldTerminate()){
            if(!connections.isLoggedIn(id)){
        sendError(reciptId, "user wasnt logged in", msg , "");
            }
            if(connections.isSubscribedToChannel(destination,id)){
        sendError(reciptId, "user already subscribed to channel", msg, "");
            }
            else{
                connections.subscribe(destination,id,subId);
                sendReceipt(reciptId);
            }

    }

    } 
    

    @Override
    public boolean shouldTerminate() {
        
        return shouldTerminate;
    }

   
    public void sendReceipt(String receiptId){
        String recipt = "RECEIPT\n"+"receipt-id: "+receiptId+"\n\n"+'\u0000';
        connections.send(id,(T) recipt);
        }

    public void sendError(String receiptId,String description,String copy,String body){

        String frame;
        frame="ERROR\n";
        if(receiptId!=null){
            frame=frame+"receipt-id: "+receiptId+"\n";
        }
        frame=frame+"message: "+description+"\n";
        frame=frame+"\n";
        frame=frame+"The message:\n";
        frame=frame+"-----\n";
        String msg=copy.replace('\u0000',' ');
        frame=frame+msg+"\n";
        frame=frame+"-----\n";
        frame=frame+body+"\n";
        frame=frame+'\u0000';
        connections.send(id, (T) frame);
        connections.disconnect(id);
        shouldTerminate=true;

    }

    public void sendConnected(){
        String frame = "CONNECTED\n"+"version:1.2\n"+"\n"+'\u0000';
        connections.send(id, (T)frame);
    }


    public void sendMessage(String msg,String subID,int messageID,String topic){
        String frame="MESSAGE\n"+"subscription:"+subID+"\n"+"message-id:"+messageID+
        "\n"+"destination:"+topic+"\n"+
        "\n"+msg+'\u0000';
        connections.send(topic, (T)frame);
        
        
    }
}
