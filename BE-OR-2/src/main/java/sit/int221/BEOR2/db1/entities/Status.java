package sit.int221.BEOR2.db1.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@Entity
@ToString
@Table(name = "statuses")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;

    @Column(name = "description", length = 200)
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "status")
    private List<Task> tasks;

    @Transient
    private Integer count;

    public Integer getCount() {
        return tasks == null ? 0 : tasks.size();
    }

    @SuppressWarnings("deprecation")
    public void setName(String name) {
        this.name = StringUtils.trimWhitespace(name);
    }

    @SuppressWarnings("deprecation")
    public void setDescription(String description) {
        this.description = StringUtils.trimWhitespace(description);
    }
}
