package sit.int221.BEOR2.db1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private Integer id;
    private String title;
    private String assignees;
    private StatusDto status;
}
