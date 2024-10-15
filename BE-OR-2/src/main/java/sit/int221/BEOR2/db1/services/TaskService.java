package sit.int221.BEOR2.db1.services;

import jakarta.persistence.EntityManager;
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
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    public List<Task> getTasks(String boardId) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));

        if (user == null && board.getVisibility().equals("PRIVATE")) {
            throw new ApiForbiddenException("Acess Denied");
        } else {
            checkBoardAccess(boardId);
        }

        return taskRepository.findByBoardId(boardId);
    }

    public Task getTaskById(String boardId, Integer taskId) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));

        if (user == null && board.getVisibility().equals("PRIVATE")) {
            throw new ApiForbiddenException("Acess Denied");
        } else {
            checkBoardAccess(boardId);
        }

        Task task = taskRepository.findByBoardIdAndId(boardId, taskId)
                .orElseThrow(() -> new ApiNotFoundException("Task id '" + taskId + "' in board id '" + boardId + "' not found"));

        return task;
    }

    @Transactional
    public Task addTask(String boardId, Task task) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);

        Board existingBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));

        boolean isOwner = existingBoard.getUser().getOid().equals(user.getOid());
        if (user == null && existingBoard.getVisibility().equals("PRIVATE") || !isOwner) {
            throw new ApiForbiddenException("Acess Denied");
        } else {
            checkBoardAccess(boardId);
        }

        if (task == null) {
            throw new ApiBadRequestException("Nobody Know");
        }

        // if (task.getStatus() == null || !statusRepository.existsById(task.getStatus().getId())) {
        //     throw new ApiBadRequestException("Task status cannot be null or invalid");
        // }

        task.setBoard(existingBoard);
        Task addedTask = taskRepository.saveAndFlush(task);
        entityManager.refresh(addedTask);
        return addedTask;
    }

    @Transactional
    public Task editTask(String boardId, Integer taskId, Task task) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);

        Board existingBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));

        boolean isOwner = existingBoard.getUser().getOid().equals(user.getOid());
        if (user == null && existingBoard.getVisibility().equals("PRIVATE") || !isOwner) {
            throw new ApiForbiddenException("Acess Denied");
        } else {
            checkBoardAccess(boardId);
        }

        if (task == null) {
            System.out.println("3");
            throw new ApiBadRequestException("Nobody Know");
        }

        Task existingTask = taskRepository.findByBoardIdAndId(boardId, taskId)
                .orElseThrow(() -> new ApiNotFoundException("Task id '" + taskId + "' in board id '" + boardId + "' not found"));

        // if (task.getStatus() == null || !statusRepository.existsById(task.getStatus().getId())) {
        //     throw new ApiBadRequestException("Task status cannot be null or invalid");
        // }

        task.setId(existingTask.getId());
        task.setBoard(existingTask.getBoard());
        Task editedTask = taskRepository.saveAndFlush(task);
        entityManager.refresh(editedTask);
        return editedTask;
    }

    @Transactional
    public void deleteTask(String boardId, Integer taskId) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElse(null);

        Board existingBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));

        boolean isOwner = existingBoard.getUser().getOid().equals(user.getOid());
        if (user == null || !isOwner) {
            throw new ApiForbiddenException("Acess Denied");
        } else {
            checkBoardAccess(boardId);
        }

        Task task = taskRepository.findByBoardIdAndId(boardId, taskId)
                .orElseThrow(() -> new ApiNotFoundException("Task id '" + taskId + "' in board id '" + boardId + "' not found"));

        taskRepository.delete(task);
    }

    private void checkBoardAccess(String boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(userName + "Task");

        User user = userRepository.findByUsername(userName).orElse(null);
        if (user != null) {
            boolean isOwner = board.getUser().getOid().equals(user.getOid());
            boolean isPublic = "PUBLIC".equalsIgnoreCase(board.getVisibility());

            // ถ้าไม่ใช่เจ้าของและบอร์ดเป็น private ให้ส่ง 403 Forbidden
            if (!isOwner && !isPublic) {
                throw new ApiForbiddenException("Access denied: You do not have permission to access this board.");
            }
        }
    }
}

