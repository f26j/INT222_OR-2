package sit.int221.BEOR2.db2.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.util.StringUtils;

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

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    @SuppressWarnings("deprecation")
    public void setName(String name) {
        this.name = StringUtils.trimWhitespace(name);
    }

    @SuppressWarnings("deprecation")
    public void setUsername(String username) {
        this.username = StringUtils.trimWhitespace(username);
    }
}
