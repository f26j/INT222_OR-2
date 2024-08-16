package sit.int221.BEOR2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.entities.Users;
import sit.int221.BEOR2.services.UsersService;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://ip23or2.sit.kmutt.ac.th",
        "http://intproj23.sit.kmutt.ac.th"
})
public class UsersController {
    @Autowired
    private UsersService usersService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<Users> getUsers() {
        return usersService.getUsers();
    }
}
