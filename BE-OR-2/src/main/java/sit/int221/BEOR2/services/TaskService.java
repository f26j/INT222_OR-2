package sit.int221.BEOR2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.BEOR2.entities.Task;
import sit.int221.BEOR2.exceptions.APINotFoundException;
import sit.int221.BEOR2.repositories.StatusRepository;
import sit.int221.BEOR2.repositories.TaskRepository;

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
        if (task.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task status is required");
        }

        if (!statusRepository.existsById(task.getStatus().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested status does not exist");
        }

        try {
            statusRepository.findById(task.getStatus().getId());
            return taskRepository.saveAndFlush(task);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task title and status must not be null, or JSON properties are not correctly formatted");
        }
    }

    @Transactional
    public Task editTask(@PathVariable Integer id, @RequestBody Task task) {
        if (!statusRepository.existsById(task.getStatus().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task status must be selected from an existing");
        }

        if (!taskRepository.existsById(id)) {
            throw new APINotFoundException();
        }

        try {
            if (taskRepository.existsById(id)) {
                task.setId(id);
                return taskRepository.saveAndFlush(task);
            }
            return null;
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task title and status must not be null, or JSON properties are not correctly formatted");
        }
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