package exception;

import java.io.IOException;

public class NoConnectionToTheServer extends IOException {

    public NoConnectionToTheServer(String message) {
        super(message);
    }
}
