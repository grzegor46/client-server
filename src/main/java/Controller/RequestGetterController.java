package Controller;

import constant.Role;
import message.ServerMessage;
import service.UserManagement;
import utils.PropertiesUtils;
import utils.Stream;

import java.io.IOException;
import java.time.Instant;

import static service.UserManagement.activeUser;

public class RequestGetterController {

    private final Stream stream;
    private final Instant createdInstant;
    private final String createdServerDate;
    private UserManagement userManagement;

    public RequestGetterController(Stream stream, String date, Instant instant) {
        this.stream = stream;
        this.createdInstant = instant;
        this.createdServerDate = date;
        this.userManagement = new UserManagement();
    }

    public String getRequest(String commandFromClient) throws IOException {
        switch (commandFromClient) {
            case "help":
                stream.printWriter.println(ServerMessage.getHelp());
                return"";
            case "info":
                stream.printWriter.println(ServerMessage.getInfo(createdServerDate, PropertiesUtils.applicationVersion));
                return"";
            case "uptime":
                stream.printWriter.println(ServerMessage.getUpTime(createdInstant));
                return"";
            case "create user":
                createANewUser();
                return"";
            case "delete user":
                getUserNickNameToDelete();
                return"";
            case "update user":
                updateUserData();
                return"";
            case "login":
                logIn();
                return"";
            case "show users":
                getAllExistingUsers();
                return"";
            case "send msg":
                sendMsgToUser();
                return"";
            case "check mailbox":
                checkUserMailBox();
                return"";
            case "read mail":
                readUserMessage();
                return"";
            default:
                invalidCommand();
        }
        return commandFromClient;
    }

    private void readUserMessage() throws IOException {
        if (activeUser != null) {
            stream.printWriter.println("please type number of message to read it: 1 or 2 and etc.");
            String userChoice = userInput();
            stream.printWriter.println(userManagement.readMessage(userChoice));
        }
    }

    private void checkUserMailBox() {
        stream.printWriter.println(userManagement.checkMailBox());
    }

    private void sendMsgToUser() throws IOException {
        if (activeUser == null) {
            stream.printWriter.println("first log in to send msg --> ");

        } else {
            stream.printWriter.println("to which user do you want send a msg?");
            String receiver = userInput();
            stream.printWriter.println("type you message. Remember only 255 characters");
            String messageToSend = userInput();
            stream.printWriter.println(userManagement.sendMsg(receiver, messageToSend));
        }
    }

    private void getAllExistingUsers() {
        stream.printWriter.println(userManagement.getUsers());
    }

    private void createANewUser() throws IOException {
        stream.printWriter.println("write name");
        String name = userInput();
        stream.printWriter.println("write password");
        String password = userInput();
        String[] credentialsForNewUser = new String[] {name,password};
        stream.printWriter.println(userManagement.createUser(credentialsForNewUser));
    }

    private void logIn() throws IOException {
        if(activeUser == null) {
            stream.printWriter.println("write login:");
            String login = userInput();
            stream.printWriter.println("write password:");
            String password = userInput();
            stream.printWriter.println(userManagement.loginUser(login, password));
        } else {
            logout();
            stream.printWriter.println("You have been logged out from active profile, if you want to login to other account, please login again");
        }
    }
    private void logout() {
        activeUser = null;
    }

    private void getUserNickNameToDelete() throws IOException {
        stream.printWriter.println("write nickname to delete user");
        String name = userInput();
        if(!name.isEmpty()) {
            stream.printWriter.println(userManagement.deleteUser(name));
        } else {
            stream.printWriter.println("didn't write nickname");
        }
    }

    private void updateUserData() throws IOException {
        String nickname = "";
        String role = "";
        String password = "";
        if(activeUser.getRole().equals(Role.ADMIN)) {
            stream.printWriter.println("write nickname to update");
            nickname = userInput();
            stream.printWriter.println("what do you want to update: role or password?");
            String choice = userInput();
            if (choice.equals("role")) {
                stream.printWriter.println("which role do you want to add--> ADMIN/USER ? "); //
                role = userInput().toLowerCase();
            }
            if (choice.equals("password")) {
                stream.printWriter.println("Write new password: ");
                password = userInput().toLowerCase();
            }
            stream.printWriter.println(userManagement.updateUserDataAsAdmin(nickname, role, password));

        } else if (activeUser.getRole().equals(Role.USER)) {
            stream.printWriter.println("Write new password: ");
            password = userInput().toLowerCase();
            stream.printWriter.println(userManagement.updateUserDataAsUser(password));
        }
    }

    private String userInput() throws IOException {
        return stream.bufferedReader.readLine();
    }

    private void invalidCommand() {
        stream.printWriter.println("There is no such command");
    }
}
