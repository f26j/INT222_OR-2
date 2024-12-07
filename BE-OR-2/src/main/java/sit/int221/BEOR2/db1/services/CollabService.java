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
    public CollabDto addCollab(String boardId, CollabDto collabDto) {
        Board board = validateBoardAccessForCollaborators(boardId);
        validateOwnership(board);

        if (collabDto == null) {
            throw new ApiBadRequestException("Collaborator data is required");
        }
        System.out.println(collabDto.getEmail());
        if (collabDto.getEmail() == null || collabDto.getEmail().isEmpty()) {
            throw new ApiBadRequestException("Email is required and cannot be empty");
        }

        if (collabDto.getAccess_right() == null ||
                (!"READ".equalsIgnoreCase(collabDto.getAccess_right()) &&
                        !"WRITE".equalsIgnoreCase(collabDto.getAccess_right()))) {
            throw new ApiBadRequestException("Invalid access right value: " + collabDto.getAccess_right());
        }

        Optional<User> userOpt = userRepository.findByEmail(collabDto.getEmail());
        System.out.println(1);
        System.out.println(userOpt.isEmpty());
        if (userOpt.isEmpty()) {
            throw new ApiNotFoundException("Email not found in shared repository");
        }

        User user = userOpt.get();
        if (user.getOid().equals(board.getUser().getOid())) {
            throw new ApiConflictException("Cannot add the board owner as a collaborator");
        }

        boolean isAlreadyCollaborator = collabRepository.existsByBoardIdAndEmail(boardId, collabDto.getEmail());
        if (isAlreadyCollaborator) {
            throw new ApiConflictException("Collaborator already exists for this board");
        }

        Collab collab = new Collab();
        collab.setName(userOpt.get().getName());
        collab.setEmail(collabDto.getEmail());
        collab.setOid(userOpt.get().getOid());
        collab.setAccess_right(collabDto.getAccess_right());
        collab.setBoard(board);

        Collab savedCollab = collabRepository.saveAndFlush(collab);

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

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUser)
                .orElseThrow(() -> new ApiForbiddenException("Access Denied"));

        boolean isOwner = board.getUser().getOid().equals(user.getOid());
        boolean isCollaborator = collabOid.equals(user.getOid());

        if (!isOwner && !isCollaborator) {
            throw new ApiForbiddenException("Access Denied");
        }

        Collab collab = collabRepository.findByBoardIdAndOid(boardId, collabOid)
                .orElseThrow(() -> new ApiNotFoundException("Collaborator not found"));

        collabRepository.delete(collab);
    }
}
