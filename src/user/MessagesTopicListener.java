package user;

/**
 * Created by Yoan on 07/11/2015.
 */
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
        }else if (message instanceof StreamMessage){
            StreamMessage mess = (StreamMessage) message;

            try {
                switch (message.getJMSType()) {
                    case "Tweet" :
                        String u = mess.readString();
                        String m = mess.readString();
                        System.out.println(u+" : "+m);
                        break;
                    default: break;
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

}

