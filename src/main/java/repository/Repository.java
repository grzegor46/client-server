package repository;

import message.UserMessage;
import user.User;

import java.util.List;

public interface Repository {

    void save(User user);
    void update(User userWithNewUpdatedData);
    void delete(String nickname);
    User findUserName(String name);
    List<User> getAllUsers();

    List<UserMessage> getUserMailBox(User user);
    int getAmountOfUnReadMessages(User user);
    void addMessageToMailbox(UserMessage sentMessage);
}
