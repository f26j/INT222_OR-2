package sit.int221.BEOR2.db1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.entities.Task;
import sit.int221.BEOR2.db1.entities.User;
import sit.int221.BEOR2.db1.repositories.BoardRepository;
import sit.int221.BEOR2.db1.repositories.TaskRepository;
import sit.int221.BEOR2.db1.repositories.UserRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiForbiddenException;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;

    // ดึง Task ทั้งหมดใน Board
    public List<Task> getTasks(String boardId) {
        Board board = validateBoardAccess(boardId);
        return taskRepository.findByBoardId(boardId);
    }

    // ดึง Task ตาม ID
    public Task getTaskById(String boardId, Integer taskId) {
        Board board = validateBoardAccess(boardId);
        return taskRepository.findByBoardIdAndId(boardId, taskId)
                .orElseThrow(() -> new ApiNotFoundException("Task not found in the board"));
    }

    @Transactional
    public Task addTask(String boardId, Task task) {
        Board board = validateBoardAccess(boardId);
        validateOwnership(board);

        if (task == null) {
            throw new ApiBadRequestException("Task data is required");
        }

        task.setBoard(board);
        return taskRepository.saveAndFlush(task);
    }

    @Transactional
    public Task editTask(String boardId, Integer taskId, Task task) {
        Board board = validateBoardAccess(boardId);
        validateOwnership(board);

        Task existingTask = taskRepository.findByBoardIdAndId(boardId, taskId)
                .orElseThrow(() -> new ApiNotFoundException("Task not found"));

        task.setId(existingTask.getId());
        task.setBoard(existingTask.getBoard());
        return taskRepository.saveAndFlush(task);
    }

    @Transactional
    public void deleteTask(String boardId, Integer taskId) {
        Board board = validateBoardAccess(boardId);
        validateOwnership(board);

        Task task = taskRepository.findByBoardIdAndId(boardId, taskId)
                .orElseThrow(() -> new ApiNotFoundException("Task not found"));

        taskRepository.delete(task);
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

