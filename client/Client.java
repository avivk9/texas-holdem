package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    private final int port;
    private final String address;
    private String sessionCookie;
    AtomicBoolean connected;

    public Client(int port, String address) {
        this.port = port;
        this.address = address;
    }
    public void startClient(){
        connected = new AtomicBoolean(true);
        try {
            sessionCookie = null;
            Socket theServer = new Socket(address, port);
            System.out.println("Welcome To Texas Hold'em Server\n For help please enter /help");
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(theServer.getInputStream()));
            PrintWriter outToServer = new PrintWriter(theServer.getOutputStream(), true);
            new Thread(()->mainPrintingLoop(inFromServer)).start();
            mainInputLoop(outToServer);
            outToServer.close();
            inFromServer.close();
        } catch (Exception ignored){System.out.println("Server is not up yet");}
    }

    private void mainInputLoop(PrintWriter outToServer){
        String location = "~";
        Scanner readLine = new Scanner(System.in);
        String input;
        while(connected.get()){
            System.out.print(location);
            input = readLine.nextLine().trim();
            if(input.startsWith("/exit")){
                connected = new AtomicBoolean(false);
            }
            else if(input.startsWith("/help")){
                System.out.println("""
                        ~Help Instructions~
                        /ping                                   used to check if the server response well
                        /exit                                   used for closing the connection with the server
                        /signup 'username' 'password' 'email'   create a new account and add it to the server db
                        /login 'username' 'password'            logs you into the server, make it possible for you
                                                                to send messages to other online users
                        /broad 'message'                        send a message to ALL online users
                        /msg 'username' 'message'               send a private message ONLY to the username specified
                        /disconnect                             if you're logged in, you'll disconnect from your account
                        /cookie                                 reveal your session cookie, don't bother to understand what's written :)""");
            }

            else if(input.startsWith("/ping")){
                outToServer.println("/ping");
            }

            else if(input.startsWith("/signup")){
                if(sessionCookie != null)
                    System.out.println("for signup, please disconnect first, try using /disconnect");
                else if(input.contains(" ")){
                    if(input.split(" ").length == 4)
                        outToServer.println(input);
                    else System.out.println("command hasn't wrote properly, please try again - or use /help for help");
                }
                else System.out.println("command hasn't wrote properly, please try again - or use /help for help");
            }

            else if(input.startsWith("/login")){
                if(sessionCookie != null)
                    System.out.println("already logged in, for login out please use /disconnect");
                else if(input.contains(" ")){
                    if(input.split(" ").length == 3)
                        outToServer.println(input);
                    else System.out.println("command hasn't wrote properly, please try again - or use /help for help");
                }
                else System.out.println("command hasn't wrote properly, please try again - or use /help for help");
            }

            else if(input.equals("/disconnect")){
                outToServer.println("/disconnect");
                sessionCookie = null;
            }

            else if(input.equals("/cookie")){
                if(sessionCookie == null) System.out.println("You're not logged in, so there's no cookie for you :(");
                else System.out.println(sessionCookie);
            }
            else if(input.startsWith("/msg")){
                if(sessionCookie == null)
                    System.out.println("for sending private messages, please first login");
                else if(input.contains(" ")){
                    if(input.split(" ").length == 3)
                        outToServer.println(input + " " + sessionCookie);
                    else System.out.println("command hasn't wrote properly, please try again - or use /help for help");
                }
                else System.out.println("command hasn't wrote properly, please try again - or use /help for help");
            }
            else if(input.startsWith("/broad")){
                if(sessionCookie == null)
                    System.out.println("for broadcasting messages, please first login");
                else if(input.contains(" ")){
                    if(input.split(" ").length == 2)
                        outToServer.println(input + " " + sessionCookie);
                    else System.out.println("command hasn't wrote properly, please try again - or use /help for help");
                }
                else System.out.println("command hasn't wrote properly, please try again - or use /help for help");
            }


            else{ // unknown command
                System.out.println("unknown command entered, for help please type /help");
            }
        }
        outToServer.println("/exit");
    }

    private void mainPrintingLoop(BufferedReader inFromServer){
        String cmd;
        while (connected.get()){
            try {
                cmd = inFromServer.readLine();
            } catch (IOException e) {
                connected = new AtomicBoolean(false);
                continue;
            }
            if(cmd.startsWith("/print")){
                System.out.println("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "](SERVER) " + cmd.substring(7));
                System.out.print("~");
            }
            if(cmd.startsWith("/m_print")){
                System.out.println(cmd.substring(9));
                System.out.print("~");
            }
            if(cmd.startsWith("/cookie")){
                sessionCookie = cmd.substring(8);
            }
        }
    }

}
