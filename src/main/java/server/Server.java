package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Server {

    public static void main(String[] args) throws IOException {

        Socket socketCLient = null;
        ServerSocket serverSocket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        InputStream inputStream = new FileInputStream("src/main/resources/application-server.properties");
        Properties prop = new Properties();
        prop.load(inputStream);

        serverSocket = new ServerSocket(Integer.parseInt(prop.getProperty("server.port")));

    }





}
