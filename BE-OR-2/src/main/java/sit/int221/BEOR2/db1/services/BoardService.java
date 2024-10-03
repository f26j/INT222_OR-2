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
    private StatusRepository statusRepository;  // ประกาศ statusRepository
    @Autowired
    private StatusService statusService;

    public List<Board> getBoards() {
        return boardRepository.findAll();
    }

    public BoardDto getBoardById(@PathVariable String id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found"));

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isOwner = board.getUser().getUsername().equals(userName);
        boolean isPublic = "PUBLIC".equalsIgnoreCase(board.getVisibility());

        if (!isOwner && !isPublic) {
            throw new ApiForbiddenException("Access denied");
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
        // ดึงชื่อผู้ใช้จาก SecurityContext
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ApiBadRequestException("User is required to add a board"));

        // ตั้งค่าผู้ใช้ให้กับบอร์ด
        board.setUser(user);

        // ตรวจสอบชื่อบอร์ด
        String boardName = board.getName();
        if (boardName == null || boardName.trim().isEmpty() || boardName.length() > 120) {
            throw new ApiBadRequestException("Board name cannot be empty or overlength");
        }

        // ตั้งค่า visibility ให้เป็น PRIVATE ถ้าไม่ได้ระบุ
        if (board.getVisibility() == null || board.getVisibility().isEmpty()) {
            board.setVisibility("PRIVATE".toUpperCase().trim());
        }

        // สร้าง id สำหรับบอร์ด (10 ตัวอักษร)
        board.setId(NanoId.generate(10));

        Board savedBoard = boardRepository.saveAndFlush(board);
        List<Status> status = statusRepository.findDefaultStatuses();

        if (board != null) {
            for (Status defaultStatus : status) {
                Status newStatus = new Status(board, defaultStatus.getName(), defaultStatus.getDescription());
                statusService.addStatus(board.getId(), newStatus);
            }
        }

        // ตรวจสอบการตอบกลับ
        BoardDto responseDto = new BoardDto();
        responseDto.setId(savedBoard.getId());
        responseDto.setName(savedBoard.getName());
        responseDto.setVisibility(savedBoard.getVisibility());
        responseDto.setOwner(new UserDto(
                savedBoard.getUser().getOid(),
                savedBoard.getUser().getUsername()
        ));
        return responseDto;
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
        return new BoardDto(board.getId(), board.getName(), board.getVisibility(),
                new UserDto(board.getUser().getOid(), board.getUser().getUsername()));
    }

    @Transactional
    public void deleteBoard(@PathVariable String id) {
        if (!boardRepository.existsById(id)) {
            throw new ApiNotFoundException("Board id '" + id + "' not found");
        }

        boardRepository.deleteById(id);
    }
}
