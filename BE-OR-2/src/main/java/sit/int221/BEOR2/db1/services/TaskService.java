package sit.int221.BEOR2.db1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.BEOR2.db1.entities.Task;
import sit.int221.BEOR2.exceptions.APINotFoundException;
import sit.int221.BEOR2.db1.repositories.StatusRepository;
import sit.int221.BEOR2.db1.repositories.TaskRepository;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StatusRepository statusRepository;

    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(@PathVariable Integer id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task id " + id + " does not exist"));
    }

    @Transactional
    public Task addTask(@RequestBody Task task) {
        if (!statusRepository.existsById(task.getStatus().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested status does not exist");
        }
        statusRepository.findById(task.getStatus().getId());
        return taskRepository.saveAndFlush(task);
    }

    @Transactional
    public Task editTask(@PathVariable Integer id, @RequestBody Task task) {
        if (!taskRepository.existsById(id)) {
            throw new APINotFoundException();
        }

        if (taskRepository.existsById(id)) {
            task.setId(id);
            return taskRepository.saveAndFlush(task);
        }
        return null;
    }

    @Transactional
    public void deleteTask(@PathVariable Integer id) {
        if (!taskRepository.existsById(id)) {
            throw new APINotFoundException();
        }

        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        }
    }
}