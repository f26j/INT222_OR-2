package sit.int221.BEOR2.db2.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.util.*;

@Component
public class JwtUtil {
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Value("${jwt.expiration.time}")
    private long EXPIRATION_TIME; // 30 mins

    @SneakyThrows
    private Key getSigningKey() {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = md.digest(this.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String createToken(Map<String, Object> claims) {
        UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequestUri().build();
        String issuerUrl = uriComponents.getScheme() + "://" + uriComponents.getHost() + "/or2" + uriComponents.getPath();
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuer(issuerUrl)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String name, String oid, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", name);
        claims.put("oid", oid);
        claims.put("email", email);
        claims.put("role", role);
        return createToken(claims);
    }
}
