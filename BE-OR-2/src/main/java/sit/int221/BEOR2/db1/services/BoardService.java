//package sit.int221.BEOR2.db1.services;
//
//import io.viascom.nanoid.NanoId;
//import jakarta.transaction.Transactional;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.PathVariable;
//import sit.int221.BEOR2.db1.dtos.BoardDto;
//import sit.int221.BEOR2.db1.dtos.UserDto;
//import sit.int221.BEOR2.db1.entities.Board;
//import sit.int221.BEOR2.db1.entities.Status;
//import sit.int221.BEOR2.db1.entities.User;
//import sit.int221.BEOR2.db1.repositories.BoardRepository;
//import sit.int221.BEOR2.db1.repositories.StatusRepository;
//import sit.int221.BEOR2.db1.repositories.UserRepository;
//import sit.int221.BEOR2.exceptions.ApiBadRequestException;
//import sit.int221.BEOR2.exceptions.ApiForbiddenException;
//import sit.int221.BEOR2.exceptions.ApiNotFoundException;
//
//import java.util.List;
//
//@Service
//@Slf4j
//public class BoardService {
//    @Autowired
//    private BoardRepository boardRepository;
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private StatusRepository statusRepository;
//    @Autowired
//    private StatusService statusService;
//
//    public List<BoardDto> getBoards() {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName)
//                .orElseThrow(() -> new ApiNotFoundException("User not found"));
//
//        List<Board> boards = boardRepository.findByOwnerOidOrVisibilityPublic(user.getOid());
//        return boards.stream().map(board -> new BoardDto(
//                board.getId(),
//                board.getName(),
//                board.getVisibility(),
//                new UserDto(
//                        board.getUser().getOid(),
//                        board.getUser().getUsername()
//                )
//        )).toList();
//    }
//
//    public BoardDto getBoardById(@PathVariable String id) {
//        System.out.println("ID: " + id);
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        System.out.println("board Service");
//        System.out.println(userName);
//        User user = userRepository.findByUsername(userName).orElse(null);
//
//        Board board = boardRepository.findById(id)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found"));
//        System.out.println(user);
//        if (user == null && board.getVisibility().equals("PRIVATE")) {
//            throw new ApiForbiddenException("Access Denied");
//        } else {
//            checkBoardAccess(id);
//        }
//
//        // Step 4: การตอบกลับปกติ (200 OK)
//        return new BoardDto(
//                board.getId(),
//                board.getName(),
//                board.getVisibility(),
//                new UserDto(
//                        board.getUser().getOid(),
//                        board.getUser().getUsername()
//                )
//        );
//    }
//
//    @Transactional
//    public BoardDto addBoard(Board board) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName)
//                .orElseThrow(() -> new ApiBadRequestException("User is required to add a board"));
//
//        String boardName = board.getName();
//        if (boardName == null || boardName.trim().isEmpty() || boardName.length() > 120) {
//            throw new ApiBadRequestException("Board name cannot be empty or overlength");
//        }
//
//        if (board == null) {
//            throw new ApiBadRequestException("Nobody Know");
//        }
//
//        if (board.getVisibility() == null || board.getVisibility().isEmpty()) {
//            board.setVisibility("PRIVATE".toUpperCase().trim());
//        }
//
//        // Step 4: เพิ่มบอร์ดลงฐานข้อมูล
//        board.setUser(user);
//        board.setId(NanoId.generate(10));
//        Board savedBoard = boardRepository.saveAndFlush(board);
//
//        List<Status> status = statusRepository.findDefaultStatuses();
//        if (board != null) {
//            for (Status defaultStatus : status) {
//                Status newStatus = new Status(board, defaultStatus.getName(), defaultStatus.getDescription());
//                statusService.addStatus(board.getId(), newStatus);
//            }
//        }
//
//        // Step 5: การตอบกลับปกติ (200 OK)
//        return new BoardDto(
//                savedBoard.getId(),
//                savedBoard.getName(),
//                savedBoard.getVisibility(),
//                new UserDto(
//                        savedBoard.getUser().getOid(),
//                        savedBoard.getUser().getUsername()
//                )
//        );
//    }
//
//    @Transactional
//    public BoardDto editBoard(@PathVariable String id, Board board) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName).orElse(null);
//
//        Board existingBoard = boardRepository.findById(id)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found"));
//        if (user == null && board.getVisibility().equals("PRIVATE")) {
//            throw new ApiForbiddenException("Access Denied");
//        }
//
//        boolean isOwner = existingBoard.getUser().getOid().equals(user.getOid());
//        if (!isOwner) {
//            throw new ApiForbiddenException("Access denied: You do not have permission to edit this board");
//        }
//
//        if (board == null) {
//            throw new ApiBadRequestException("Nobody Know");
//        }
//
//        // Step 4: การแก้ไขบอร์ดและการตอบกลับปกติ (200 OK)
//        board.setId(existingBoard.getId());
//        board.setUser(existingBoard.getUser());
//        boardRepository.saveAndFlush(board);
//        return new BoardDto(
//                board.getId(),
//                board.getName(),
//                board.getVisibility(),
//                new UserDto(
//                        board.getUser().getOid(),
//                        board.getUser().getUsername()
//                )
//        );
//    }
//
//    @Transactional
//    public BoardDto editBoardVisibility(@PathVariable String id, String visibility) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName).orElse(null);
//        System.out.println(visibility);
//
//        Board board = boardRepository.findById(id)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found"));
//
//        boolean isOwner = board.getUser().getOid().equals(user.getOid());
//        if (user == null && board.getVisibility().equals("PRIVATE") || !isOwner) {
//            throw new ApiForbiddenException("Access Denied");
//        } else {
//            checkBoardAccess(id);
//        }
//
//        if (visibility == null) {
//            throw new ApiBadRequestException("Nobody Know");
//        }
//
//        if (!isOwner) {
//            throw new ApiForbiddenException("Access denied: You do not have permission to edit visibility of this board");
//        }
//
//        if (!"PRIVATE".equalsIgnoreCase(visibility) && !"PUBLIC".equalsIgnoreCase(visibility)) {
//            throw new ApiBadRequestException("Visibility must be 'PRIVATE' or 'PUBLIC'");
//        }
//
//        board.setVisibility(visibility.toUpperCase().trim());
//        boardRepository.saveAndFlush(board);
//        return new BoardDto(
//                board.getId(),
//                board.getName(),
//                board.getVisibility(),
//                new UserDto(board.getUser().getOid(), board.getUser().getUsername())
//        );
//    }
//
//    @Transactional
//    public void deleteBoard(@PathVariable String id) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName).orElse(null);
//
//        Board board = boardRepository.findById(id)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found"));
//
//        if (user == null && board.getVisibility().equals("PRIVATE")) {
//            throw new ApiForbiddenException("Access Denied");
//        } else {
//            checkBoardAccess(id);
//        }
//
//        if (!boardRepository.existsById(id)) {
//            throw new ApiNotFoundException("Board id '" + id + "' not found");
//        }
//
//        if (board == null) {
//            throw new ApiBadRequestException("Nobody Know");
//        }
//
//        // Step 2: ลบบอร์ด
//        boardRepository.deleteById(id);
//    }
//
//    private void checkBoardAccess(String boardId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        Board board = boardRepository.findById(boardId)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
//
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName).orElse(null);
//
//        if (user != null) {
//            boolean isOwner = board.getUser().getOid().equals(user.getOid());
//            boolean isPublic = "PUBLIC".equalsIgnoreCase(board.getVisibility());
//
//            // ถ้าไม่ใช่เจ้าของและบอร์ดเป็น private ให้ส่ง 403 Forbidden
//            if (!isOwner && !isPublic) {
//                throw new ApiForbiddenException("Access denied: You do not have permission to access this board");
//            }
//        }
//    }
//}

