package user;

/**
 * Created by Yoan on 07/11/2015.
 */

import bdd.objetBdd.UserBDD;

import javax.jms.*;

/**
 * Created by david on 06/11/2015.
 */
public class TemporaryQueueListener implements MessageListener {
    private User user;

    public TemporaryQueueListener(User u) {
        user = u;
    }

    @Override
    public void onMessage(Message message) {
        boolean result;
        if (message instanceof StreamMessage) {
            StreamMessage mess = (StreamMessage) message;
            try {
                switch (message.getJMSType()) {
                    case "RespInitTempQueue" :
                        int serverId = mess.readInt();
                        user.respMsgTempQueue(serverId);
                        break;
                    case "RespConnection" :
                        result = mess.readBoolean();
                        String login = mess.readString();
                        user.respMsgTempQueueConnection(result, login);
                        break;
                    case "RespSignIn" :
                        result = mess.readBoolean();
                        user.respMsgTempQueueSignIn(result);
                        break;
                    case "RespSignOut" :
                        result = mess.readBoolean();
                        user.respMsgTempQueueSignOut(result);
                        break;
                    default: break;
                }

            } catch (JMSException e) {
                e.printStackTrace();
            }
        }else if(message instanceof ObjectMessage){
            ObjectMessage mess = (ObjectMessage) message;
            try {
                // do smthing


            } catch (Exception exception) {
                System.err.println("Failed to get message text: " + exception);
            }
        }
    }


}

