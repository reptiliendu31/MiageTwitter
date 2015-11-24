package user;

/**
 * Created by Yoan on 07/11/2015.
 */
import bdd.objetBdd.UserBDD;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by david on 06/11/2015.
 */
public class User {
    private static final String factoryName = "ConnectionFactory";
    private static final String topicMessages = "Messages";
    private static final String twitterQueue = "twitter";

    private Context context ;
    private ConnectionFactory factory;
    private Connection connection;
    private Destination destMessages ;
    private Destination destTwitter ;
    private Session session ;
    private MessageConsumer receiverMessagesTopic ;
    private MessageProducer senderTwitterQueue ;
    private BufferedReader waiter ;
    private TemporaryQueue tempo ;
    private UserBDD userCourant;
    private int serverID;

    public static void main(String[] args) {
        User u = new User();
    }


    public User() {
        try {
            serverID = 0;
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

            // create the receiverMessagesTopic on message Topic
            receiverMessagesTopic = session.createConsumer(destMessages);

            // create the senderTwitterQueue
            senderTwitterQueue = session.createProducer(destTwitter);

            // register a listener
            receiverMessagesTopic.setMessageListener(new MessagesTopicListener(this));

            // start the respConnection, to enable message receipt
            connection.start();

            System.out.println("Waiting for messages...");
            System.out.println("Press [return] to send messages to topic");

            waiter = new BufferedReader(new InputStreamReader(System.in));
            waiter.readLine();

            System.out.println("sending create temp request...");
            sendMsgCreateTempQueue();
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
                    break;
                case "2":
                    sendMsgConnexion();
                    break;
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
        System.out.println("Choose password:");
        waiter = new BufferedReader(new InputStreamReader(System.in));
        String pswd = waiter.readLine();
        System.out.println("Choose name:");
        waiter = new BufferedReader(new InputStreamReader(System.in));
        String name = waiter.readLine();
        System.out.println("Choose first name:");
        waiter = new BufferedReader(new InputStreamReader(System.in));
        String firstName = waiter.readLine();
        InscriptionMessage(login,pswd,name,firstName);
            System.out.println("Inscription envoyée");

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void InscriptionMessage(String login, String pswd, String name, String fName) {
        try {
            UserBDD user = new UserBDD(login,pswd,name,fName);
            ObjectMessage req = session.createObjectMessage(user);
            req.setJMSType("SignIn");
            req.setJMSReplyTo(tempo);
            senderTwitterQueue.send(req);
            System.out.println("envoie de la demande d'inscription");

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    // send connexion demand
    public void sendMsgConnexion(){
        try {
            System.out.println("Enter login:");
            waiter = new BufferedReader(new InputStreamReader(System.in));
            String login = waiter.readLine();
            System.out.println("Enter password:");
            waiter = new BufferedReader(new InputStreamReader(System.in));
            String pwd = waiter.readLine();


            StreamMessage req = session.createStreamMessage();
            req.clearBody();
            // id serveur
            req.writeInt(serverID);
            req.writeString(login);
            req.writeString(pwd);
            req.setJMSType("Connection");
            senderTwitterQueue.send(req);
            System.out.println("envoi de la demande de sendMsgConnexion");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
    }

    // première connexion au serveur : demande de file temp pour communiquer
    public void sendMsgCreateTempQueue() {
        try {
            tempo = session.createTemporaryQueue();
            MessageConsumer consumerTempo = session.createConsumer(tempo);
            consumerTempo.setMessageListener(new TemporaryQueueListener(this));
            StreamMessage req = session.createStreamMessage();
            req.clearBody();
            req.setJMSType("InitTempQueue");

            req.setJMSReplyTo(tempo);
            senderTwitterQueue.send(req);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    // réception

    // response from temp queue init
    public void respMsgTempQueue(int id) {
        serverID = id;
    }



    public void setUserCourant(UserBDD user){
        userCourant = user;
    }
}