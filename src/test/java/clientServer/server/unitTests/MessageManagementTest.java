package clientServer.server.unitTests;

import constant.Role;
import message.UserMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import service.MessageManagement;
import user.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class MessageManagementTest {

    private String sender = "Sender";
    private String receiver = "Receiver";
    private String content = "contentOfMessage";
    private String password = "dummyPassword";
    private User user;
    private List<UserMessage> mailbox;
    private UserMessage newUserMessage;
    private UserMessage newUserMessage2;

    private MessageManagement messageManagement;

    @BeforeEach
    public void setUp() {
        messageManagement = mock(MessageManagement.class);
        user = new User(sender, password, Role.USER);
        mailbox = new ArrayList<>();
        newUserMessage = new UserMessage(sender,receiver,content);
        newUserMessage2 = new UserMessage(sender,receiver,content);
    }

    @Test
    public void shouldReturnMessagesFromMailBox() {
        user.setMailBox(mailbox);
        List<String> arrayWithContent = new ArrayList<>();

        arrayWithContent.add(newUserMessage.toString());
        arrayWithContent.add(newUserMessage2.toString());
        when(messageManagement.checkMailBox(user)).thenReturn(arrayWithContent);

        List<String> result = messageManagement.checkMailBox(user);

        assertEquals(2, result.size());

        verify(messageManagement, atLeastOnce()).checkMailBox(user);
    }

    @Test
    public void shouldReturnReadMessageFromUserMailBox() {
        int indexOfMessage = 0;

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
