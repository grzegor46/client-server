package user;


import message.Message;

import java.util.ArrayList;
import java.util.List;


public class User {

    private String nickName;
    private String password;
    private String role;
    private List<Message> mailBox;

    public User(String nickName, String password, String role) {
        this.nickName = nickName;
        this.password = password;
        if(role.equals("user")) {
            this.mailBox = new ArrayList<>(5);
        } else {
            this.mailBox = new ArrayList<>();
        }
        this.role = role.toUpperCase();
    }

    public String getNickName() { return nickName; }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public List getMailBox() {
        return mailBox;
    }

}


