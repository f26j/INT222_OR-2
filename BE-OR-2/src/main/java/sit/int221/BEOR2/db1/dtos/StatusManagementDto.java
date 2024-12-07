package sit.int221.BEOR2.db1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollabDto {
    private Integer id;
    private String oid;
    private BoardDto board_id;
    private String name;
    private String email;
    private String accessRight;
}
