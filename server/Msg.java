package server;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Msg implements Comparable {
    String content;
    String sender;
    Date time;
    public Msg(String content, String sender, Date time)
    {
        this.time=time;
        this.content=content;
        this.sender=sender;
    }
    @Override public String toString(){
        return "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time) + "] (" + sender + ") " + content;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
