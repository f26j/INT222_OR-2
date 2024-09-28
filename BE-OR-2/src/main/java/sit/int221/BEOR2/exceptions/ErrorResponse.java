package sit.int221.BEOR2.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private Date timestamp; // Date format ควรเป็นในรูปแบบ 2024-09-27T00:00:00.000+00:00
    private Integer status;
    private String message;
    private String instance;
}
