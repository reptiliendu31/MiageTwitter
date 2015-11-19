package server;

/**
 * Created by Yoan on 07/11/2015.
 */

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

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
                        server.initTemporaryQueue(message, text.getText().split(";")[1]);
                }

            } catch (JMSException exception) {
                System.err.println("Failed to get message text: " + exception);
            }
        }
    }
}

