package client;


public class Main {

    public static void main(String[] args) throws InterruptedException {
        int PORT = 6969;
        //String address = "192.168.1.185";
        String address = "localhost";
        Client c = new Client(PORT, address);
        c.startClient();
    }
}
