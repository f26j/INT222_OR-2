package sit.int221.BEOR2.db1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sit.int221.BEOR2.db1.entities.Collab;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollabRepository extends JpaRepository<Collab, String> {
    List<Collab> findByBoardId(String boardId);

    Optional<Collab> findByBoardIdAndOid(String boardId, String collabOid);

    boolean existsByBoardIdAndEmail(String boardId, String email);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Collab c WHERE c.board.id = ?1 AND c.email = ?2 AND c.access_right = ?3")
    boolean existsByBoardIdAndEmailAndAccess_right(String boardId, String email, String access_right);
}
