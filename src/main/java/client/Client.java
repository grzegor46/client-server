package client;


import exception.NoConnectionToTheServer;
import utils.PropertiesUtils;
import utils.Stream;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket = null;
    private final int serverPort = PropertiesUtils.serverPort;
    private final String hostNameServer = PropertiesUtils.hostNameServer;
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
                    scanner.close();
                    throw new NoConnectionToTheServer("server is not responding");
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
