package server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

//TODO SPECIFY NAMES SYSTEM NEEDS

public class NameSystem {
    private PrintWriter writeToData;
    private BufferedReader readFromData;
    private final ConcurrentHashMap<String, String> namesToPasswords;

    public NameSystem(){
        namesToPasswords = new ConcurrentHashMap<>();
        try {
            String dataFileLocation = "src\\server\\name_stock.csv";
            File data = new File(dataFileLocation);
            writeToData = new PrintWriter(data);
            readFromData = new BufferedReader(new FileReader(data));
            String line; String[] temp;
            while((line = readFromData.readLine()) != null){
                temp = line.split(",");
                if(temp.length > 1)
                    namesToPasswords.put(temp[0], temp[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public String addUser(String username, String password){
        if(isUsernameExist(username)) return "ALREADY_EXIST";
        else if(username.length() < 4) return "USERNAME_SHORT";
        else{
            createUser(username, password);
            return "DONE";
        }
    }

    public String removeUser(String username, String password){


        return null;
    }

}