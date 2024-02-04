package clientServer.server;

import client.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserManagementIntegrationTest {

    Client client;
    Server server;

    @BeforeEach
    void setUpServerAndClient() throws InterruptedException {
        server = new Server();
        Thread.sleep(5000);
        client = new Client();
    }

    @Test
    void shouldReturnListOfExistingUsers() {
        assertNotNull(client);
        assertNotNull(server);
    }

}
