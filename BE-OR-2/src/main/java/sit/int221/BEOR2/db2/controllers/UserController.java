package sit.int221.BEOR2.db2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.BEOR2.BeOr2CrossOrigin;
import sit.int221.BEOR2.db2.dtos.JwtResponse;
import sit.int221.BEOR2.db2.dtos.UserLoginDto;
import sit.int221.BEOR2.db2.services.UserService;

@RestController
@RequestMapping("")
@BeOr2CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public JwtResponse loginUser(@RequestBody UserLoginDto login) {
        return userService.loginUser(login.getUserName(), login.getPassword());
    }
}
