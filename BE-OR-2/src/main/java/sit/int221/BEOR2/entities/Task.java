package sit.int221.BEOR2.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "taskTitle")
    private String title;

    @Column(name = "taskDescription")
    private String description;

    @Column(name = "taskAssignees")
    private String assignees;

    @Column(name = "taskStatus")
    private String status;

    @Column(name = "createdOn")
    private ZonedDateTime createdOn;

    @Column(name = "updatedOn")
    private ZonedDateTime updatedOn;
}
