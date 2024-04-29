package server;


import controller.RequestGetterController;
import database.DataBaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private RequestGetterController requestGetterController;
    private static final Logger logger = LoggerFactory.getLogger(ServerConnectionImpl.class);


    @Override
    public void startConnection() {

        try {
            serverSocket = new ServerSocket(PropertiesUtils.serverPort);
            socket = serverSocket.accept();
            this.stream = new Stream(socket);
            this.requestGetterController = new RequestGetterController(stream, createdServerDate, createdInstant);
            while (true) {

                String msgFromClient = userInput();

                logger.info("Client: " + msgFromClient);

                if (!msgFromClient.contains("stop")) {
                    requestGetterController.getRequest(msgFromClient);
                } else {
                    logger.info("Received 'stop' command from client");
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("Server encountered an error "+e.getMessage());
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
            logger.info("Server: connection closed");
        } catch (IOException e) {
            logger.error("Attempt to close all streams failed", e);
        }
    }

    private String userInput() throws IOException {
        return stream.bufferedReader.readLine();
    }
}
