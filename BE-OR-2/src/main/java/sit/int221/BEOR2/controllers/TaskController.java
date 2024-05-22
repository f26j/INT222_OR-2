package sit.int221.BEOR2.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.dtos.AddEditTaskDTO;
import sit.int221.BEOR2.dtos.SimpleTaskDTO;
import sit.int221.BEOR2.dtos.TaskDTO;
import sit.int221.BEOR2.entities.Task;
import sit.int221.BEOR2.services.TaskService;

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
    public List<SimpleTaskDTO> getTasks(
            @RequestParam(value = "filterStatuses", required = false) List<String> statuses,
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sort,
            @RequestParam(value = "orderBy", required = false, defaultValue = "asc") String order
    ) {
        List<Task> tasks = taskService.getTasks(statuses, sort, order);
        return tasks.stream().map(task -> new SimpleTaskDTO(task.getId(), task.getTitle(), task.getAssignees(), task.getStatus())).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO getTaskById(@PathVariable Integer id) {
        return modelMapper.map(taskService.getTaskById(id), TaskDTO.class);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public AddEditTaskDTO addTask(@RequestBody Task task) {
        return modelMapper.map(taskService.addTask(task), AddEditTaskDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AddEditTaskDTO editTask(@PathVariable Integer id, @RequestBody Task task) {
        return modelMapper.map(taskService.editTask(id, task), AddEditTaskDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
    }
}
