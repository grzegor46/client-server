package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Server {

    public static void main(String[] args) throws IOException {


        Socket socket = null;
        ServerSocket serverSocket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        InputStream inputStream = new FileInputStream("src/main/resources/application-server.properties");
        Properties prop = new Properties();
        prop.load(inputStream);

        serverSocket = new ServerSocket(Integer.parseInt(prop.getProperty("server.port")));

        try {

            socket = serverSocket.accept();

            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);


            while (true) {


                String msgFromClient = bufferedReader.readLine();

                System.out.println("Client: " + msgFromClient);

                bufferedWriter.write("Message received");
                bufferedWriter.newLine();
                bufferedWriter.flush();

                if (msgFromClient.equalsIgnoreCase("stop")) {
                    break;
                }
            }
            socket.close();
            inputStreamReader.close();
            outputStreamWriter.close();
            bufferedReader.close();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
