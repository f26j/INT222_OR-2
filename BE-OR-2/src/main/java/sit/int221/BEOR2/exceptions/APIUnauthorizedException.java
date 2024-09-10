package sit.int221.BEOR2.exceptions;

public class APIUnauthorizedException extends RuntimeException {
    public APIUnauthorizedException(String message) {
        super(message);
    }
}
