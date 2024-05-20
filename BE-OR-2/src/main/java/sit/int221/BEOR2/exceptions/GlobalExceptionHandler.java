package sit.int221.BEOR2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(APINotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFoundException(APINotFoundException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setInstance(exception.getInstance());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
