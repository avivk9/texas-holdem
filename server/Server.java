package server;

import server.users.FullSocket;
import server.users.NameSystem;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Server {
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
        Boolean connected = true;
        String sessionCookie = null;
        String msg;
        server.users.FullSocket fs = null;
        try {
            fs = new FullSocket(client);
            while (connected) {
                msg = fs.in.readLine();

                if (msg.startsWith("/exit")) {
                    connected = false;
                }

                else if (msg.startsWith("/ping")) {
                    fs.out.println("/print pong!");
                }

                else if(msg.startsWith("/login")){     //login username password
                    String ret_val = ns.loginToServer(msg.split(" ")[1], msg.split(" ")[2], fs);
                    if(ret_val.equals("not_exist")) fs.out.println("/print Failed to login, such username don't exist");
                    else if(ret_val.equals("wrong_password")) fs.out.println("/print Failed to login, wrong password - try again");
                    else {
                        sessionCookie = ret_val;
                        fs.out.println("/print Just logged in as " + msg.split(" ")[1]);
                        fs.out.println("/cookie " +  sessionCookie);
                    }
                }

                else if(msg.startsWith("/signup")){    //signup username password email
                    int ret_val = ns.signupNewUser(msg.split(" ")[1], msg.split(" ")[2], msg.split(" ")[3]);
                    if(ret_val == 1) fs.out.println("/print Account: " + msg.split(" ")[1] + " created successfully!");
                    else if(ret_val == 0) fs.out.println("/print Failed to sign up, this username is already in use");
                    else if(ret_val == -1) fs.out.println("/print Failed to sign up, an error has been occurred");
                }

                else if(msg.equals("/disconnect")){
                    fs.out.println("Disconnected from the server successfully!");
                    ns.disconnectFromActive(sessionCookie);
                    sessionCookie = null;
                }

                else if(msg.startsWith("/msg")){       //msg username text sessionCookie
                    String ret_val = sendPrivateMsg(msg.split(" ")[1], msg.split(" ")[2], msg.split(" ")[3]);
                    if(ret_val.equals("sender_not_found")) fs.out.println("/print broadcast failed, please try to disconnect and login again");
                    else if(ret_val.equals("dest_not_found")) fs.out.println("/print the user you tried messaging not found");
                    else if(ret_val.equals("ok")) fs.out.println("/print message delivered successfully!");
                }

                else if(msg.startsWith("/broad")){     //broad text sessionCookie
                    String ret_val = broad(msg.split(" ")[1], msg.split(" ")[2]);
                    if(ret_val.equals("not_found")) fs.out.println("/print broadcast failed, please try to disconnect and login again");
                    else if(ret_val.equals("ok")) fs.out.println("/print Just broadcast: " + msg.split(" ")[1]);
                }
            }

        }catch (Exception SocketClosed){
            // close socket
        }
        finally {
            if(sessionCookie != null) ns.disconnectFromActive(sessionCookie);
            if(fs != null) fs.close();
        }
    }

    private String broad(String txtToBroad, String senderSessionCookie){  // broadcast to all clients beside the client sent the message
        if(!ns.activeConnections.containsKey(senderSessionCookie))
            return "not_found";
        String msg = "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "] ("
                + ns.activeConnections.get(senderSessionCookie).username + ") " + txtToBroad;
        for (String client_cookie : ns.activeConnections.keySet()){
            if(!client_cookie.equals(senderSessionCookie)){
                ns.activeConnections.get(client_cookie).sendMsg("/m_print " + msg);
            }
        }
        return "ok";
    }

    private String sendPrivateMsg(String destinationUsername, String txtToMsg, String senderSessionCookie) {
        if(!ns.activeConnections.containsKey(senderSessionCookie))
            return "sender_not_found";
        String msg = "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "] ("
                + ns.activeConnections.get(senderSessionCookie).username + ") PRIVATE MESSAGE: " + txtToMsg;
        for(String client_cookie : ns.activeConnections.keySet()){
            if(ns.activeConnections.get(client_cookie).username.equals(destinationUsername)){
                ns.activeConnections.get(client_cookie).fs.out.println("/m_print " + msg);
                return "ok";
            }
        }
        return "dest_not_found";
    }

    public void closeServer(){
        stop = true;
    }
}
