package bdd.objetBdd;

import java.io.Serializable;

/**
 * Created by Stéfan on 08/11/2015.
 */
public class User implements Serializable {
    private String login,password,name,firstName;

    public User(String login, String password, String name, String firstName) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.firstName = firstName;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", firstName='" + firstName + '\'' +
                '}';
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
}
