package sit.int221.BEOR2.db1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.entities.Status;
import sit.int221.BEOR2.db1.entities.Task;
import sit.int221.BEOR2.db1.entities.User;
import sit.int221.BEOR2.db1.repositories.BoardRepository;
import sit.int221.BEOR2.db1.repositories.UserRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiForbiddenException;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;
import sit.int221.BEOR2.db1.repositories.StatusRepository;
import sit.int221.BEOR2.db1.repositories.TaskRepository;

import java.util.List;

@Service
public class StatusService {
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Status> getStatuses(String boardId) {
        Board board = validateBoardAccess(boardId);
        return statusRepository.findByBoardId(boardId);
    }

    public Status getStatusById(String boardId, Integer statusId) {
        Board board = validateBoardAccess(boardId);
        return statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status not found"));
    }

    @Transactional
    public Status addStatus(String boardId, Status status) {
        Board board = validateBoardAccess(boardId);
        validateOwnership(board);

        if (statusRepository.existsByNameAndBoardId(status.getName().trim(), boardId)) {
            throw new ApiBadRequestException("Status name already exists on this board");
        }

        status.setBoard(board);
        return statusRepository.saveAndFlush(status);
    }

    @Transactional
    public Status editStatus(String boardId, Integer statusId, Status status) {
        Board board = validateBoardAccess(boardId);
        validateOwnership(board);

        Status existingStatus = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status not found"));

        status.setId(existingStatus.getId());
        status.setBoard(existingStatus.getBoard());
        return statusRepository.saveAndFlush(status);
    }

    @Transactional
    public void deleteStatus(String boardId, Integer statusId) {
        Board board = validateBoardAccess(boardId);
        validateOwnership(board);

        Status status = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Status not found"));

        if (status.getName().equalsIgnoreCase("No Status") || status.getName().equalsIgnoreCase("Done")) {
            throw new ApiBadRequestException("The status cannot be deleted");
        }

        statusRepository.delete(status);
    }

    @Transactional
    public void transferStatus(String boardId, Integer statusId, Integer newStatusId) {
        Board board = validateBoardAccess(boardId);
        validateOwnership(board);

        if (statusId.equals(newStatusId)) {
            throw new ApiBadRequestException("Cannot transfer tasks to the same status");
        }

        Status oldStatus = statusRepository.findByBoardIdAndId(boardId, statusId)
                .orElseThrow(() -> new ApiNotFoundException("Old status not found"));

        Status newStatus = statusRepository.findByBoardIdAndId(boardId, newStatusId)
                .orElseThrow(() -> new ApiNotFoundException("New status not found"));

        List<Task> tasks = taskRepository.findByStatus(oldStatus);
        for (Task task : tasks) {
            task.setStatus(newStatus);
        }

        taskRepository.saveAllAndFlush(tasks);
        statusRepository.delete(oldStatus);
    }

    private Board validateBoardAccess(String boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board not found"));

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);

        if (user == null && board.getVisibility().equals("PRIVATE")) {
            throw new ApiForbiddenException("Access Denied");
        }
        return board;
    }

    private void validateOwnership(Board board) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ApiForbiddenException("User not found"));

        if (!board.getUser().getOid().equals(user.getOid())) {
            throw new ApiForbiddenException("You do not own this board");
        }
    }
}
