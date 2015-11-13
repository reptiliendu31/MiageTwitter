package bdd.objetBdd;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Stéfan on 09/11/2015.
 */
public class MessageBDD {
    private String content;
    private int idMessage;
    private UserBDD user;
    private Timestamp date;

    public UserBDD getUser() { return user;};

    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public MessageBDD(String c, UserBDD u, Timestamp d){
        content=c;
        user=u;
        date=d;
    }

    public static ArrayList<MessageBDD> testM(){
        ArrayList<MessageBDD> l=new ArrayList<MessageBDD>();

        l.add(new MessageBDD("LOL1",null,null));

        return l;
    }

    @Override
    public String toString() {
        return "MessageBDD{" +
                "content='" + content + '\'' +
                ", idUser=" + user.getId() +
                ", idMessage=" + idMessage +
                ", date=" + date +
                '}';
    }
}
