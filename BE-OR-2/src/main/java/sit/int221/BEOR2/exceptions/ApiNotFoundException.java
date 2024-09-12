package sit.int221.BEOR2.exceptions;

public class ApiNotFoundException extends RuntimeException {
    public ApiNotFoundException(String message) {
        super(message);
    }
}
