package server;


import service.UserManagement;
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
    private final String createdServerDate = LocalDate.now().toString();
    private Socket socket = null;
    private ServerSocket serverSocket;
    private Stream stream = null;
    private UserManagement userManagement;


    @Override
    public void startConnection() {

        try {
            serverSocket = new ServerSocket(PropertiesUtils.serverPort);
            socket = serverSocket.accept();
            this.stream = new Stream(socket);
            this.userManagement = new UserManagement(stream, createdServerDate, createdInstant);
            while (true) {

                String msgFromClient = userInput();

                System.out.println("Client: " + msgFromClient);

                if (!msgFromClient.contains("stop")) {
                    userManagement.takeRequest(msgFromClient);
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

    private String userInput() throws IOException {
        return stream.bufferedReader.readLine();
    }
}
