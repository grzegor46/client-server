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
//    private Stream stream;
//    private final Instant createdInstant;
//    private final String createdServerDate;
    private final MessageManagement messageManagement;

//    public UserManagement(Stream stream,String date, Instant instant) {
        public UserManagement() {
//        this.stream = stream;
//        this.createdInstant = instant;
//        this.createdServerDate = date;
        this.userRepository = new UserRepository();
        this.messageManagement = new MessageManagement(this.userRepository);
    }


//    public String takeRequest(String commandFromClient) throws IOException {
//        switch (commandFromClient) {
//            case "help":
//                stream.printWriter.println(ServerMessage.getHelp());
//                return"";
//            case "info":
//                stream.printWriter.println(ServerMessage.getInfo(createdServerDate, PropertiesUtils.applicationVersion));
//                return"";
//            case "uptime":
//                stream.printWriter.println(ServerMessage.getUpTime(createdInstant));
//                return"";
//            case "create user":
////                createUser();
//                setCredentialsForNewUser();
//                return"User created";
//            case "delete user":
//                deleteUser();
//                return"";
//            case "update user":
//                updateUser();
//                return"";
//            case "login":
//                loginUser();
//                return"";
//            case "show users":
//                getUsers();
//                return"";
//            case "send msg":
//                sendMsg();
//                return"";
//            case "check mailbox":
//                checkMailBox();
//                return"";
//            case "read mail":
//                readMessage();
//                return"";
//            default:
//                invalidCommand();
//        }
//        return commandFromClient;
//    }

    private void checkMailBox(){
        if (activeUser != null) {
        List<String> mailbox = messageManagement.checkMailBox(activeUser);
        if(mailbox == null) {
            stream.printWriter.println("your mailbox is empty");
        } else {
            stream.printWriter.println(mailbox);
        }
        }else {
            stream.printWriter.println("you need to be logged to check users");
        }
    }

    private void loginUser() throws IOException {
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
                activeUser = user;
                stream.printWriter.println("user successfully logged in as: " + activeUser.getNickName());
            } else {
                stream.printWriter.println("there is no such user in DB or incorrect password");
            }
        } else {
            //  logout current user and login with new credentials
            activeUser = null;
            this.loginUser();
        }
    }

    private void deleteUser() throws IOException {
        if(activeUser != null){
            if (activeUser.getRole().equals(Role.ADMIN)) {
                stream.printWriter.println("write nickname to delete user");
                String name = userInput();
                this.deleteUserFromDataBase(name);
                stream.printWriter.println("user deleted");
            } else {
                stream.printWriter.println("you don't have permission");
            }
        } else {
            stream.printWriter.println("you need to be logged to delete user data");
        }
    }

    private void getUsers(){
        if (activeUser != null) {
            List<User> users = showUsers();
            stream.printWriter.println(users.toString());
        } else {
            stream.printWriter.println("you need to be logged to check list of users");
        }
    }

    private void updateUser() throws IOException {
        User user;
        if(activeUser != null){
            if (activeUser.getRole().equals(Role.ADMIN)) {
                stream.printWriter.println("write nickname to update");
                String nickname = userInput();
                user = findUser(nickname);
                if(user != null) {
                    stream.printWriter.println("what do you want to update: role or password?");
                    String userChoice = userInput().toLowerCase();
                    if (userChoice.equals("role")) {
                        changeRoleName(user);
                    }
                    if (userChoice.equals("password")) {
                        changePassword(user);
                    }
                    updateUser(user);
                }else {
                    stream.printWriter.println("there is no such user in DB");
                }
            } else if (activeUser.getRole().equals(Role.USER)) {
                user = findUser(activeUser.getNickName());
                changePassword(user);
                updateUser(user);
            }
        } else {
            stream.printWriter.println("you need to be logged to update user data");
        }
    }

    private void changeRoleName(User user) throws IOException {
        stream.printWriter.println("which role do you want add? ");
        String role = userInput().toLowerCase();
        if (role.equals("user") && user.getNickName().endsWith("_admin")) {
            int position  = user.getNickName().lastIndexOf("_admin");
            user.setNickName(user.getNickName().substring(0,position));
        } else {
        user.setNickName(user.getNickName() + "_admin");
        user.setRole(Role.ADMIN);
        }
        stream.printWriter.println("Role changed for user: " + user.getNickName());
    }
    private void changePassword(User user) throws IOException {
        stream.printWriter.println("Write new password: ");
        String newPassword = userInput();
        user.setPassword(newPassword);
        stream.printWriter.println("Password changed for user: " + user.getNickName());
    }

//    private String userInput() throws IOException {
//        return stream.bufferedReader.readLine();
//    }

    public void createUser(String[] credentials) throws IOException {
        Role userRole;

//        stream.printWriter.println("write name");
//        String name = userInput();

        if(credentials[0].endsWith("_admin")) {
            userRole = Role.ADMIN;
        } else {
            userRole = Role.USER;
        }

//        stream.printWriter.println("write password");
//        String password = userInput();

        User user = new User(credentials[0], credentials[1], userRole);

        saveUser(user);
//        stream.printWriter.println("User created");
    }

    private void sendMsg() throws IOException {
        if (activeUser == null) {
            stream.printWriter.write("first log in to send msg --> ");
            loginUser();
        } else {
            stream.printWriter.println("to which user do you want send a msg?");
            String receiver = userInput();
            User existingUser = findUser(receiver);
            if (existingUser != null) {
                stream.printWriter.println("type you message. Remember only 255 characters");
                String messageToSend = userInput();
                int mailBoxCapacity = messageManagement.countUnreadUserMsgs(existingUser);
                if ((mailBoxCapacity < 5 && existingUser.getRole().equals(Role.USER)) || existingUser.getRole().equals(Role.ADMIN)) {
                    UserMessage userMessage = new UserMessage(activeUser.getNickName(), receiver, messageToSend);
                    messageManagement.sendMessage(userMessage);
                    stream.printWriter.println("message sent");
                } else {
                    stream.printWriter.println("user has more than 5 msgs");
                }
            } else {
                stream.printWriter.println("didn't find user");
            }
        }
    }

    private void readMessage() throws IOException {
        stream.printWriter.println("please type number of message to read it: 1 or 2 and etc.");
        int numberOfMessage = Integer.parseInt(userInput())-1;
        List<UserMessage> userMailBox = activeUser.getMailBox();
        if(userMailBox.isEmpty()) {
            stream.printWriter.println("there are no mails to read");
        } else {
            stream.printWriter.println(messageManagement.readMessageFromMailBox(activeUser,numberOfMessage));
        }
    }

//    private void invalidCommand() {
//        stream.printWriter.println("There is no such command");
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
