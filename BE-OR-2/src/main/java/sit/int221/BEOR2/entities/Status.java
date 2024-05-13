package sit.int221.BEOR2.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sit.int221.BEOR2.utilities.CharConverter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "statusName", nullable = false, length = 50, unique = true)
    @Convert(converter = CharConverter.class)
    private String name;

    @Column(name = "statusDescription", length = 200)
    @Convert(converter = CharConverter.class)
    private String description;

    @OneToMany(mappedBy = "statuses")
    private List<Task> tasks;
}
