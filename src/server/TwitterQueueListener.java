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
        if (message instanceof TextMessage) {
            TextMessage text = (TextMessage) message;
            try {
                System.out.println("Received from user : " + text.getText());

                switch (text.getText().split(";")[0]) {
                    case "Connexion" :
                        // init temporary queue with login name
                        System.out.println(" -temporary queue request");
                        System.out.println("   -> sending ack to user");
                        server.initTemporaryQueue(message);
                }

            } catch (JMSException exception) {
                System.err.println("Failed to get message text: " + exception);
            }
        }
        else if (message instanceof ObjectMessage){
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
    }
}

