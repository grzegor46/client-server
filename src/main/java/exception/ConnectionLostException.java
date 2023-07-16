package exception;

import java.io.IOException;

public class ConnectionLostException extends IOException {

    public ConnectionLostException(String msg) {
        super(msg);
    }

}
