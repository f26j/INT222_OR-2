package sit.int221.BEOR2.db1.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

    @Column(name = "created_on", nullable = false, updatable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updated_on", nullable = false)
    private ZonedDateTime updatedOn;

    // ใช้ String.trim() เพื่อ trim ช่องว่างของ title, description, และ assignees
    public void setTitle(String title) {
        this.title = title != null ? title.trim() : null;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }

    public void setAssignees(String assignees) {
        this.assignees = assignees != null ? assignees.trim() : null;
    }

    // ตั้งค่าเวลาสร้างและปรับปรุง
    @PrePersist
    protected void onCreate() {
        this.createdOn = ZonedDateTime.now();
        this.updatedOn = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedOn = ZonedDateTime.now();
    }
}
