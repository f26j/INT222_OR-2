package sit.int221.BEOR2.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiBadRequestException.class)
    protected ResponseEntity<ErrorResponse> handleApiBadRequestException(ApiBadRequestException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(ZonedDateTime.now());  // ใช้ ZonedDateTime
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setInstance(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiUnauthorizedException.class)
    protected ResponseEntity<ErrorResponse> handleApiUnauthorizedException(ApiUnauthorizedException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(ZonedDateTime.now());  // ใช้ ZonedDateTime
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setInstance(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ApiForbiddenException.class)
    protected ResponseEntity<ErrorResponse> handleApiForbiddenException(ApiForbiddenException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(ZonedDateTime.now());  // ใช้ ZonedDateTime
        errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setInstance(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ApiNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleApiNotFoundException(ApiNotFoundException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(ZonedDateTime.now());  // ใช้ ZonedDateTime
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setInstance(request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
