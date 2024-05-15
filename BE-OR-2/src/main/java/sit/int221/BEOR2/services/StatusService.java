package sit.int221.BEOR2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import sit.int221.BEOR2.entities.Status;
import sit.int221.BEOR2.exceptions.TaskNotFoundException;
import sit.int221.BEOR2.repositories.StatusRepository;

import java.util.List;

@Service
public class StatusService {
    private final String statusString = "NOT FOUND";
    private final String pathInstance = "/v2/statuses";

    @Autowired
    private StatusRepository statusRepository;

    public List<Status> getStatuses() {
        return statusRepository.findAll(Sort.by("StatusID"));
    }

    public Status addStatus(@RequestBody Status status) {
        return statusRepository.saveAndFlush(status);
    }

    public Status editStatus(@PathVariable Integer id, @RequestBody Status editedStatus) {
        if (!statusRepository.existsById(id)) {
            throw new ResourceNotFoundException("The requested task does not exist");
        } else if (statusRepository.existsById(id)) {
            editedStatus.setStatusID(id);
            return statusRepository.save(editedStatus);
        }
        return null;
    }

    public void deleteStatus(@PathVariable Integer id) {
        Status status = statusRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(statusString, pathInstance + id));
        if (!"NO_STATUS".equals(status.getName())) {
            statusRepository.deleteById(id);
        }
    }

    public void transferStatus(@PathVariable Integer id, @PathVariable Integer newId) {
        if (id.equals(newId)) {
            return;
        }

        if (statusRepository.existsById(id)) {
            Status status = statusRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(statusString, pathInstance + id));
            status.setStatusID(newId);
            statusRepository.save(status);
        }
    }
}