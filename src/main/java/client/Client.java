package client;

import utils.Connection;

public class Client {

    Connection connection = new ClientConnectionImpl();

    public static void main(String[] args) {
        Client client = new Client();
        client.connection.startConnection();
    }
}
