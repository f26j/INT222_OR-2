package sit.int221.BEOR2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.BEOR2.entities.Status;
import sit.int221.BEOR2.entities.Task;
import sit.int221.BEOR2.exceptions.APINotFoundException;
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
        return statusRepository.findAll();
    }

    public Status getStatusById(@PathVariable Integer id) {
        return statusRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status id " + id + " does not exist !!!"));
    }

    @Transactional
    public Status addStatus(@RequestBody Status status) {
        return statusRepository.saveAndFlush(status);
    }

    @Transactional
    public Status editStatus(@PathVariable Integer id, @RequestBody Status status) {
        if (!statusRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested task does not exist");
        }
        
        if (status.getId() == null || status.getId() == 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (statusRepository.existsById(id)) {
            status.setId(id);
            return statusRepository.saveAndFlush(status);
        }
        return null;
    }

    @Transactional
    public void deleteStatus(@PathVariable Integer id) {
        if (!statusRepository.existsById(id)) {
            throw new APINotFoundException(statusString, pathInstance + id);
        }

        if (id == 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (statusRepository.existsById(id)) {
            statusRepository.deleteById(id);
        }
    }

    @Transactional
    public void transferStatus(@PathVariable Integer id, @PathVariable Integer newId) {
        if (newId.equals(id) || id == 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Status deleteId = statusRepository.findById(id).orElseThrow(() -> new APINotFoundException(statusString, pathInstance + id));
        Status transferId = statusRepository.findById(newId).orElse(null);
        List<Task> tasks = taskRepository.findByStatus(deleteId);

        for (Task task : tasks) {
            task.setStatus(transferId);
        }

        taskRepository.saveAllAndFlush(tasks);
        statusRepository.delete(deleteId);
    }
}