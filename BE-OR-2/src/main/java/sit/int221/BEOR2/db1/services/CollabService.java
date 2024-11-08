package sit.int221.BEOR2.db1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.BEOR2.db1.components.ValidateAuth;
import sit.int221.BEOR2.db1.dtos.BoardDto;
import sit.int221.BEOR2.db1.dtos.CollabDto;
import sit.int221.BEOR2.db1.dtos.UserDto;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.entities.Collab;
import sit.int221.BEOR2.db1.entities.User;
import sit.int221.BEOR2.db1.repositories.CollabRepository;
import sit.int221.BEOR2.db1.repositories.UserRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiConflictException;
import sit.int221.BEOR2.exceptions.ApiForbiddenException;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class CollabService extends ValidateAuth {
    @Autowired
    private CollabRepository collabRepository;
    @Autowired
    private UserRepository userRepository;

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
        validateBoardAccessForCollaborators(boardId);
        return collabRepository.findByBoardId(boardId);
    }

    public Collab getCollabByOid(String boardId, String collabOid) {
        validateBoardAccessForCollaborators(boardId);
        return collabRepository.findByBoardIdAndOid(boardId, collabOid)
                .orElseThrow(() -> new ApiNotFoundException("Collaborator not found"));
    }

    @Transactional
    public CollabDto addCollab(String boardId, Collab collab) {
        // Validate board access and ownership
        Board board = validateBoardAccessForCollaborators(boardId);
        validateOwnership(board);

        // Validate that collab data is provided
        if (collab == null) {
            throw new ApiBadRequestException("Collaborator data is required");
        }

        // Check if the access right is valid (must be either "READ" or "WRITE")
        String accessRight = collab.getAccess_right();
        if (!"READ".equalsIgnoreCase(accessRight) && !"WRITE".equalsIgnoreCase(accessRight)) {
            throw new ApiBadRequestException("Invalid access right value");
        }

        // Check if email exists in itbkk_shared repository
        Optional<User> userOpt = userRepository.findByEmail(collab.getEmail());
        if (userOpt.isEmpty()) {
            throw new ApiNotFoundException("Email not found in shared repository");
        }

        // Check if the email belongs to the board owner
        User user = userOpt.get();
        if (user.getOid().equals(board.getUser().getOid())) {
            throw new ApiConflictException("Cannot add the board owner as a collaborator");
        }

        // Check if the email is already a collaborator on the board
        boolean isAlreadyCollaborator = collabRepository.existsByBoardIdAndEmail(boardId, collab.getEmail());
        if (isAlreadyCollaborator) {
            throw new ApiConflictException("Collaborator already exists for this board");
        }

        // Set the board reference in the collab entity and save it
        collab.setBoard(board);
        Collab savedCollab = collabRepository.saveAndFlush(collab);

        // Convert the saved collaborator to DTO and return
        return convertToDto(savedCollab);
    }

    @Transactional
    public CollabDto editCollabAccessRight(String boardId, String collabOid, String accessRight) {
        Board board = validateBoardAccessForCollaborators(boardId);
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
        Board board = validateBoardAccessForCollaborators(boardId);

        // Get the current user from security context
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUser)
                .orElseThrow(() -> new ApiForbiddenException("Access Denied"));

        // Check if the user is the board owner or the collaborator themselves
        boolean isOwner = board.getUser().getOid().equals(user.getOid());
        boolean isCollaborator = collabOid.equals(user.getOid());

        // Only allow delete if user is owner or collaborator is trying to remove themselves
        if (!isOwner && !isCollaborator) {
            throw new ApiForbiddenException("Access Denied");
        }

        // Find the collaborator and delete
        Collab collab = collabRepository.findByBoardIdAndOid(boardId, collabOid)
                .orElseThrow(() -> new ApiNotFoundException("Collaborator not found"));

        collabRepository.delete(collab);
    }
}
