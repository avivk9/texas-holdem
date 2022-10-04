package server.users;

import server.FullSocket;

public class ConnectedUser {
    public String sessionID;
    public String username;
    public FullSocket fs;
    public ConnectedUser(String sessionID, String username, FullSocket fs){
        this.sessionID=sessionID;
        this.username=username;
        this.fs = fs;
    }

}
