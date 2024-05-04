package sit.int221.BEOR2.exceptions;

import lombok.Getter;

@Getter
public class TaskNotFoundException extends RuntimeException {
    private final String instance;

    public TaskNotFoundException(String message, String instance) {
        super(message);
        this.instance = instance;
    }
}