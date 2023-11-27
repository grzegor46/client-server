package service;

import message.UserMessage;
import repository.UserRepository;
import user.User;

import java.util.List;

public class MessageManagement {

    private final UserRepository userRepository = new UserRepository();

    public void sendMessage(UserMessage sentMessage) {

        User receiver = userRepository.findUserName(sentMessage.getReceiver());
        if(receiver.getMailBox().size() <5) {
            userRepository.delete(receiver.getNickName());
            List<UserMessage> tmpUserMessageMailBox = receiver.getMailBox();
            tmpUserMessageMailBox.add(sentMessage);

            receiver.setMailBox(tmpUserMessageMailBox);
            userRepository.save(receiver);
        } else {
            System.out.print("user has more than 5 msgs");
//            TODO throw excpetion, use try catch
        }
    }

    public String getMessageAsJsonRepresentation() {

        return "";
    }
}
