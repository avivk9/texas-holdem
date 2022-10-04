package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class FullSocket {
    public Socket s;
    public PrintWriter out;
    public BufferedReader in;
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
