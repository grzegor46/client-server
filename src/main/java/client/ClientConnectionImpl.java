package client;

import database.DataBaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Connection;
import utils.PropertiesUtils;
import utils.Stream;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientConnectionImpl implements Connection {

    private static final Logger logger = LoggerFactory.getLogger(ClientConnectionImpl.class);

    private final int serverPort = PropertiesUtils.serverPort;
    private final String hostNameServer = PropertiesUtils.hostNameServer;
    private Socket socket = null;
    private Stream stream = null;

    @Override
    public void startConnection() {
        try {
            socket = new Socket(hostNameServer, serverPort);
            this.stream = new Stream(socket);

            communicationWithServer();

        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void communicationWithServer() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String msgToServer = scanner.nextLine();
            stream.printWriter.println(msgToServer);

            String msgFromServer = stream.bufferedReader.readLine();
            if (msgFromServer == null && msgToServer.equals("stop")) {
                break;
            }
            System.out.println("Server: " + msgFromServer);
        }
        scanner.close();
    }

    @Override
    public void closeConnection() {
        try {
            stream.closeStreams();
            socket.close();
            logger.info("Client: connection closed");
        } catch (IOException e) {
            logger.error("Attempt to close all streams failed", e);
        }
    }
}
