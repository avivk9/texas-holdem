package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String exitWord = "exit";
    private Thread sendMessages;
    private String username;
    private int port;
    private String address;
    boolean stop;
    private Socket theServer;
    public Client(String username, int port, String address) {
        this.username = username;
        this.port = port;
        this.address = address;
    }
    public void startClient(){
        stop = false;
        try {
            theServer = new Socket(address, port);
            System.out.println("Connected to server!");
            sendMessages = new Thread(this::sendMsgs);
            sendMessages.start();
            new Thread(this::printMsgs).start();

        } catch (Exception e){e.printStackTrace();}
    }

    public void printMsgs(){
        String line = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(theServer.getInputStream()));
            while(!stop && !((line = in.readLine()) == null)){
                System.out.println(line);
            }
            stop = true;
            in.close();
        } catch (IOException e) {e.printStackTrace();}
    }

    public void sendMsgs(){
        try {
            Scanner input = new Scanner(System.in);
            String line = null;
            PrintWriter out = new PrintWriter(theServer.getOutputStream(), true);
            out.println(username);
            while(!(line = input.nextLine()).equals(exitWord)) {
                out.println(line);
            }
            out.println(exitWord);
            out.close();
            stop = true;
        }catch (Exception e){e.printStackTrace();}
    }


}
