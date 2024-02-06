package clientServer.server.integrationTests;

import client.Client;
import org.junit.jupiter.api.*;
import server.Server;
import service.UserManagement;
import utils.PropertiesUtils;
import utils.Stream;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserManagementIntegrationTest {

    @Test
    void setUp() {
        // before running these tests, please run first server manually or run gradle script "runServer"
    }

    Socket socketCLient;
    Stream streamClient;

    public UserManagementIntegrationTest() throws IOException {

        final int serverPort = PropertiesUtils.serverPort;
        final String hostNameServer = PropertiesUtils.hostNameServer;

        socketCLient = new Socket(hostNameServer, serverPort);
        streamClient = new Stream(socketCLient);

    }

    @Test
    void test() throws IOException {

        String msgToServer = "help";
        streamClient.printWriter.println(msgToServer);

        String msgFromServer = streamClient.bufferedReader.readLine();
        System.out.println(msgFromServer);

    }

    @Test
    void test2() throws IOException {

        String msgToServer = "info";
        streamClient.printWriter.println(msgToServer);

        String msgFromServer = streamClient.bufferedReader.readLine();
        System.out.println(msgFromServer);

        Assertions.assertEquals(msgFromServer, "{\"createdServerDate\":\"2024-02-06\",\"appVersion\":\"1.1.0\"}");


    }

    @AfterAll
    void stopTheServer() {
        streamClient.printWriter.println("stop");
    }

}
