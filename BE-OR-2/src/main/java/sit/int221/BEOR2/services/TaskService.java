package sit.int221.BEOR2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.entities.Task;
import sit.int221.BEOR2.repositories.TaskRepository;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(@PathVariable Integer id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task id " + id + " does not exist !!!"));
    }

    public Task addTask(@RequestBody Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(@PathVariable Integer id, @RequestBody Task updatedTask) {
        if (taskRepository.existsById(id)) {
            updatedTask.setId(id);
            return taskRepository.save(updatedTask);
        }
        return null;
    }

    public void deleteTask(@PathVariable Integer id) {
        taskRepository.deleteById(id);
    }
}
