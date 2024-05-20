package sit.int221.BEOR2.exceptions;

import lombok.Getter;

@Getter
public class APINotFoundException extends RuntimeException {
    private final String instance;

    public APINotFoundException(String message, String instance) {
        super(message);
        this.instance = instance;
    }
}
