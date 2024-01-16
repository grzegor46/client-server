package user;


import constant.Role;
import message.UserMessage;

import java.util.ArrayList;
import java.util.List;


public class User {
    private String nickName;
    private String password;
    private Role userRole;
    private List<UserMessage> mailBox;

    public User(String nickName, String password, Role userRole) {
        this.nickName = nickName;
        this.password = password;
        this.userRole = userRole;
        if(this.userRole.equals(Role.USER)) {
            this.mailBox = new ArrayList<>(5);
        } else {
            this.mailBox = new ArrayList<>();
        }
    }

    public User(String nickName, String password) {
        this.nickName = nickName;
        this.password = password;
    }

    // this constructor should be because during deserialization (reading from file) error appears
    public User() {
    }

    public String getNickName() { return nickName; }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return userRole;
    }

    public List<UserMessage> getMailBox() {
        return mailBox;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role userRole) {
        this.userRole = userRole;
    }

    public void setMailBox(List<UserMessage> mailBox) {
        this.mailBox = mailBox;
    }

    @Override
    public String toString() {
        return this.getNickName();
    }
}


