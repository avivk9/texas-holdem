package server.users;

import server.FullSocket;
import server.Server;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ClientLogin {
    // RETURN SESSION ID
    public static String Login(FullSocket fullSocket, NameSystem ns){
        int EXIT_CODE;
        String username = requestUsername(fullSocket);
        while (!((EXIT_CODE = isUsernameLegal(username)) == 0)){
            if(EXIT_CODE == 1) fullSocket.out.println("FAIL - username null exception");
            else if(EXIT_CODE == 2) fullSocket.out.println("FAIL - username too short");

            username = requestUsername(fullSocket);
        }
        // username is legal, now let's see if we log in to exist user or creating a new one
        if(!ns.isUsernameExist(username)){
            createAccount(fullSocket, username, ns);
        }
        // account created
        String password = requestPassword(fullSocket,username, ns);
        while (!((EXIT_CODE = isPasswordCorrect(username, password, ns)) == 0)){
            if(EXIT_CODE == 1) fullSocket.out.println("FAIL - password not correct");
        }
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        String clientSessionID = new String(array, StandardCharsets.UTF_8);
        ns.addActive(clientSessionID, new ConnectedUser(clientSessionID, username, fullSocket));
        return clientSessionID;
    }

    private static int isPasswordCorrect(String username, String password, NameSystem ns) {
        if(!ns.checkPassword(username, password)) return 1;
        else return 0;
    }

    private static int isPasswordLeagal(String password) {
        if(password.length() < 5) return 1;
        // check if password is in common passwords file
        else return 0;
    }

    private static String requestPassword(FullSocket fullSocket, String username, NameSystem ns) {
        fullSocket.out.println("REQUEST - password");
        try {
            return fullSocket.in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void createAccount(FullSocket fullSocket, String username, NameSystem ns) {
        // TODO - IMPLEMENT
        // TODO - MAYBE ADD SEARCHING IN THE MOST COMMON PASSWORDS
        // DO NOT RETURN UNTIL ACCOUNT WAS CREATED
    }

    private static String requestUsername(FullSocket fullSocket){
        fullSocket.out.println("REQUEST - username");
        try {
            return fullSocket.in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int isUsernameLegal(String username){
        if(username == null) return 1;
        if(username.length() > 2) return 2;
        return 0;  // Success
    }
}
