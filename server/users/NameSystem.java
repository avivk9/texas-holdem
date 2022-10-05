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
    private File data;

    public NameSystem(){
        activeConnections = new ConcurrentHashMap<>();
        namesToPasswords = new ConcurrentHashMap<>();
        try {
            String dataFileLocation = "server\\users\\name_stock.csv";
            this.data = new File(dataFileLocation);
            data.createNewFile();
            BufferedReader readFromData = new BufferedReader(new FileReader(data));
            // Fill hash map so data will be in RAM
            String line; String[] temp;
            while((line = readFromData.readLine()) != null){
                temp = line.split(",");
                if(temp.length > 1)
                    namesToPasswords.put(temp[0], temp[1]);
            }
            readFromData.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void createUser(String username, String password){
        namesToPasswords.put(username, password);
        PrintWriter writeToData = null; // this way new lines will append to the old ones
        try {
            writeToData = new PrintWriter(new FileWriter(data, true));
            writeToData.println(username + "," + password + ",");
            writeToData.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isUsernameExist(String username){
        return namesToPasswords.containsKey(username);
    }

    public void disconnectFromActive(String sessionID){ // keeps account in all accounts data but remove from activeConnections
        activeConnections.remove(sessionID);
    }

    public boolean checkPassword(String username, String password) { // checks if password is right
        if(namesToPasswords.containsKey(username)){ // loaded to RAM already
            return password == namesToPasswords.get(username);
        }
        return false;
    }

    public void addActive(String clientSessionID, ConnectedUser connectedUser) {
        activeConnections.put(clientSessionID, connectedUser);
    }
}