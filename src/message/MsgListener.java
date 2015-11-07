package message;

/**
 * Created by Yoan on 07/11/2015.
 */
import javax.jms.*;

/**
 * Created by david on 06/11/2015.
 */
public class MsgListener implements MessageListener {
    @Override
    public void onMessage(javax.jms.Message message) {
        if (message instanceof TextMessage) {
            TextMessage text = (TextMessage) message;
            try {
                System.out.println("Received: " + text.getText());
            } catch (JMSException exception) {
                System.err.println("Failed to get message text: " + exception);
            }
        }
    }
}

