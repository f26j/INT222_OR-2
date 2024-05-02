package sit.int221.BEOR2.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private String status;
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;
}
