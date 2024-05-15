package sit.int221.BEOR2.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.dtos.StatusDTO;
import sit.int221.BEOR2.entities.Status;
import sit.int221.BEOR2.services.StatusService;

import java.util.List;

@RestController
@RequestMapping("/v2/statuses")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://ip23or2.sit.kmutt.ac.th",
    "http://intproj23.sit.kmutt.ac.th"
}) // Front-end
public class StatusController {
    @Autowired
    private StatusService statusService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK) // 200
    public List<StatusDTO> getStatuses() {
        List<Status> statuses = statusService.getStatuses();
        return statuses.stream().map(status -> new StatusDTO(status.getId(), status.getName(), status.getDescription())).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201
    public StatusDTO addStatus(@RequestBody Status status) {
        return modelMapper.map(statusService.addStatus(status), StatusDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) // 200
    public StatusDTO editStatus(@PathVariable Integer id, @RequestBody Status editedStatus) {
        return modelMapper.map(statusService.editStatus(id, editedStatus), StatusDTO.class);
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
