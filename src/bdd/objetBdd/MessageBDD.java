package bdd.objetBdd;

import user.UserConnection;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Stéfan on 09/11/2015.
 */
public class MessageBDD {
    private String content;
    private int idUser, idMessage;
    private Timestamp date;

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

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

    public MessageBDD(String c, int u, Timestamp d){
        content=c;
        idUser=u;
        date=d;
    }

    public static ArrayList<MessageBDD> testM(){
        ArrayList<MessageBDD> l=new ArrayList<MessageBDD>();

        l.add(new MessageBDD("LOL1",1,null));
        l.add(new MessageBDD("LOL2",1,null));
        l.add(new MessageBDD("LOL3",1,null));

        return l;
    }

    @Override
    public String toString() {
        return "MessageBDD{" +
                "content='" + content + '\'' +
                ", idUser=" + idUser +
                ", idMessage=" + idMessage +
                ", date=" + date +
                '}';
    }
}
