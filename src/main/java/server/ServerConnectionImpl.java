package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import utils.Connection;
import utils.PropertiesUtils;
import utils.Stream;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

public class ServerConnectionImpl implements Connection {

    private final Instant createdInstant = Instant.now();
    private final String applicationVersion = PropertiesUtils.applicationVersion;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Socket socket = null;
    private ServerSocket serverSocket;
    private final String createdServerDate = LocalDate.now().toString();
    private Stream stream = null;


    @Override
    public void startConnection() {

        try {

            serverSocket = new ServerSocket(PropertiesUtils.serverPort);
            socket = serverSocket.accept();
            this.stream = new Stream(socket);

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
                return getHelpCommand();
            case "info":
                return getInfoCommand();
            case "uptime":
                return getUpTimeCommand();
            case "stop":
                closeConnection();
            default:
                return "Invalid command";
        }
    }

    private String getHelpCommand() {
        ObjectNode helpCommandAsJson = objectMapper.createObjectNode();
        helpCommandAsJson.put("info", "zwraca numer wersji serwera, datę jego utworzenia");
        helpCommandAsJson.put("uptime", "zwraca czas życia serwera");
        helpCommandAsJson.put("help", "zwraca listę dostępnych komend z krótkim opisem");
        helpCommandAsJson.put("stop", "zatrzymuje jednocześnie serwer i klienta");

        String jsonString = helpCommandAsJson.toString();
        return jsonString;
    }

    private String getInfoCommand() {
        ObjectNode infoCommandAsJson = objectMapper.createObjectNode();
        infoCommandAsJson.put("createdServerDate", createdServerDate);
        infoCommandAsJson.put("appVersion", applicationVersion);

        String jsonString = infoCommandAsJson.toString();
        return jsonString;
    }

    private String getUpTimeCommand() {
        ObjectNode upTimeCommandAsJson = objectMapper.createObjectNode();
        Duration duration = Duration.between(createdInstant, Instant.now());
        long durationSeconds = duration.getSeconds() % 60;
        long durationMinutes = duration.toMinutes() % 60;
        long durationHours = duration.toHours();
        long durationDays = duration.toDays();
        upTimeCommandAsJson.put("days", durationDays);
        upTimeCommandAsJson.put("hours", durationHours);
        upTimeCommandAsJson.put("minutes", durationMinutes);
        upTimeCommandAsJson.put("seconds", durationSeconds);

        String jsonString = upTimeCommandAsJson.toString();
        return jsonString;
    }

}
