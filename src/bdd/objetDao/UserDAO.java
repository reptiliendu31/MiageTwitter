package bdd.objetDao;

import bdd.DAO;
import bdd.objetBdd.MessageBDD;
import bdd.objetBdd.UserBDD;

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
                        result.getString("firstName"),
                        result.getString("localisation")
                );
                userBDD.setId(result.getInt("iduser"));

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

                // récupération des abonnés liés à un user
                ResultSet resultAbonne = this.connect.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE).executeQuery(
                        "SELECT * FROM subscription WHERE iduser = " + id
                );

                if (resultAbonne.first()) {
                    //
                    DAO<UserBDD> daoUser = new UserDAO();
                    userBDD.putSub(daoUser.find(resultAbonne.getInt(2)));
                    while (resultAbonne.next()) {
                        userBDD.putSub(daoUser.find(resultAbonne.getInt(2)));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userBDD;
    }

    public UserBDD findbyLogin(String log) {
        UserBDD userBDD = null;
        try {
            ResultSet result = this .connect
                    .createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ).executeQuery(
                            "SELECT * FROM UserTwitter WHERE login = '" + log +"'"
                    );
            if(result.first()) {
                userBDD = find(result.getInt("iduser"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userBDD;
    }

    public ArrayList<String> findSearch(String search) {
        ArrayList<String> res = new ArrayList<>();
        try {
            ResultSet result = this .connect
                    .createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ).executeQuery(
                            "SELECT * FROM UserTwitter WHERE login like '%"+ search+"%'" +
                                    "OR name like '%"+ search+"%'" +
                                    "OR firstname like '%"+ search+"%'" +
                                    "OR localisation like '%"+ search+"%'"
                    );
            while(result.next()){
                res.add(result.getString("login"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean checkLogin(String log) {
        UserBDD userBDD = null;
        boolean res= false;
        try {
            ResultSet result = this .connect
                    .createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ).executeQuery(
                            "SELECT * FROM UserTwitter WHERE login = '" + log +"'"
                    );
            if(result.first()) {
                res = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public UserBDD create(UserBDD obj) {
        try {
            // insertion de l'objet
            PreparedStatement prepare =
                    this.connect.prepareStatement(
                            "INSERT INTO UserTwitter (password, name, firstName, login, localisation) VALUES (?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
            prepare.setString(1, obj.getPassword());
            prepare.setString(2, obj.getName());
            prepare.setString(3, obj.getFirstName());
            prepare.setString(4, obj.getLogin());
            prepare.setString(5, obj.getLocalisation());
            prepare.executeUpdate();
            // récupération des valeurs de l'insert
            ResultSet rs = prepare.getGeneratedKeys();
            rs.next();
            return find(rs.getInt(1));
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
                            " password = '" + obj.getPassword() + "'," +
                            " name = '" + obj.getName() + "'," +
                            " firstname = '" + obj.getFirstName() + "'," +
                            " localisation = '" + obj.getLocalisation() + "'" +
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
    public UserBDD addSub(UserBDD sub, UserBDD user, Timestamp datesub) {
        try {
            // maj table posseder
            PreparedStatement prepare =
                    this.connect.prepareStatement(
                            "INSERT INTO subscription (subscriber, iduser, datesubscription ) VALUES(?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
            prepare.setInt(1, sub.getId());
            prepare.setInt(2, user.getId());
            prepare.setTimestamp(3, datesub);
            prepare.executeUpdate();

            sub = this.find(sub.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    // faire une méthode addAbonne / removeAbonne / sendMessage
    public boolean checkSub(UserBDD sub, UserBDD user) {

            UserBDD userBDD = null;
            int idSub = sub.getId();
            int idUser = user.getId();
            boolean res = false;
            try {
                ResultSet result = this .connect
                        .createStatement(
                                ResultSet.TYPE_SCROLL_INSENSITIVE,
                                ResultSet.CONCUR_UPDATABLE
                        ).executeQuery(
                                "SELECT * FROM subscription WHERE subscriber = " + idSub + " AND iduser = " + idUser
                        );
                if(result.first()){
                    res = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return res;
    }

    public void removeSub(UserBDD sub, UserBDD user) {
        try {

            this.connect.createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ).executeUpdate(
                    " DELETE FROM subscription WHERE iduser = " + user.getId() + " AND subscriber = " + sub.getId()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
