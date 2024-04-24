package clientServer.server.unitTests;

import constant.Role;
import message.UserMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class MessageManagementTestV2 {

    private String sender = "Sender";
    private String receiver = "Receiver";
    private String content = "contentOfMessage";
    private String password = "dummyPassword";

    private MessageManagement messageManagement;


    @BeforeEach
    public void setUp() {
        messageManagement = mock(MessageManagement.class);
    }

    @Test
    public void shouldReturnMessagesFromMailBox() {

        User user = new User(sender, password, Role.USER);
        List<UserMessage> mailbox = new ArrayList<>();
        user.setMailBox(mailbox);

        List<String> arrayWithContent = new ArrayList<>();
        arrayWithContent.add("{\"Sender\":\"conte...\"}");
        arrayWithContent.add("{\"Sender1\":\"conte2...\"}");
        when(messageManagement.checkMailBox(user)).thenReturn(arrayWithContent);

        List<String> result = messageManagement.checkMailBox(user);


        assertEquals(2, result.size());
        assertEquals("{\"Sender\":\"conte...\"}", result.get(0));

        verify(messageManagement, atLeastOnce()).checkMailBox(user);
    }

    @Test
    public void shouldReturnReadMessageFromUserMailBox() {
        User user = new User(sender, password, Role.USER);
        UserMessage newUserMessage = new UserMessage(sender,receiver,content);
        UserMessage newUserMessage2 = new UserMessage(sender,receiver,content);
        int indexOfMessage = 0;
        List<UserMessage> mailbox = new ArrayList<>();

        mailbox.add(newUserMessage);
        mailbox.add(newUserMessage2);
        user.setMailBox(mailbox);
        UserMessage userMessageFromUserMailbox = mailbox.get(indexOfMessage);
        when(messageManagement.readMessageFromMailBox(user, indexOfMessage)).thenReturn(userMessageFromUserMailbox.getSender() + ": " + userMessageFromUserMailbox.getContent());
        String response = messageManagement.readMessageFromMailBox(user, indexOfMessage);

        assertEquals(userMessageFromUserMailbox.getSender() + ": " + userMessageFromUserMailbox.getContent(),response);

        verify(messageManagement, atLeastOnce()).readMessageFromMailBox(user, indexOfMessage);
    }

    @Test
    public void shouldReturnMessageAsJsonRepresentation() {
        when(messageManagement.getMessageAsJsonRepresentation("UserName", content)).thenReturn("{\"UserName\":\"contentOfMessage\"}");

        String messageAsJson = messageManagement.getMessageAsJsonRepresentation("UserName", content);
        String expectedResult = "{\"UserName\":\"contentOfMessage\"}";
        assertEquals(expectedResult, messageAsJson);

    }

}
