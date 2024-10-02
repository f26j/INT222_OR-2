package sit.int221.BEOR2.db1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.int221.BEOR2.db1.entities.Status;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
    // ค้นหา Status ทั้งหมดโดยใช้ boardId
    List<Status> findByBoardId(String boardId);

    // ค้นหา Status เฉพาะเจาะจงโดยใช้ boardId และ statusId
    Optional<Status> findByBoardIdAndId(String boardId, Integer id);

    // ตรวจสอบว่ามี Status ที่มีชื่อซ้ำหรือไม่
    boolean existsByNameAndBoardId(String name, String boardId);  // เปลี่ยนจาก Optional เป็น boolean เพื่อให้ตรงกับการใช้งาน

    @Query("SELECT s FROM Status s WHERE s.board.id IS NULL")
    List<Status> findDefaultStatuses();
}
