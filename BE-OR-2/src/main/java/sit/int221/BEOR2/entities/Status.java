package sit.int221.BEOR2.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

@Getter
@Setter
@Entity
@ToString
@Table(name = "status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "statusName", nullable = false, length = 50, unique = true)
    private String name;

    @Column(name = "statusDescription", length = 200)
    private String description;

    public void setName(String name) {
        this.name = StringUtils.trimWhitespace(name);
    }

    public void setDescription(String description) {
        this.description = StringUtils.trimWhitespace(description);
    }
}