package sit.int221.BEOR2.db1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int221.BEOR2.db1.entities.Status;
import sit.int221.BEOR2.db1.entities.Task;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByBoardId(String boardId);
    Optional<Task> findByBoardIdAndId(String boardId, Integer id);
    List<Task> findByStatus(Status deleteId);
}
