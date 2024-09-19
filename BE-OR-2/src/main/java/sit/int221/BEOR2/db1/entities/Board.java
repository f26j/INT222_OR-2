package sit.int221.BEOR2.db1.entities;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
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

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User user;

    @Column(name = "created_on", insertable = false, updatable = false, nullable = false)
    private ZonedDateTime created_on;

    @Column(name = "updated_on", insertable = false, updatable = false, nullable = false)
    private ZonedDateTime updated_on;

    @JsonIgnore
    @OneToMany(mappedBy = "board")
    private List<Task> tasks;

    @JsonIgnore
    @OneToMany(mappedBy = "board")
    private List<Status> statuses;

    public Board() {
        this.id = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 10);
    }
}
