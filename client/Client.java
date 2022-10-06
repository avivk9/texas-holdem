package client;

import server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String exitWord = "exit";
    private final int port;
    private final String address;
    private String sessionID;
    boolean stop;
    private Socket theServer;

    public Client(int port, String address) {
        this.port = port;
        this.address = address;
    }
    public void startClient(){
        stop = false;
        Scanner input = new Scanner(System.in);
        try {
            theServer = new Socket(address, port);
            System.out.println("Welcome To Texas Hold'em Server!");
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(theServer.getInputStream()));
            PrintWriter outToServer = new PrintWriter(theServer.getOutputStream());
            Connect(outToServer, inFromServer);

            outToServer.println("exit");
            outToServer.close();
            inFromServer.close();
        } catch (Exception e){e.printStackTrace();}
    }

    private void Connect(PrintWriter outToServer, BufferedReader inFromServer) throws IOException {

        Scanner s = new Scanner(System.in);
        String username = null;
        System.out.print("Please enter your username: ");
        outToServer.println(s.nextLine());

        String lineFromServer;
        while ((lineFromServer=inFromServer.readLine()).contains("FAIL")){
            System.out.println(lineFromServer + ", please try again");
            System.out.print("Please enter your username: ");
            outToServer.println(username = s.nextLine());
        }
        // Username Checked
        lineFromServer=inFromServer.readLine();
        if(lineFromServer.equals("ALERT:Creating new account")){
            // send details for account creation
            System.out.println("=CREATING NEW ACCOUNT=");
            System.out.print("username: " + username + "\nplease enter your password: ");
            outToServer.println(s.nextLine());
            while (!(lineFromServer = inFromServer.readLine()).contains("SUCCESS")){
                System.out.println(lineFromServer);
                System.out.print("username: " + username + "\nplease enter your password: ");
                outToServer.println(s.nextLine());
            }
            // account is created
        }
        System.out.print("username: " + username + "\nplease enter your password: ");
        outToServer.println(s.nextLine());
        while (!(lineFromServer = inFromServer.readLine()).contains("SUCCESS")){
            System.out.println(lineFromServer);
            System.out.print("username: " + username + "\nplease enter your password: ");
            outToServer.println(s.nextLine());
        }
        sessionID = inFromServer.readLine().substring(10);
        s.close();
    }

    public void printMsgs(){
        String line = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(theServer.getInputStream()));
            while(!stop && !((line = in.readLine()) == null)){
                System.out.println(line);
            }
            in.close();
        } catch (IOException ignored) {}
    }

    public void sendMsgs(){
        try {
            Scanner input = new Scanner(System.in);
            String line = null;
            PrintWriter out = new PrintWriter(theServer.getOutputStream(), true);
            //out.println(username);
            while(!(line = input.nextLine()).equals(exitWord)) {
                out.println(line);
            }
            out.println(exitWord);
            out.close();
            stop = true;
        }catch (Exception e){e.printStackTrace();}
    }


}
