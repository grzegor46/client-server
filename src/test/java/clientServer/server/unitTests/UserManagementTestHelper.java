package clientServer.server.unitTests;

import service.UserManagement;
import user.User;

public class UserManagementTestHelper {

    public UserManagementTestHelper() {
    }

    public static String[] createCredentials() {
        String name = "tata";
        String[] credentials = {name, "tataPassword"};
        return credentials;
    }

    public User createTemporaryUser(){
        String name = "tata";
        String[] credentialsUser = {name, "tataPassword"};
        User user = new User(credentialsUser[0],credentialsUser[1]);
        return user;
    }
}
