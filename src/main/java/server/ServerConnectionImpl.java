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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;


public class ServerConnectionImpl implements Connection {

    public static User activeUser = null;
    private final Instant createdInstant = Instant.now();
    private final String applicationVersion = PropertiesUtils.applicationVersion;
    private final String createdServerDate = LocalDate.now().toString();
    private Socket socket = null;
    private ServerSocket serverSocket;
    private Stream stream = null;
    private ServerMessage serverMessage;
    private UserManagement userManagement;

    private MessageManagement messageManagement;

    private PrintWriter printWriter;

    @Override
    public void startConnection() {

        try {
            serverSocket = new ServerSocket(PropertiesUtils.serverPort);
            socket = serverSocket.accept();
            this.stream = new Stream(socket);
            this.serverMessage = new ServerMessage();
            this.userManagement = new UserManagement();
            this.printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
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
                return serverMessage.getInfo(createdServerDate, applicationVersion);
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
                showUser();
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
            List<UserMessage> userMailBox = userManagement.findUser(activeUser.getNickName()).getMailBox();

            for (UserMessage userMsgs : userMailBox) {
                stream.bufferedWriter.write(userMsgs.getContent() + ";   ");
            }
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

//        stream.bufferedWriter.write("write name");
        printWriter.println("write name");
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

    private void showUser() throws IOException {
        if (activeUser != null) {
            List<User> users = userManagement.showUsers();
            for (User user : users) {
                stream.bufferedWriter.write(user.getNickName() + ", ");
            }
        } else {
            stream.bufferedWriter.write("you need to be logged to check users");
        }
    }

    //TODO admin and User panel
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
                stream.bufferedWriter.write("user successfully logged in");
            } else {
                stream.bufferedWriter.write("there is no such user in DB");
            }
        } else {
            activeUser = null;
            this.loginUser();
        }
    }

    private void sendMsg() throws IOException {
        stream.bufferedWriter.write("to which user do you want send a msg?");
        String receiver = userInput();

        if(userManagement.findUser(receiver)!=null) {

            stream.bufferedWriter.write("type you message. Remember only 255 characters");
            String messageToSend = userInput();
            UserMessage userMessage = new UserMessage(activeUser.getNickName(), receiver, messageToSend);
            messageManagement.sendMessage(userMessage);
        }else {
            stream.bufferedWriter.write("didn't find user");
        }
    }
}

// TODO check if msgs can be more than 5 in mailbox
// TODO check if is allwed more than 255 chars in content
// TODO refactor stream.bufferedWriter.write, maybe printwriter?