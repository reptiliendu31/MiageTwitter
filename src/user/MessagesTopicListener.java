package user;

/**
 * Created by Yoan on 07/11/2015.
 */
import bdd.objetBdd.MessageBDD;
import bdd.objetBdd.UserBDD;

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

                        int idmsg,iduser;
                        String content,localization,login,name,fname;
                        long date;


                        idmsg=mess.readInt();
                        content=mess.readString();
                        iduser=mess.readInt();
                        date=mess.readLong();
                        localization=mess.readString();
                        login=mess.readString();
                        name=mess.readString();
                        fname=mess.readString();


                        user.respTweet(idmsg,content,iduser,date,localization,login,name,fname);



                        break;
                    default: break;
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

}

