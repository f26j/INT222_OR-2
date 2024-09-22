package sit.int221.BEOR2.db1.entities;

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
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "assignees", length = 30)
    private String assignees;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Column(name = "created_on", insertable = false, updatable = false, nullable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updated_on", insertable = false, updatable = false, nullable = false)
    private ZonedDateTime updatedOn;

    @SuppressWarnings("deprecation")
    public void setTitle(String title) {
        this.title = StringUtils.trimWhitespace(title);
    }

    @SuppressWarnings("deprecation")
    public void setDescription(String description) {
        if (description == null || description.isEmpty()) {
            this.description = null;
        } else {
            this.description = StringUtils.trimWhitespace(description);
        }
    }

    @SuppressWarnings("deprecation")
    public void setAssignees(String assignees) {
        if (assignees == null || assignees.isEmpty()) {
            this.assignees = null;
        } else {
            this.assignees = StringUtils.trimWhitespace(assignees);
        }
    }
}
