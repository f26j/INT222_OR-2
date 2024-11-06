package sit.int221.BEOR2.db1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.BEOR2.db1.components.ValidateAuth;
import sit.int221.BEOR2.db1.dtos.BoardDto;
import sit.int221.BEOR2.db1.dtos.CollabDto;
import sit.int221.BEOR2.db1.dtos.UserDto;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.entities.Collab;
import sit.int221.BEOR2.db1.repositories.CollabRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;

import java.util.List;

@Service
public class CollabService extends ValidateAuth {
    @Autowired
    private CollabRepository collabRepository;

    private CollabDto convertToDto(Collab collab) {
        return new CollabDto(
                collab.getOid(),
                new BoardDto(
                        collab.getBoard().getId(),
                        collab.getBoard().getName(),
                        collab.getBoard().getVisibility(),
                        new UserDto(
                                collab.getBoard().getUser().getOid(),
                                collab.getBoard().getUser().getUsername()
                        )
                ),
                collab.getName(),
                collab.getEmail(),
                collab.getAccess_right()
        );
    }

    public List<Collab> getCollabs(String boardId) {
        validateBoardAccess(boardId);
        return collabRepository.findByBoardId(boardId);
    }

    public Collab getCollabByOid(String boardId, String collabOid) {
        validateBoardAccess(boardId);
        return collabRepository.findByBoardIdAndOid(boardId, collabOid)
                .orElseThrow(() -> new ApiNotFoundException("Collaborator not found"));
    }

    @Transactional
    public CollabDto addCollab(String boardId, Collab collab) {
        Board board = validateBoardAccess(boardId);
        validateOwnership(board);

        if (collab == null) {
            throw new ApiBadRequestException("Collaborator data is required");
        }

        if (!"READ".equalsIgnoreCase(collab.toString()) && !"WRITE".equalsIgnoreCase(collab.toString())) {
            throw new ApiBadRequestException("Invalid access right value");
        }

        collab.setBoard(board);
        Collab savedCollab = collabRepository.saveAndFlush(collab);

        return convertToDto(savedCollab);
    }

    @Transactional
    public CollabDto editCollabAccessRight(String boardId, String collabOid, String accessRight) {
        Board board = validateBoardAccess(boardId);
        validateOwnership(board);

        Collab existingCollab = collabRepository.findByBoardIdAndOid(boardId, collabOid)
                .orElseThrow(() -> new ApiNotFoundException("Collaborator not found"));

        if (!"READ".equalsIgnoreCase(accessRight) && !"WRITE".equalsIgnoreCase(accessRight)) {
            throw new ApiBadRequestException("Invalid access right value");
        }

        existingCollab.setBoard(board);
        existingCollab.setAccess_right(accessRight.toUpperCase());
        collabRepository.saveAndFlush(existingCollab);
        return convertToDto(existingCollab);
    }

    @Transactional
    public void deleteCollab(String boardId, String collabOid) {
        Board board = validateBoardAccess(boardId);
        validateOwnership(board);

        Collab collab = collabRepository.findByBoardIdAndOid(boardId, collabOid)
                .orElseThrow(() -> new ApiNotFoundException("Collaborator not found"));

        collabRepository.delete(collab);
    }
}
