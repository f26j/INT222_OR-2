package sit.int221.BEOR2.db2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.BEOR2.db2.dtos.UsersLoginDTO;
import sit.int221.BEOR2.db2.entities.Users;
import sit.int221.BEOR2.db2.services.UsersService;

@RestController
@RequestMapping("")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://ip23or2.sit.kmutt.ac.th",
        "http://intproj23.sit.kmutt.ac.th"
})
public class UsersController {
    @Autowired
    private UsersService usersService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Users usersLogin(@RequestBody UsersLoginDTO login) {
        boolean isValid = usersService.matchUsernameAndPassword(login.getUsername(), login.getPassword());

        if (!isValid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return null;
    }
}
