package sit.int221.BEOR2.db1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Deprecated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusManagementDto {
    private Integer id;
    private String name;
    private String description;
}
