package sit.int221.BEOR2.db1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int221.BEOR2.db1.entities.Status;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
    List<Status> findByBoardId(String boardId);
    Optional<Status> findByBoardIdAndId(String boardId, Integer id);
}
