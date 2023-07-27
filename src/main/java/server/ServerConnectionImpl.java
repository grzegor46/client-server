package server;

import exception.ConnectionLostException;
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

    @Override
    public void startConnection() {

        try {

            serverSocket = new ServerSocket(PropertiesUtils.serverPort);
            socket = serverSocket.accept();
            this.stream = new Stream(socket);
            this.message = new Message();
            this. userManagement = new UserManagement(socket, serverSocket, stream);
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
//            TODO    CREATING USER
//                UserManagement.createUser();
                return "user created";
            case "login":
//            TODO   LOGGING USER
                return "logged out";
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

}
// TODO w login stworz funckje z petla while i wysylaniem wiadomosci itp?