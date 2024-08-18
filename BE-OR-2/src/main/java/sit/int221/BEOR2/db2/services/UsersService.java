package sit.int221.BEOR2.db2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.BEOR2.db2.entities.Users;
import sit.int221.BEOR2.db2.repositories.UsersRepository;

import java.util.List;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;

    public List<Users> getUsersByUsername(@PathVariable String username) {
        if (username == null || username.trim().isEmpty() || username.length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        List<Users> users = usersRepository.findByUsername(username);
        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return usersRepository.findByUsername(username);
    }
}
