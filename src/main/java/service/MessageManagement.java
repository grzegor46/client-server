package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import message.UserMessage;
import repository.Repository;
import user.User;

import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.SQLDataType.*;

public class MessageManagement {

    private final Repository userRepository;


    public MessageManagement(Repository userRepository) {
        this.userRepository = userRepository;
    }

    public void sendMessage(UserMessage sentMessage) {
        userRepository.addMessageToMailbox(sentMessage);
    }

    public String getMessageAsJsonRepresentation(String name, String content) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode helpCommandAsJson = objectMapper.createObjectNode();
        helpCommandAsJson.put(name, content);

        return helpCommandAsJson.toString();
    }

    public String readMessageFromMailBox(User user, int indexOfMessage ) {
        List<UserMessage> userMailBox = userRepository.getUserMailBox(user);
        UserMessage userMessage = userMailBox.get(indexOfMessage);
        userMessage.setRead(true);
        userMailBox.set(indexOfMessage,userMessage);
        user.setMailBox(userMailBox);
        userRepository.update(user);
        return (userMessage.getSender()+ ": " +userMessage.getContent());
    }

    public List<String> checkMailBox(User user) {
        List<UserMessage> userMailBox = userRepository.getUserMailBox(user);
        if (!userMailBox.isEmpty()) {
            List<String> stringList = new ArrayList<>();
            for (UserMessage userMsgs : userMailBox) {
                String mail;
                if (!userMsgs.isRead()) {
                    if(userMsgs.getContent().length() <= 5) {
                        mail = getMessageAsJsonRepresentation(userMsgs.getSender(), userMsgs.getContent());
                    } else {
                        mail = getMessageAsJsonRepresentation(userMsgs.getSender(), userMsgs.getContent().substring(0, 5) + "...");
                    }
                } else {
                    mail = getMessageAsJsonRepresentation(userMsgs.getSender(), userMsgs.getContent());
                }
                stringList.add(mail);
            }
            return stringList;
        }
        return null;

//    public int countUnreadUserMsgs(User user) {
//        return userRepository.getUnReadMessages(user);
//    }
}}