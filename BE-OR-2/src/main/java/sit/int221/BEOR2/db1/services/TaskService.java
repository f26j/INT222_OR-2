package sit.int221.BEOR2.db1.services;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.entities.Task;
import sit.int221.BEOR2.db1.repositories.BoardRepository;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;
import sit.int221.BEOR2.db1.repositories.StatusRepository;
import sit.int221.BEOR2.db1.repositories.TaskRepository;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private EntityManager entityManager;
    public List<Task> getTasks(@PathVariable String boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new ApiNotFoundException("Board id '" + boardId + "' not found");
        }

        return taskRepository.findByBoardId(boardId);
    }

    public Task getTaskById(@PathVariable String boardId, Integer taskId) {
        return taskRepository.findByBoardIdAndId(boardId, taskId)
                .orElseThrow(() -> new ApiNotFoundException("Task id '" + taskId + "' in board id '" + boardId + "' not found"));
    }

    @Transactional
    public Task addTask(@PathVariable String boardId, @RequestBody Task task) {
        Board existingBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));

        if (!statusRepository.existsById(task.getStatus().getId())) {
            throw new ApiNotFoundException("The requested status does not exist");
        }

        task.setBoard(existingBoard);
//        return taskRepository.saveAndFlush(task);
        Task addedTask = taskRepository.saveAndFlush(task);
        entityManager.refresh(addedTask);
        return  addedTask;
    }

    @Transactional
    public Task editTask(@PathVariable String boardId, @PathVariable Integer taskId, @RequestBody Task task) {
        Task existingTask = taskRepository.findByBoardIdAndId(boardId, taskId)
                .orElseThrow(() -> new ApiNotFoundException("Task id '" + taskId + "' in board id '" + boardId + "' not found"));

        if (!statusRepository.existsById(task.getStatus().getId())) {
            throw new ApiNotFoundException("The requested status does not exist");
        }

        task.setId(existingTask.getId());
        task.setBoard(existingTask.getBoard());
//        return taskRepository.saveAndFlush(task);}
        Task editedTask = taskRepository.saveAndFlush(task);
        entityManager.refresh(editedTask);
        return editedTask;
    }
    @Transactional
    public void deleteTask(@PathVariable String boardId, @PathVariable Integer taskId) {
        Task task = taskRepository.findByBoardIdAndId(boardId, taskId)
                .orElseThrow(() -> new ApiNotFoundException("Task id '" + taskId + "' in board id '" + boardId + "' not found"));

        taskRepository.delete(task);
    }
}
