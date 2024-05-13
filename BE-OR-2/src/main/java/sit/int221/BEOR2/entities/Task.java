package sit.int221.BEOR2.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sit.int221.BEOR2.utilities.CharConverter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "taskTitle", nullable = false, length = 100)
    @Convert(converter = CharConverter.class)
    private String title;

    @Column(name = "taskDescription", length = 500)
    @Convert(converter = CharConverter.class)
    private String description;

    @Column(name = "taskAssignees", length = 30)
    @Convert(converter = CharConverter.class)
    private String assignees;

    @Column(name = "taskStatus", nullable = false)
    private String status;

    @Column(name = "createdOn", insertable = false, updatable = false, nullable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updatedOn", insertable = false, updatable = false, nullable = false)
    private ZonedDateTime updatedOn;

    @ManyToOne
    @JoinColumn(name = "statusId")
    private Status statuses;

    public void setStatus(String status) {
        if (status == null || status.isEmpty()) {
            this.status = "NO_STATUS";
        } else {
            this.status = status;
        }
    }
}