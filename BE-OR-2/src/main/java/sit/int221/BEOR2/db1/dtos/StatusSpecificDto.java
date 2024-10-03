package sit.int221.BEOR2.db1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusSpecificDto {
    private Integer id;
    private BoardDto board;
    private String name;
    private String description;
    private ZonedDateTime created_on;
    private ZonedDateTime updated_on;
}
