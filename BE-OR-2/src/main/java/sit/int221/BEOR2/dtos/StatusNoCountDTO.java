package sit.int221.BEOR2.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusNoCountDTO {
    private Integer id;
    private String name;
    private String description;
}
