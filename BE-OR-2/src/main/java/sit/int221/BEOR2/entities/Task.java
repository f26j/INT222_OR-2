package sit.int221.BEOR2.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "taskTitle", nullable = false, length = 100)
    private String title;

    @Column(name = "taskDescription", length = 500)
    private String description;

    @Column(name = "taskAssignees", length = 30)
    private String assignees;

    @ManyToOne
    @JoinColumn(name = "taskStatus", nullable = false)
    private Status status;

    @Column(name = "createdOn", insertable = false, updatable = false, nullable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updatedOn", insertable = false, updatable = false, nullable = false)
    private ZonedDateTime updatedOn;

    public void setTitle(String title) {
        this.title = StringUtils.trimAllWhitespace(title);
    }

    public void setDescription(String description) {
        this.description = StringUtils.trimAllWhitespace(description);
    }

    public void setAssignees(String assignees) {
        this.assignees = StringUtils.trimAllWhitespace(assignees);
    }
}
