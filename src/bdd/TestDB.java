package bdd;

import bdd.objetBdd.MessageBDD;
import bdd.objetBdd.UserBDD;
import bdd.objetDao.MessageDAO;
import bdd.objetDao.UserDAO;

import java.sql.Date;
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
        String strDate = "2015-11-09 15:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        java.util.Date date = sdf.parse(strDate);
        Date aujourdhui = new Date(date.getTime());

        for(int i = 0; i < 15; i++){
            //MessageBDD msg = new MessageBDD("test d'ajout message numéro " +i, 15, aujourdhui);
            //msg = msgDAO.create(msg);
            //UserBDD usr = new UserBDD("login"+i,"mdp"+i,"nom"+i,"fname"+i);
            //usr = userDAO.create(usr);
        }
        UserBDD usr = userDAO.find(12);
        UserBDD sub = userDAO.find(15);
        System.out.println(usr.toString());
        System.out.println(sub.toString());

        usr = userDAO.addSub(usr,sub,aujourdhui);
    }
}
