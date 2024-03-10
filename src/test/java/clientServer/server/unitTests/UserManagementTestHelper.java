package clientServer.server.unitTests;

import service.UserManagement;

public class UserManagementTestHelper {

    UserManagement userManagement;


    public UserManagementTestHelper(UserManagement userManagement) {
        this.userManagement = userManagement;
    }

    public static String[] createCredentials() {
        String name = "tata";
        String[] credentials = {name, "tataPassword"};
        return credentials;
    }

    public void loginAsAdmin() {
        String[] credentialsAdmin = {"tata_admin", "tataPassword"};
        userManagement.createUser(credentialsAdmin);
        userManagement.loginUser("tata_admin", "tataPassword");
    }

    public void loginAsUser() {
        String[] credentialsAdmin = {"tata", "tataPassword"};
        userManagement.createUser(credentialsAdmin);
        userManagement.loginUser("tata", "tataPassword");
    }

    public String createTemporaryUser(){
        String name = "tata";
        String[] credentialsUser = {name, "tataPassword"};
        userManagement.createUser(credentialsUser);
        return name;
    }

    public void logoutActiveUser(){
        UserManagement.activeUser = null;
    }

}
