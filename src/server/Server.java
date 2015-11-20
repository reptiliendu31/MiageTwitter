package server;

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
    private static int nbTempQueues = 0;

    public static void main(String[] args) {
        Server s = new Server();
    }

    public Server() {

        try {
            tempQueues = new HashMap<Integer, MessageProducer>();

            // create the JNDI initial context.
            context = new InitialContext();

            // look up the ConnectionFactory
            factory = (ConnectionFactory) context.lookup(factoryName);

            // look up the Destination
            destMessages = (Destination) context.lookup(topicMessages);
            destTwitter = (Destination) context.lookup(twitterQueue);

            // create the connection
            connection = factory.createConnection();

            // create the session
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);

            // create the sender
            sender = session.createProducer(destMessages);

            receiver = session.createConsumer(destTwitter);

            // register a listener
            receiver.setMessageListener(new TwitterQueueListener(this));

            // start the connection, to enable message sends
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

            // close the connection
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public void initTemporaryQueue(Message req) {
        try {
            Destination tempo = req.getJMSReplyTo();
            MessageProducer mp = null;
            mp = session.createProducer(tempo);
            tempQueues.put(nbTempQueues,mp);
            TextMessage rep = session.createTextMessage("ACK - Temp queue created, number : " + nbTempQueues);
            nbTempQueues++;
            mp.send(rep);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void signIn(UserBDD user){
        UserDAO usr = new UserDAO();
        user = usr.create(user);
        if(user != null){
            System.out.println("user created");
            // envoi mess sur file tempo
        }
        else{
            // envoi mess erreur
        }
    }

    public void connection(String login, String password){
        UserDAO usr = new UserDAO();
        UserBDD user = usr.findbyLogin(login);
        if(user != null){
            System.out.println("User Found");
            // envoi mess sur file tempo
        }
        else{
            // envoi mess erreur
            System.out.println("User not Found");

        }
    }
}