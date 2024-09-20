package sit.int221.BEOR2.db1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@Table(name = "users")
public class User {
    @Id
    @UuidGenerator
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oid", nullable = false, length = 36, unique = true)
    private UUID oid;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "created_on", insertable = false, updatable = false, nullable = false)
    private ZonedDateTime created_on;

    @Column(name = "updated_on", insertable = false, updatable = false, nullable = false)
    private ZonedDateTime updated_on;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Board> boards;

    @SuppressWarnings("deprecation")
    public void setName(String name) {
        this.name = StringUtils.trimWhitespace(name);
    }

    @SuppressWarnings("deprecation")
    public void setUsername(String username) {
        this.username = StringUtils.trimWhitespace(username);
    }
}