package sit.int221.BEOR2.db1.services;

import io.viascom.nanoid.NanoId;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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

    public List<BoardDto> getBoards() {
        String username = getAuthenticatedUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiNotFoundException("User not found"));

        List<Board> boards = boardRepository.findByOwnerOidOrVisibilityPublic(user.getOid());
        return boards.stream().map(this::convertToDto).toList();
    }

    public BoardDto getBoardById(String id) {
        String username = getAuthenticatedUsername();
        User user = userRepository.findByUsername(username).orElse(null);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found"));

        if (board.getVisibility().equals("PRIVATE") && (user == null || !isBoardOwner(user, board))) {
            throw new ApiForbiddenException("Access Denied");
        }
        return convertToDto(board);
    }

    @Transactional
    public BoardDto addBoard(Board board) {
        String username = getAuthenticatedUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiBadRequestException("User is required to add a board"));

        validateBoardName(board.getName());

        board.setUser(user);
        board.setId(NanoId.generate(10));
        board.setVisibility(board.getVisibility() != null ? board.getVisibility().toUpperCase() : "PRIVATE");
        Board savedBoard = boardRepository.saveAndFlush(board);

        statusRepository.findDefaultStatuses().forEach(defaultStatus -> {
            Status newStatus = new Status(savedBoard, defaultStatus.getName(), defaultStatus.getDescription());
            statusService.addStatus(savedBoard.getId(), newStatus);
        });

        return convertToDto(savedBoard);
    }

    @Transactional
    public BoardDto editBoard(String id, Board updatedBoard) {
        Board existingBoard = getBoardEntity(id);
        User user = getAuthenticatedUser();

        if (!isBoardOwner(user, existingBoard)) {
            throw new ApiForbiddenException("Access Denied");
        }

        existingBoard.setName(updatedBoard.getName());
        existingBoard.setVisibility(updatedBoard.getVisibility());
        boardRepository.saveAndFlush(existingBoard);

        return convertToDto(existingBoard);
    }

    @Transactional
    public BoardDto editBoardVisibility(String id, String visibility) {
        Board board = getBoardEntity(id);
        User user = getAuthenticatedUser();

        if (!isBoardOwner(user, board)) {
            throw new ApiForbiddenException("Access Denied");
        }

        if (!"PRIVATE".equalsIgnoreCase(visibility) && !"PUBLIC".equalsIgnoreCase(visibility)) {
            throw new ApiBadRequestException("Invalid visibility value");
        }

        board.setVisibility(visibility.toUpperCase());
        boardRepository.saveAndFlush(board);
        return convertToDto(board);
    }

    @Transactional
    public void deleteBoard(String id) {
        Board board = getBoardEntity(id);
        User user = getAuthenticatedUser();

        if (!isBoardOwner(user, board)) {
            throw new ApiForbiddenException("Access Denied");
        }

        boardRepository.deleteById(id);
    }

    private Board getBoardEntity(String id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found"));
    }

    private User getAuthenticatedUser() {
        String username = getAuthenticatedUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiForbiddenException("User not found"));
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    private boolean isBoardOwner(User user, Board board) {
        return board.getUser().getOid().equals(user.getOid());
    }

    private void validateBoardName(String name) {
        if (name == null || name.trim().isEmpty() || name.length() > 120) {
            throw new ApiBadRequestException("Invalid board name");
        }
    }

    private BoardDto convertToDto(Board board) {
        return new BoardDto(
                board.getId(),
                board.getName(),
                board.getVisibility(),
                new UserDto(board.getUser().getOid(), board.getUser().getUsername())
        );
    }
}
