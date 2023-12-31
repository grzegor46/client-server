package client;


import exception.ConnectionLostException;
import utils.Connection;
import utils.PropertiesUtils;
import utils.Stream;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientConnectionImpl implements Connection {

    private final int serverPort = PropertiesUtils.serverPort;
    private final String hostNameServer = PropertiesUtils.hostNameServer;
    private Socket socket = null;
    private Stream stream = null;

    @Override
    public void startConnection() {
        try {

            socket = new Socket(hostNameServer, serverPort);
            this.stream = new Stream(socket);

            Scanner scanner = new Scanner(System.in);
            while (true) {

                String msgToServer = scanner.nextLine();
                stream.bufferedWriter.write(msgToServer);
                stream.bufferedWriter.newLine();
                stream.bufferedWriter.flush();

                String msgFromServer = stream.bufferedReader.readLine();
                if (msgFromServer == null && msgToServer.equals("stop")) {
                    break;
                }
                System.out.println("Server: " + msgFromServer);
            }
        } catch (IOException e) {
            System.out.println("Client encountered an error ");
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
            System.out.println("Client: connection closed");
        } catch (IOException e) {
            System.out.println("Attempt to close all streams failed");
            e.printStackTrace();
        }
    }
}
