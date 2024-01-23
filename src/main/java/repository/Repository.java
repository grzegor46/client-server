package repository;

import user.User;

import java.util.List;

public interface Repository {

    void save(User user);
    void update(User userWithNewUpdatedData);
    void delete(String nickname);
    User findUserName(String name);
    List<User> getAllUsers();
}
