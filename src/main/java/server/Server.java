package server;


import utils.Connection;

public class Server {

    Connection connection = new ServerConnectionImpl();

    public static void main(String[] args){
        Server server = new Server();
        server.connection.startConnection();
    }
}
