package server;

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
    private Scanner scanner = new Scanner(System.in);
    private String applicationVersion;

    private void loadProperties() throws IOException {
        InputStream inputStream = new FileInputStream("src/main/resources/application-server.properties");
        Properties prop = new Properties();
        prop.load(inputStream);
        serverSocket = new ServerSocket(Integer.parseInt(prop.getProperty("server.port")));
        applicationVersion = prop.getProperty("application.version");
    }

    private void startServer() throws IOException {

        try {

            socket = serverSocket.accept();

            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);

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
                return scanner.nextLine();
        }
    }

    private String getHelpCommand(){
        return "_uptime_ - zwraca czas życia serwera, _info_ - zwraca numer wersji serwera, datę jego utworzenia, _help_ - zwraca listę dostępnych komend z krótkim opisem (tych komend, z tej listy, którą właśnie czytasz, czyli inaczej, komend realizowanych przez serwer), _stop_ - zatrzymuje jednocześnie serwer i klienta";
    }

    private String getInfoCommand(){
        return "server was created: " + createdServerDate + ", version of this app is: " + applicationVersion ;
    }

//    TODO refactor --> after 60 sec is 61 and gets more
    private String getUpTimeCommand() {
        Duration duration = Duration.between(createdInstant, Instant.now());
        long durationSeconds = duration.getSeconds() % 60;
        long durationMinutes = duration.toMinutes() % 60;
        long durationHours = duration.toHours();
        long durationDays = duration.toDays();

        return durationDays + "days : " +durationHours+ "hours : " + durationMinutes+ "minutes : " + durationSeconds+"seconds ";
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
