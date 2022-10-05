package server;

import server.users.ClientLogin;
import server.users.ConnectedUser;
import server.users.NameSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;


public class Server {
    private static final String exitWord = "exit";
    private final int port;
    private boolean stop;
    private final ConcurrentHashMap<Integer, FullSocket> allConnections; // to keep up all connections, also those who didn't log in
    private final PriorityBlockingQueue<Msg> messages;
    public final NameSystem ns;


    public Server(int port){
        this.ns = new NameSystem();
        this.port = port;
        allConnections = new ConcurrentHashMap<>();
        messages = new PriorityBlockingQueue<>(1000);
    }

    public void start(){
        stop = false;
        new Thread(this::startServer).start();
    }

    private void startServer(){
        try{
            ServerSocket server = new ServerSocket(port);
            server.setSoTimeout(1000);
            System.out.println("server is up");

            //new Thread(this::keepBroadcasting).start();

            while(!stop){
                try {
                    Socket client = server.accept();
                    allConnections.put(client.getPort(), new FullSocket(client));
                    new Thread(() -> handleClient(client)).start();
                }catch (IOException ignored){}
            }


            server.close();
            System.out.println("Server is closed");
        }catch (Exception e){e.printStackTrace();}
    }

    private void handleClient(Socket client){
        try {
            // Connection
            String clientSession = ClientLogin.Login(new FullSocket(client), ns); // should be added in each msg
            String clientInput;
            var outToClient = ns.activeConnections.get(clientSession).fs.out;
            outToClient.println("CONNECTED - " + clientSession);
            while(!(clientInput = ns.activeConnections.get(clientSession).fs.in.readLine()).contains(exitWord)){
                if(!clientInput.contains(clientSession)){
                    // TODO handle not getting clientSessionID in the msg
                }
                else{
                    // TODO: handle different type of requests from client
                }
            }
        }
        catch (Exception ignored){} // socket closed
        finally {
            removeClient(client.getPort());
        }
    }

    private void broad(Msg msgToBroad){  // broadcast to all clients beside the client sent the message
        PrintWriter out;
        for (String s_id : ns.activeConnections.keySet()) {
            if (!msgToBroad.sender.equals(ns.activeConnections.get(s_id).username)) {
                out = ns.activeConnections.get(s_id).fs.out;
                out.println(msgToBroad);
            }
        }
    }

    private void keepBroadcasting(){
        try {
            Msg m;
            while (!stop) {
                m = messages.poll(1, TimeUnit.SECONDS);
                if(m != null) {
                    broad(m); // broadcast to all clients beside the client sent the message}
                }
            }
        }catch (Exception e){e.printStackTrace();}
        finally {System.out.println("broadcasting ended");}
    }

    private void removeClient(int clientID) { // TODO: remove client properly
        if(allConnections.containsKey(clientID)) {
            allConnections.get(clientID).close();
            allConnections.remove(clientID);
        }
    }

    public void closeServer(){
        try {
            allConnections.keySet().forEach(this::removeClient);
        }catch (Exception e){e.printStackTrace();}
        stop = true;
    }
}
