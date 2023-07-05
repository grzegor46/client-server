package server;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Properties;
import java.util.Scanner;

public class Server {

    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private InputStreamReader inputStreamReader = null;
    private OutputStreamWriter outputStreamWriter = null;
    private BufferedReader bufferedReader = null;
    private BufferedWriter bufferedWriter = null;
    private String createdServerDate = LocalDate.now().toString();
    private Instant createdInstant = Instant.now();
    private String applicationVersion;
    private ObjectMapper objectMapper = new ObjectMapper();

    private void loadProperties() throws IOException {
        InputStream inputStream = new FileInputStream("src/main/resources/application-server.properties");
        Properties prop = new Properties();
        prop.load(inputStream);
        serverSocket = new ServerSocket(Integer.parseInt(prop.getProperty("server.port")));
        applicationVersion = prop.getProperty("application.version");
    }

    private void acceptClientAndCreateStreams() throws IOException {
        socket = serverSocket.accept();
        inputStreamReader = new InputStreamReader(socket.getInputStream());
        outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

        bufferedReader = new BufferedReader(inputStreamReader);
        bufferedWriter = new BufferedWriter(outputStreamWriter);
    }

    private void startServer() throws IOException {

        try {
            acceptClientAndCreateStreams();
            while (true) {

                String msgFromClient = bufferedReader.readLine();

                System.out.println("Client: " + msgFromClient);

                if(!msgFromClient.contains("stop")) {
                    String msgToClient = returnResponse(msgFromClient);

                    bufferedWriter.write(msgToClient);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeConnection();
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
        helpCommandAsJson.put("info","zwraca numer wersji serwera, datę jego utworzenia");
        helpCommandAsJson.put("uptime","zwraca czas życia serwera");
        helpCommandAsJson.put("help","zwraca listę dostępnych komend z krótkim opisem");
        helpCommandAsJson.put("stop","zatrzymuje jednocześnie serwer i klienta");

        String jsonString = helpCommandAsJson.toString();
        return jsonString;
    }

    private String getInfoCommand(){
        ObjectNode infoCommandAsJson = objectMapper.createObjectNode();
        infoCommandAsJson.put("createdServerDate",createdServerDate);
        infoCommandAsJson.put("appVersion",applicationVersion);

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
        upTimeCommandAsJson.put("days",durationDays);
        upTimeCommandAsJson.put("hours",durationHours);
        upTimeCommandAsJson.put("minutes",durationMinutes);
        upTimeCommandAsJson.put("seconds",durationSeconds);

        String jsonString = upTimeCommandAsJson.toString();
        return jsonString;
    }

    private void closeConnection() throws IOException {
        bufferedReader.close();
        bufferedWriter.close();
        inputStreamReader.close();
        outputStreamWriter.close();
        socket.close();
        serverSocket.close();

    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.loadProperties();
        server.startServer();
    }
}
