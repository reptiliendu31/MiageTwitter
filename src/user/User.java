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
import java.util.ArrayList;

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
    private boolean connected = false;

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
        try {
            if(connected){
                System.out.println("Menu :");
                System.out.println("1 - Send Message");
                System.out.println("2 - Search User");
                System.out.println("3 - Follow");
                System.out.println("4 - UnFollow");
                System.out.println("5 - Sign Out");
                waiter = new BufferedReader(new InputStreamReader(System.in));
                String choix = waiter.readLine();
                switch (choix) {
                    case "1":
                        sendTweet();
                        break;
                    case "2":
                        sendMsgSearch();
                        break;
                    case "3":
                        sendMsgFollow();
                        break;
                    case "4":
                        sendMsgUnFollow();
                        break;
                    case "5":
                        signOut();
                        break;
                    default:
                        System.out.println("Mauvais choix");
                }
            }else {
                System.out.println("Menu :");
                System.out.println("1 - Sign In");
                System.out.println("2 - Connexion");
                waiter = new BufferedReader(new InputStreamReader(System.in));

                String choix = waiter.readLine();
                switch (choix) {
                    case "1":
                        signIn();
                        break;
                    case "2":
                        sendMsgConnection();
                        break;
                    default:
                        System.out.println("Mauvais choix");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void signIn() {
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
            System.out.println("Choose localisation:");
            waiter = new BufferedReader(new InputStreamReader(System.in));
            String localisation = waiter.readLine();
            sendMsgSignIn(login, pswd, name, firstName, localisation);
            System.out.println("Sign in sent");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void signOut() {
        sendMsgSignOut(userCourant.getLogin());
        System.out.println("Sign Out sent");
    }

    public void sendMsgSignIn(String login, String pswd, String name, String fName, String localisation) {
        try {
            // create message
            StreamMessage req = session.createStreamMessage();
            req.clearBody();
            // id server
            req.writeInt(serverID);
            req.writeString(login);
            req.writeString(pswd);
            req.writeString(name);
            req.writeString(fName);
            req.writeString(localisation);
            req.setJMSType("SignIn");
            senderTwitterQueue.send(req);
            System.out.println("Sent respSignIn");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendMsgSignOut(String login) {
        try {
            // create message
            StreamMessage req = session.createStreamMessage();
            req.clearBody();
            // id server
            req.writeInt(serverID);
            req.writeString(login);
            req.setJMSType("SignOut");
            senderTwitterQueue.send(req);
            System.out.println("Sent respSignOut");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    // send connexion demand
    public void sendMsgConnection(){
        try {
            System.out.println("Enter login:");
            waiter = new BufferedReader(new InputStreamReader(System.in));
            String login = waiter.readLine();
            System.out.println("Enter password:");
            waiter = new BufferedReader(new InputStreamReader(System.in));
            String pwd = waiter.readLine();
            StreamMessage req = session.createStreamMessage();
            req.clearBody();
            // id server
            req.writeInt(serverID);
            req.writeString(login);
            req.writeString(pwd);
            req.setJMSType("Connection");
            senderTwitterQueue.send(req);
            System.out.println("Sent connection request");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
    }

    // send research demand
    public void sendMsgSearch(){
        try {
            System.out.println("Who are you searching ?");
            waiter = new BufferedReader(new InputStreamReader(System.in));
            String search = waiter.readLine();
            StreamMessage req = session.createStreamMessage();
            req.clearBody();
            // id server
            req.writeInt(serverID);
            req.writeString(search);
            req.setJMSType("Search");
            senderTwitterQueue.send(req);
            System.out.println("Sent Search request");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
    }

    // send follow demand
    public void sendMsgFollow(){
        try {
            System.out.println("Enter login user you want to follow:");
            waiter = new BufferedReader(new InputStreamReader(System.in));
            String login = waiter.readLine();
            StreamMessage req = session.createStreamMessage();
            req.clearBody();
            // id server
            req.writeInt(serverID);
            req.writeString(userCourant.getLogin());
            req.writeString(login);
            req.setJMSType("Follow");
            senderTwitterQueue.send(req);
            System.out.println("Sent Follow request");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
    }

    // send follow demand
    public void sendMsgUnFollow(){
        try {
            System.out.println("Enter login user you want to unFollow:");
            waiter = new BufferedReader(new InputStreamReader(System.in));
            String login = waiter.readLine();
            StreamMessage req = session.createStreamMessage();
            req.clearBody();
            // id server
            req.writeInt(serverID);
            req.writeString(userCourant.getLogin());
            req.writeString(login);
            req.setJMSType("UnFollow");
            senderTwitterQueue.send(req);
            System.out.println("Sent UnFollow request");
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

    public void sendTweet(){
        try {
            System.out.println("Taper votre tweet:");
            waiter = new BufferedReader(new InputStreamReader(System.in));
            String tweet = waiter.readLine();
            StreamMessage req = session.createStreamMessage();
            req.clearBody();
            // Construction du streammessage
            req.writeInt(serverID);
            req.writeString(userCourant.getLogin());
            req.writeString(tweet);
            req.writeLong(System.currentTimeMillis());
            req.setJMSType("Tweet");
            System.out.println("Sent Tweet");
            senderTwitterQueue.send(req);
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // réception
    // response from temp queue init
    public void respMsgTempQueue(int id) {
        serverID = id;
    }

    // réception
    // response from temp queue init for sign in
    public void respMsgTempQueueSignIn(boolean res) {
        if(res){
            System.out.println("Sign In successful");
            sendMsgSignOut(userCourant.getLogin());
        }else{
            System.out.println("Sign In Failed");
        }
    }

    public void respMsgTempQueueSignOut(boolean res) {
        if(res){
            System.out.println("Sign Out successful");
            connected = false;
            userCourant = null;
        }else{
            System.out.println("Sign Out Failed");
        }
    }

    public void respMsgTempQueueFollow(boolean res) {
        if(res){
            System.out.println("Follow successful");
        }else{
            System.out.println("Follow Failed");
        }
    }

    public void respMsgTempQueueUnFollow(boolean res) {
        if(res){
            System.out.println("UnFollow successful");
        }else{
            System.out.println("UnFollow Failed");
        }
    }

    public void respMsgTempQueueSearch(boolean res) {
        System.out.println("No users found");
    }

    // réception
    // response from temp queue init for connection
    public void respMsgTempQueueConnection(String error) {
        System.out.println("Connexion Failed -> " + error);
    }
    // réception
    // response from temp queue init for connection
    public void respMsgTempQueueConnection(UserBDD usr) {
        connected = true;
        setUserCourant(usr);
        System.out.println("Connexion successful");
    }

    // réception
    // response from temp queue init for connection
    public void respMsgTempQueueSearch(ArrayList<String> list) {
        System.out.println("List of results");
        for(String log : list){
            System.out.println("User login : " + log);
        }
    }

    public void setUserCourant(UserBDD user){
        userCourant = user;
    }
}