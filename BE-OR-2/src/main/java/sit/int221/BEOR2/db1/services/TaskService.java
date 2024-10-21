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

//@Service
//public class TaskService {
//    @Autowired
//    private TaskRepository taskRepository;
//    @Autowired
//    private BoardRepository boardRepository;
//    @Autowired
//    private EntityManager entityManager;
//    @Autowired
//    private UserRepository userRepository;
//
//    public List<Task> getTasks(String boardId) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName).orElse(null);
//
//        Board board = boardRepository.findById(boardId)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
//
//        if (user == null && board.getVisibility().equals("PRIVATE")) {
//            throw new ApiForbiddenException("Access Denied");
//        } else {
//            checkBoardAccess(boardId);
//        }
//
//        return taskRepository.findByBoardId(boardId);
//    }
//
//    public Task getTaskById(String boardId, Integer taskId) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName).orElse(null);
//
//        Board board = boardRepository.findById(boardId)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
//
//        if (user == null && board.getVisibility().equals("PRIVATE")) {
//            throw new ApiForbiddenException("Access Denied");
//        } else {
//            checkBoardAccess(boardId);
//        }
//
//        Task task = taskRepository.findByBoardIdAndId(boardId, taskId)
//                .orElseThrow(() -> new ApiNotFoundException("Task id '" + taskId + "' in board id '" + boardId + "' not found"));
//
//        return task;
//    }
//
//    @Transactional
//    public Task addTask(String boardId, Task task) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName).orElse(null);
//
//        Board existingBoard = boardRepository.findById(boardId)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
//
//        boolean isOwner = existingBoard.getUser().getOid().equals(user.getOid());
//        if (user == null && existingBoard.getVisibility().equals("PRIVATE") || !isOwner) {
//            throw new ApiForbiddenException("Access Denied");
//        } else {
//            checkBoardAccess(boardId);
//        }
//
//        if (task == null) {
//            throw new ApiBadRequestException("Nobody Know");
//        }
//
//        task.setBoard(existingBoard);
//        Task addedTask = taskRepository.saveAndFlush(task);
//        entityManager.refresh(addedTask);
//        return addedTask;
//    }
//
//    @Transactional
//    public Task editTask(String boardId, Integer taskId, Task task) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName).orElse(null);
//
//        Board existingBoard = boardRepository.findById(boardId)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
//
//        boolean isOwner = existingBoard.getUser().getOid().equals(user.getOid());
//        if (user == null && existingBoard.getVisibility().equals("PRIVATE") || !isOwner) {
//            throw new ApiForbiddenException("Access Denied");
//        } else {
//            checkBoardAccess(boardId);
//        }
//
//        if (task == null) {
//            System.out.println("3");
//            throw new ApiBadRequestException("Nobody Know");
//        }
//
//        Task existingTask = taskRepository.findByBoardIdAndId(boardId, taskId)
//                .orElseThrow(() -> new ApiNotFoundException("Task id '" + taskId + "' in board id '" + boardId + "' not found"));
//
//        task.setId(existingTask.getId());
//        task.setBoard(existingTask.getBoard());
//        Task editedTask = taskRepository.saveAndFlush(task);
//        entityManager.refresh(editedTask);
//        return editedTask;
//    }
//
//    @Transactional
//    public void deleteTask(String boardId, Integer taskId) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(userName).orElse(null);
//
//        Board existingBoard = boardRepository.findById(boardId)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
//
//        boolean isOwner = existingBoard.getUser().getOid().equals(user.getOid());
//        if (user == null || !isOwner) {
//            throw new ApiForbiddenException("Access Denied");
//        } else {
//            checkBoardAccess(boardId);
//        }
//
//        Task task = taskRepository.findByBoardIdAndId(boardId, taskId)
//                .orElseThrow(() -> new ApiNotFoundException("Task id '" + taskId + "' in board id '" + boardId + "' not found"));
//
//        taskRepository.delete(task);
//    }
//
//    private void checkBoardAccess(String boardId) {
//        Board board = boardRepository.findById(boardId)
//                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));
//
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        System.out.println(userName + "Task");
//
//        User user = userRepository.findByUsername(userName).orElse(null);
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

