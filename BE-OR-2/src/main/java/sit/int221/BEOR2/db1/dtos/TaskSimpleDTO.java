package sit.int221.BEOR2.db1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.BEOR2.db1.entities.Status;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskSimpleDTO {
    private Integer id;
    private String title;
    private String assignees;
    private Status status;
}
