package sit.int221.BEOR2.db1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.int221.BEOR2.db1.entities.Board;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, String> {

    // Custom query สำหรับดึงข้อมูล Board ที่ Oid ตรงกัน หรือ visibility เป็น public
    @Query("SELECT b FROM Board b WHERE b.user.oid = :ownerOid OR b.visibility = :visibility")
    List<Board> findByOwnerOidOrVisibility(@Param("ownerOid") Long ownerOid, @Param("visibility") String visibility);
}
