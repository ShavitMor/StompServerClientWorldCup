package bgu.spl.net.impl.echo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class EchoClient {

    public static void main(String[] args) throws IOException {
        String msg = "CONNECT\n" + "accept-version:1.2\n" +"host:stomp.cs.bgu.ac.il\n"
        + "login:meni"+ "\n" +"passcode:123123" + "\n" + "\n" + "\0";
        if (args.length == 0) {
            args = new String[]{"localhost", msg};
        }

        if (args.length < 2) {
            System.out.println("you must supply two arguments: host, message");
            System.exit(1);
        }

        //BufferedReader and BufferedWriter automatically using UTF-8 encoding
        try (Socket sock = new Socket(args[0], 7777);
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {

            System.out.println("sending message to server");
            out.write(msg);
            out.newLine();
            out.flush();

            System.out.println("awaiting response");
            int a =12;
            while(a>0) {
                String line = in.readLine();
                System.out.println("message from server: " + line);
                a--;
            }

        }
    }
}
