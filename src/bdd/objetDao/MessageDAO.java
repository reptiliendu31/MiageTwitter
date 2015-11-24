package bdd.objetDao;

import bdd.DAO;

import bdd.objetBdd.MessageBDD;
import bdd.objetBdd.UserBDD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Stéfan on 09/11/2015.
 */
public class MessageDAO extends DAO<MessageBDD>{
    @Override
    public ArrayList<MessageBDD> getInstances() {
        ArrayList<MessageBDD> msg = new ArrayList<MessageBDD>();
        try {

            // récupération de la station
            ResultSet result = this.connect.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE).executeQuery(
                    "SELECT * FROM message");
            while (result.next()) {
                MessageBDD s = this.find(result.getInt(1));
                msg.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    public MessageBDD find(long id) {
        MessageBDD mssBDD = null;
        try {
            ResultSet result = this .connect
                    .createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ).executeQuery(
                            "SELECT * FROM message WHERE idmessage = " + id
                    );
            if(result.first()) {
                mssBDD = new MessageBDD(
                        result.getString("content"),
                        result.getInt("iduser"),
                        result.getTimestamp("datepublication"),
                        result.getString("localization")
                );
                mssBDD.setIdMessage(result.getInt("idmessage"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mssBDD;
    }

    @Override
    public MessageBDD create(MessageBDD obj) {
        try {
            // insertion de l'objet
            PreparedStatement prepare =
                    this.connect.prepareStatement(
                            "INSERT INTO message (content, iduser,datepublication,localisation) VALUES (?, ?, ?,?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
            prepare.setString(1, obj.getContent());
            prepare.setInt(2, obj.getIdUser());
            prepare.setTimestamp(3,obj.getDate());
            prepare.setString(4,obj.getLocalization());
            prepare.executeUpdate();
            // récupération des valeurs de l'insert
            ResultSet rs = prepare.getGeneratedKeys();
            rs.next();
            return find(rs.getInt(3));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public MessageBDD update(MessageBDD obj) {
        try {
            this.connect
                    .createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ).executeUpdate(
                    "UPDATE message SET content = '" + obj.getContent() + "',"+
                            " iduser = '" + obj.getIdUser() + "'" + "',"+
                            " localization = '" + obj.getLocalization() + "'" +
                            " WHERE idmessage = " + obj.getIdMessage()
            );
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(MessageBDD obj) {

    }
}
