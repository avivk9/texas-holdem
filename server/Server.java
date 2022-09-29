package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static java.lang.System.currentTimeMillis;

public class Server {
    HashMap<String, String> connections; // client ip - client name

    public Server(){
        try {
            ServerSocket serv = new ServerSocket(6969);
            System.out.println("Server is up!");
            serv.accept();

        } catch (IOException e) {throw new RuntimeException(e);}
    }

    public static void main(String[] args){
        // test
        Msg m = new Msg("hello world", "avivk9", new Date());
        System.out.println(m.toString());
    }

}
