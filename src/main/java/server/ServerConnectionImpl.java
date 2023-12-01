package server;

import constant.Role;
import exception.NoUserFoundException;
import message.ServerMessage;
import message.UserMessage;
import service.MessageManagement;
import service.UserManagement;
import user.User;
import utils.Connection;
import utils.PropertiesUtils;
import utils.Stream;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ServerConnectionImpl implements Connection {

    public static User activeUser = null;
    private final Instant createdInstant = Instant.now();
    private final String createdServerDate = LocalDate.now().toString();
    private Socket socket = null;
    private ServerSocket serverSocket;
    private Stream stream = null;
    private ServerMessage serverMessage;
    private UserManagement userManagement;
    private MessageManagement messageManagement;



    @Override
    public void startConnection() {

        try {
            serverSocket = new ServerSocket(PropertiesUtils.serverPort);
            socket = serverSocket.accept();
            this.stream = new Stream(socket);
            this.serverMessage = new ServerMessage();
            this.userManagement = new UserManagement();
            this.messageManagement = new MessageManagement();
            while (true) {

                String msgFromClient = userInput();

                System.out.println("Client: " + msgFromClient);

                if (!msgFromClient.contains("stop")) {
                    returnResponse(msgFromClient);

                } else {
                    System.out.println("Received 'stop' command from client");
                    break;

                }
            }
        } catch (IOException e) {
            System.out.println("Server encountered an error ");
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void returnResponse(String msgFromClient) throws IOException {
        switch (msgFromClient) {
            case "help":
                stream.printWriter.println(serverMessage.getHelp());
            case "info":
                stream.printWriter.println(serverMessage.getInfo(createdServerDate, PropertiesUtils.applicationVersion));
            case "uptime":
                stream.printWriter.println(serverMessage.getUpTime(createdInstant));
            case "stop":
                closeConnection();
            case "create user":
                createUser();
            case "delete user":
                deleteUser();
            case "update user":
                updateUser();
            case "login":
                loginUser();
            case "show users":
                getUsers();
            case "send msg":
                sendMsg();
            case "check mailbox":
                checkMailBox();
            default:
                invalidCommand();
        }
    }

    private void checkMailBox() {
        if (activeUser != null) {
            User user = userManagement.findUser(activeUser.getNickName());
            List<UserMessage> userMailBox = user.getMailBox();
            List<String> stringList = new ArrayList<>();
            for (UserMessage userMsgs : userMailBox) {

                String mail =  messageManagement.getMessageAsJsonRepresentation(userMsgs.getSender(),userMsgs.getContent());
                stringList.add(mail);

            }
            stream.printWriter.println(stringList);
        } else {
            stream.printWriter.println("you need to be logged to check users");
        }
    }

    @Override
    public void closeConnection() {
        try {
            serverSocket.close();
            stream.closeStreams();
            socket.close();
            System.out.println("Server: connection closed");
        } catch (IOException e) {
            System.out.println("Attempt to close all streams failed");
            e.printStackTrace();
        }
    }

    private void createUser() throws IOException {

        stream.printWriter.println("write name");
        String name = userInput();

        stream.printWriter.println("write password");
        String password = userInput();

        userManagement.createUser(name,password);
        stream.printWriter.println("User created");
    }

    private void deleteUser() throws IOException {

        if (activeUser.getRole().equals(Role.ADMIN)) {
            stream.printWriter.println("write nickname to delete user");
            String name = userInput();
            userManagement.deleteUser(name);
            stream.printWriter.println("user deleted");
        } else {
            stream.printWriter.println("you don't have permission");
        }
    }

    private void getUsers(){
        if (activeUser != null) {
            List<User> users = userManagement.showUsers();
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
        userManagement.updateUser(nickname, newPassword);

        stream.printWriter.println("Password changed for user: " + nickname);
    }

    private String userInput() throws IOException {
        return stream.bufferedReader.readLine();
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

            User user = userManagement.findUser(activeUser.getNickName());
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

    private void sendMsg() throws IOException {
        if (activeUser == null) {
            stream.printWriter.write("first log in to send msg --> ");
            loginUser();
        } else {
            stream.printWriter.println("to which user do you want send a msg?");
            String receiver = userInput();
            User existingUser = userManagement.findUser(receiver);
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

    private void invalidCommand() {
        stream.printWriter.println("There is no such command");
    }
}
//TODO przenies wszystko do userManagement