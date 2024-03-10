package clientServer.server.unitTests;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import service.UserManagement;
import utils.PropertiesUtils;

import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserManagementTest {

    private final UserManagement userManagement;
    private UserManagementTestHelper userManagementTestHelper;

    public UserManagementTest() {
        this.userManagement = new UserManagement();
        this.userManagementTestHelper = new UserManagementTestHelper(userManagement);
    }


    @Test
    void shouldReturnCreatedUserWithSuccess() {
        String[] credentials = UserManagementTestHelper.createCredentials();
        String infoFromServer = userManagement.createUser(credentials);

        assertEquals("User " +credentials[0] + " created",infoFromServer);
    }

    @Test
    void shouldDeleteUserAsAdmin() {
        String UserTempName = userManagementTestHelper.createTemporaryUser();
        userManagementTestHelper.createAndLoginAsAdmin();
        String response = userManagement.deleteUser(UserTempName);

        assertEquals("user " +UserTempName+ " deleted",response);
    }

    @Test
    void shouldReturnExistingUsers() {
        userManagementTestHelper.createTemporaryUser();
        userManagementTestHelper.createAndLoginAsAdmin();
        String response =userManagement.getUsers();
        assertEquals("[tata, tata_admin]", response);
    }

    @Test
    void shouldSendMsgWithSuccess(){
        String nameTempUser = userManagementTestHelper.createTemporaryUser();
        userManagementTestHelper.createAndLoginAsAdmin();
        String response = userManagement.sendMsg(nameTempUser, "hej");

        assertEquals("message sent", response);
    }

    @Test
    void shouldReturnContentOfMailWithSuccess(){
        String nameTempUser = userManagementTestHelper.createTemporaryUser();
        userManagementTestHelper.createAndLoginAsAdmin();

        userManagement.sendMsg(nameTempUser, "hej");

        userManagementTestHelper.logoutActiveUser();
        userManagementTestHelper.loginAsUser();

        String response = userManagement.readMessage("1");
        assertEquals("tata_admin: hej", response);
    }

    @Test
    void shouldUpdateRoleUserDataAsAdminWithSuccess(){
        String nameTempUser = userManagementTestHelper.createTemporaryUser();
        userManagementTestHelper.createAndLoginAsAdmin();
        String response = userManagement.updateUserDataAsAdmin(nameTempUser,"admin","");
        assertEquals("Role changed for user: " + nameTempUser+"_admin", response);
    }

    @AfterEach
    void cleanUp() throws IOException {
        new FileWriter(PropertiesUtils.databasePath, false).close();
    }
}
