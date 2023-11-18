package service;

import constant.Role;
import repository.UserRepository;
import user.User;

import java.util.List;


public class UserManagement {

    private final UserRepository userRepository = new UserRepository();

    public void createUser(String nickname, String password) {

        Role userRole;
        if(nickname.endsWith("_admin")) {
            userRole = Role.ADMIN;
        } else {
            userRole = Role.USER;
        }
        User user = new User(nickname, password, userRole);
        userRepository.save(user);
    }

    public void deleteUser(String nickname) {
        userRepository.delete(nickname);
    }

    public void updateUser(String nickname, String passwordToChange) {
        userRepository.update(nickname,passwordToChange);
    }

    public User findUser(String nickname) {
        return userRepository.findUserName(nickname);
    }

    public List<User> showUsers() {
        return userRepository.getAllUsers();
    }

}
