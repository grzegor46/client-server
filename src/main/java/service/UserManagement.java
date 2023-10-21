package service;

import repository.UserRepository;
import user.User;

import java.io.IOException;

public class UserManagement {

    private final UserRepository userRepository = new UserRepository();

    public void createUser(String[] userInput) throws IOException {
        User user = new User(userInput[0], userInput[1], userInput[2]);
        userRepository.save(user);
    }

    public void deleteUser(String nickname) {
        userRepository.delete(nickname);
    }

    private void updateUser(String nickname) {

//        userRepository.update(nickname);
    }

    //   TODO  method createUser
    //   TODO delete user
    //   TODO updte user
}
