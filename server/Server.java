package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;


public class Server {
    private static final String exitWord = "exit";
    private final int port;
    private boolean stop;
    private final ConcurrentHashMap<Integer, FullSocket> connections;
    private final ConcurrentHashMap<Integer, String> connectionNames;
    private final ConcurrentHashMap<String, Integer> namesConnections;
    private final PriorityBlockingQueue<Msg> messages;

    private static class FullSocket{
        Socket s;
        PrintWriter out;
        BufferedReader in;
        public FullSocket(Socket s) {
            this.s = s;
            try {
                out = new PrintWriter(s.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void close(){
            try {
                out.close();
                in.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public Server(int port){
        this.port = port;
        connections = new ConcurrentHashMap<>();
        messages = new PriorityBlockingQueue<>(1000);
        connectionNames = new ConcurrentHashMap<>();
        namesConnections = new ConcurrentHashMap<>();
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

            new Thread(this::keepBroadcasting).start();

            while(!stop){
                try {
                    Socket client = server.accept();
                    connections.put(client.getPort(), new FullSocket(client));
                    new Thread(() -> handleClient(client)).start();
                }catch (IOException ignored){}
            }


            server.close();
            System.out.println("Server is closed");
        }catch (Exception e){e.printStackTrace();}
    }

    private void handleClient(Socket client){
        try {
            String line;
            BufferedReader in = connections.get(client.getPort()).in;
            String name = in.readLine(); // first message from client will always be his name
            connectionNames.put(client.getPort(), name);
            namesConnections.put(name, client.getPort());

            broad(new Msg(name + " Just Landed In The Chat", "SERVER", new Date()));

            while(!(line = in.readLine()).equals(exitWord)){
                messages.put(new Msg(line, name, new Date()));
                System.out.println("FROM HANDLE CLIENT: " + "new msg added: " + new Msg(line, name, new Date()));
            }
        }
        catch (Exception ignored){} // socket closed
        finally {removeClient(client.getPort());}
    }

    private void broad(Msg msgToBroad){  // broadcast to all clients beside the client sent the message
        PrintWriter out;
        for (int id : connections.keySet()) {
            if (!connectionNames.get(id).equals(msgToBroad.sender)) {
                out = connections.get(id).out;
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

    private void removeClient(int clientID) {
        if(connections.containsKey(clientID)) {
            connections.get(clientID).close();
            connections.remove(clientID);
            broad(new Msg(connectionNames.get(clientID) + " Just Left The Chat :(", "SERVER", new Date()));
            connectionNames.remove(clientID);
        }
    }

    public void closeServer(){
        try {
            connections.keySet().forEach(this::removeClient);
        }catch (Exception e){e.printStackTrace();}
        stop = true;
    }
}
