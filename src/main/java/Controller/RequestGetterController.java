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
//                createUser();
                setCredentialsForNewUser();
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
            default:
                invalidCommand();
        }
        return commandFromClient;
    }
//    TODO add activeUser here?

//    return user Created from userManagement method
    private void setCredentialsForNewUser() throws IOException {
        stream.printWriter.println("write name");
        String name = userInput();
        stream.printWriter.println("write password");
        String password = userInput();
        String[] credentialsForNewUser = new String[] {name,password};
        userManagement.createUser(credentialsForNewUser);
        stream.printWriter.println("User created");
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
            stream.printWriter.println("didnt write nickname");
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
