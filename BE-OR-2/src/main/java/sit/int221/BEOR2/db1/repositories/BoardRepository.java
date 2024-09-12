package sit.int221.BEOR2.db1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int221.BEOR2.db1.entities.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, String> {
}
