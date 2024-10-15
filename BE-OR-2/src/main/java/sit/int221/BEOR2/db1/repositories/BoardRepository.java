package sit.int221.BEOR2.db1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.int221.BEOR2.db1.entities.Board;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, String> {

    @Query("SELECT b FROM Board b WHERE b.user.oid = :ownerOid OR b.visibility = :visibility")
    List<Board> findByOwnerOidOrVisibility(@Param("ownerOid") Long ownerOid, @Param("visibility") String visibility);

    // Query เพื่อดึงเฉพาะบอร์ดที่เป็นของผู้ใช้หรือบอร์ดที่มี visibility เป็น public
    @Query("SELECT b FROM Board b WHERE b.user.oid = :ownerOid OR b.visibility = 'PUBLIC'")
    List<Board> findByOwnerOidOrVisibilityPublic(String ownerOid);
}