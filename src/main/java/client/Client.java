package client;

public class Client {

    ClientConnection clientConnection = new ClientConnectionImpl();

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    private void start() {
        clientConnection.startConnection();
    }
}
