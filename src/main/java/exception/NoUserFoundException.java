package exception;

import java.io.IOException;

public class NoUserFoundException extends IOException {

    public NoUserFoundException(String message) {
        super(message);
    }
}
