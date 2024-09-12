package sit.int221.BEOR2.exceptions;

public class ApiBadRequestException extends RuntimeException {
    public ApiBadRequestException(String message) {
        super(message);
    }
}
