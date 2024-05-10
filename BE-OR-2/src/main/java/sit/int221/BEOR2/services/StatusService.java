package sit.int221.BEOR2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import sit.int221.BEOR2.entities.Status;
import sit.int221.BEOR2.exceptions.TaskNotFoundException;
import sit.int221.BEOR2.repositories.StatusRepository;

import java.util.List;

@Service
public class StatusService {
    @Autowired
    private StatusRepository statusRepository;

    public List<Status> getStatuses() {
        return statusRepository.findAll(Sort.by("id"));
    }

    public Status addStatus(@RequestBody Status status) {
        return statusRepository.save(status);
    }

    public Status editStatus(@PathVariable Integer id, @RequestBody Status editedStatus) {
        if (!statusRepository.existsById(id)) {
            throw new TaskNotFoundException("NOT FOUND", "/v1/tasks/" + id);
        } else if (statusRepository.existsById(id)) {
            editedStatus.setId(id);
            return statusRepository.save(editedStatus);
        }
        return null;
    }

    public void deleteStatus(@PathVariable Integer id) {
        if (!statusRepository.existsById(id)) {
            throw new TaskNotFoundException("NOT FOUND", "/v1/tasks/" + id);
        } else if (statusRepository.existsById(id)) {
            statusRepository.deleteById(id);
        }
    }

    public void transferStatus(@PathVariable Integer id, @PathVariable Integer newId) {
        if (statusRepository.existsById(id)) {
            Status status = statusRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("NOT FOUND", "/v1/tasks/" + id));
            status.setId(newId);
            statusRepository.save(status);
        }
    }
}
