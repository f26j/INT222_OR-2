package sit.int221.BEOR2.db1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "boards")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 10, unique = true)
    private String id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "owner_id", nullable = false, length = 36)
    private String owner_id;

    @Column(name = "created_on", insertable = false, updatable = false, nullable = false)
    private ZonedDateTime created_on;

    @Column(name = "updated_on", insertable = false, updatable = false, nullable = false)
    private ZonedDateTime updated_on;

    // @JsonIgnore
    // @OneToMany(mappedBy = "board_id")
    // private List<Task> tasks;

    // @JsonIgnore
    // @OneToMany(mappedBy = "board_id")
    // private List<Status> statuses;
}
