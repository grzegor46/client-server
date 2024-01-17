package repository;

import user.User;

import java.util.List;

public interface Repository {

    void save(User user);
    void update(String nickname, String passwordToChange);
    void delete(String nickname);
    void writeUsersToJson(List<User> userList);
    User findUserName(String name);
    List<User> getAllUsers();
}
