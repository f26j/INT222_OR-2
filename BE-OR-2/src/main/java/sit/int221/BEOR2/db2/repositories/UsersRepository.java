package sit.int221.BEOR2.db2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.BEOR2.db2.entities.Users;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, String> {
    Optional<Users> findByUsername(String username);
}
