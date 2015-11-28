package bdd.objetBdd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Stéfan on 08/11/2015.
 */
public class UserBDD implements Serializable {
    private String login,password,name,firstName,localisation;
    private ArrayList<MessageBDD> messages;
    private ArrayList<UserBDD> abonnements;
    private int id;

    private static final long serialVersionUID = 6529685098267757690L;

    public UserBDD(String login, String password, String name, String firstName, String localisation) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.firstName = firstName;
        this.abonnements = new ArrayList<UserBDD>();
        this.messages = new ArrayList<MessageBDD>();
        this.localisation = localisation;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public String toString() {
        String s = "UserBDD{" +
                "id = '" + id + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", firstName='" + firstName + '\'' +
                '}';

        for (MessageBDD msg : messages) {
            s+= "\n message - " + msg.toString();
        }
        for (UserBDD usr : abonnements) {
            s+= "\n user - " + usr.getLogin();
        }

        return s;
    }

    public ArrayList<UserBDD> getAbonnements() {
        return abonnements;
    }


    public void removeAbonnement(String login) {
        Iterator<UserBDD> iter = abonnements.iterator();
        while(iter.hasNext()) {
            UserBDD user = iter.next();
            if (user.getLogin() == login) {
                abonnements.remove(user);
            }
        }
    }

    public ArrayList<MessageBDD> getMessages() {
        return messages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public boolean putMessage(MessageBDD m){
        return messages.add(m);
    }

    public boolean putSub(UserBDD s){
        return abonnements.add(s);
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }
}
