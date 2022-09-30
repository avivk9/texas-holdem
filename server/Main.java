package server;

import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Server s = new Server(6969);
        s.start();
        Scanner input = new Scanner(System.in);
        System.out.print("click enter for closing server");
        input.nextLine();
        s.closeServer();
        System.out.println("\n\n\nfinished");
    }
}
