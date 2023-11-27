package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    public String getMessageAsJsonRepresentation(String name, String content) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode helpCommandAsJson = objectMapper.createObjectNode();
        helpCommandAsJson.put(name, content);

        String jsonString = helpCommandAsJson.toString();
        return jsonString;

    }
}
