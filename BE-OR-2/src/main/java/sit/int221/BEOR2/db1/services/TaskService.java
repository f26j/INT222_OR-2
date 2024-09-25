package sit.int221.BEOR2.db1.services;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import sit.int221.BEOR2.db1.entities.Board;
import sit.int221.BEOR2.db1.entities.Status;
import sit.int221.BEOR2.db1.entities.Task;
import sit.int221.BEOR2.db1.repositories.BoardRepository;
import sit.int221.BEOR2.db1.repositories.StatusRepository;
import sit.int221.BEOR2.db1.repositories.TaskRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiNotFoundException;

import javax.validation.Valid;
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

    // ดึง Tasks ทั้งหมดจาก boardId ที่กำหนด
    public List<Task> getTasks(@PathVariable String boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new ApiNotFoundException("Board id '" + boardId + "' not found");
        }
        return taskRepository.findByBoardId(boardId);
    }

    // ดึง Task โดยใช้ boardId และ taskId
    public Task getTaskById(@PathVariable String boardId, Integer taskId) {
        return taskRepository.findByBoardIdAndId(boardId, taskId)
                .orElseThrow(() -> new ApiNotFoundException("Task id '" + taskId + "' in board id '" + boardId + "' not found"));
    }

    // เพิ่ม Task ใหม่ใน Board ที่กำหนด
    @Transactional
    public Task addTask(@PathVariable String boardId, @RequestBody @Valid Task task) {
        Board existingBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiNotFoundException("Board id '" + boardId + "' not found"));

        if (task.getStatus() == null || !statusRepository.existsById(task.getStatus().getId())) {
            throw new ApiBadRequestException("Task status cannot be null or invalid");
        }

        task.setBoard(existingBoard);
        Task addedTask = taskRepository.saveAndFlush(task);
        entityManager.refresh(addedTask); // รีเฟรชหลังการบันทึก
        return addedTask;
    }

    // แก้ไข Task ที่มีอยู่ใน Board
    @Transactional
    public Task editTask(@PathVariable String boardId, @PathVariable Integer taskId, @RequestBody @Valid Task task) {
        Task existingTask = taskRepository.findByBoardIdAndId(boardId, taskId)
                .orElseThrow(() -> new ApiNotFoundException("Task id '" + taskId + "' in board id '" + boardId + "' not found"));

        if (task.getStatus() == null || !statusRepository.existsById(task.getStatus().getId())) {
            throw new ApiBadRequestException("Task status cannot be null or invalid");
        }

        task.setId(existingTask.getId());
        task.setBoard(existingTask.getBoard());  // คง Board เดิมไว้
        Task editedTask = taskRepository.saveAndFlush(task);
        entityManager.refresh(editedTask);  // รีเฟรชเพื่อให้มั่นใจว่าข้อมูลเป็นปัจจุบัน
        return editedTask;
    }

    // ลบ Task ที่กำหนด
    @Transactional
    public void deleteTask(@PathVariable String boardId, @PathVariable Integer taskId) {
        Task task = taskRepository.findByBoardIdAndId(boardId, taskId)
                .orElseThrow(() -> new ApiNotFoundException("Task id '" + taskId + "' in board id '" + boardId + "' not found"));

        taskRepository.delete(task);
    }
}
