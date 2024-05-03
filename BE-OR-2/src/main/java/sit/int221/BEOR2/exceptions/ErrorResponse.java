package sit.int221.BEOR2.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private ZonedDateTime timestamp;
    private Integer status;
    private String message;
    private String instance;
}
