package sit.int221.BEOR2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.entities.Status;
import sit.int221.BEOR2.services.StatusService;

import java.util.List;

@RestController
@RequestMapping("/v2/statuses")
@CrossOrigin // Front-end
public class StatusController {
    @Autowired
    private StatusService statusService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK) // 200
    public List<Status> getStatuses() {
        return statusService.getStatuses();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201
    public Status addStatus(@RequestBody Status status) {
        return statusService.addStatus(status);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) // 200
    public Status editStatus(@PathVariable Integer id, @RequestBody Status editedStatus) {
        return statusService.editStatus(id, editedStatus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) // 200
    public void deleteStatus(@PathVariable Integer id) {
        statusService.deleteStatus(id);
    }

    @DeleteMapping("/{id}/{newId}")
    @ResponseStatus(HttpStatus.OK) // 200
    public void transferStatus(@PathVariable Integer id, @PathVariable Integer newId) {
        statusService.transferStatus(id, newId);
    }
}
