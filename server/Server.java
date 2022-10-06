package server;

import server.users.ClientLogin;
import server.users.NameSystem;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;


public class Server {
    private static final String exitWord = "exit";
    private final int port;
    private boolean stop;
    public final NameSystem ns;


    public Server(int port){
        this.ns = new NameSystem();
        this.port = port;
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

            while(!stop){
                try {
                    Socket client = server.accept();
                    new Thread(() -> handleClient(client)).start();
                }catch (IOException ignored){}
            }


            server.close();
            System.out.println("Server is closed");
        }catch (Exception e){e.printStackTrace();}
    }

    private void handleClient(Socket client){
        Boolean connected = false;
        PrintWriter outToClient = null;
        String clientSession = null;
        try {
            // Messaging protocol: SESSION_ID,MSG_TYPE(BROADCAST/PRIVATE/?),TEXT
            // Connection
            boolean flag = true;
            String[] temp;
            clientSession = ClientLogin.Login(new FullSocket(client), ns); // should be added in each msg
            String clientInput;
            outToClient = ns.activeConnections.get(clientSession).fs.out;
            outToClient.println("CONNECTED:" + clientSession);
            connected = true;
            while(flag){
                clientInput = ns.activeConnections.get(clientSession).fs.in.readLine();
                if(clientInput.contains("/exit")){
                    flag = false;
                    continue;
                }
                temp = clientInput.split(","); // temp[0] = session, temp[1] = type, temp[2] = plain text
                if(temp.length != 3) throw new RuntimeException();
                if(temp[0].equals(clientSession)) throw new RuntimeException();
                // input is legal
                if(temp[1].contains("broad")){
                    broad(temp[2], clientSession);
                }
                //msg_username
                if(temp[1].contains("msg")){
                    String destUsername = temp[1].substring(4);
                    try {
                        sendPrivateMsg(temp[2], clientSession, destUsername);
                    }catch (Exception e){
                        outToClient.println("username not found");
                    }
                }
            }
        }
        catch (Exception SocketClosed){
            if(connected) ns.disconnectFromActive(clientSession);
            else {
                assert outToClient != null;
                outToClient.close();
            }
        }
    }

    private void broad(String txtToBroad, String senderSessionID){  // broadcast to all clients beside the client sent the message
        for (String s_id : ns.activeConnections.keySet()) {
            if (!s_id.equals(senderSessionID)) {
                //[time](username) txt
                ns.activeConnections.get(s_id).fs.out.println
                        ("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "] (" + ns.activeConnections.get(senderSessionID).username + ") " + txtToBroad);
            }
        }
    }

    private void sendPrivateMsg(String txtToMsg, String senderSessionID, String destinationUsername) throws Exception {

        for(String s_id : ns.activeConnections.keySet()){
            if(ns.activeConnections.get(s_id).username.equals(destinationUsername)){
                ns.activeConnections.get(s_id).fs.out.println
                        ("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "] (" + ns.activeConnections.get(senderSessionID).username + ") PRIVATE MSG: " + txtToMsg);
                return;
            }
        }
        throw new Exception("FAIL:destination not found");
    }

    public void closeServer(){
        stop = true;
    }
}
