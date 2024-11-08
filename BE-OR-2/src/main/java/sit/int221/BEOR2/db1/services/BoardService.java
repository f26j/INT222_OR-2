package sit.int221.BEOR2.db1.services;

import io.viascom.nanoid.NanoId;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.BEOR2.db1.components.BoardAuth;
import sit.int221.BEOR2.db1.dtos.BoardCollabDto;
import sit.int221.BEOR2.db1.dtos.BoardDto;
import sit.int221.BEOR2.db1.dtos.UserDto;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.entities.Status;
import sit.int221.BEOR2.db1.entities.User;
import sit.int221.BEOR2.db1.repositories.BoardRepository;
import sit.int221.BEOR2.db1.repositories.CollabRepository;
import sit.int221.BEOR2.db1.repositories.StatusRepository;
import sit.int221.BEOR2.db1.repositories.UserRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiForbiddenException;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;

import java.util.List;

@Service
@Slf4j
public class BoardService extends BoardAuth {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private CollabRepository collabRepository;
    @Autowired
    private StatusService statusService;

    private BoardDto convertToDto(Board board) {
        return new BoardDto(
                board.getId(),
                board.getName(),
                board.getVisibility(),
                new UserDto(board.getUser().getOid(), board.getUser().getUsername())
        );
    }

    public BoardCollabDto getBoards() {
        String username = getAuthenticatedUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiNotFoundException("User not found"));

        List<Board> ownedOrPublicBoards = boardRepository.findByOwnerOidOrVisibilityPublic(user.getOid());
        List<Board> collaborativeBoardsList = boardRepository.findByCollaboratorsEmail(user.getEmail());

        List<BoardDto> personalBoards = ownedOrPublicBoards .stream()
                .filter(board -> board.getUser().getOid().equals(user.getOid()))
                .map(this::convertToDto)
                .toList();

        List<BoardDto> collaborativeBoards = collaborativeBoardsList.stream()
                .filter(board -> board.getUser().getOid().equals(user.getOid()))
                .map(this::convertToDto)
                .toList();

        return new BoardCollabDto(personalBoards, collaborativeBoards);
    }

    public BoardDto getBoardById(String id) {
        String username = getAuthenticatedUsername();
        User user = userRepository.findByUsername(username).orElse(null);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found"));

        boolean isCollaborator = user != null && collabRepository.existsByBoardIdAndEmail(id, user.getEmail());
        boolean isOwner = user != null && board.getUser().getOid().equals(user.getOid());

        // Allow access if the board is public, the user is the owner, or the user is a collaborator
        if (!board.getVisibility().equals("PUBLIC") && !isOwner && !isCollaborator) {
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
}
