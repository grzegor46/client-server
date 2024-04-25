package clientServer.server.unitTests;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import service.UserManagement;
import user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserManagementTest {

    private final UserManagement userManagement;
    private UserManagementTestHelper userManagementTestHelper;
    private User userTempName;


    public UserManagementTest() {
        userManagement = mock(UserManagement.class);
        userManagementTestHelper = new UserManagementTestHelper();
    }

    @BeforeEach
    public void setUp() {
        userTempName = userManagementTestHelper.createTemporaryUser();
    }


    @Test
    void shouldReturnCreatedUserWithSuccess() {
        String[] credentials = UserManagementTestHelper.createCredentials();
        when(userManagement.createUser(credentials)).thenReturn("User " +credentials[0] + " created");
        String infoFromServer = userManagement.createUser(credentials);

        assertEquals("User " +credentials[0] + " created",infoFromServer);
    }

    @Test
    void shouldDeleteUserAsAdmin() {
        when(userManagement.deleteUser(userTempName.getNickName())).thenReturn("user " +userTempName.getNickName()+ " deleted");
        String response = userManagement.deleteUser(userTempName.getNickName());

        assertEquals("user " +userTempName+ " deleted",response);
    }

    @Test
    void shouldReturnExistingUsers() {
        when(userManagement.getUsers()).thenReturn("[tata, tata_admin]");
        String response =userManagement.getUsers();

        assertEquals("[tata, tata_admin]", response);
        verify(userManagement, atLeastOnce()).getUsers();
    }

    @Test
    void shouldSendMsgWithSuccess(){
        when(userManagement.sendMsg(userTempName.getNickName(), "hej")).thenReturn("message sent");
        String response = userManagement.sendMsg(userTempName.getNickName(), "hej");

        assertEquals("message sent", response);
        verify(userManagement, atLeastOnce()).sendMsg(userTempName.getNickName(), "hej");
    }
}
