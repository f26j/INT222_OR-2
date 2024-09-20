package sit.int221.BEOR2.db2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.BEOR2.db2.dtos.JwtResponse;
import sit.int221.BEOR2.db2.entities.Users;
import sit.int221.BEOR2.db2.repositories.UsersRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiUnauthorizedException;

import java.util.Optional;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private JwtUtil jwtUtil;

    private final Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder(16, 32, 1, 60000, 10);

    @Transactional
    public JwtResponse loginUser(String userName, String password) {
        if (userName == null || userName.isEmpty() || userName.length() > 50 ||
                password == null || password.isEmpty() || password.length() > 14) {
            throw new ApiBadRequestException("Username or password must not be empty, or overlength");
        }

        String name = userName.trim();
        Optional<Users> user = usersRepository.findByUsername(name);

        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            throw new ApiUnauthorizedException("Username or password is incorrect");
        }

        String token = jwtUtil.generateToken(
                user.get().getName(),
                user.get().getOid(),
                user.get().getEmail(),
                user.get().getRole()
        );
        return new JwtResponse(token);
    }
}
