package sit.int221.BEOR2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.BEOR2.entities.Users;

public interface UsersRepository extends JpaRepository<Users, String> {
}
