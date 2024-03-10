package clientServer.server.unitTests;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import repository.UserRepository;
import service.UserManagement;
import user.User;
import utils.PropertiesUtils;

import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserManagementTest {

    private final UserManagement userManagement;
    public static User activeUser;
    private UserManagementTestHelper userManagementTestHelper;

    public UserManagementTest() {
        this.userManagement = new UserManagement();
    }


    @Test
    void shouldReturnCreatedUserWithSuccess() {
        String[] credentials = UserManagementTestHelper.createCredentials();
        String infoFromServer = userManagement.createUser(credentials);

        assertEquals("User " +credentials[0] + " created",infoFromServer);
    }

    @Test
    void shouldDeleteUserAsAdmin() {
        String name = "tata";
        String[] credentialsUser = {name, "tataPassword"};
        String infoFromServer = userManagement.createUser(credentialsUser);

        String[] credentialsAdmin = {"tata_admin", "tataPassword"};
        String infoFromServer1 = userManagement.createUser(credentialsAdmin);

        userManagement.loginUser("tata_admin", "tataPassword");

        String response = userManagement.deleteUser(name);

        assertEquals("user " +name+ " deleted",response);
    }

    @Test
    void shouldReturnExistingUsers() {
        String name = "tata";
        String[] credentialsUser = {name, "tataPassword"};
        String infoFromServer = userManagement.createUser(credentialsUser);

        String[] credentialsAdmin = {"tata_admin", "tataPassword"};
        String infoFromServer1 = userManagement.createUser(credentialsAdmin);
        userManagement.loginUser("tata_admin", "tataPassword");
        String response =userManagement.getUsers();
        assertEquals("[tata, tata_admin]", response);
    }

    @Test
    void shouldSendMsgWithSuccess(){
        String name = "tata";
        String[] credentialsUser = {name, "tataPassword"};
        String infoFromServer = userManagement.createUser(credentialsUser);

        String[] credentialsAdmin = {"tata_admin", "tataPassword"};
        String infoFromServer1 = userManagement.createUser(credentialsAdmin);
        userManagement.loginUser("tata_admin", "tataPassword");

        String response = userManagement.sendMsg(name, "hej");

        assertEquals("message sent", response);
    }

    @Test
    void shouldReturnContentOfMailWithSuccess(){
        String name = "tata";
        String[] credentialsUser = {name, "tataPassword"};
        String infoFromServer = userManagement.createUser(credentialsUser);

        String[] credentialsAdmin = {"tata_admin", "tataPassword"};
        String infoFromServer1 = userManagement.createUser(credentialsAdmin);
        userManagement.loginUser("tata_admin", "tataPassword");

        userManagement.sendMsg(name, "hej");

        userManagement.loginUser("tata_admin", "tataPassword");

        String response = userManagement.readMessage("1");
        assertEquals("mail", response);
    }


    @AfterEach
    void cleanUp() throws IOException {
        new FileWriter(PropertiesUtils.databasePath, false).close();
    }
}
