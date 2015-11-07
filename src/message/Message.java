package message;

/**
 * Created by Yoan on 07/11/2015.
 */
import user.User;

import java.sql.Date;
import java.util.ArrayList;

public class Message{
    private String content;
    private User user;
    private Date date;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Message(String c, User u, Date d){
        content=c;
        user=u;
        date=d;

    }

    public static ArrayList<Message> testM(){
        ArrayList<Message> l=new ArrayList<Message>();

        l.add(new Message("LOL1",null,null));
        l.add(new Message("LOL2",null,null));
        l.add(new Message("LOL3",null,null));

        return l;
    }
}