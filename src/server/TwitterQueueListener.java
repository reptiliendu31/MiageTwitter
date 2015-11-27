package server;

/**
 * Created by Yoan on 07/11/2015.
 */

import bdd.objetBdd.UserBDD;

import javax.jms.*;

/**
 * Created by david on 06/11/2015.
 */
public class TwitterQueueListener implements MessageListener {
    private Server server;

    public TwitterQueueListener(Server s) {
        server = s;
    }
    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage){
            ObjectMessage mess = (ObjectMessage) message;
            try {
                if(message.getJMSType().equals("SignIn")){
                    /*UserBDD user = (UserBDD) mess.getObject();
                    System.out.println("user test : " + user.getLogin());
                    server.respSignIn(user);
                    System.out.println(user.toString());
                    */
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        else if (message instanceof StreamMessage){
            StreamMessage mess = (StreamMessage) message;
            int serverId;
            try {
                switch (message.getJMSType()) {
                    case "InitTempQueue" :
                        System.out.println(" -temporary queue request");
                        System.out.println("   -> sending ack to user");
                        server.respInitTempQueue(message);
                        break;
                    case "Connection" :
                        serverId = mess.readInt();
                        String login = mess.readString();
                        String password = mess.readString();
                        server.respConnection(serverId,login,password);
                        break;
                    case "SignIn" :
                        serverId = mess.readInt();
                        String loginS = mess.readString();
                        String passwordS = mess.readString();
                        String nameS = mess.readString();
                        String firstNameS = mess.readString();
                        server.respSignIn(serverId, loginS, passwordS, nameS, firstNameS);
                        break;
                    case "SignOut" :
                        serverId = mess.readInt();
                        String loginSO = mess.readString();
                        server.respSignOut(serverId,loginSO);
                        break;
                    case "Follow" :
                        serverId = mess.readInt();
                        String loginU = mess.readString();
                        String loginF = mess.readString();
                        server.respFollow(serverId, loginU,loginF);
                        break;
                    case "UnFollow" :
                        serverId = mess.readInt();
                        String loginUN = mess.readString();
                        String loginFN = mess.readString();
                        server.respUnFollow(serverId, loginUN,loginFN);
                        break;
                    case "Tweet" :
                        serverId = mess.readInt();
                        String usercourant = mess.readString();
                        String tweet = mess.readString();
                        long time = mess.readLong();
                        server.respTweet(serverId,usercourant,tweet,time);
                    default: break;
                }

            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}

