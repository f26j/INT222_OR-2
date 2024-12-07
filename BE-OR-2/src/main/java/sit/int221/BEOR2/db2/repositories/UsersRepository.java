package sit.int221.BEOR2.db2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int221.BEOR2.db1.entities.User;
import sit.int221.BEOR2.db2.entities.Users;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
    // ค้นหา Users โดยใช้ username
    Optional<Users> findByUsername(String username);

    // ค้นหา Users โดยใช้ oid
    Users findByOid(String oid);

    Optional<Users> findByEmail(String email);
}
