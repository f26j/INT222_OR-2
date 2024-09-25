package sit.int221.BEOR2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sit.int221.BEOR2.db2.filters.JwtRequestFilter;
import sit.int221.BEOR2.exceptions.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // ปิด CSRF
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/error", "/login/**").permitAll()  // อนุญาตการเข้าถึง /error และ /login โดยไม่ต้อง authenticate
                        .anyRequest().authenticated())  // ต้อง authenticate สำหรับ request อื่นๆ
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint))  // จัดการข้อผิดพลาดในการ authentication
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // ใช้ session แบบ stateless
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);  // เพิ่ม JWT filter
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();  // กำหนด AuthenticationManager
    }
}
