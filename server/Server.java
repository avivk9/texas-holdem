package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/*
    When Connecting to the server first msg should be the nickname of the user

 */


public class Server {
    private static final String exitWord = "exit";
    private final int port;
    private boolean stop;
    private final HashMap<Integer, Socket> connections;
    private final HashMap<Integer, Thread> connectionThreads;
    private final HashMap<Integer, String> connectionNames;
    private final PriorityBlockingQueue<Msg> messages;


    public Server(int port){
        this.port = port;
        connections = new HashMap<>();
        messages = new PriorityBlockingQueue<>(1000);
        connectionThreads = new HashMap<>();
        connectionNames = new HashMap<>();
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
            new Thread(()->broadcastMsgs()).start();
            while(!stop){
                try{
                    Socket client = server.accept();
                    connections.put(client.getPort(), client);
                    System.out.println("new connection: " + client.toString());
                    Thread t = new Thread(()->handleClient(client));
                    connectionThreads.put(client.getPort(), t);
                    t.start();
                }catch (SocketTimeoutException e){}
            }
            server.close();
            System.out.println("Server is closed");
        }catch (Exception e){e.printStackTrace();}
    }

    private void handleClient(Socket client){
        try {
            String line = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String name = in.readLine();
            connectionNames.put(client.getPort(), name);
            System.out.println("connection name: " + name);
            while(!(line = in.readLine()).equals(exitWord) /*&& !client.isClosed()*/){
                messages.put(new Msg(line, name, new Date()));
            }
            in.close();
            removeClient(client.getPort());
        }catch (Exception e){e.printStackTrace();}
    }

    private void broadcastMsgs(){
        try {
            Msg m;
            while (!stop) {
                m = null;
                m = messages.poll(1, TimeUnit.SECONDS);
                if(m != null) {
                    for (int id : connections.keySet()) {
                        if (connectionNames.get(id).equals(m.sender)) continue;
                        Socket client = connections.get(id);
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                        out.println(m.toString());
                        out.close();
                    }
                }
            }
        }catch (Exception e){e.printStackTrace();}
        System.out.println("broadcasting ended");
    }

    private void removeClient(int clientID) {
        if(connections.containsKey(clientID)) {
            try {
                connections.get(clientID).close();
                connections.remove(clientID);
            } catch (IOException e) {}
        }
    }

    public void closeServer(){
        try {
            connections.keySet().forEach(this::removeClient);
        }catch (Exception e){e.printStackTrace();}
        stop = true;
    }
}
