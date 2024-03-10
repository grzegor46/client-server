package clientServer.server.unitTests;

import org.junit.jupiter.api.Test;

public class UserManagementTestHelper {

    UserManagementTest userManagementTest;

    public UserManagementTestHelper(UserManagementTest userManagementTest) {
        this.userManagementTest = userManagementTest;
    }

    public static String[] createCredentials() {
        String name = "tata";
        String[] credentials = {name, "tataPassword"};
        return credentials;
    }
}
