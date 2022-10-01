package client;

import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner s = new Scanner(System.in);
        Client c = new Client("avivk9", 6969, "127.0.0.1");
        c.startClient();
    }
}
