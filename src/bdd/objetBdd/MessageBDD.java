package bdd.objetBdd;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Stéfan on 09/11/2015.
 */
public class MessageBDD {
    private String content,localization;
    private int idMessage;
    private int user;
    private Timestamp date;

    public int getIdUser() { return user;};

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

    public MessageBDD(String content,int user,  Timestamp date, String localization) {
        this.content = content;
        this.localization = localization;
        this.user = user;
        this.date = date;
    }

    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }

    public static ArrayList<MessageBDD> testServ(){
        ArrayList<MessageBDD> l=new ArrayList<MessageBDD>();

        l.add(new MessageBDD("server",1,new Timestamp(1),"toulouse"));

        return l;
    }

    public static ArrayList<MessageBDD> testUser(){
        ArrayList<MessageBDD> l=new ArrayList<MessageBDD>();

        l.add(new MessageBDD("user",1,new Timestamp(1),"toulouse"));

        return l;
    }


    @Override
    public String toString() {
        return "MessageBDD{" +
                "content='" + content + '\'' +
                ", idUser=" + user +
                ", idMessage=" + idMessage +
                ", date=" + date +
                '}';
    }
}
