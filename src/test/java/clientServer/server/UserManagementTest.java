package clientServer.server;


import client.Client;
import constant.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.Server;
import utils.Stream;

import java.io.IOException;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserManagementTest {

    private final String nickname = "UserName";
    private final String password = "UserPassword";
    private final Role role = Role.USER;


    @Mock
    Stream streamMock;

    @Mock
    Server server;

    @Mock
    Client client;

    @BeforeEach
    void setUp() {


    }

    @Test
    public void shouldReturnInfoFromHelpCommand() throws IOException {

        // Act
//        userManagement.takeRequest("help");

        // Assert
        verify(streamMock.printWriter, times(1));

    }

    @Test
    public void shouldReturnEmptyString() {
        String result = "";
        assertEquals("",result);
    }

    @Test
    public void shouldReturnSomeInt() {
        int result = 1;
        assertEquals(1,result);
    }

}
