package service;

import constant.Role;
import message.ServerMessage;
import message.UserMessage;
import repository.Repository;
import repository.UserRepository;
import user.User;
import utils.PropertiesUtils;
import utils.Stream;

import java.io.IOException;
import java.time.Instant;
import java.util.List;


public class UserManagement {

    private final Repository userRepository;
    public static User activeUser;
    private final MessageManagement messageManagement;

        public UserManagement() {
        this.userRepository = new UserRepository();
        this.messageManagement = new MessageManagement(this.userRepository);
    }

    public String checkMailBox(){
        if (activeUser != null) {
        List<String> mailbox = messageManagement.checkMailBox(activeUser);
        if(mailbox == null) {
            return "your mailbox is empty";
        } else {
            return mailbox.toString();
        }
        }else {
            return "you need to be logged to check users";
        }
    }

    public String loginUser(String nicknameToLogIn, String password)  {
        if (activeUser == null) {

            activeUser = new User();
            activeUser.setNickName(nicknameToLogIn);
            activeUser.setPassword(password);

            User user = findUser(activeUser.getNickName());
            if (user != null && activeUser.getNickName().equals(user.getNickName()) && activeUser.getPassword().equals(user.getPassword())) {
                activeUser = user;
                return "user successfully logged in as: " + activeUser.getNickName();
            } else {
               return "there is no such user in DB or incorrect password";
            }
        }
        return "action login met a problem";
    }

    public String deleteUser(String name){
        if(activeUser != null){
            if (activeUser.getRole().equals(Role.ADMIN)) {
                this.deleteUserFromDataBase(name);
                return "user " +name+ " deleted";
            } else {
                return "you don't have permission";
            }
        } else {
            return "you need to be logged to delete user data";
        }
    }

    public String getUsers(){
        if (activeUser != null) {
            List<User> users = showUsers();
            return users.toString();
        } else {
            return "you need to be logged to check list of users";
        }
    }

    public String updateUserDataAsAdmin(String userNicknameToDataUpdate, String role, String password){
        User user;
        if(activeUser != null){
            if (activeUser.getRole().equals(Role.ADMIN)) {
                user = findUser(userNicknameToDataUpdate);
                if(user != null) {
                    if (!role.isEmpty()) {
                        changeRoleName(user, role);
                        updateUser(user);
                        return "Role changed for user: " + user.getNickName();
                    }
                    if (!password.isEmpty()) {
                        changePassword(user, password);
                        updateUser(user);
                        return "Password changed for user: " + user.getNickName();
                    }
                }else {
                    return "there is no such user in DB";
                }
            }
        } else {
            return "you need to be logged to update user data";
        }
        return "action update as admin met problem";
    }
    public String updateUserDataAsUser(String password) {
        User user;
        if(activeUser != null){
            if (activeUser.getRole().equals(Role.USER)) {
                user = findUser(activeUser.getNickName());
                if(user != null) {
                    changePassword(user, password);
                    updateUser(user);
                    return "Password changed for user: " + user.getNickName();
                }else {
                    return "there is no such user in DB";
                }
            }
        } else {
            return "you need to be logged to update user data";
        }
        return "operation update met problem";
    }

    private void changeRoleName(User user, String role){
        if (role.equals("user") && user.getNickName().endsWith("_admin")) {
            int position  = user.getNickName().lastIndexOf("_admin");
            user.setNickName(user.getNickName().substring(0,position));
        } else {
        user.setNickName(user.getNickName() + "_admin");
        user.setRole(Role.ADMIN);

        }
    }
    private void changePassword(User user, String newPassword)  {
        user.setPassword(newPassword);
    }

    public void createUser(String[] credentials) {
        Role userRole;

        if(credentials[0].endsWith("_admin")) {
            userRole = Role.ADMIN;
        } else {
            userRole = Role.USER;
        }
        User user = new User(credentials[0], credentials[1], userRole);

        saveUser(user);
    }

    public String sendMsg(String receiver, String messageToSend) {
        User existingUser = findUser(receiver);
        if (existingUser != null) {
            int mailBoxCapacity = messageManagement.countUnreadUserMsgs(existingUser);
            if ((mailBoxCapacity < 5 && existingUser.getRole().equals(Role.USER)) || existingUser.getRole().equals(Role.ADMIN)) {
                UserMessage userMessage = new UserMessage(activeUser.getNickName(), receiver, messageToSend);
                messageManagement.sendMessage(userMessage);
                return "message sent";
            } else {
                return "user has more than 5 msgs";
            }
        } else {
            return "didn't find user";
        }
    }

    public String readMessage(String userChoice) throws IOException {
        int numberOfMessage = Integer.parseInt(userChoice)-1;
        List<UserMessage> userMailBox = activeUser.getMailBox();
        if(userMailBox.isEmpty()) {
            return "there are no mails to read";
        } else {
            return messageManagement.readMessageFromMailBox(activeUser,numberOfMessage);
        }
    }


    private void deleteUserFromDataBase(String nickname) {
        userRepository.delete(nickname);
    }

    private void updateUser(User userWithUpdatedData) {
        userRepository.update(userWithUpdatedData);
    }

    public User findUser(String nickname) {
        return userRepository.findUserName(nickname);
    }

    private void saveUser(User user) {
        userRepository.save(user);
    }

    private List<User> showUsers() {
        return userRepository.getAllUsers();
    }

}
