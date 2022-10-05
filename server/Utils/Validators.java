package server.Utils;

public class Validators {
    public static boolean passwordValidate(String password){
        if(password.length()<3) return false;
        //TODO: ADD SEARCH IN COMMON PASSWORDS FILE
        return true;
    }

    public static int isUsernameLegal(String username){
        if(username == null) return 1;
        if(username.length() > 2) return 2;
        return 0;  // Success
    }
}
