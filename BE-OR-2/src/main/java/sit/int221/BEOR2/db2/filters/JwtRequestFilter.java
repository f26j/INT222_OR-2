package sit.int221.BEOR2.db2.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sit.int221.BEOR2.db2.entities.Users;
import sit.int221.BEOR2.db2.repositories.UsersRepository;
import sit.int221.BEOR2.db2.services.JwtUtil;
import sit.int221.BEOR2.exceptions.CustomAuthenticationEntryPoint;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @Autowired
    private UsersRepository usersRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;

        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
            }

            if (token != null && !jwtUtil.isTokenExpired(token)) {
                log.info("1.1");
                String oid = jwtUtil.getOid(token);
                Users user = usersRepository.findByOid(oid);

                log.info(token);
                log.info(oid);
                log.info(String.valueOf(user));

                if (user != null) {
                    log.info("2.1");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user.getUsername(), null, Collections.emptyList());
                    log.info(String.valueOf(authToken));
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } else {
                log.info("3.1");
                if (!request.getMethod().matches("GET")) {
                    log.info("4");
                    handleAuthenticationException(request, response, "No Token");
                    return;
                }
            }
        } catch (ExpiredJwtException e) {
            handleAuthenticationException(request, response, "Token is expired");
            return;
        } catch (MalformedJwtException e) {
            handleAuthenticationException(request, response, "Token is not well-formed JWT");
            return;
        } catch (SignatureException | UnsupportedJwtException e) {
            handleAuthenticationException(request, response, "Token has been tampered with");
            return;
        } catch (IllegalArgumentException e) {
            handleAuthenticationException(request, response, "Invalid token");
            return;
        }

        if (request.getServletPath().equals("/token")) {
            request.setAttribute("refreshToken", token);
        }

        chain.doFilter(request, response);
    }

    private void handleAuthenticationException(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        request.setAttribute("exception", message);
        customAuthenticationEntryPoint.commence(request, response, new InsufficientAuthenticationException(message));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/login");
    }
}
