package sit.int221.BEOR2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sit.int221.BEOR2.db2.filters.JwtRequestFilter;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // No 403 error
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint()))
                .addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");

            Map<String, Object> error = new LinkedHashMap<>();
            error.put("timestamp", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ZonedDateTime.ofInstant(new Date().toInstant(), ZoneOffset.UTC)) + "+00:00");
            error.put("status", HttpStatus.UNAUTHORIZED.value());
            String exceptionMessage = (String) request.getAttribute("exception");
            error.put("message", exceptionMessage != null ? exceptionMessage : "UNAUTHORIZED");
            error.put("instance", request.getRequestURI());

            response.getOutputStream().println(new ObjectMapper().writeValueAsString(error));
        };
    }
}
