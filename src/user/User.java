package user;

import message.Message;

import java.util.ArrayList;

/**
 * Created by Yoan on 07/11/2015.
 */
public class User {
    private String name;
    private String firstName;
    private String login;
    private String password;
    private ArrayList<Message> messages;
    private ArrayList<User> abonnements;

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public ArrayList<User> getAbonnements() {
        return abonnements;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public User(String name, String firstName, String login, String password) {
        this.name = name;
        this.firstName = firstName;
        this.login = login;
        this.password = password;
        messages = new ArrayList<Message>();
        abonnements = new ArrayList<User>();
    }
}
