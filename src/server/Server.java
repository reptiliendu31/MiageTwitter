package server;

import javax.jws.soap.SOAPBinding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import javax.jms.*;

import bdd.objetBdd.MessageBDD;
import bdd.objetBdd.UserBDD;
import bdd.objetDao.MessageDAO;
import bdd.objetDao.UserDAO;

/**
 * Created by david on 06/11/2015.
 */
public class Server {
    private Context context ;
    private ConnectionFactory factory;
    private Connection connection;
    private String factoryName = "ConnectionFactory";
    private String topicMessages = "Messages";
    private String twitterQueue = "twitter";
    private Destination destMessages;
    private Destination destTwitter;
    private Session session ;
    private MessageProducer sender;
    private MessageConsumer receiver;
    private HashMap<Integer, MessageProducer> tempQueues;
    private static int nbTempQueues = 1;
    private HashMap<String, Boolean> connectedUsers;


    public static void main(String[] args) {
        Server s = new Server();
        System.out.println("Server launched !");
        System.out.println("Press enter to end process...");
        BufferedReader waiter = new BufferedReader(new InputStreamReader(System.in));
        try {
            waiter.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Server() {

        try {
            tempQueues = new HashMap<Integer, MessageProducer>();

            connectedUsers = new HashMap<String, Boolean>();

            // on peuple tous les users pas connectés
            UserDAO userDAO = new UserDAO();
            for (UserBDD u : userDAO.getInstances()
                 ) {
                connectedUsers.put(u.getLogin(),false);
            }

            // create the JNDI initial context.
            context = new InitialContext();

            // look up the ConnectionFactory
            factory = (ConnectionFactory) context.lookup(factoryName);

            // look up the Destination
            destMessages = (Destination) context.lookup(topicMessages);
            destTwitter = (Destination) context.lookup(twitterQueue);

            // create the respConnection
            connection = factory.createConnection();

            // create the session
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);

            // create the sender
            sender = session.createProducer(destMessages);

            receiver = session.createConsumer(destTwitter);

            // register a listener
            receiver.setMessageListener(new TwitterQueueListener(this));

            // start the respConnection, to enable message sends
            connection.start();
            BufferedReader waiter = new BufferedReader(new InputStreamReader(System.in));
            waiter.readLine();


        } catch (JMSException exception) {
            exception.printStackTrace();
        } catch (NamingException exception) {
            exception.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close the context
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException exception) {
                    exception.printStackTrace();
                }
            }

            // close the respConnection
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public void respInitTempQueue(Message req) {
        try {
            // getting temp queue destination
            Destination tempo = req.getJMSReplyTo();
            MessageProducer mp = null;
            mp = session.createProducer(tempo);
            // stockage producteur et id user
            tempQueues.put(nbTempQueues,mp);
            // envoi ack à l'user avec son numéro de queue
            StreamMessage rep = session.createStreamMessage();
            rep.clearBody();
            rep.writeInt(nbTempQueues);
            rep.setJMSType("RespInitTempQueue");
            mp.send(rep);
            nbTempQueues++;
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void respSignIn(int idClient, String login, String password, String name, String firstName, String localisation) {
        UserDAO usr = new UserDAO();
        try {
            // check if user already exist
            if (!usr.checkLogin(login)) {
                UserBDD user = new UserBDD(login, password, name, firstName, localisation);
                user = usr.create(user);
                // ajout de l'user à la hashmap des users
                connectedUsers.put(user.getLogin(),false);
                // getting temp queue destination
                MessageProducer mp = tempQueues.get(idClient);
                StreamMessage rep = session.createStreamMessage();
                rep.clearBody();
                rep.writeBoolean(true);
                rep.setJMSType("RespSignIn");
                mp.send(rep);
            }else{
                System.out.println("user already exist");
                // getting temp queue destination
                MessageProducer mp = tempQueues.get(idClient);
                StreamMessage rep = session.createStreamMessage();
                rep.clearBody();
                rep.writeBoolean(false);
                rep.setJMSType("RespSignIn");
                mp.send(rep);
            }
        }catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public void respSignOut(int idClient, String login){
        UserDAO usr = new UserDAO();
        UserBDD user = usr.findbyLogin(login);
        boolean isUser = (user != null);
        if(isUser){
            try {
                // si l'utilisateur est connecté
                if(isUserConnected(user)){
                    // on le déconnecte
                    disconnectUserFromServer(user);
                    System.out.println("user signed out");
                    // getting temp queue destination
                    MessageProducer mp = tempQueues.get(idClient);
                    StreamMessage rep = null;
                    rep = session.createStreamMessage();
                    rep.clearBody();
                    rep.writeBoolean(isUser);
                    rep.setJMSType("RespSignOut");
                    mp.send(rep);
                }else{
                    // getting temp queue destination
                    MessageProducer mp = tempQueues.get(idClient);
                    StreamMessage rep = null;
                    rep = session.createStreamMessage();
                    rep.clearBody();
                    rep.writeBoolean(false);
                    rep.setJMSType("RespSignOut");
                    mp.send(rep);
                }
            }catch (JMSException e) {
                e.printStackTrace();
            }
        }
        else{
            // envoi mess erreur
        }
    }

    // respConnection method
    // checks if user exists, and sends result to client
    public void respConnection(int idClient, String login, String password){
        try {
            UserDAO usr = new UserDAO();
            UserBDD user = usr.findbyLogin(login);
            String errorConnection="";
            boolean isUser = (user != null);
            if(isUser){
                if(user.getPassword().equals(password)){
                     if(!isUserConnected(user)){
                        connectUserToServer(user);
                        System.out.println("User connected" + connectedUsers.get(user.getLogin()));
                     }else{
                        isUser = false;
                        errorConnection = "Utilisateur déjà connecté";
                    }
                }else{
                    isUser = false;
                    errorConnection = "Mauvais password";
                }
                // getting temp queue destination
                MessageProducer mp = tempQueues.get(idClient);
                ObjectMessage rep = session.createObjectMessage();
                rep.clearBody();
                rep.setObject(user);
                rep.setJMSType("RespConnection");
                mp.send(rep);
            } else {
                errorConnection = "Mauvais nom d'utilisateur";
                // getting temp queue destination
                MessageProducer mp = tempQueues.get(idClient);
                StreamMessage rep = session.createStreamMessage();
                rep.clearBody();
                rep.writeString(errorConnection);
                rep.setJMSType("RespConnection");
                mp.send(rep);
            }
            if(isUser){
            }else{
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    // respConnection method
    // checks if user didn't already follow the user, follow user if not and sends result to client
    public void respFollow(int idClient, String loginUser, String loginSub){
        try {
            UserDAO usr = new UserDAO();
            UserDAO usrF = new UserDAO();
            UserBDD user = usr.findbyLogin(loginUser);
            UserBDD userF = usrF.findbyLogin(loginSub);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            String resp;
            boolean isUser = (user != null && userF != null);
            if(isUser){
                    if(!usr.checkSub(userF,user)){
                        user = usr.addSub(userF,user,now);
                        isUser = (user != null);
                        if(isUser){
                            System.out.println("User follow " + userF.getLogin());
                        }
                    }else{
                        isUser = false;
                        System.out.println("User already followed " + userF.getLogin());
                    }
                // getting temp queue destination
                    MessageProducer mp = tempQueues.get(idClient);
                    // send the user
                    ObjectMessage rep = session.createObjectMessage();
                    //StreamMessage rep = session.createStreamMessage();
                    rep.clearBody();
                    rep.setObject(userF);
                    rep.setJMSType("RespFollow");
                    mp.send(rep);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    // respConnection method
    // checks if user unfollow, and sends result to client
    public void respUnFollow(int idClient, String loginUser, String loginSub){
        try {
            UserDAO usr = new UserDAO();
            UserDAO usrF = new UserDAO();
            UserBDD user = usr.findbyLogin(loginUser);
            UserBDD userF = usrF.findbyLogin(loginSub);
            boolean isUser = (user != null && userF != null);
            if(isUser){
                if(usr.checkSub(userF,user)){
                    usr.removeSub(userF, user);
                    // check if deleted
                    if(!usr.checkSub(userF,user)){
                        System.out.println("User UnFollow " + userF.getLogin());
                    }else{
                        System.out.println("User not UnFollowed " + userF.getLogin());
                    }
                }else{
                    isUser = false;
                    System.out.println("User already UnFollowed " + userF.getLogin());
                }
            }else{
                System.out.println("Sub Unknow " + loginSub);
            }
            // getting temp queue destination
            MessageProducer mp = tempQueues.get(idClient);
            StreamMessage rep = session.createStreamMessage();
            rep.clearBody();
            rep.writeBoolean(isUser);
            rep.setJMSType("RespUnFollow");
            mp.send(rep);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    // respSearch method
    // checks if  found users, and send list of user as result to client
    public void respSearch(int idClient, String search){
        try {
            UserDAO usr = new UserDAO();
            ArrayList<UserBDD> listLoginUser = usr.findSearch(search);
            boolean isListUser = (listLoginUser.isEmpty());
            System.out.println("List not Empty and sent");
            if(!isListUser){
                System.out.println("List not Empty and sent");
                // send list of logins
                // getting temp queue destination
                MessageProducer mp = tempQueues.get(idClient);
                ObjectMessage rep = session.createObjectMessage();
                rep.clearBody();
                rep.setObject(listLoginUser);
                rep.setJMSType("RespSearch");
                mp.send(rep);
            }else{
                System.out.println("List Empty");
                // getting temp queue destination
                MessageProducer mp = tempQueues.get(idClient);
                StreamMessage rep = session.createStreamMessage();
                rep.clearBody();
                rep.writeBoolean(isListUser);
                rep.setJMSType("RespSearch");
                mp.send(rep);
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Traitement d'un changement de localisation chez un user
     * @param idClient
     * @param loc
     */
    public void respLoc(int idClient, String loginUser, String loc) {
        UserDAO u = new UserDAO();
        UserBDD user = u.findbyLogin(loginUser);
        user.setLocalisation(loc);
        user = u.update(user);
        System.out.println(("Received loc request : " + idClient + " > " + loc));

        try {
            // ack for user
            MessageProducer mp = tempQueues.get(idClient);
            StreamMessage rep = session.createStreamMessage();
            rep.clearBody();
            rep.writeBoolean((u != null));
            System.out.println("Sent " + (u != null));
            rep.setJMSType("RespLocalisation");
            mp.send(rep);

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }


    /**
     * Traitement d'un tweet reçu dans la file, enregistrement en bdd + publication sur le topic + envoi vers user (ack)
     * @param idClient
     * @param usercourant
     * @param tweet
     */
    public void respTweet(int idClient, String usercourant, String tweet,long time) {
        UserDAO u = new UserDAO();
        Timestamp t = new Timestamp(time);
        UserBDD user = u.findbyLogin(usercourant);
        MessageBDD m = new MessageBDD(tweet,user.getId(),t,user.getLocalisation());
        MessageDAO mess = new MessageDAO();
        //adding tweet in db
        mess.create(m);
        System.out.println("received tweet");
        //sending to topic

        try {
            // to topic
            StreamMessage req = session.createStreamMessage();
            req.clearBody();
            req.writeInt(m.getIdMessage());
            req.writeString(m.getContent());
            req.writeInt(m.getIdUser());
            req.writeLong(m.getDate().getTime());
            req.writeString(m.getLocalization());



            req.writeString(user.getLogin());
            req.writeString(user.getName());
            req.writeString(user.getFirstName());

            req.setJMSType("Tweet");
            req.setStringProperty("follow", usercourant);
            req.setStringProperty("ville",user.getLocalisation());
            sender.send(req);

            // ack for user
            MessageProducer mp = tempQueues.get(idClient);
            StreamMessage rep = session.createStreamMessage();
            rep.clearBody();
            rep.writeBoolean(true);
            rep.setJMSType("RespTweet");
            mp.send(rep);

        } catch (JMSException e) {
            e.printStackTrace();
        }

        System.out.println("tweet sent to users");

    }

    // renvoie si le user est connecté au serveur
    public boolean isUserConnected(UserBDD u) {
        return connectedUsers.get(u.getLogin());
    }

    public boolean connectUserToServer(UserBDD u) {
        return connectedUsers.put(u.getLogin(),true);
    }

    public boolean disconnectUserFromServer(UserBDD u) {
        return connectedUsers.put(u.getLogin(),false);
    }

}