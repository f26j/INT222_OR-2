package sit.int221.BEOR2.db2.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sit.int221.BEOR2.db2.services.JwtUtil;
import sit.int221.BEOR2.exceptions.CustomAuthenticationEntryPoint;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;

        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
            }

            if (token != null && jwtUtil.isTokenExpired(token)) {
                Claims claims = jwtUtil.extractAllClaims(token);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, null);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (ExpiredJwtException e) {
            handleAuthenticationException(request, response, "Token is expired");
            return;
        } catch (MalformedJwtException e) {
            handleAuthenticationException(request, response, "Token is not well-formed JWT");
            return;
        } catch (SignatureException | UnsupportedJwtException e) {
            handleAuthenticationException(request, response, "Token has been tampered");
            return;
        }

        chain.doFilter(request, response);
    }

    private void handleAuthenticationException(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        request.setAttribute("exception", message);
        customAuthenticationEntryPoint.commence(request, response, new InsufficientAuthenticationException(null));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/login");
    }
}
