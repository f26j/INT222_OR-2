package sit.int221.BEOR2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.entities.Status;
import sit.int221.BEOR2.services.StatusService;

import java.util.List;

@RestController
@RequestMapping("/v2/statuses")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://ip23or2.sit.kmutt.ac.th",
    "http://intproj23.sit.kmutt.ac.th"
})
public class StatusController {
    @Autowired
    private StatusService statusService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<Status> getStatuses() {
        return statusService.getStatuses();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Status getStatusById(@PathVariable Integer id) {
        return statusService.getStatusById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Status addStatus(@RequestBody Status status) {
        return statusService.addStatus(status);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Status editStatus(@PathVariable Integer id, @RequestBody Status status) {
        return statusService.editStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteStatus(@PathVariable Integer id) {
        statusService.deleteStatus(id);
    }

    @DeleteMapping("/{id}/{newId}")
    @ResponseStatus(HttpStatus.OK)
    public void transferStatus(@PathVariable Integer id, @PathVariable Integer newId) {
        statusService.transferStatus(id, newId);
    }
}
