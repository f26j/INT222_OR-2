package sit.int221.BEOR2.db1.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.db1.dtos.StatusDto;
import sit.int221.BEOR2.db1.dtos.TaskManagementDto;
import sit.int221.BEOR2.db1.dtos.TaskDto;
import sit.int221.BEOR2.db1.entities.Task;
import sit.int221.BEOR2.db1.services.TaskService;
import sit.int221.BEOR2.BeOr2CrossOrigin;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@RestController
@RequestMapping("/v3/boards/{id}/tasks")
@BeOr2CrossOrigin
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDto> getTasks(@PathVariable String id) {
        List<Task> tasks = taskService.getTasks(id);

        return tasks.stream().map(task -> new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getAssignees(),
                new StatusDto(
                        task.getStatus().getId(),
                        task.getStatus().getName(),
                        task.getStatus().getDescription()
                )
        )).toList();
    }

    @GetMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public Task getTaskById(@PathVariable String id, @PathVariable Integer taskId) {
        return taskService.getTaskById(id, taskId);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskManagementDto addTask(@PathVariable String id, @RequestBody(required = false) Task task) {
        return modelMapper.map(taskService.addTask(id, task), TaskManagementDto.class);
    }

    @PutMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskManagementDto editTask(@PathVariable String id,
                                      @PathVariable Integer taskId,
                                      @RequestBody(required = false) Task task) {
        return modelMapper.map(taskService.editTask(id, taskId, task), TaskManagementDto.class);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable String id, @PathVariable Integer taskId) {
        taskService.deleteTask(id, taskId);
    }
}
