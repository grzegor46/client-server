package server;

import message.Message;
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



public class ServerConnectionImpl implements Connection {

    private final Instant createdInstant = Instant.now();
    private final String applicationVersion = PropertiesUtils.applicationVersion;
    private final String createdServerDate = LocalDate.now().toString();
    private Socket socket = null;
    private ServerSocket serverSocket;
    private Stream stream = null;
    private Message message;
    private UserManagement userManagement;
    public static User activeUser;



    @Override
    public void startConnection() {

        try {
            serverSocket = new ServerSocket(PropertiesUtils.serverPort);
            socket = serverSocket.accept();
            this.stream = new Stream(socket);
            this.message = new Message();
            this.userManagement = new UserManagement();
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
                return message.getHelp();
            case "info":
                return message.getInfo(createdServerDate, applicationVersion);
            case "uptime":
                return message.getUpTime(createdInstant);
            case "stop":
                closeConnection();
            case "create user":
                createUser();
                return "user created";
            case "delete user":
                deleteUser();
                return "user deleted";
            case "update user":
                updateUser();
                return "";
            case "login":
                loginUser();
                return "user logged";
            default:
                return "Invalid command";
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

    public void createUser() throws IOException {

        stream.bufferedWriter.write("write name");
        String name = userInput();

        stream.bufferedWriter.write("write password");
        String password = userInput();

        stream.bufferedWriter.write("write role");
        String role = userInput();

        userManagement.createUser(new String[]{name, password, role});
    }

    //TODO admin panel
    public void deleteUser() throws IOException {
        stream.bufferedWriter.write("write nickname to delete");
        String name = userInput();

        userManagement.deleteUser(name);
    }
    //TODO admin and User panel
    public void updateUser() throws IOException {
        stream.bufferedWriter.write("write nickname to update");
        String nickname = userInput();
        stream.bufferedWriter.write("write password to change");
        String passwordToChange = userInput();
        userManagement.updateUser(nickname,passwordToChange);
    }

    private String userInput() throws IOException {
        stream.bufferedWriter.newLine();
        stream.bufferedWriter.flush();
        return stream.bufferedReader.readLine();
    }

    private void loginUser() throws IOException {
        stream.bufferedWriter.write("write login");
        String login = userInput();
        activeUser = new User();
        activeUser.setNickName(login);

        stream.bufferedWriter.write("write password");
        String password = userInput();
        activeUser.setPassword(password);

        User user = userManagement.findUser(activeUser.getNickName());
        if(activeUser.getNickName().equals(user.getNickName()) && activeUser.getPassword().equals(user.getPassword())) {
            stream.bufferedWriter.write("user successfully logged in");
        } else {
            stream.bufferedWriter.write("there is no such user in DB");
        }
    }

}
// TODO w login stworz funckje z petla while i wysylaniem wiadomosci itp?