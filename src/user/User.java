package user;

/**
 * Created by Yoan on 07/11/2015.
 */
import bdd.objetBdd.MessageBDD;
import bdd.objetBdd.UserBDD;
import user.ihm.UserIHM;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
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
    private String locTemp;
    private int serverID;
    private boolean connected,follow,ville;
    private UserIHM ihm;


    public static void main(String[] args) {
        User u = new User();
        System.out.println("User launched !");
        System.out.println("Press enter to end process...");
        BufferedReader waiter = new BufferedReader(new InputStreamReader(System.in));
        try {
            //waiter.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public User() {
        try {
            ihm = new UserIHM(this);
            connected = false;

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

            System.out.println("sending create temp request...");
            sendMsgCreateTempQueue();

            //System.out.println("Waiting for messages...");
            waiter = new BufferedReader(new InputStreamReader(System.in));
            //waiter.readLine();


        } catch (Exception exception) {
            exception.printStackTrace();
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
        if (userCourant != null) {
            sendMsgSignOut(userCourant.getLogin());
            System.out.println("Sign Out sent");
        }
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
    public void sendMsgConnection(String login, String pwd){
        try {
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

    // send localistion change request
    public void sendMsgLoc(String loc){
        try {
            StreamMessage req = session.createStreamMessage();
            req.clearBody();
            // id server
            req.writeInt(serverID);
            req.writeString(userCourant.getLogin());
            req.writeString(loc);
            req.setJMSType("LocalisationChange");
            senderTwitterQueue.send(req);
            System.out.println("Sent loc change request : " + loc);
            locTemp = loc;
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




    public void setFilter(UserBDD usr){
        try {

            String filter = "";

            receiverMessagesTopic.close();

            if(!usr.getAbonnements().isEmpty() || !usr.getLocalisation().isEmpty()){


                String filterf="";
                String loc="";
                if(!usr.getAbonnements().isEmpty()){

                    String res="";
                    for(UserBDD u : usr.getAbonnements()){
                        res+="'"+u.getLogin()+"',";
                    }

                    filterf=res.substring(0,res.length()-1);
                    filter+="(follow in (" + filterf + "))";
                    follow=true;
                }else{
                    follow=false;
                }

                if(!usr.getLocalisation().equals("Aucun")){

                    ville=true;
                    loc=usr.getLocalisation();
                    if(filter.equals("")){
                        filter+="ville = '"+loc+"'";
                    }else{
                        filter+=" OR (ville = '"+loc+"')";
                    }
                }else{
                    ville=false;
                }

                receiverMessagesTopic = session.createConsumer(destMessages, filter);


                System.out.println(filter);
                receiverMessagesTopic.setMessageListener(new MessagesTopicListener(this));
            }else{

                follow=false;
                ville=false;
            }

        } catch (JMSException e) {
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

    public void sendTweet(String tweet){
        try {
            StreamMessage req = session.createStreamMessage();
            req.clearBody();
            // Construction du streammessage
            req.writeInt(serverID);
            req.writeString(userCourant.getLogin());
            req.writeString(tweet);
            req.writeLong(System.currentTimeMillis());
            req.setJMSType("Tweet");
            senderTwitterQueue.send(req);
            System.out.println("Sent Tweet");
            userCourant.putMessage(new MessageBDD(tweet, userCourant.getId(), new Timestamp(System.currentTimeMillis()), userCourant.getLocalisation()));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    // réception
    // response from temp queue init
    // on peut initialiser l'ihm, la connexion s'est faire
    public void respMsgTempQueue(int id) {
        serverID = id;
        // init ihm
        ihm.setVisible(true);
        System.out.println("Temp queue with server successful");
    }

    // réception
    // response from temp queue init for sign in
    public void respMsgTempQueueSignIn(boolean res) {
        if(res){
            System.out.println("Sign In successful");
            ihm.callbackSubscribeSuccessful();
        }else{
            System.out.println("Sign In Failed");
            userCourant = null;
            ihm.callbackSubscribeFailed();
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
            setFilter(userCourant);
        }else{
            System.out.println("Follow Failed");
        }
    }

    public void respMsgTempQueueUnFollow(boolean res) {
        if(res){
            System.out.println("UnFollow successful");
            setFilter(userCourant);
        }else{
            System.out.println("UnFollow Failed");
        }
    }

    public void respMsgTempQueueSearch(boolean res) {
        System.out.println("No users found");
    }

    // réception
    // response from temp queue init for connection
    // cas erreur
    public void respMsgTempQueueConnection(String error) {
        System.out.println("Connexion Failed -> " + error);
        ihm.callbackConnexionFailed(error);
    }
    // réception
    // response from temp queue init for connection
    public void respMsgTempQueueConnection(UserBDD usr) {
        connected = true;
        setUserCourant(usr);
        System.out.println("Connexion successful");
        ihm.callbackConnexionSuccessful();
        setFilter(usr);
    }

    // réception
    // response from temp queue init for connection
    public void respMsgTempQueueSearch(ArrayList<UserBDD> list) {
        System.out.println("List of results");
        for(UserBDD usr : list){
            System.out.println("User login : " + usr);
        }
    }

    // maj tweets tableau IHM de l'user
    public void respMsgTempQueueTweet(boolean result) {
        if (result) {
            System.out.println("Tweet processed by server");
            ihm.majTweetsUser();
        }
    }

    public void respTweet(int idmsg, String content, int iduser, long date, String localization, String login,String name, String fname){

        MessageBDD m=new MessageBDD(content,iduser,new Timestamp(date),localization);
        Object[] tab= new Object[2];
        tab[0]=m;
        UserBDD u = new UserBDD(login,null,name,fname,null);

        tab[1]=u;
        if(follow){
            ihm.majFlux(tab);
        }
        if(ville && localization.equals(userCourant.getLocalisation())){
             ihm.majLoc(tab);
        }
    }

    // réception ack demande chgt de loc
    public void respMsgTempQueueLoc(boolean result) {
        if (result) {
            ihm.callbackLocSuccessfull(locTemp);
            userCourant.setLocalisation(locTemp);
            setFilter(userCourant);
        } else {
            ihm.callbackLocFailed();
        }
    }


    public void setUserCourant(UserBDD user){
        userCourant = user;
    }

    public UserBDD getUserCourant() { return userCourant;}
}