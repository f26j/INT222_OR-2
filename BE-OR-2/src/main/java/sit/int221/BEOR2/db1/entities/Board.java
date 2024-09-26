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
    @Column(name = "id", nullable = false, length = 10, unique = true)
    private String id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "visibility", nullable = false)
    private String visibility;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User user;

    @Column(name = "created_on", nullable = false, updatable = false)
    private ZonedDateTime created_on;

    @Column(name = "updated_on", nullable = false)
    private ZonedDateTime updated_on;

    @JsonIgnore
    @OneToMany(mappedBy = "board")
    private List<Task> tasks;

    @JsonIgnore
    @OneToMany(mappedBy = "board")
    private List<Status> statuses;

    // ใช้ @PrePersist และ @PreUpdate สำหรับการตั้งค่าเวลาอัตโนมัติ
    @PrePersist
    protected void onCreate() {
        this.created_on = ZonedDateTime.now();
        this.updated_on = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated_on = ZonedDateTime.now();
    }
}
