package user;


import message.UserMessage;

import java.util.ArrayList;
import java.util.List;


public class User {

    private String nickName;
    private String password;
    private String role;
    private List<UserMessage> mailBox;

    public User(String nickName, String password, String role) {
        this.nickName = nickName;
        this.password = password;
        this.role = role.toUpperCase();
        if(this.role.equalsIgnoreCase("user")) {
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

    public String getRole() {
        return role;
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

    public void setRole(String role) {
        this.role = role;
    }

    public void setMailBox(List<UserMessage> mailBox) {
        this.mailBox = mailBox;
    }
}


