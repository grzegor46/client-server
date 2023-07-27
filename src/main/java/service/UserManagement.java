package service;

import utils.Stream;

import java.net.ServerSocket;
import java.net.Socket;

public class UserManagement {

    private Socket socket;
    private ServerSocket serverSocket;
    private Stream stream;

    public UserManagement(Socket socket, ServerSocket serverSocket, Stream stream) {
        this.socket = socket;
        this.serverSocket = serverSocket;
        this.stream = stream;
    }

//    method createUser

}
