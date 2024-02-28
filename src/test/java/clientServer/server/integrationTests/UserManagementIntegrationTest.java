package clientServer.server.integrationTests;

import client.Client;
import com.fasterxml.jackson.databind.util.JSONPObject;
import database.DataBaseManager;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import repository.Repository;
import repository.UserRepository;
import server.Server;
import service.UserManagement;
import user.User;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserManagementIntegrationTest {

    private final Stream streamClient;

    @Test
    void setUp(){
        // before running these tests, please run first server manually or run gradle script "runServer" ->
        // in terminal please type command --> "./gradlew runServer"
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

        String msgFromServer = streamClient.bufferedReader.readLine(); // receiving info from server
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
    }

    @Test
    void shouldAsAdminDeleteExistingUserInDataBase() throws IOException {
        createListOfUsersInDB();

        String msgToServer = "login";
        streamClient.printWriter.println(msgToServer);

        String msgFromServer = streamClient.bufferedReader.readLine();
        System.out.println(msgFromServer);
        streamClient.printWriter.println("user_nickname_admin");

        msgFromServer = streamClient.bufferedReader.readLine(); //write password
        streamClient.printWriter.println("user_password");
        msgFromServer = streamClient.bufferedReader.readLine();
        System.out.println(msgFromServer);
        // logged as admin

        // step to delete user
        streamClient.printWriter.println("delete user");
        msgFromServer = streamClient.bufferedReader.readLine();
        streamClient.printWriter.println("user_nickname");
        msgFromServer = streamClient.bufferedReader.readLine();
        System.out.println(msgFromServer);
        assertEquals(120,new File(PropertiesUtils.databasePath).length());
    }
//TODO false positive --> adjust that test
    @Test
    void shouldUpdateDataUserInDB() throws IOException, InterruptedException {
        createUserWithUserRoleInDB();
        List<User> userListBefore = new DataBaseManager().readUsersFromJson();
        String password = String.valueOf(userListBefore.get(0).getPassword());

        String msgToServer = "login";
        streamClient.printWriter.println(msgToServer);

        String msgFromServer = streamClient.bufferedReader.readLine();
        System.out.println(msgFromServer);
        streamClient.printWriter.println("user_nickname");

        msgFromServer = streamClient.bufferedReader.readLine(); //write password
        streamClient.printWriter.println("user_password");
        msgFromServer = streamClient.bufferedReader.readLine();
        System.out.println(msgFromServer);


        streamClient.printWriter.println("update user");
        msgFromServer = streamClient.bufferedReader.readLine();
        streamClient.printWriter.println("user_newPassword1");
        msgFromServer = streamClient.bufferedReader.readLine();
        System.out.println(msgFromServer);

        List<User> userListafter = new DataBaseManager().readUsersFromJson();
        String passwordAfter = String.valueOf(userListafter.get(0).getPassword());

        System.out.println(password);
        System.out.println(passwordAfter);
//        TODO adjust that
//        assertSame(f1, f2);
        assertEquals(password, passwordAfter);


    }

    @Test
    void logInAsExistingUserInDataBase() throws IOException {
        createUserWithUserRoleInDB();
        //TODO wydziel czesc wspolna
        String msgToServer = "login";
        streamClient.printWriter.println(msgToServer);

        String msgFromServer = streamClient.bufferedReader.readLine();
        System.out.println(msgFromServer);
        streamClient.printWriter.println("user_nickname");

        msgFromServer = streamClient.bufferedReader.readLine(); //write password
        streamClient.printWriter.println("user_password");
         msgFromServer = streamClient.bufferedReader.readLine();
        System.out.println(msgFromServer);
         assertEquals(msgFromServer, "user successfully logged in as: user_nickname");
    }



    void createListOfUsersInDB() throws IOException {
        FileWriter dB = new FileWriter(PropertiesUtils.databasePath);
        dB.write("[{\"nickName\":\"user_nickname\",\"password\":\"user_password\",\"mailBox\":[],\"role\":\"USER\"},{\"nickName\":\"user_nickname_admin\",\"password\":\"user_password\",\"mailBox\":[],\"role\":\"ADMIN\"}]");
        dB.close();
    }

    void createUserWithUserRoleInDB() throws IOException {
        FileWriter dB = new FileWriter(PropertiesUtils.databasePath);
        dB.write("[{\"nickName\":\"user_nickname\",\"password\":\"user_password\",\"mailBox\":[],\"role\":\"USER\"}]");
        dB.close();
    }

    void createUserWithAdminRoleInDB() throws IOException {
        FileWriter dB = new FileWriter(PropertiesUtils.databasePath);
        dB.write("[{\"nickName\":\"user_nickname_admin\",\"password\":\"user_password\",\"mailBox\":[],\"role\":\"ADMIN\"}]");
        dB.close();
    }

//    @AfterEach
//    void cleanUpDataBase() throws IOException {
//        cleanUpDB();
//    }
//
//    @AfterAll
//    void stopTheServer() throws IOException {
//        cleanUpDB();
//        streamClient.printWriter.println("stop");
//        streamClient.closeStreams();
//    }

    void cleanUpDB() throws IOException {
        new FileWriter(PropertiesUtils.databasePath, false).close();
    }

}
