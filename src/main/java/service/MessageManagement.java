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

        String jsonString = helpCommandAsJson.toString();
        return jsonString;

    }

    public String readMessageFromMailBox(User user, int indexOfMessage ) {
        UserMessage userMessage = user.getMailBox().get(indexOfMessage);
        userRepository.delete(user.getNickName());
        userMessage.setRead(true);
        userRepository.save(user);
        return (userMessage.getSender()+ ": " +userMessage.getContent());
    }

// TODO    checkMailBox()

//        private void checkMailBox() {
//        if (activeUser != null) {
//            User user = userManagement.findUser(activeUser.getNickName());
//            List<UserMessage> userMailBox = user.getMailBox();
//            List<String> stringList = new ArrayList<>();
//            for (UserMessage userMsgs : userMailBox) {
//
//                String mail =  messageManagement.getMessageAsJsonRepresentation(userMsgs.getSender(),userMsgs.getContent());
//                stringList.add(mail);
//
//            }
//            stream.printWriter.println(stringList);
//        } else {
//            stream.printWriter.println("you need to be logged to check users");
//        }
//    }
//    zwrocimy tutaj liste z mailami i przekazemy ja do userManagement o odczytanie
}
