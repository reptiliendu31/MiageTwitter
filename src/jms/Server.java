package jms;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import javax.jms.*;
import message.Message;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;

/**
 * Created by david on 06/11/2015.
 */
public class Server {

    public static void main(String[] args) {
        Context context = null;
        ConnectionFactory factory = null;
        Connection connection = null;
        String factoryName = "ConnectionFactory";
        String destName = "Messages";
        Destination dest = null;
        int count = 5;
        Session session ;
        MessageProducer sender = null;

        try {
            // create the JNDI initial context.
            context = new InitialContext();

            // look up the ConnectionFactory
            factory = (ConnectionFactory) context.lookup(factoryName);

            // look up the Destination
            dest = (Destination) context.lookup(destName);

            // create the connection
            connection = factory.createConnection();

            // create the session
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);

            // create the sender
            sender = session.createProducer(dest);

            // start the connection, to enable message sends
            connection.start();

            ArrayList<Message> l = Message.testM();
            for (Message m : l) {
                TextMessage message = session.createTextMessage();
                message.setText(m.getContent());
                sender.send(message);
                System.out.println("Sent: " + message.getText());
            }

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

            // close the connection
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}