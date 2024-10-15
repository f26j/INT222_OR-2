package sit.int221.BEOR2.db1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "users")
public class User {
    @Id
    @Column(name = "oid", nullable = false, length = 36, unique = true)
    private String oid;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "created_on", nullable = false, updatable = false)
    private ZonedDateTime created_on;

    @Column(name = "updated_on", nullable = false)
    private ZonedDateTime updated_on;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Board> boards;

    // ใช้ String.trim() แทน StringUtils.trimWhitespace() เพื่อให้สั้นลงและไม่ใช้ method ที่ deprecated
    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public void setUsername(String username) {
        this.username = username != null ? username.trim() : null;
    }

    // ใช้ @PrePersist และ @PreUpdate เพื่อให้เวลาสร้างและปรับปรุงอัตโนมัติ
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
