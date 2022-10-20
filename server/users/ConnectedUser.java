package server.users;

public class ConnectedUser {
    public String status;
    public String sessionCookie;
    public String username;
    public FullSocket fs;

    public ConnectedUser(String sessionCookie, String username, FullSocket fs) {
        this.status = "available";
        this.sessionCookie = sessionCookie;
        this.username = username;
        this.fs = fs;
    }

    public void sendMsg(String text){
        fs.out.println("/print " + text);
    }

}