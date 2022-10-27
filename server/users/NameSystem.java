package server.users;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;


public class NameSystem {
    public ConcurrentHashMap<String, ConnectedUser> activeConnections; // sessionID to users
    private final ConcurrentHashMap<String, String> usernamesToPasswords; // username to password - from file
    private final ConcurrentHashMap<String, String> usernamesToEmails;
    private File data;

    public NameSystem(){ // load all usernamesToPasswords
        activeConnections = new ConcurrentHashMap<>();
        usernamesToPasswords = new ConcurrentHashMap<>();
        usernamesToEmails = new ConcurrentHashMap<>();
        try {
            String dataFileLocation = "server\\users\\name_stock.csv";
            this.data = new File(dataFileLocation);
            data.createNewFile();
            BufferedReader readFromData = new BufferedReader(new FileReader(data));
            // Fill hash map so data will be in RAM
            String line; String[] temp;
            while((line = readFromData.readLine()) != null){
                temp = line.split(",");
                if(temp.length > 1) {
                    usernamesToPasswords.put(temp[0], temp[1]);
                    usernamesToEmails.put(temp[0], temp[2]);
                }
            }
            readFromData.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * if user signed up correctly return 1
    * if username already exist return 0
    * if any exception happens return -1
    * */
    public int signupNewUser(String username, String password, String email){

        if(usernamesToPasswords.containsKey(username) || usernamesToEmails.containsKey(username)) return 0;
        usernamesToPasswords.put(username, password);
        usernamesToEmails.put(username, email);
        PrintWriter writeToData = null; // this way new lines will append to the old ones
        try {
            writeToData = new PrintWriter(new FileWriter(data, true));
            writeToData.println(username + "," + password + "," + email + ",");
            writeToData.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }
    public String loginToServer(String username, String password, FullSocket fs){ // return a new session id
        if(!usernamesToPasswords.containsKey(username))
            return "not_exist";
        if(!usernamesToPasswords.get(username).equals(password))
            return "wrong_password";
        String sessionCookie = server.Utils.Generators.generateRandom();
        activeConnections.put(sessionCookie, new ConnectedUser(sessionCookie, username, fs));
        return sessionCookie;
    }
    public void disconnectFromActive(String sessionCookie){ // keeps account in all accounts data but remove from activeConnections
        try {
            activeConnections.remove(sessionCookie);
        }
        catch (Exception ignored){}
    }

}