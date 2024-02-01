package repository;

import database.DataBaseManager;
import user.User;

import java.util.List;


public class UserRepository implements Repository {

    private final DataBaseManager dataBaseManager;
    private final List<User> userList;

    public UserRepository() {
        this.dataBaseManager = new DataBaseManager();
        this.userList = dataBaseManager.readUsersFromJson();
    }

    public List<User> getAllUsers() {
        return this.userList;
    }

    public void save(User user) {
        this.userList.add(user);
        dataBaseManager.writeUsersToJson(this.userList);
    }

    public void update(User userWithNewUpdatedData) {
        User userToUpdate = findUserName(userWithNewUpdatedData.getNickName());
        if (userToUpdate != null) {
            this.delete(userToUpdate.getNickName());
            this.save(userToUpdate);
        } else {
            System.out.println("User not found.");
        }
    }

    public void delete(String nickname) {
        User userToDelete = findUserName(nickname);

        if (userToDelete != null) {
            boolean isUserDeleted = this.userList.remove(userToDelete);
            if (isUserDeleted) {
                dataBaseManager.writeUsersToJson(this.userList);
            }
        } else {
            System.out.println("User not found.");
        }
    }

    public User findUserName(String name) {
        for (User user : this.userList) {
            if (user.getNickName().equalsIgnoreCase(name.toLowerCase())) {
                return user;
            }
        }
        return null;
    }
}
