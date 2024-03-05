package clientServer.server.unitTests;

import constant.Role;
import org.junit.jupiter.api.Test;
import user.User;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private String nickName = "JanKowalski";
    private String password = "secretPassword";
    private Role roleAsUser = Role.USER;
    private Role roleAsAdmin = Role.ADMIN;


    @Test
    void shouldCreateUserWithUserRole(){
        User user = new User(nickName,password,roleAsUser);
        assertEquals(user.getRole(), Role.USER);
    }

    @Test
    void shouldCreateUserWithAdminRole(){
        User user = new User(nickName,password,roleAsAdmin);
        assertEquals(user.getRole(), Role.ADMIN);
    }

    @Test
    void shouldCreateUserWithSpecificCredentials() {
        User user = new User(nickName,password,roleAsUser);
        assertEquals(nickName, user.getNickName());
        assertEquals(password,user.getPassword());
    }
}
