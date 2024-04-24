package clientServer.server.unitTests;


import constant.Role;
import message.UserMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import service.MessageManagement;
import service.UserManagement;
import user.User;
import utils.PropertiesUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserManagementTest {

    private final UserManagement userManagement;
    private UserManagementTestHelper userManagementTestHelper;
    private User UserTempName;


    public UserManagementTest() {
        userManagement = mock(UserManagement.class);
        userManagementTestHelper = new UserManagementTestHelper();
    }

    @BeforeEach
    public void setUp() {
        UserTempName = userManagementTestHelper.createTemporaryUser();
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
        when(userManagement.deleteUser(UserTempName.getNickName())).thenReturn("user " +UserTempName.getNickName()+ " deleted");
        String response = userManagement.deleteUser(UserTempName.getNickName());

        assertEquals("user " +UserTempName+ " deleted",response);
    }

    @Test
    void shouldReturnExistingUsers() {
        when(userManagement.getUsers()).thenReturn("[tata, tata_admin]");
        String response =userManagement.getUsers();

        verify(userManagement, atLeastOnce()).getUsers();

        assertEquals("[tata, tata_admin]", response);

    }
//
//    @Test
//    void shouldSendMsgWithSuccess(){
//        String nameTempUser = userManagementTestHelper.createTemporaryUser();
//        userManagementTestHelper.createAndLoginAsAdmin();
//        String response = userManagement.sendMsg(nameTempUser, "hej");
//
//        assertEquals("message sent", response);
//    }
//
//    @Test
//    void shouldReturnContentOfMailWithSuccess(){
//        String nameTempUser = userManagementTestHelper.createTemporaryUser();
//        userManagementTestHelper.createAndLoginAsAdmin();
//
//        userManagement.sendMsg(nameTempUser, "hej");
//
//        userManagementTestHelper.logoutActiveUser();
//        userManagementTestHelper.createAndLoginAsUser();
//
//        String response = userManagement.readMessage("1");
//        assertEquals("tata_admin: hej", response);
//    }
//
//    @Test
//    void shouldCheckMailBoxWithSuccess() {
//        String nameTempUser = userManagementTestHelper.createTemporaryUser();
//        userManagementTestHelper.createAndLoginAsAdmin();
//
//        userManagement.sendMsg(nameTempUser, "hej");
//
//        userManagementTestHelper.logoutActiveUser();
//        userManagementTestHelper.createAndLoginAsUser();
//
//        String response = userManagement.checkMailBox();
//        assertEquals("[{\"tata_admin\":\"hej\"}]", response);
//    }
//
//    @Test
//    void shouldUpdateRoleUserDataAsAdminWithSuccess(){
//        String nameTempUser = userManagementTestHelper.createTemporaryUser();
//        userManagementTestHelper.createAndLoginAsAdmin();
//        String response = userManagement.updateUserDataAsAdmin(nameTempUser,"admin","");
//        assertEquals("Role changed for user: " + nameTempUser+"_admin", response);
//    }
//
//    @Test
//    void shouldUpdatePasswordUserDataAsAdminWithSuccess(){
//        String nameTempUser = userManagementTestHelper.createTemporaryUser();
//        userManagementTestHelper.createAndLoginAsAdmin();
//        String response = userManagement.updateUserDataAsAdmin(nameTempUser,"","NewPassword");
//        assertEquals("Password changed for user: " + nameTempUser, response);
//    }
//
//    @Test
//    void shouldUpdatePasswordUserDataAsUserWithSuccess(){
//        String nameTempUser = userManagementTestHelper.createTemporaryUser();
//        userManagementTestHelper.createAndLoginAsUser();
//        String response = userManagement.updateUserDataAsUser("NewPassword");
//        assertEquals("Password changed for user: " + nameTempUser, response);
//    }
//
//    @AfterEach
//    void cleanUp() throws IOException {
//        userManagementTestHelper.logoutActiveUser();
//        new FileWriter(PropertiesUtils.databasePath, false).close();
//
//    }
}
