package server.users;

import server.FullSocket;
import server.Server;
import server.Utils.Generators;
import server.Utils.Validators;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ClientLogin {
    // RETURN SESSION ID
    public static String Login(FullSocket fullSocket, NameSystem ns){
        int EXIT_CODE;
        String username = requestUsername(fullSocket);
        while (!((EXIT_CODE = Validators.isUsernameLegal(username)) == 0)){
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
        while (!ns.checkPassword(username, password)){
            fullSocket.out.println("FAIL - password not correct");
        }
        String clientSessionID = Generators.generateRandom();
        ns.addActive(clientSessionID, new ConnectedUser(clientSessionID, username, fullSocket));
        return clientSessionID;
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
        String password = requestPassword(fullSocket,username, ns);
        while(!Validators.passwordValidate(password)){
            fullSocket.out.println("FAIL - password not good");
            password = requestPassword(fullSocket, username, ns);
        }
        ns.createUser(username, password);
        // ACCOUNT CREATED
        fullSocket.out.println("SUCCESS - account created");
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
}