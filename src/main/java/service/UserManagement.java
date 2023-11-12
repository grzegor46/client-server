package service;

import repository.UserRepository;
import user.User;


public class UserManagement {

    private final UserRepository userRepository = new UserRepository();

    public void createUser(String[] userInput) {
        User user = new User(userInput[0], userInput[1], userInput[2]);
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

}
