package sit.int221.BEOR2.db1.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.db1.dtos.TaskManagementDto;
import sit.int221.BEOR2.db1.dtos.TaskDto;
import sit.int221.BEOR2.db1.entities.Task;
import sit.int221.BEOR2.db1.services.TaskService;
import sit.int221.BEOR2.BeOr2CrossOrigin;

import java.util.List;

@RestController
@RequestMapping("/v2/tasks")
@BeOr2CrossOrigin
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDto> getTasks() {
        List<Task> tasks = taskService.getTasks();
        return tasks.stream().map(task -> new TaskDto(
                    task.getId(),
                    task.getTitle(),
                    task.getAssignees(),
                    task.getStatus()
                )).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Task getTaskById(@PathVariable Integer id) {
        return taskService.getTaskById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskManagementDto addTask(@RequestBody Task task) {
        return modelMapper.map(taskService.addTask(task), TaskManagementDto.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskManagementDto editTask(@PathVariable Integer id, @RequestBody Task task) {
        return modelMapper.map(taskService.editTask(id, task), TaskManagementDto.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
    }
}
