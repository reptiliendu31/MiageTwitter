package bdd;

import bdd.objetBdd.MessageBDD;
import bdd.objetBdd.UserBDD;
import bdd.objetDao.MessageDAO;
import bdd.objetDao.UserDAO;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * Created by Stéfan on 09/11/2015.
 */
public class TestDB {
    public static void main(String[] args) throws ParseException {
        UserDAO userDAO = new UserDAO();
        MessageDAO msgDAO = new MessageDAO();

        // test messages
        Timestamp now = new Timestamp(System.currentTimeMillis());
        /*for(int i = 0; i < 15; i++){
            MessageBDD msg = new MessageBDD("test d'ajout message numéro " +i, 17, now);
            msg = msgDAO.create(msg);
            //UserBDD usr = new UserBDD("login"+i,"mdp"+i,"nom"+i,"fname"+i);
            //usr = userDAO.create(usr);
        }*/
        UserBDD usr = userDAO.findbyLogin("login1");
        UserBDD sub = userDAO.find(17);
        System.out.println(usr.toString());
        System.out.println(sub.toString());
        //usr = userDAO.addSub(sub,usr,now);
        //usr = userDAO.removeSub(usr,sub);

    }
}
