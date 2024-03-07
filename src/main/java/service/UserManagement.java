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
    public static User activeUser=null;
    private final MessageManagement messageManagement;

        public UserManagement() {
        this.userRepository = new UserRepository();
        this.messageManagement = new MessageManagement(this.userRepository);
    }

//    private void checkMailBox(){
//        if (activeUser != null) {
//        List<String> mailbox = messageManagement.checkMailBox(activeUser);
//        if(mailbox == null) {
//            stream.printWriter.println("your mailbox is empty");
//        } else {
//            stream.printWriter.println(mailbox);
//        }
//        }else {
//            stream.printWriter.println("you need to be logged to check users");
//        }
//    }

//    private void loginUser() throws IOException {
//        if (activeUser == null) {
//            stream.printWriter.println("write login:");
//            String login = userInput();
//            activeUser = new User();
//            activeUser.setNickName(login);
//
//            stream.printWriter.println("write password:");
//            String password = userInput();
//            activeUser.setPassword(password);
//
//            User user = findUser(activeUser.getNickName());
//            if (user != null && activeUser.getNickName().equals(user.getNickName()) && activeUser.getPassword().equals(user.getPassword())) {
//                activeUser = user;
//                stream.printWriter.println("user successfully logged in as: " + activeUser.getNickName());
//            } else {
//                stream.printWriter.println("there is no such user in DB or incorrect password");
//            }
//        } else {
//            //  logout current user and login with new credentials
//            activeUser = null;
//            this.loginUser();
//        }
//    }

    public String deleteUser(String name){
        if(activeUser != null){
            if (activeUser.getRole().equals(Role.ADMIN)) {
                this.deleteUserFromDataBase(name);
                return "user deleted";
            } else {
                return "you don't have permission";
            }
        } else {
            return "you need to be logged to delete user data";
        }
    }

//    private void getUsers(){
//        if (activeUser != null) {
//            List<User> users = showUsers();
//            stream.printWriter.println(users.toString());
//        } else {
//            stream.printWriter.println("you need to be logged to check list of users");
//        }
//    }

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
                    if (password.equals("password")) {
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
    private void changePassword(User user, String password)  {
        String newPassword = password;
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

//    private void sendMsg() throws IOException {
//        if (activeUser == null) {
//            stream.printWriter.write("first log in to send msg --> ");
//            loginUser();
//        } else {
//            stream.printWriter.println("to which user do you want send a msg?");
//            String receiver = userInput();
//            User existingUser = findUser(receiver);
//            if (existingUser != null) {
//                stream.printWriter.println("type you message. Remember only 255 characters");
//                String messageToSend = userInput();
//                int mailBoxCapacity = messageManagement.countUnreadUserMsgs(existingUser);
//                if ((mailBoxCapacity < 5 && existingUser.getRole().equals(Role.USER)) || existingUser.getRole().equals(Role.ADMIN)) {
//                    UserMessage userMessage = new UserMessage(activeUser.getNickName(), receiver, messageToSend);
//                    messageManagement.sendMessage(userMessage);
//                    stream.printWriter.println("message sent");
//                } else {
//                    stream.printWriter.println("user has more than 5 msgs");
//                }
//            } else {
//                stream.printWriter.println("didn't find user");
//            }
//        }
//    }

//    private void readMessage() throws IOException {
//        stream.printWriter.println("please type number of message to read it: 1 or 2 and etc.");
//        int numberOfMessage = Integer.parseInt(userInput())-1;
//        List<UserMessage> userMailBox = activeUser.getMailBox();
//        if(userMailBox.isEmpty()) {
//            stream.printWriter.println("there are no mails to read");
//        } else {
//            stream.printWriter.println(messageManagement.readMessageFromMailBox(activeUser,numberOfMessage));
//        }
//    }


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

    public List<User> showUsers() {
        return userRepository.getAllUsers();
    }

}
