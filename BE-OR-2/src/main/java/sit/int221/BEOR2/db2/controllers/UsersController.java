package sit.int221.BEOR2.db2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.BeOr2CrossOrigin;
import sit.int221.BEOR2.db2.dtos.JwtResponse;
import sit.int221.BEOR2.db2.dtos.UsersLoginDto;
import sit.int221.BEOR2.db2.entities.Users;
import sit.int221.BEOR2.db2.services.UsersService;

@RestController
@RequestMapping("")
@BeOr2CrossOrigin
public class UsersController {
    @Autowired
    private UsersService usersService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public JwtResponse loginUsers(@RequestBody UsersLoginDto login) {
        return usersService.loginUsers(login.getUserName(), login.getPassword());
    }

    @PostMapping("/**")
    @ResponseStatus(HttpStatus.OK)
    public Users validateToken(@RequestHeader("Authorization") String token) {
        return usersService.validateToken(token);
    }
}
