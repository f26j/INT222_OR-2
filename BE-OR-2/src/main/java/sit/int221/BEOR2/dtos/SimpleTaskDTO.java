package sit.int221.BEOR2.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.BEOR2.entities.Status;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleTaskDTO {
    private Integer id;
    private String title;
    private String assignees;
    private Status status;

    public String getStatus() {
        return status.getName();
    }
}
