package server;

import javax.jws.soap.SOAPBinding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import javax.jms.*;

import bdd.objetBdd.MessageBDD;
import bdd.objetBdd.UserBDD;
import bdd.objetDao.UserDAO;

/**
 * Created by david on 06/11/2015.
 */
public class Server {
    private Context context = null;
    private ConnectionFactory factory = null;
    private Connection connection = null;
    private String factoryName = "ConnectionFactory";
    private String topicMessages = "Messages";
    private String twitterQueue = "twitter";
    private Destination destMessages = null;
    private Destination destTwitter = null;
    private Session session ;
    private MessageProducer sender = null;
    private MessageConsumer receiver = null;
    private HashMap<Integer, MessageProducer> tempQueues;
    private static int nbTempQueues = 1;
    private HashMap<UserBDD, Boolean> connectedUsers;


    public static void main(String[] args) {
        Server s = new Server();

    }

    public Server() {

        try {
            tempQueues = new HashMap<Integer, MessageProducer>();

            connectedUsers = new HashMap<UserBDD, Boolean>();
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
            System.out.println("Waiting for messages...");
            BufferedReader waiter = new BufferedReader(new InputStreamReader(System.in));
            waiter.readLine();
            System.out.println("sending...");
            ArrayList<MessageBDD> l = MessageBDD.testServ();
            for (MessageBDD m : l) {
                TextMessage message = session.createTextMessage();
                message.setText(m.getContent());
                sender.send(message);
                System.out.println("Sent: " + message.getText());
            }

            System.out.println("Waiting for messages...");
            waiter = new BufferedReader(new InputStreamReader(System.in));
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
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void respSignIn(int idClient, String login, String password, String name, String firstName){
        UserDAO usr = new UserDAO();
        UserBDD user = new UserBDD(login,password,name,firstName);
        user = usr.create(user);
        boolean isUser = (user != null);
        if(isUser){
            try {
                System.out.println("user created");
                // getting temp queue destination
                MessageProducer mp = tempQueues.get(idClient);
                StreamMessage rep = null;
                rep = session.createStreamMessage();
                rep.clearBody();
                rep.writeBoolean(isUser);
                rep.setJMSType("RespSignIn");
                mp.send(rep);
            }catch (JMSException e) {
                e.printStackTrace();
            }
        }
        else{
            // envoi mess erreur
        }
    }

    public void respSignOut(int idClient, String login){
        UserDAO usr = new UserDAO();
        UserBDD user = usr.findbyLogin(login);
        boolean isUser = (user != null);
        if(isUser){
            try {
                if(connectedUsers.containsKey(user)){
                    connectedUsers.remove(user);
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
            boolean isUser = (user != null);
            if(isUser){
                connectedUsers.put(user,true);
            }
            // getting temp queue destination
            MessageProducer mp = tempQueues.get(idClient);
            StreamMessage rep = session.createStreamMessage();
            rep.clearBody();
            rep.writeBoolean(isUser);
            rep.writeString(user.getLogin());
            rep.setJMSType("RespConnection");
            mp.send(rep);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    // réception msg

}