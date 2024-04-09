package repository;

import database.DataBaseManager;
import message.UserMessage;
import user.User;

import java.util.ArrayList;
import java.util.List;


public class UserRepository implements Repository {

    private final DataBaseManager dataBaseManager;
    private final List<User> userList;

    public UserRepository() {
        this.dataBaseManager = new DataBaseManager();
        this.userList = new ArrayList<>();
    }
    @Override
    public List<String> getAllUsers() {
        return dataBaseManager.getAllExistingUsers();
    }
    @Override
    public void save(User user) {
        dataBaseManager.add(user);
    }

    @Override
    public void addMessageToMailbox(UserMessage sentMessage) {
        dataBaseManager.addMessageToUserMessageTable(sentMessage);

    }
    @Override
    public void update(User userWithNewUpdatedData) {
        User userToUpdate = findUserName(userWithNewUpdatedData.getNickName());
        if (userToUpdate != null) {
            this.delete(userToUpdate.getNickName());
            this.save(userToUpdate);
        } else {
            System.out.println("User not found.");
        }
    }
    @Override
    public void delete(String nickname) {
//        TODO duplikacja znajdowania uzytkownika
        User foundUserToDelete = findUserName(nickname);

        dataBaseManager.deleteUserFromDB(foundUserToDelete);
    }
    @Override
    public User findUserName(String name) {
        return dataBaseManager.findUserInDB(name);
    }

    @Override
    public int getAmountOfUnReadMessages(User user) {
        return dataBaseManager.countUnReadUserMessages(user);
    }

    @Override
    public List<UserMessage> getUserMailBox(User user) {
        return dataBaseManager.getUserMessages(user);
    }
}
