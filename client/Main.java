package client;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        int PORT = 6969;
        //String address = "192.168.1.185";
        String address = "localhost";
        Scanner s = new Scanner(System.in);
        String username = s.nextLine();
        Client c = new Client("avivk8", PORT, address);
        c.startClient();
    }
}
