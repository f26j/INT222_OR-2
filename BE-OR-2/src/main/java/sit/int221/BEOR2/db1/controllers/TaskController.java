package sit.int221.BEOR2.db1.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.db1.dtos.TaskAddEditDTO;
import sit.int221.BEOR2.db1.dtos.TaskSimpleDTO;
import sit.int221.BEOR2.db1.entities.Task;
import sit.int221.BEOR2.db1.services.TaskService;

import java.util.List;

@RestController
@RequestMapping("/v2/tasks")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://ip23or2.sit.kmutt.ac.th",
    "http://intproj23.sit.kmutt.ac.th"
})
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskSimpleDTO> getTasks() {
        List<Task> tasks = taskService.getTasks();
        return tasks.stream().map(task -> new TaskSimpleDTO(
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
    public TaskAddEditDTO addTask(@RequestBody Task task) {
        return modelMapper.map(taskService.addTask(task), TaskAddEditDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskAddEditDTO editTask(@PathVariable Integer id, @RequestBody Task task) {
        return modelMapper.map(taskService.editTask(id, task), TaskAddEditDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
    }
}
