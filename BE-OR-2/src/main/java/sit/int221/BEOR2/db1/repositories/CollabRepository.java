package sit.int221.BEOR2.db1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int221.BEOR2.db1.entities.Collab;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollabRepository extends JpaRepository<Collab, String> {
    List<Collab> findByBoardId(String boardId);

    Optional<Collab> findByBoardIdAndOid(String boardId, String collabOid);
}
