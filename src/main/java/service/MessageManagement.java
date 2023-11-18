package service;

import message.UserMessage;
import repository.UserRepository;
import user.User;

import java.util.List;

public class MessageManagement {

    private final UserRepository userRepository = new UserRepository();

    public void sendMessage(UserMessage sentMessage) {

        User receiver = userRepository.findUserName(sentMessage.getReceiver());
        userRepository.delete(receiver.getNickName());
        if(receiver.getMailBox().size() <5) {
            List<UserMessage> tmpUserMessageMailBox = receiver.getMailBox();
            tmpUserMessageMailBox.add(sentMessage);

            receiver.setMailBox(tmpUserMessageMailBox);
            userRepository.save(receiver);
        }
    }
}
