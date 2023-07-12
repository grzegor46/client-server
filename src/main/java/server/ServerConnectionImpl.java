package server;

import message.Message;
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


    @Override
    public void startConnection() {

        try {

            serverSocket = new ServerSocket(PropertiesUtils.serverPort);
            socket = serverSocket.accept();
            this.stream = new Stream(socket);
            this.message = new Message();

            while (true) {

                String msgFromClient = stream.bufferedReader.readLine();

                System.out.println("Client: " + msgFromClient);

                if (!msgFromClient.contains("stop")) {
                    String msgToClient = returnResponse(msgFromClient);

                    stream.bufferedWriter.write(msgToClient);
                    stream.bufferedWriter.newLine();
                    stream.bufferedWriter.flush();
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    @Override
    public void closeConnection() {
        try {
            stream.closeStreams();
            socket.close();
            serverSocket.close();
            System.out.println("connection closed");
        } catch (IOException e) {
            System.out.println("attempt to close all streams failed");
            e.printStackTrace();
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
            default:
                return "Invalid command";
        }
    }

}
