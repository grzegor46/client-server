package client;


import utils.LoadProperties;
import utils.Stream;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket = null;
    private final int serverPort = LoadProperties.serverPort;
    private final String hostNameServer = LoadProperties.hostNameServer;
    private Stream stream = null;

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.startConnection();
    }

    private void startConnection() throws IOException {

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
                    System.out.println("server is closed");
                    scanner.close();
                    break;
                }
                System.out.println("Server: " + msgFromServer);
            }
        } finally {
            System.out.println("closing connection");
            closeConnection();
        }
    }

    private void closeConnection() {
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
