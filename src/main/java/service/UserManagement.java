package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import repository.UserRepository;
import user.User;
import utils.PropertiesUtils;
import utils.Stream;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UserManagement {

    private Socket socket;
    private ServerSocket serverSocket;
    private Stream stream;
    private UserRepository userRepository;

    public UserManagement(Socket socket, ServerSocket serverSocket, Stream stream) {
        this.socket = socket;
        this.serverSocket = serverSocket;
        this.stream = stream;
        this.userRepository = new UserRepository();
    }


    public void createUser() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        serverSocket = new ServerSocket(PropertiesUtils.serverPort);
        socket = serverSocket.accept();
        this.stream = new Stream(socket);
        stream.bufferedWriter.write("write name");
        String name = stream.bufferedReader.readLine();
        stream.bufferedWriter.write("write pssword");
        String password = stream.bufferedReader.readLine();
        stream.bufferedWriter.write("write role");
        String role = stream.bufferedReader.readLine();
        User user = new User(name, password, role);
        objectMapper.writeValue(new File("src/main/java/database/UserDB.json"), user);
    }

    //   TODO  method createUser
    //   TODO delete user
    //   TODO updte user
}
