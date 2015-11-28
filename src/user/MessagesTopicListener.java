package user;

/**
 * Created by Yoan on 07/11/2015.
 */
import bdd.objetBdd.MessageBDD;

import javax.jms.*;

/**
 * Created by david on 06/11/2015.
 */
public class MessagesTopicListener implements MessageListener {
    private User user;

    public MessagesTopicListener(User u) {
        user = u;
    }

    @Override
    public void onMessage(javax.jms.Message message) {
        if (message instanceof TextMessage) {
            TextMessage text = (TextMessage) message;
            try {
                System.out.println("Received from topic : " + text.getText());
            } catch (JMSException exception) {
                System.err.println("Failed to get message text: " + exception);
            }
        }else if (message instanceof ObjectMessage){
            ObjectMessage mess = (ObjectMessage) message;

            try {
                switch (message.getJMSType()) {
                    case "Tweet" :
                        user.respTweet((MessageBDD)mess.getObject());



                        break;
                    default: break;
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

}

