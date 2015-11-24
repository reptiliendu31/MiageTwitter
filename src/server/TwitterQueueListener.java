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
                    UserBDD user = (UserBDD) mess.getObject();
                    System.out.println("user test : " + user.getLogin());
                    server.signIn(user);
                    System.out.println(user.toString());
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        else if (message instanceof StreamMessage){
            StreamMessage mess = (StreamMessage) message;
            try {
                switch (message.getJMSType()) {
                    case "InitTempQueue" :
                        System.out.println(" -temporary queue request");
                        System.out.println("   -> sending ack to user");
                        server.initTemporaryQueue(message);
                        break;
                    case "Connection" :
                        int serverId = mess.readInt();
                        String login = mess.readString();
                        String password = mess.readString();
                        server.connection(serverId,login,password);
                        System.out.println("test connexion : login = " + login + " password : " + password);
                        break;
                    default: break;
                }

            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}

