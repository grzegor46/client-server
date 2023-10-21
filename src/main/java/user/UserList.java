package user;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.List;


//@JsonRootName("Users")
public class UserList {

    List<User> userList;

    public UserList() {
        this.userList = new ArrayList<>();
    }

    public void add(User user) {
        this.userList.add(user);
    }

    @JsonValue
    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
