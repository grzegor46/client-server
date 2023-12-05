package service;

import constant.Role;
import message.ServerMessage;
import message.UserMessage;
import repository.UserRepository;
import user.User;
import utils.PropertiesUtils;
import utils.Stream;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class UserManagement {

    private final UserRepository userRepository = new UserRepository();
    public static User activeUser = null;
    private Stream stream;
    private final Instant createdInstant;
    private final String createdServerDate;
    private MessageManagement messageManagement;

    public UserManagement(Stream stream,String date, Instant instant) {
        this.stream = stream;
        this.createdInstant = instant;
        this.createdServerDate = date;
        this.messageManagement = new MessageManagement();
    }


    public void takeRequest(String commandFromClient) throws IOException {
        switch (commandFromClient) {
            case "help":
                stream.printWriter.println(ServerMessage.getHelp());
                return;
            case "info":
                stream.printWriter.println(ServerMessage.getInfo(createdServerDate, PropertiesUtils.applicationVersion));
                return;
            case "uptime":
                stream.printWriter.println(ServerMessage.getUpTime(createdInstant));
                return;
            case "create user":
                createUser();
                return;
            case "delete user":
                deleteUser();
                return;
            case "update user":
                updateUser();
                return;
            case "login":
                loginUser();
                return;
            case "show users":
                getUsers();
                return;
            case "send msg":
                sendMsg();
                return;
            case "check mailbox":
                checkMailBox();
                return;
            default:
                invalidCommand();
        }
    }

//    checkMailBox(User user/activeUser){
//    stream.printWriter(messageManagement.checkMailbox(activeUser))
//    }


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

    private void deleteUser() throws IOException {

        if (activeUser.getRole().equals(Role.ADMIN)) {
            stream.printWriter.println("write nickname to delete user");
            String name = userInput();
            deleteUser(name);
            stream.printWriter.println("user deleted");
        } else {
            stream.printWriter.println("you don't have permission");
        }
    }

    private void getUsers(){
        if (activeUser != null) {
            List<User> users = showUsers();
            for (User user : users) {
                stream.printWriter.println(user.getNickName() + ", ");
            }
        } else {
            stream.printWriter.println("you need to be logged to check list of users");
        }
    }

    private void updateUser() throws IOException {
        String nickname = null;

        if (activeUser.getRole().equals(Role.ADMIN)) {
            stream.printWriter.println("write nickname to update");
            nickname = userInput();

        } else if (activeUser.getRole().equals(Role.USER)) {
            nickname = activeUser.getNickName();
        }
        stream.printWriter.println("Write new password: ");
        String newPassword = userInput();
        updateUser(nickname, newPassword);

        stream.printWriter.println("Password changed for user: " + nickname);
    }

// TODO make as interface?
    private String userInput() throws IOException {
        return stream.bufferedReader.readLine();
    }

    private void createUser() throws IOException {
        Role userRole;

        stream.printWriter.println("write name");
        String name = userInput();

        if(name.endsWith("_admin")) {
            userRole = Role.ADMIN;
        } else {
            userRole = Role.USER;
        }

        stream.printWriter.println("write password");
        String password = userInput();

        User user = new User(name, password, userRole);
        userRepository.save(user);

        stream.printWriter.println("User created");
    }

    private void sendMsg() throws IOException {
        if (activeUser == null) {
            stream.printWriter.write("first log in to send msg --> ");
//            loginUser();
        } else {
            stream.printWriter.println("to which user do you want send a msg?");
            String receiver = userInput();
            User existingUser = findUser(receiver);
            if (existingUser != null) {
                stream.printWriter.println("type you message. Remember only 255 characters");
                String messageToSend = userInput();
                int mailBoxCapacity = existingUser.getMailBox().size();
                if ((mailBoxCapacity < 5 && existingUser.getRole().equals(Role.USER)) || existingUser.getRole().equals(Role.ADMIN)) {
                    UserMessage userMessage = new UserMessage(activeUser.getNickName(), receiver, messageToSend);
                    messageManagement.sendMessage(userMessage);
                } else {
                    stream.printWriter.println("user has more than 5 msgs");
                }
            } else {
                stream.printWriter.println("didn't find user");
            }
        }
    }

    private void checkMailBox() {
        if (activeUser != null) {
            User user = findUser(activeUser.getNickName());
            List<UserMessage> userMailBox = user.getMailBox();
            List<String> stringList = new ArrayList<>();
            for (UserMessage userMsgs : userMailBox) {

                String mail;
                if(!userMsgs.isRead()) {
                    mail = messageManagement.getMessageAsJsonRepresentation(userMsgs.getSender(), userMsgs.getContent().substring(0, 5) + "...");
                } else {
                    mail = messageManagement.getMessageAsJsonRepresentation(userMsgs.getSender(), userMsgs.getContent());
                }
                stringList.add(mail);
            }
            stream.printWriter.println(stringList);
        } else {
            stream.printWriter.println("you need to be logged to check users");
        }
    }

    private void readMessage() throws IOException {
        stream.printWriter.println("please type number of message to read it");
        String numberOfMessage = userInput();

    }

    private void invalidCommand() {
        stream.printWriter.println("There is no such command");
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
