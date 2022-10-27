package server.Utils;

public class Validators {
    public static boolean passwordValidate(String password){
        if(password == null) return false;
        if(password.length()<3) return false;
        return true;
    }

    public static boolean emailValidate(String email){
        if(!email.contains("@")) return false;
        String username = email.split("@")[0];
        if(!isUsernameLegal(username)) return false;
        String domain = email.split("@", 2)[1];
        if(!domain.contains(".")) return false;
        if(!isUsernameLegal(domain.split(".")[0])) return false;
        return true;
    }

    public static boolean isUsernameLegal(String username){
        if(username == null) return false;
        if(username.length() < 3) return false;
        String legal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ023456789";
        for(int i = 0; i < username.length(); i++){
            if(!legal.contains(username.substring(i, i+1))) return false;
        }
        return true;
    }
}
