package sit.int221.BEOR2.db1.services;

import io.viascom.nanoid.NanoId;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import sit.int221.BEOR2.db1.dtos.BoardDto;
import sit.int221.BEOR2.db1.dtos.UserDto;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.entities.Status;
import sit.int221.BEOR2.db1.entities.User;
import sit.int221.BEOR2.db1.repositories.BoardRepository;
import sit.int221.BEOR2.db1.repositories.StatusRepository;
import sit.int221.BEOR2.db1.repositories.UserRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiForbiddenException;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;

import javax.validation.Valid;
import java.util.List;

@Service
@Slf4j
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private StatusService statusService;

    public List<Board> getBoardsByOwnerOidOrPublic(Long ownerOid) {
        return boardRepository.findByOwnerOidOrVisibility(ownerOid, "PUBLIC");
    }


    // Method สำหรับดึง Board โดยตรวจสอบ Oid และ visibility
    public BoardDto getBoardById(@PathVariable String id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found"));

        // ดึงชื่อผู้ใช้จาก SecurityContext (ผู้ที่กำลังทำงานอยู่)
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isOwner = board.getUser().getUsername().equals(userName);
        boolean isPublic = "PUBLIC".equalsIgnoreCase(board.getVisibility());

        // ถ้าไม่ใช่เจ้าของบอร์ดและบอร์ดไม่ได้เป็น public จะโยนข้อยกเว้น 403 Forbidden
        if (!isOwner && !isPublic) {
            throw new ApiForbiddenException("Access denied: You are not the owner and the board is private");
        }

        return new BoardDto(
                board.getId(),
                board.getName(),
                board.getVisibility(),
                new UserDto(
                        board.getUser().getOid(),
                        board.getUser().getUsername()
                )
        );
    }

    @Transactional
    public BoardDto addBoard(@RequestBody @Valid Board board) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ApiBadRequestException("User is required to add a board"));

        board.setUser(user);

        // ตรวจสอบชื่อบอร์ด
        String boardName = board.getName();
        if (boardName == null || boardName.trim().isEmpty() || boardName.length() > 120) {
            throw new ApiBadRequestException("Board name cannot be empty or overlength");
        }

        // ตั้งค่า visibility เป็น PRIVATE ถ้าไม่ได้ระบุ
        if (board.getVisibility() == null || board.getVisibility().isEmpty()) {
            board.setVisibility("PRIVATE");
        }

        board.setId(NanoId.generate(10));
        Board savedBoard = boardRepository.saveAndFlush(board);
        List<Status> status = statusRepository.findDefaultStatuses();

        if (board != null) {
            for (Status defaultStatus : status) {
                Status newStatus = new Status(board, defaultStatus.getName(), defaultStatus.getDescription());
                statusService.addStatus(board.getId(), newStatus);
            }
        }

        return new BoardDto(
                savedBoard.getId(),
                savedBoard.getName(),
                savedBoard.getVisibility(),
                new UserDto(
                        savedBoard.getUser().getOid(),
                        savedBoard.getUser().getUsername()
                )
        );
    }

    @Transactional
    public BoardDto editBoard(@PathVariable String id, @RequestBody @Valid Board board) {
        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found"));

        board.setId(existingBoard.getId());
        board.setUser(existingBoard.getUser());
        boardRepository.saveAndFlush(board);

        return new BoardDto(
                board.getId(),
                board.getName(),
                board.getVisibility(),
                new UserDto(
                        board.getUser().getOid(),
                        board.getUser().getUsername()
                )
        );
    }

    @Transactional
    public BoardDto editBoardVisibility(@PathVariable String id, @RequestBody @Valid String visibility) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found"));

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!board.getUser().getUsername().equals(userName)) {
            throw new ApiForbiddenException("User is not board owner");
        }

        if (!"PRIVATE".equalsIgnoreCase(visibility) && !"PUBLIC".equalsIgnoreCase(visibility)) {
            throw new ApiBadRequestException("Visibility must be 'PRIVATE' or 'PUBLIC'");
        }

        board.setVisibility(visibility.toUpperCase().trim());
        boardRepository.saveAndFlush(board);

        return new BoardDto(
                board.getId(),
                board.getName(),
                board.getVisibility(),
                new UserDto(board.getUser().getOid(), board.getUser().getUsername())
        );
    }

    @Transactional
    public void deleteBoard(@PathVariable String id) {
        if (!boardRepository.existsById(id)) {
            throw new ApiNotFoundException("Board id '" + id + "' not found");
        }

        boardRepository.deleteById(id);
    }
}
