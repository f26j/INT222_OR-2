package sit.int221.BEOR2.exceptions;

public class APINotFoundException extends RuntimeException {
    public APINotFoundException(String message) {
        super(message);
    }
}
