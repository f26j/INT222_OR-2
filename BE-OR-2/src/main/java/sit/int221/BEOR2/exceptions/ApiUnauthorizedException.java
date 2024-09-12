package sit.int221.BEOR2.exceptions;

public class ApiUnauthorizedException extends RuntimeException {
    public ApiUnauthorizedException(String message) {
        super(message);
    }
}
