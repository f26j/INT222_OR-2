package sit.int221.BEOR2.services;

import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.BEOR2.entities.Task;
import sit.int221.BEOR2.exceptions.APINotFoundException;
import sit.int221.BEOR2.repositories.StatusRepository;
import sit.int221.BEOR2.repositories.TaskRepository;
import sit.int221.BEOR2.specifications.TaskSpecification;

import java.util.Arrays;
import java.util.List;

@Service
public class TaskService {
    private final String statusString = "NOT FOUND";
    private final String pathInstance = "/v2/tasks/";

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StatusRepository statusRepository;

    public List<Task> getTasks(List<String> statuses, String sort, String order) {
        Specification<Task> spec = Specification.where(null);
        if (statuses != null && !statuses.isEmpty()) {
            validateStatuses(statuses);
            spec = spec.and(TaskSpecification.hasStatuses(statuses));
        }
        Sort sorting = Sort.by(Sort.Direction.fromString(order), sort);
        return taskRepository.findAll(spec, sorting);
    }

    private void validateStatuses(List<String> statuses) {
        List<String> fields = Arrays.stream(Task.class.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .map(field -> field.getAnnotation(Column.class).name())
                .toList();

        for (String status : statuses) {
            if (fields.contains(status)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid filter parameter");
            }
        }
    }

    public Task getTaskById(@PathVariable Integer id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task id " + id + " does not exist !!!"));
    }

    @Transactional
    public Task addTask(@RequestBody Task task) {
        if (!statusRepository.existsById(task.getStatus().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        statusRepository.findById(task.getStatus().getId());
        return taskRepository.saveAndFlush(task);
    }

    @Transactional
    public Task editTask(@PathVariable Integer id, @RequestBody Task task) {
        if (!taskRepository.existsById(id)) {
            throw new APINotFoundException(statusString, pathInstance + id);
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
            throw new APINotFoundException(statusString, pathInstance + id);
        }

        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        }
    }
}