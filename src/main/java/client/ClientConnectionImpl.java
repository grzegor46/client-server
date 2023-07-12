package client;


import exception.NoConnectionToTheServer;
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

                stream.bufferedWriter.write(scanner.nextLine());
                stream.bufferedWriter.newLine();
                stream.bufferedWriter.flush();

                String msgFromServer = stream.bufferedReader.readLine();
                if (msgFromServer == null) {
                    throw new NoConnectionToTheServer("no connection with the server");
                }
                System.out.println("Server: " + msgFromServer);
            }
        } catch (IOException e) {
            System.out.println("failed to start connection to the server.");
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
            System.out.println("connection closed");
        } catch (IOException e) {
            System.out.println("attempt to close all streams failed");
            e.printStackTrace();
        }
    }
}
