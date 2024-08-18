package sit.int221.BEOR2.db2.services;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sit.int221.BEOR2.db2.entities.Users;
import sit.int221.BEOR2.db2.repositories.UsersRepository;
import sit.int221.BEOR2.db2.dtos.LoginRequest;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.util.Optional;
import java.util.Map;

@Service
public class AuthService {

    private final UsersRepository usersRepository;
    private final JWTService jwtService;

    public AuthService(UsersRepository usersRepository, JWTService jwtService) {
        this.usersRepository = usersRepository;
        this.jwtService = jwtService;
    }

    public ResponseEntity<?> authenticateUser(LoginRequest request) {
        Optional<Users> user = usersRepository.findByUsername(request.getUsername());

        if (user.isPresent()) {
            Argon2 argon2 = Argon2Factory.create();
            if (argon2.verify(user.get().getPassword(), request.getPassword().toCharArray())) {
                String token = jwtService.generateToken(Map.of("role", user.get().getRole()), user.get().getUsername());
                return ResponseEntity.ok(Map.of("access_token", token));
            }
        }

        return ResponseEntity.status(401).body("Username or Password is incorrect.");
    }
}