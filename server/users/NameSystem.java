package server.users;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

//TODO SPECIFY NAMES SYSTEM NEEDS

public class NameSystem {
    public ConcurrentHashMap<String, ConnectedUser> activeConnections; // sessionID to users
    private final ConcurrentHashMap<String, String> namesToPasswords; // name to password - from file
    private PrintWriter writeToData;
    private BufferedReader readFromData;

    public NameSystem(){
        activeConnections = new ConcurrentHashMap<>();
        namesToPasswords = new ConcurrentHashMap<>();
        try {
            String dataFileLocation = "src\\server\\name_stock.csv";
            File data = new File(dataFileLocation);
            writeToData = new PrintWriter(new FileWriter(data, true)); // this way new lines will append to the old ones
            readFromData = new BufferedReader(new FileReader(data));

            // Fill hash map so data will be in RAM
            String line; String[] temp;
            while((line = readFromData.readLine()) != null){
                temp = line.split(",");
                if(temp.length > 1)
                    namesToPasswords.put(temp[0], temp[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void finalize(){
        writeToData.close();
        try {
            readFromData.close();
        } catch (IOException ignored){}
    }

    private void createUser(String username, String password){
        namesToPasswords.put(username, password);
        writeToData.println(username + "," + password);
    }

    public boolean isUsernameExist(String username){
        return namesToPasswords.containsKey(username);
    }

    public void disconnectFromActive(String sessionID){ // keeps account in all accounts data but remove from activeConnections
        activeConnections.remove(sessionID);
    }

    public boolean checkPassword(String username, String password) {
        if(namesToPasswords.containsKey(username)){ // loaded to RAM already
            return password == namesToPasswords.get(username);
        }
        return false;
    }

    public void addActive(String clientSessionID, ConnectedUser connectedUser) {
        activeConnections.put(clientSessionID, connectedUser);
    }
}