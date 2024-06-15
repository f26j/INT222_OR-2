package sit.int221.BEOR2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private TaskRepository taskRepository;

    public List<Status> getStatuses() {
        return statusRepository.findAll();
    }

    public Status getStatusById(@PathVariable Integer id) {
        return statusRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status id " + id + " does not exist"));
    }

    @Transactional
    public Status addStatus(@RequestBody Status status) {
        try {
            return statusRepository.saveAndFlush(status);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status name must be unique, or JSON properties are not correctly formatted");
        }
    }

    @Transactional
    public Status editStatus(@PathVariable Integer id, @RequestBody Status status) {
        // Test on Postman: status.getId() == null || status.getId() == 1
        if (id == null || id == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status id " + id + " cannot be edited");
        }

        if (!statusRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested status does not exist");
        }

        try {
            if (statusRepository.existsById(id)) {
                status.setId(id);
                return statusRepository.saveAndFlush(status);
            }
            return null;
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status name must be unique, or JSON properties are not correctly formatted");
        }
    }

    @Transactional
    public void deleteStatus(@PathVariable Integer id) {
        if (id == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status id " + id + " cannot be deleted");
        }

        Status deleteId = statusRepository.findById(id).orElseThrow(APINotFoundException::new);

        if (taskRepository.findByStatus(deleteId).isEmpty()) {
            if (statusRepository.existsById(id)) {
                statusRepository.deleteById(id);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete status id " + id + " because it is still referenced by tasks");
        }
    }

    @Transactional
    public void transferStatus(@PathVariable Integer id, @PathVariable Integer newId) {
        if (newId.equals(statusRepository.existsById(id)) || id == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status id " + id + " and new id " + newId + " cannot be transferred");
        }

        Status deleteId = statusRepository.findById(id).orElseThrow(APINotFoundException::new);
        Status transferId = statusRepository.findById(newId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status new id " + newId + " does not exist"));
        List<Task> tasks = taskRepository.findByStatus(deleteId);

        for (Task task : tasks) {
            task.setStatus(transferId);
        }
        taskRepository.saveAllAndFlush(tasks);

        if (taskRepository.findByStatus(deleteId).isEmpty()) {
            statusRepository.delete(deleteId);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete status id " + id + " and transfer to new id " + newId + " because it is still referenced by tasks");
        }
    }
}