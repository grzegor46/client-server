package service;

import constant.Role;

import message.UserMessage;
import repository.Repository;
import repository.UserRepository;
import user.User;

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
//         TODO   add auth with password here
            activeUser = userRepository.findUserName(nicknameToLogIn);
            if(activeUser.getNickName().equals(nicknameToLogIn) && activeUser.getPassword().equals(password)) {
                return "user successfully logged in as: " + activeUser.getNickName();
            }
        }
        activeUser = null;
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
            List<String> listOfUsers = showUsers();
            return listOfUsers.toString();
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
                        return "Role changed for user: " + user.getNickName() + " to" + user.getRole();
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
            if(user.getRole().equals(Role.USER) && role.equals("admin")) {
                user.setRole(Role.ADMIN);
            } else {
                user.setRole(Role.USER);
            }
    }
    private void changePassword(User user, String newPassword)  {
        user.setPassword(newPassword);
    }

    public String createUser(String[] credentials) {
        Role userRole = credentials[0].endsWith("_admin") ? Role.ADMIN : Role.USER;
        if (userRole == Role.ADMIN) {
            credentials[0] = credentials[0].substring(0, credentials[0].lastIndexOf("_admin"));
        }

        User user = new User(credentials[0], credentials[1], userRole);
        saveUser(user);
        return "User " + user.getNickName() + " created";
    }

    public String sendMsg(String receiver, String messageToSend) {
        UserMessage userMessage = new UserMessage(activeUser.getNickName(), receiver, messageToSend);
        messageManagement.sendMessage(userMessage);
        return "message sent";
    }

    public String readMessage(String userChoice) {
        int numberOfMessage = Integer.parseInt(userChoice)-1;
        return messageManagement.readMessageFromMailBox(activeUser,numberOfMessage);
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

    private List<String> showUsers() {
        return userRepository.getAllUsers();
    }

}
