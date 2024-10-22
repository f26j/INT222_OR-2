package sit.int221.BEOR2.db2.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Value("${jwt.expiration.time}")
    private long EXPIRATION_TIME; // 30 นาที

    @Value("${jwt.refresh.time}")
    private long REFRESH_TIME; // 24 ชั่วโมง

    @SneakyThrows
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String getIssuerUrl() {
        UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequestUri().build();
        String host = uriComponents.getHost();
        String basePath = (host.equals("intproj23.sit.kmutt.ac.th") || host.equals("ip23or2.sit.kmutt.ac.th")) ? "or2/" : "";
        return uriComponents.getScheme() + "://" + host + "/" + basePath;
    }

    @SuppressWarnings("deprecation")
    private String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuer(getIssuerUrl())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @SuppressWarnings("deprecation")
    private String createRefreshToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuer(getIssuerUrl())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String name, String oid, String email, String role) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("name", name);
        claims.put("oid", oid);
        claims.put("email", email);
        claims.put("role", role);
        return createToken(claims);
    }

    public String generateRefreshToken(String oid) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("oid", oid);
        return createRefreshToken(claims);
    }

    public String getOid(String token) {
        return extractAllClaims(token).get("oid", String.class);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claim = extractAllClaims(token);
        return claimResolver.apply(claim);
    }

    @SuppressWarnings("deprecation")
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
