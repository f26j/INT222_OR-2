package sit.int221.BEOR2.db1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.BeOr2CrossOrigin;
import sit.int221.BEOR2.db1.dtos.CollabDto;
import sit.int221.BEOR2.db1.entities.Collab;
import sit.int221.BEOR2.db1.services.CollabService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v3/boards/{id}/collabs")
@BeOr2CrossOrigin
public class CollabController {
    @Autowired
    private CollabService collabService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<Collab> getCollabs(@PathVariable String id) {
        return collabService.getCollabs(id);
    }

    @GetMapping("/{collabOid}")
    @ResponseStatus(HttpStatus.OK)
    public Collab getCollabByOid(@PathVariable String id, @PathVariable String collabOid) {
        return collabService.getCollabByOid(id, collabOid);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public CollabDto addCollab(@PathVariable String id, @RequestBody(required = false) Collab collab) {
        return collabService.addCollab(id, collab);
    }

    @PatchMapping("/{collabOid}")
    @ResponseStatus(HttpStatus.OK)
    public CollabDto editCollabAccessRight(@PathVariable String id,
                                           @PathVariable String collabOid,
                                           @RequestBody(required = false) Map<String, String> requestBody) {
        String collab = (requestBody != null) ? requestBody.get("accessRight") : null;
        return collabService.editCollabAccessRight(id, collabOid, collab);
    }

    @DeleteMapping("/{collabOid}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCollab(@PathVariable String id, @PathVariable String collabOid) {
        collabService.deleteCollab(id, collabOid);
    }
}
