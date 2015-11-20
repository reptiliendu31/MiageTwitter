package user;

/**
 * Created by Yoan on 07/11/2015.
 */
import bdd.objetBdd.MessageBDD;
import bdd.objetBdd.UserBDD;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by david on 06/11/2015.
 */
public class User {
    private Context context = null;
    private ConnectionFactory factory = null;
    private Connection connection = null;
    private String factoryName = "ConnectionFactory";
    private String topicMessages = "Messages";
    private String twitterQueue = "twitter";
    private Destination destMessages = null;
    private Destination destTwitter = null;
    private Session session = null;
    private MessageConsumer receiverMessagesTopic = null;
    private MessageProducer senderTwitterQueue = null;
    private BufferedReader waiter = null;
    private TemporaryQueue tempo = null;

    public static void main(String[] args) {
        User u = new User();
    }


    public User() {
        try {
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

            // create the receiverMessagesTopic on message Topic
            receiverMessagesTopic = session.createConsumer(destMessages);

            // create the senderTwitterQueue
            senderTwitterQueue = session.createProducer(destTwitter);

            // register a listener
            receiverMessagesTopic.setMessageListener(new MessagesTopicListener(this));

            // start the connection, to enable message receipt
            connection.start();

            System.out.println("Waiting for messages...");
            System.out.println("Press [return] to send messages to topic");

            waiter = new BufferedReader(new InputStreamReader(System.in));
            waiter.readLine();

            System.out.println("sending create temp request...");
            createTempQueue();
            while (true){
                menuUser();
            }
            //System.out.println("Waiting for messages...");
            //waiter = new BufferedReader(new InputStreamReader(System.in));
            //waiter.readLine();


        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (JMSException exception) {
            exception.printStackTrace();
        } catch (NamingException exception) {
            exception.printStackTrace();
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

    public void createTempQueue() {
        try {
            tempo = session.createTemporaryQueue();
            MessageConsumer consumerTempo = session.createConsumer(tempo);
            consumerTempo.setMessageListener(new TemporaryQueueListener(this));
            TextMessage req = session.createTextMessage("Connexion");
            req.setJMSReplyTo(tempo);
            senderTwitterQueue.send(req);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void menuUser(){
        System.out.println("Menu :");
        System.out.println("1 - Inscription");
        System.out.println("2 - Connexion");
        System.out.println("3 - Recherche");
        System.out.println("4 - Envoi message");
        waiter = new BufferedReader(new InputStreamReader(System.in));
        try {
            String choix = waiter.readLine();

            switch (choix){
                case "1":
                    inscription();
                default:
                    System.out.println("Mauvais choix");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void inscription() {
        try {
        System.out.println("Choose login:");
        waiter = new BufferedReader(new InputStreamReader(System.in));
        String login = waiter.readLine();
        System.out.println("Choose password for login :" + login);
        waiter = new BufferedReader(new InputStreamReader(System.in));
        String pswd = waiter.readLine();
        System.out.println("Choose name:");
        waiter = new BufferedReader(new InputStreamReader(System.in));
        String name = waiter.readLine();
        System.out.println("Choose first name:");
        waiter = new BufferedReader(new InputStreamReader(System.in));
        String firstName = waiter.readLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}