package sit.int221.BEOR2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.BEOR2.entities.Status;
import sit.int221.BEOR2.entities.Task;
import sit.int221.BEOR2.exceptions.TaskNotFoundException;
import sit.int221.BEOR2.repositories.StatusRepository;
import sit.int221.BEOR2.repositories.TaskRepository;

import java.util.List;

@Service
public class StatusService {
    private final String statusString = "NOT FOUND";
    private final String pathInstance = "/v2/statuses/";

    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private TaskRepository taskRepository;

    public List<Status> getStatuses() {
        return statusRepository.findAll(Sort.by("id"));
    }

    public Status getStatusById(@PathVariable Integer id) {
        return statusRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Status id " + id + " does not exist !!!"));
    }

    @Transactional
    public Status addStatus(@RequestBody Status status) {
        return statusRepository.saveAndFlush(status);
    }

    @Transactional
    public Status editStatus(@PathVariable Integer id, @RequestBody Status editedStatus) {
        if (!statusRepository.existsById(id)) {
            throw new ResourceNotFoundException("The requested task does not exist");
        }

        if (statusRepository.existsById(id)) {
            if (editedStatus.getId() == 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            editedStatus.setId(id);
            return statusRepository.saveAndFlush(editedStatus);
        }
        return null;
    }

    @Transactional
    public void deleteStatus(@PathVariable Integer id) {
        if (!statusRepository.existsById(id)) {
            throw new TaskNotFoundException(statusString, pathInstance + id);
        }

        if (statusRepository.existsById(id)) {
            statusRepository.deleteById(id);
        }
    }

    @Transactional
    public void transferStatus(@PathVariable Integer id, @PathVariable Integer newId) {
        Status deleteId = statusRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(statusString, pathInstance + id));
        Status transferId = statusRepository.findById(newId).orElseThrow(() -> new TaskNotFoundException(statusString, pathInstance + id));
        List<Task> tasks = taskRepository.findByStatus(deleteId);
        for (Task task : tasks) {
            task.setStatus(transferId);
        }
        taskRepository.saveAllAndFlush(tasks);
        statusRepository.delete(deleteId);
    }
}