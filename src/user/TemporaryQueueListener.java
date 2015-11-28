package user;

/**
 * Created by Yoan on 07/11/2015.
 */

import bdd.objetBdd.UserBDD;

import javax.jms.*;
import java.util.ArrayList;

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
            System.out.println("Sign In successful");

            StreamMessage mess = (StreamMessage) message;
            try {
                switch (message.getJMSType()) {
                    case "RespInitTempQueue" :
                        int serverId = mess.readInt();
                        user.respMsgTempQueue(serverId);
                        break;
                    case "RespConnection" :
                        String error = mess.readString();
                        user.respMsgTempQueueConnection(error);
                        break;
                    case "RespSignIn" :
                        result = mess.readBoolean();
                        user.respMsgTempQueueSignIn(result);
                        break;
                    case "RespSignOut" :
                        result = mess.readBoolean();
                        user.respMsgTempQueueSignOut(result);
                        break;
                    case "RespFollow" :
                        result = mess.readBoolean();
                        user.respMsgTempQueueFollow(result);
                        break;
                    case "RespUnFollow" :
                        result = mess.readBoolean();
                        user.respMsgTempQueueUnFollow(result);
                        break;
                    case "RespSearch" :
                        result = mess.readBoolean();
                        user.respMsgTempQueueSearch(result);
                    case "RespTweet" :
                        result = mess.readBoolean();
                        user.respMsgTempQueueTweet(result);
                        break;
                    case "RespLocalisation" :
                        result = mess.readBoolean();
                        user.respMsgTempQueueLoc(result);
                        break;
                    default: break;
                }

            } catch (JMSException e) {
                e.printStackTrace();
            }
        }else if(message instanceof ObjectMessage){
            ObjectMessage mess = (ObjectMessage) message;
            try {
                switch (mess.getJMSType()) {
                    case "RespConnection" :
                        UserBDD usr = (UserBDD)mess.getObject();
                        user.respMsgTempQueueConnection(usr);
                        break;
                    case "RespSearch" :
                        ArrayList<String> list = (ArrayList<String>)mess.getObject();
                        user.respMsgTempQueueSearch(list);
                        break;
                    default: break;
                }
            } catch (Exception exception) {
                System.err.println("Failed to get message text: " + exception);
            }
        }
    }


}

