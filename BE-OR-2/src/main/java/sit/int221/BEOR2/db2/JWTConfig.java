package sit.int221.BEOR2.db2;

import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JWTConfig {

    @Bean
    public SecretKey secretKey() {
        return Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
    }
}