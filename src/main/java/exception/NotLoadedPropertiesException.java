package exception;

import java.io.IOException;

public class NotLoadedPropertiesException extends IOException {

    public NotLoadedPropertiesException(String message) {
        super(message);
    }
}
