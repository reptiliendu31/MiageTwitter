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
                        user.respMsgTempQueueConnection(result);
                        break;
                    case "RespSignIn" :
                        result = mess.readBoolean();
                        user.respMsgTempQueueSignIn(result);
                        break;
                    default: break;
                }

            } catch (JMSException e) {
                e.printStackTrace();
            }
        }else if(message instanceof ObjectMessage){
            ObjectMessage mess = (ObjectMessage) message;
            try {
                if(user != null){
                    UserBDD usr = (UserBDD) mess.getObject();
                    user.setUserCourant(usr);
                    System.out.println("Connection established!");
                }else{
                    System.out.println("Error Connection");
                }
            } catch (JMSException exception) {
                System.err.println("Failed to get message text: " + exception);
            }
        }
    }


}

