package sit.int221.BEOR2.db1.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.entities.User;
import sit.int221.BEOR2.db1.repositories.BoardRepository;
import sit.int221.BEOR2.db1.repositories.CollabRepository;
import sit.int221.BEOR2.db1.repositories.UserRepository;
import sit.int221.BEOR2.exceptions.ApiForbiddenException;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;

@Component
public class ValidateAuth {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CollabRepository collabRepository;

    protected Board validateBoardAccess(String boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board not found"));

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);

        if (user == null && board.getVisibility().equals("PRIVATE")) {
            throw new ApiForbiddenException("Access Denied");
        }

        return board;
    }

    protected void validateCollabOwnership(Board board) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ApiForbiddenException("User not found"));

        if (!board.getUser().getOid().equals(user.getOid()) && !collabRepository.existsByBoardIdAndEmail(board.getId(), user.getEmail())) {
            throw new ApiForbiddenException("You do not own this board");
        }
    }

    protected void validateOwnership(Board board) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ApiForbiddenException("User not found"));

        if (!board.getUser().getOid().equals(user.getOid())) {
            throw new ApiForbiddenException("You do not own this board");
        }
    }

    protected Board validateBoardAccessForCollaborators(String boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board not found"));

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);
        boolean isOwner = user != null && board.getUser().getOid().equals(user.getOid());
        boolean isCollaborator = user != null && collabRepository.existsByBoardIdAndEmail(boardId, user.getEmail());

        if (!(board.getVisibility().equals("PUBLIC") || isOwner || isCollaborator)) {
            throw new ApiForbiddenException("Access Denied");
        }

        return board;
    }

    protected boolean isOwnerOrWriteCollaborator(Board board) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);

        // Check if the user is the board owner
        if (user != null && board.getUser().getOid().equals(user.getOid())) {
            return true;
        }

        // Check if the user is a collaborator with WRITE access
        return user != null && collabRepository.existsByBoardIdAndEmailAndAccessRight(board.getId(), user.getEmail(), "WRITE");
    }
}
