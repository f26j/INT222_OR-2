package sit.int221.BEOR2.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(APINotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleAPINotFoundException(HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage("Not Found".toUpperCase());
        errorResponse.setInstance(request.getRequestURI());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIUnauthorizedException.class)
    protected ResponseEntity<ErrorResponse> handleAPIUnauthorizedException(APIUnauthorizedException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setInstance(request.getRequestURI());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
