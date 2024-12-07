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
@Table(name = "collabs")
public class Collab {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "oid", nullable = false, length = 36, unique = true)
    private String oid;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "access_right", nullable = false)
    private String access_right;

    @Column(name = "added_on", nullable = false, updatable = false)
    private ZonedDateTime added_on;

    @SuppressWarnings("deprecation")
    public void setName(String name) {
        this.name = StringUtils.trimWhitespace(name);
    }

    // ใช้ @PrePersist และ @PreUpdate สำหรับการตั้งค่าเวลาอัตโนมัติ
    @PrePersist
    protected void onCreate() {
        this.added_on = ZonedDateTime.now();
    }
}
