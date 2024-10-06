package sit.int221.BEOR2.db1.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.List;

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

    @Column(name = "created_on", nullable = false, updatable = false)
    private ZonedDateTime created_on;

    @Column(name = "updated_on", nullable = false)
    private ZonedDateTime updated_on;

    @JsonIgnore
    @OneToMany(mappedBy = "status")
    private List<Task> tasks;

    @Transient
    private Integer count;

    // Getter สำหรับ count ที่แสดงจำนวน task ที่เชื่อมโยงกับสถานะนี้
    public Integer getCount() {
        return tasks == null ? 0 : tasks.size();
    }

    // ใช้ String.trim() เพื่อทำให้ชื่อและคำอธิบายไม่มีช่องว่างเกิน
    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }

    // Constructor แบบไม่มี argument (default constructor)
    public Status() {
        // default constructor
    }

    // Constructor ใหม่สำหรับการรับ Board, name, description
    public Status(Board board, String name, String description) {
        this.board = board;
        this.name = name;
        this.description = description;
    }

    // Constructor สำหรับรับ id โดยตรง (เพื่อให้สามารถ deserialize ได้จาก JSON)
    public Status(int id) {
        this.id = id;
    }

    // ตั้งค่าเวลาสร้างและปรับปรุง
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
