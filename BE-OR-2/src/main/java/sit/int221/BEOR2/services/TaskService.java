package sit.int221.BEOR2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.BEOR2.entities.Task;
import sit.int221.BEOR2.exceptions.TaskNotFoundException;
import sit.int221.BEOR2.repositories.TaskRepository;

import java.util.List;

@Service
public class TaskService {
    private final String statusString = "NOT FOUND";
    private final String pathInstance = "/v2/tasks/";

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StatusService statusService;

    public List<Task> getTasks() {
        return taskRepository.findAll(Sort.by("id"));
    }

    public Task getTaskById(@PathVariable Integer id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task id " + id + " does not exist !!!"));
    }

    @Transactional
    public Task addTask(@RequestBody Task task) {
        if (task.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        statusService.getStatusById(task.getStatus().getId());
        return taskRepository.saveAndFlush(task);
    }

    @Transactional
    public Task editTask(@PathVariable Integer id, @RequestBody Task editedTask) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(statusString, pathInstance + id);
        }

        if (taskRepository.existsById(id)) {
            editedTask.setId(id);
            return taskRepository.saveAndFlush(editedTask);
        }
        return null;
    }

    @Transactional
    public void deleteTask(@PathVariable Integer id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(statusString, pathInstance + id);
        }

        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        }
    }
}