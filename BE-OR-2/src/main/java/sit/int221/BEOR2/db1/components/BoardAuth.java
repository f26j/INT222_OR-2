package sit.int221.BEOR2.db1.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.entities.User;
import sit.int221.BEOR2.db1.repositories.BoardRepository;
import sit.int221.BEOR2.db1.repositories.UserRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiForbiddenException;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;

@Component
public class BoardAuth {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;

    protected Board getBoardEntity(String id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + id + "' not found"));
    }

    protected User getAuthenticatedUser() {
        String username = getAuthenticatedUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiForbiddenException("User not found"));
    }

    protected String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    protected boolean isBoardOwner(User user, Board board) {
        return board.getUser().getOid().equals(user.getOid());
    }

    protected void validateBoardName(String name) {
        if (name == null || name.trim().isEmpty() || name.length() > 120) {
            throw new ApiBadRequestException("Invalid board name");
        }
    }
}
