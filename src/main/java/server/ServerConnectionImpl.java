package server;

import constant.Role;
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

                String msgFromClient = stream.bufferedReader.readLine();

                System.out.println("Client: " + msgFromClient);

                if (!msgFromClient.contains("stop")) {
                    String msgToClient = returnResponse(msgFromClient);

                    stream.bufferedWriter.write(msgToClient);
                    stream.bufferedWriter.newLine();
                    stream.bufferedWriter.flush();
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

    private String returnResponse(String msgFromClient) throws IOException {
        switch (msgFromClient) {
            case "help":
                return serverMessage.getHelp();
            case "info":
                return serverMessage.getInfo(createdServerDate, PropertiesUtils.applicationVersion);
            case "uptime":
                return serverMessage.getUpTime(createdInstant);
            case "stop":
                closeConnection();
            case "create user":
                createUser();
                return "user created";
            case "delete user":
                deleteUser();
                return "";
            case "update user":
                updateUser();
                return "";
            case "login":
                loginUser();
                return "";
            case "show users":
                getUsers();
                return "";
            case "send msg":
                sendMsg();
                return "";
            case "check mailbox":
                checkMailBox();
                return "";
            default:
                return "Invalid command";
        }
    }

    private void checkMailBox() throws IOException {
        if (activeUser != null) {
            User user = userManagement.findUser(activeUser.getNickName());
            List<UserMessage> userMailBox = user.getMailBox();
            List<String> stringList = new ArrayList<>();
            for (UserMessage userMsgs : userMailBox) {

                String mail =  messageManagement.getMessageAsJsonRepresentation(userMsgs.getSender(),userMsgs.getContent());
                stringList.add(mail);

            }
            stream.bufferedWriter.write(stringList.toString());
        } else {
            stream.bufferedWriter.write("you need to be logged to check users");
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

        stream.bufferedWriter.write("write name");
        String name = userInput();

        stream.bufferedWriter.write("write password");
        String password = userInput();

        userManagement.createUser(name,password);
    }

    private void deleteUser() throws IOException {

        if (activeUser.getRole().equals(Role.ADMIN)) {
            stream.bufferedWriter.write("write nickname to delete user");
            String name = userInput();
            userManagement.deleteUser(name);
            stream.bufferedWriter.write("user deleted");
        } else {
            stream.bufferedWriter.write("you don't have permission");
        }
    }

    private void getUsers() throws IOException {
        if (activeUser != null) {
            List<User> users = userManagement.showUsers();
            for (User user : users) {
                stream.bufferedWriter.write(user.getNickName() + ", ");
            }
        } else {
            stream.bufferedWriter.write("you need to be logged to check users");
        }
    }

    private void updateUser() throws IOException {
        String nickname = null;

        if (activeUser.getRole().equals(Role.ADMIN)) {
            stream.bufferedWriter.write("write nickname to update");
            nickname = userInput();

        } else if (activeUser.getRole().equals(Role.USER)) {
            nickname = activeUser.getNickName();
        }
        stream.bufferedWriter.write("Write new password: ");
        String newPassword = userInput();
        userManagement.updateUser(nickname, newPassword);

        stream.bufferedWriter.write("Password changed for user: " + nickname);
    }

    private String userInput() throws IOException {
        stream.bufferedWriter.newLine();
        stream.bufferedWriter.flush();
        return stream.bufferedReader.readLine();
    }

    private void loginUser() throws IOException {
        if (activeUser == null) {
            stream.bufferedWriter.write("write login");
            String login = userInput();
            activeUser = new User();
            activeUser.setNickName(login);

            stream.bufferedWriter.write("write password");
            String password = userInput();
            activeUser.setPassword(password);

            User user = userManagement.findUser(activeUser.getNickName());
            if (activeUser.getNickName().equals(user.getNickName()) && activeUser.getPassword().equals(user.getPassword())) {
                activeUser.setRole(user.getRole());
                stream.bufferedWriter.write("user successfully logged in as: " + activeUser.getNickName());
            } else {
                stream.bufferedWriter.write("there is no such user in DB");
            }
        } else {
            //  logout current user and login with new credentials
            activeUser = null;
            this.loginUser();
        }
    }

    private void sendMsg() throws IOException {
        if (activeUser == null) {
            stream.bufferedWriter.write("first log in to send msg --> ");
            loginUser();
        } else {
            stream.bufferedWriter.write("to which user do you want send a msg?");
            String receiver = userInput();
            User existingUser = userManagement.findUser(receiver);
            if (existingUser != null) {
                stream.bufferedWriter.write("type you message. Remember only 255 characters");
                String messageToSend = userInput();
                int mailBoxCapacity = existingUser.getMailBox().size();
                if ((mailBoxCapacity < 5 && existingUser.getRole().equals(Role.USER)) || existingUser.getRole().equals(Role.ADMIN)) {
                    UserMessage userMessage = new UserMessage(activeUser.getNickName(), receiver, messageToSend);
                    messageManagement.sendMessage(userMessage);
                } else {
                    stream.bufferedWriter.write("user has more than 5 msgs");
                }
            } else {
                stream.bufferedWriter.write("didn't find user");
            }
        }
    }
}

// TODO check if msgs can be more than 5 in mailbox
// TODO check if is allwed more than 255 chars in content
// TODO przetestuj funkcje gdy uzytkownik ma wiecej niz 5 wiadomosci -> tylko USER, admin powinien miec nieograniczona