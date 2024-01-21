package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import message.UserMessage;
import repository.Repository;
import user.User;

import java.util.ArrayList;
import java.util.List;

public class MessageManagement {

    private final Repository userRepository;


    public MessageManagement(Repository userRepository) {
        this.userRepository = userRepository;
    }

    public void sendMessage(UserMessage sentMessage) {

        User receiver = userRepository.findUserName(sentMessage.getReceiver());
        userRepository.delete(receiver.getNickName());
        List<UserMessage> tmpUserMessageMailBox = receiver.getMailBox();
        tmpUserMessageMailBox.add(sentMessage);
        receiver.setMailBox(tmpUserMessageMailBox);
        userRepository.save(receiver);
    }

    public String getMessageAsJsonRepresentation(String name, String content) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode helpCommandAsJson = objectMapper.createObjectNode();
        helpCommandAsJson.put(name, content);

        return helpCommandAsJson.toString();
    }

    public String readMessageFromMailBox(User user, int indexOfMessage ) {
        UserMessage userMessage = user.getMailBox().get(indexOfMessage);
        userRepository.delete(user.getNickName());
        userMessage.setRead(true);
        userRepository.save(user);
        return (userMessage.getSender()+ ": " +userMessage.getContent());
    }

    public List<String> checkMailBox(User user) {
        List<UserMessage> userMailBox = user.getMailBox();
        if (!userMailBox.isEmpty()) {
            List<String> stringList = new ArrayList<>();
            for (UserMessage userMsgs : userMailBox) {
                String mail;
                if (!userMsgs.isRead()) {
                    mail = getMessageAsJsonRepresentation(userMsgs.getSender(), userMsgs.getContent().substring(0, 5) + "...");
                } else {
                    mail = getMessageAsJsonRepresentation(userMsgs.getSender(), userMsgs.getContent());
                }
                stringList.add(mail);
            }
            return stringList;
        }
        return null;
    }

    public int countUnreadUserMsgs(User user) {
        List<UserMessage> userMailBox = user.getMailBox();
        int unreadMsgsDigit = 0;
        for (UserMessage userMsg : userMailBox) {
            if(!userMsg.isRead()){
                unreadMsgsDigit++;
            }
        }
        return unreadMsgsDigit;
    }
}