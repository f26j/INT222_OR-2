package sit.int221.BEOR2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import sit.int221.BEOR2.entities.Status;
import sit.int221.BEOR2.entities.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer>, JpaSpecificationExecutor<Task> {
    List<Task> findByStatus(Status deleteId);
}
