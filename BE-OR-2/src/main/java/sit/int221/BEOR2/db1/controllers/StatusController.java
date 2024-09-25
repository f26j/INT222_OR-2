package sit.int221.BEOR2.db1.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.db1.dtos.StatusDto;
import sit.int221.BEOR2.db1.entities.Status;
import sit.int221.BEOR2.db1.services.StatusService;
import sit.int221.BEOR2.BeOr2CrossOrigin;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v3/boards/{id}/statuses")
@BeOr2CrossOrigin
public class StatusController {
    @Autowired
    private StatusService statusService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<StatusDto> getStatuses(@PathVariable String id) {
        List<Status> statuses = statusService.getStatuses(id);
        return statuses.stream().map(status -> new StatusDto(
                status.getId(),
                status.getName(),
                status.getDescription()
        )).toList();
    }

    @GetMapping("/{statusId}")
    @ResponseStatus(HttpStatus.OK)
    public Status getStatusById(@PathVariable String id, @PathVariable Integer statusId) {
        return statusService.getStatusById(id, statusId);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public StatusDto addStatus(@PathVariable String id, @Valid @RequestBody Status status) {
        return modelMapper.map(statusService.addStatus(id, status), StatusDto.class);
    }

    @PutMapping("/{statusId}")
    @ResponseStatus(HttpStatus.OK)
    public StatusDto editStatus(@PathVariable String id,
                                @PathVariable Integer statusId,
                                @Valid @RequestBody Status status) {
        return modelMapper.map(statusService.editStatus(id, statusId, status), StatusDto.class);
    }

    @DeleteMapping("/{statusId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteStatus(@PathVariable String id, @PathVariable Integer statusId) {
        statusService.deleteStatus(id, statusId);
    }

    @DeleteMapping("/{statusId}/{statusNewId}")
    @ResponseStatus(HttpStatus.OK)
    public void transferStatus(@PathVariable String id,
                               @PathVariable Integer statusId,
                               @PathVariable Integer statusNewId) {
        statusService.transferStatus(id, statusId, statusNewId);
    }
}
