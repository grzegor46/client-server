package client;

import exception.NoConnectionToTheServer;

import java.io.IOException;

public interface ClientConnection {

    void startConnection();
    void closeConnection();


}
