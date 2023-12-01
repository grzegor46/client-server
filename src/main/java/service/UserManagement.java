package service;

import constant.Role;
import repository.UserRepository;
import user.User;
import utils.Stream;

import java.io.IOException;
import java.util.List;


public class UserManagement {

    private final UserRepository userRepository = new UserRepository();
    public static User activeUser = null;
    private Stream stream;

    public UserManagement(Stream stream) {
        this.stream = stream;
    }



    void takeRequest(String commandFromClient) {
//        take over switch from responsemethod?
    }




    public void loginUser() throws IOException {
        if (activeUser == null) {
            stream.printWriter.println("write login:");
            String login = userInput();
            activeUser = new User();
            activeUser.setNickName(login);

            stream.printWriter.println("write password:");
            String password = userInput();
            activeUser.setPassword(password);

            User user = findUser(activeUser.getNickName());
            if (user != null && activeUser.getNickName().equals(user.getNickName()) && activeUser.getPassword().equals(user.getPassword())) {
                activeUser.setRole(user.getRole());
                stream.printWriter.println("user successfully logged in as: " + activeUser.getNickName());
            } else {
                stream.printWriter.println("there is no such user in DB");
            }
        } else {
            //  logout current user and login with new credentials
            activeUser = null;
            this.loginUser();
        }
    }

// TODO make as interface?
    private String userInput() throws IOException {
        return stream.bufferedReader.readLine();
    }






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
