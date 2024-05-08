package sit.int221.BEOR2.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.dtos.AddTaskDTO;
import sit.int221.BEOR2.dtos.SimpleTaskDTO;
import sit.int221.BEOR2.dtos.TaskDTO;
import sit.int221.BEOR2.entities.Task;
import sit.int221.BEOR2.services.TaskService;

import java.util.List;

@RestController
@RequestMapping("/v1/tasks")
@CrossOrigin // Front-end
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK) // 200
    public List<SimpleTaskDTO> getTasks() {
        List<Task> tasks = taskService.getTasks();
        return tasks.stream().map(task -> new SimpleTaskDTO(task.getId(), task.getTitle(), task.getAssignees(), task.getStatus())).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) // 200
    public TaskDTO getTaskById(@PathVariable Integer id) {
        return modelMapper.map(taskService.getTaskById(id), TaskDTO.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201
    public AddTaskDTO addTask(@RequestBody Task task) {
        return modelMapper.map(taskService.addTask(task), AddTaskDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) // 200
    public TaskDTO updateTask(@PathVariable Integer id, @RequestBody Task updatedTask) {
        return modelMapper.map(taskService.updateTask(id, updatedTask), TaskDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) // 200
    public void deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
    }
}
