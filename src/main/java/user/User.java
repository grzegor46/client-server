package user;

import constant.Role;
import message.Message;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String nickName;
    private String password;
    private Role role;
    private List<Message> mailBox;


    public User(String nickName, String password, Role role) {
        this.nickName = nickName;
        this.password = password;
        this.role = role;
        if(role.equals(Role.USER)) {
            this.mailBox = new ArrayList<>(5);
        } else {
            this.mailBox = new ArrayList<>();
        }
    }

    public String getNickName() { return nickName; }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public List getMailBox() {
        return mailBox;
    }
}
