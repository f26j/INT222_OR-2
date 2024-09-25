package sit.int221.BEOR2.db1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int221.BEOR2.db1.entities.Status;
import sit.int221.BEOR2.db1.entities.Task;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    // ค้นหา Task ทั้งหมดโดยใช้ boardId
    List<Task> findByBoardId(String boardId);

    // ค้นหา Task เฉพาะเจาะจงโดยใช้ boardId และ taskId
    Optional<Task> findByBoardIdAndId(String boardId, Integer id);

    // ค้นหา Task ทั้งหมดที่มี Status เฉพาะเจาะจง
    List<Task> findByStatus(Status status);
}
