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
    private ZonedDateTime timestamp;  // ใช้ ZonedDateTime เพื่อให้จัดการเวลาได้ดีขึ้น
    private Integer status;
    private String message;
    private String instance;

    // Constructor ที่กำหนดค่า timestamp ให้เป็นค่าเริ่มต้นของ ZonedDateTime.now()
    public ErrorResponse(Integer status, String message, String instance) {
        this.timestamp = ZonedDateTime.now();
        this.status = status;
        this.message = message;
        this.instance = instance;
    }
}
