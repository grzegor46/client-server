package clientServer.server.unitTests;

import constant.Role;
import message.UserMessage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import repository.Repository;
import repository.UserRepository;
import service.MessageManagement;
import user.User;
import utils.PropertiesUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class MessageManagementTest {

    private String sender = "Sender";
    private String receiver = "Receiver";
    private String content = "contentOfMessage";
    private String password = "dummyPassword";

    private final Repository userRepository = new UserRepository();
    private final MessageManagement messageManagement = new MessageManagement(userRepository);


////TODO wydziel do utils klasy metode jako json representation

//    @Test
//    public void shouldReturnMessageAsJsonRepresentation() {
//        String messageAsJson = messageManagement.getMessageAsJsonRepresentation("UserName", content);
//        String expectedResult = "{\"UserName\":\"contentOfMessage\"}";
//        assertEquals(expectedResult, messageAsJson);
//
//    }
//TODO wydziel czesci wspolne do osobnej metody
    @Test
    public void shouldReturnUnreadSpecificUsersMessagesFromMailBox() {
        User user = new User(sender,password,Role.USER);
        List<UserMessage> mailbox = new ArrayList<>();
        mailbox.add(new UserMessage(sender,receiver,content));
        user.setMailBox(mailbox);
        List<String> messagesFromUserMailBox = messageManagement.checkMailBox(user);
        assertEquals("{\"Sender\":\"conte...\"}", messagesFromUserMailBox.get(0));
        assertFalse(user.getMailBox().get(0).isRead());
    }

//    @Test
//    public void shouldReturnUnreadMessagesDependOnAmountInMailBox() {
//        User user = new User(sender,password,Role.USER);
//        List<UserMessage> mailbox = new ArrayList<>();
//        mailbox.add(new UserMessage(sender,receiver,content));
//        user.setMailBox(mailbox);
//        int amountInMailBox = messageManagement.countUnreadUserMsgs(user);
//        assertEquals(1, amountInMailBox);
//    }

    @Test
    public void shouldReturnContentsOfTheMessageAndChangeStateOfIsReadMessageToTrue() {
        User user = new User(sender,password,Role.USER);
        List<UserMessage> mailbox = new ArrayList<>();
        mailbox.add(new UserMessage(sender,receiver,content));
        user.setMailBox(mailbox);
        userRepository.save(user);
        String msg = messageManagement.readMessageFromMailBox(user,0);
        userRepository.delete(user.getNickName());
        assertEquals(sender+": "+content,msg);
        assertTrue(user.getMailBox().get(0).isRead());
    }

    @Test
    public void shouldSendMessageWithSuccessToSpecificUser(){
        User userReceiver = new User(receiver,password,Role.USER);
        userRepository.save(userReceiver);
        UserMessage messageFromSender = new UserMessage(sender,receiver,content);
        messageManagement.sendMessage(messageFromSender);
        User userWithMessages = userRepository.findUserName(userReceiver.getNickName());
        String contentFromMailBox = userWithMessages.getMailBox().get(0).getContent();
        assertEquals(content, contentFromMailBox);
        userRepository.delete(userReceiver.getNickName());

    }

    @AfterEach
    void cleanUp() throws IOException {
        new FileWriter(PropertiesUtils.databasePath, false).close();
    }

}
