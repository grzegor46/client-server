package client;


import exception.NotLoadedPropertiesException;
import utils.LoadProperties;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class Client {

    private Socket socket = null;
    private InputStreamReader inputStreamReader = null;
    private OutputStreamWriter outputStreamWriter = null;
    private BufferedReader bufferedReader = null;
    private BufferedWriter bufferedWriter = null;
    private int serverPort = LoadProperties.serverPort;
    private String hostNameServer = LoadProperties.hostNameServer;


    private void connectToServerAndCreateStreams() throws IOException {
        socket = new Socket(hostNameServer, serverPort);
        inputStreamReader = new InputStreamReader(socket.getInputStream());
        outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

        bufferedReader = new BufferedReader(inputStreamReader);
        bufferedWriter = new BufferedWriter(outputStreamWriter);
    }

    private void startConnection() throws IOException {

        try {

            connectToServerAndCreateStreams();

            Scanner scanner = new Scanner(System.in);
            while (true) {

                String msgToSend = scanner.nextLine();
                bufferedWriter.write(msgToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();


                String msgFromServer =  bufferedReader.readLine();
                if(msgFromServer == null) {
                    closeConnection();
                    break;
                }
                System.out.println("Server: " + msgFromServer);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    private void closeConnection() throws IOException {
        bufferedReader.close();
        bufferedWriter.close();
        inputStreamReader.close();
        outputStreamWriter.close();
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.startConnection();
    }
}
