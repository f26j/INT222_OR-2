package sit.int221.BEOR2.db2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.BEOR2.db2.entities.Users;
import sit.int221.BEOR2.db2.repositories.UsersRepository;

import java.util.Optional;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;

    private final Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder(16, 32, 1, 60000, 10);

    @Transactional
    public Users usersLogin(String userName, String password) {
        Optional<Users> user = usersRepository.findByUsername(userName.trim());

        if (userName.isEmpty() || userName.length() > 50 || password.isEmpty() || password.length() > 14) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username or password format");
        }

        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The user is incorrect or does not exist, and the password must correct");
        }

        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The user password is incorrect");
        }
        return null;
    }
}
