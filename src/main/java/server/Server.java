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

                String msgToClient = returnResponse(msgFromClient);
                bufferedWriter.write(msgToClient);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                if (msgFromClient.equalsIgnoreCase("stop")) {
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    private String returnResponse(String msgFromClient) {
        switch (msgFromClient) {
            case "help":
                return getHelpCommand();
            case "info":
                return getInfoCommand();
            case "uptime":
                return getUpTimeCommand();
//            case "stop":
//                return getStopServerCommand();
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

//    TODO refactor --> after 60 sec is 61 and get more
    private String getUpTimeCommand() {
        Duration duration = Duration.between(createdInstant, Instant.now());
        long getSeconds = duration.getSeconds();
        long getMinutes = getSeconds/60;
        long getHours = getMinutes/24;
        long getDays = getHours/24;
        String time = String.valueOf(getDays + "days : " +getHours+ "hours : " + getMinutes+ "minutes : " + getSeconds+"seconds ");
        return time;
    }

    private void closeConnection() throws IOException {
        socket.close();
        inputStreamReader.close();
        outputStreamWriter.close();
        bufferedReader.close();
        bufferedWriter.close();
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.loadProperties();
        server.startServer();
    }

}
