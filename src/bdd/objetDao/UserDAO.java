package bdd.objetDao;

import bdd.DAO;
import bdd.objetBdd.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Stéfan on 08/11/2015.
 */
public class UserDAO extends DAO<User> {
    @Override
    public ArrayList<User> getInstances() {
        return null;
    }

    @Override
    public User find(long id) {
        return null;
    }

    @Override
    public User create(User obj) {
        try {
            // insertion de l'objet
            PreparedStatement prepare =
                    this.connect.prepareStatement(
                            "INSERT INTO USER (login, password, name, firstName) VALUES (?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
            prepare.setString(1, obj.getLogin());
            prepare.setString(2, obj.getPassword());
            prepare.setString(3, obj.getName());
            prepare.setString(4, obj.getFirstName());
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
    public User update(User obj) {
        return null;
    }

    @Override
    public void delete(User obj) {

    }
}
