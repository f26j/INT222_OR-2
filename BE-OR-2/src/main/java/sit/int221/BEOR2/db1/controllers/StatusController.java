package sit.int221.BEOR2.db1.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.db1.dtos.StatusDto;
import sit.int221.BEOR2.db1.entities.Status;
import sit.int221.BEOR2.db1.services.StatusService;
import sit.int221.BEOR2.BeOr2CrossOrigin;

import java.util.List;

@RestController
@RequestMapping("/v2/statuses")
@BeOr2CrossOrigin
public class StatusController {
    @Autowired
    private StatusService statusService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<StatusDto> getStatuses() {
        List<Status> statuses = statusService.getStatuses();
        return statuses.stream().map(status -> new StatusDto(
                    status.getId(),
                    status.getName(),
                    status.getDescription()
                )).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Status getStatusById(@PathVariable Integer id) {
        return statusService.getStatusById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public StatusDto addStatus(@RequestBody Status status) {
        return modelMapper.map(statusService.addStatus(status), StatusDto.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StatusDto editStatus(@PathVariable Integer id, @RequestBody Status status) {
        return modelMapper.map(statusService.editStatus(id, status), StatusDto.class);
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
