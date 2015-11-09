package bdd.objetDao;

import bdd.DAO;
import bdd.objetBdd.MessageBDD;
import bdd.objetBdd.UserBDD;
import user.User;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Stéfan on 08/11/2015.
 */
public class UserDAO extends DAO<UserBDD> {
    @Override
    public ArrayList<UserBDD> getInstances() {
        ArrayList<UserBDD> users = new ArrayList<UserBDD>();
        try {

            // récupération de la station
            ResultSet result = this.connect.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE).executeQuery(
                    "SELECT * FROM usertwitter");
            while (result.next()) {
                UserBDD s = this.find(result.getInt(1));
                users.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public UserBDD find(long id) {
       UserBDD userBDD = null;
       try {
            ResultSet result = this .connect
                    .createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ).executeQuery(
                            "SELECT * FROM UserTwitter WHERE idUser = " + id
                    );
            if(result.first()) {
                userBDD = new UserBDD(
                        result.getString("login"),
                        result.getString("password"),
                        result.getString("name"),
                        result.getString("firstName")
                );

                // récupération des messages liés à un user
                ResultSet resultMessage = this.connect.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE).executeQuery(
                        "SELECT * FROM message WHERE iduser = " + id
                );

                if (resultMessage.first()) {
                    // ajout de chaque vélo dans la station
                    DAO<MessageBDD> daoMessage = new MessageDAO();
                    userBDD.putMessage(daoMessage.find(resultMessage.getInt(1)));
                    while (resultMessage.next()) {
                        userBDD.putMessage(daoMessage.find(resultMessage.getInt(1)));
                    }
                }
/*
                // récupération des abonnés liés à un user
                ResultSet resultAbonne = this.connect.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE).executeQuery(
                        "SELECT * FROM subscription WHERE iduser = " + id
                );

                if (resultAbonne.first()) {
                    //
                    DAO<UserBDD> daoUser = new UserDAO();
                    userBDD.putSub(daoUser.find(resultMessage.getInt(1)));
                    while (resultMessage.next()) {
                        userBDD.putSub(daoUser.find(resultMessage.getInt(1)));
                    }
                }*/
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userBDD;

    }

    @Override
    public UserBDD create(UserBDD obj) {
        try {
            // insertion de l'objet
            PreparedStatement prepare =
                    this.connect.prepareStatement(
                            "INSERT INTO UserTwitter (password, name, firstName, login) VALUES (?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
            prepare.setString(1, obj.getPassword());
            prepare.setString(2, obj.getName());
            prepare.setString(3, obj.getFirstName());
            prepare.setString(4, obj.getLogin());
            prepare.executeUpdate();
            // récupération des valeurs de l'insert
            ResultSet rs = prepare.getGeneratedKeys();
            rs.next();
            //return find(rs.getInt(5));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public UserBDD update(UserBDD obj) {
        try {
            this.connect
                    .createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ).executeUpdate(
                    "UPDATE usertwitter SET login = '" + obj.getLogin() + "',"+
                            " password = '" + obj.getPassword() + "'" +
                            " name = '" + obj.getName() + "'" +
                            " firstname = '" + obj.getFirstName() + "'" +
                            " WHERE iduser = " + obj.getId()
            );
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(UserBDD obj) {
    }

    // faire une méthode addAbonne / removeAbonne / sendMessage
    public UserBDD addSub(UserBDD sub, UserBDD user, Date datesub) {
        try {
            // maj table posseder
            PreparedStatement prepare =
                    this.connect.prepareStatement(
                            "INSERT INTO subscription (subscriber, iduser, datesubscription ) VALUES(?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
            prepare.setInt(1, sub.getId());
            prepare.setInt(2, sub.getId());
            prepare.setDate(3, datesub);
            prepare.executeUpdate();

            sub = this.find(sub.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sub;
    }

    public UserBDD removeSub(UserBDD user, UserBDD sub) {
        try {
            this.connect
                    .createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ).executeUpdate(
                    " DELETE FROM subscription WHERE iduser = " + user.getId() + " AND subscriber = " + sub.getId()
            );
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
