package repository;

import message.UserMessage;
import user.User;

import java.util.List;

public interface Repository {

    void save(User user);
    void update(User userWithNewUpdatedData);
    void delete(String nickname);
    User findUserName(String name);
    List<String> getAllUsers();
    List<UserMessage> getUserMailBox(User user);
    void addMessageToMailbox(UserMessage sentMessage);
    int checkUnreadMessages(User user);
}
