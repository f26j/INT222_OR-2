package sit.int221.BEOR2.db1.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 10)
    private String id;

    @Column(name = "boardName", nullable = false, length = 120)
    private String name;

    @Column(name = "boardOwner", nullable = false)
    private String owner;
}
