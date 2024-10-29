package sit.int221.BEOR2.db2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.BEOR2.db1.entities.User;
import sit.int221.BEOR2.db1.repositories.UserRepository;
import sit.int221.BEOR2.db2.dtos.AccessTokenResponse;
import sit.int221.BEOR2.db2.dtos.JwtResponse;
import sit.int221.BEOR2.db2.entities.Users;
import sit.int221.BEOR2.db2.repositories.UsersRepository;
import sit.int221.BEOR2.exceptions.ApiBadRequestException;
import sit.int221.BEOR2.exceptions.ApiUnauthorizedException;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository; // DB2
    @Autowired
    private UserRepository userRepository; // DB1
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

        Optional<User> existingUser = userRepository.findByUsername(name);
        if (existingUser.isEmpty()) {
            User newUser = new User();
            newUser.setOid(user.get().getOid());
            newUser.setName(user.get().getName().trim());
            newUser.setUsername(user.get().getUsername().trim());
            newUser.setEmail(user.get().getEmail());
            newUser.setCreated_on(ZonedDateTime.now());
            newUser.setUpdated_on(ZonedDateTime.now());

            userRepository.saveAndFlush(newUser);
        }

        String token = jwtUtil.generateToken(
                user.get().getName(),
                user.get().getOid(),
                user.get().getEmail(),
                user.get().getRole()
        );

        String refreshToken = jwtUtil.generateRefreshToken(
                user.get().getOid()
        );

        return new JwtResponse(token, refreshToken);
    }

    @Transactional
    public AccessTokenResponse refreshToken(String refreshToken) {
        try {
            String oid = jwtUtil.getOid(refreshToken.trim());
            Optional<Users> existingUser = Optional.ofNullable(usersRepository.findByOid(oid));
            if (existingUser.isEmpty()) {
                throw new ApiUnauthorizedException("User not found");
            }

            Users user = existingUser.get();
            String token = jwtUtil.generateToken(
                    user.getName(),
                    user.getOid(),
                    user.getEmail(),
                    user.getRole()
            );

            return new AccessTokenResponse(token);

        } catch (RuntimeException e) {
            throw new ApiUnauthorizedException("Token is invalid or expired/Missing refresh_token in request header");
        }
    }
}
