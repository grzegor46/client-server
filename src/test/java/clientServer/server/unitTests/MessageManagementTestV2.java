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
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class MessageManagementTestV2 {

    private String sender = "Sender";
    private String receiver = "Receiver";
    private String content = "contentOfMessage";
    private String password = "dummyPassword";

    private MessageManagement messageManagement;
    private Repository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = mock(Repository.class);
        messageManagement = mock(MessageManagement.class);
    }

@Test
public void shouldReturnSpecificUsersMessagesFromMailBox() {

    User user = new User("Sender", "dummyPassword", Role.USER);
    List<UserMessage> mailbox = new ArrayList<>();
    mailbox.add(new UserMessage("Sender", "Receiver", "contentOfMessage"));
    user.setMailBox(mailbox);

    when(userRepository.getUserMailBox(user)).thenReturn(mailbox);
    List<String> arrayWithContent = new ArrayList<>();
    arrayWithContent.add("{\"Sender\":\"conte...\"}");

    when(messageManagement.checkMailBox(user)).thenReturn(arrayWithContent);
    List<String> result = messageManagement.checkMailBox(user);

    assertEquals(1, result.size());
    assertEquals("{\"Sender\":\"conte...\"}", result.get(0));
}






}
