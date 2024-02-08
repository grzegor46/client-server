package clientServer.server.integrationTests;

import client.Client;
import org.junit.jupiter.api.*;
import repository.Repository;
import repository.UserRepository;
import server.Server;
import service.UserManagement;
import utils.PropertiesUtils;
import utils.Stream;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserManagementIntegrationTest {

    private final Stream streamClient;
    private final Repository userRepository = new UserRepository();

    @Test
    void setUp(){
        // before running these tests, please run first server manually or run gradle script "runServer"
    }

    public UserManagementIntegrationTest() throws IOException {

        final int serverPort = PropertiesUtils.serverPort;
        final String hostNameServer = PropertiesUtils.hostNameServer;

        Socket socketCLient = new Socket(hostNameServer, serverPort);
        streamClient = new Stream(socketCLient);

    }

    @Test
    void shouldCreateUserWithSuccess() throws IOException {

        String msgToServer = "create user";
        streamClient.printWriter.println(msgToServer);

        String msgFromServer = streamClient.bufferedReader.readLine();
        streamClient.printWriter.println("user_nickname");
        msgFromServer = streamClient.bufferedReader.readLine();
        streamClient.printWriter.println("user_password");
        msgFromServer = streamClient.bufferedReader.readLine();
        assertEquals(msgFromServer,"User created");
        List<String> lines = Files.readAllLines(Path.of(PropertiesUtils.databasePath));
        for (String line : lines) {
            if (line.contains("user_nickname")) {
                System.out.println("yes!");
            }
        }
        new FileWriter(PropertiesUtils.databasePath, false).close();
    }

    @Test
    void test2() throws IOException {

        String msgToServer = "info";
        streamClient.printWriter.println(msgToServer);

        String msgFromServer = streamClient.bufferedReader.readLine();
        System.out.println(msgFromServer);

        assertEquals(msgFromServer, "{\"createdServerDate\":\"2024-02-06\",\"appVersion\":\"1.1.0\"}");


    }

    @AfterAll
    void stopTheServer() {
        streamClient.printWriter.println("stop");
    }
}
