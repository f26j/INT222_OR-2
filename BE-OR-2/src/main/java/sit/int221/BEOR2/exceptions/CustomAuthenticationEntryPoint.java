package sit.int221.BEOR2.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        Map<String, Object> error = new LinkedHashMap<>();

        // Date format ควรเป็นในรูปแบบ 2024-09-27T00:00:00.000+00:00
        error.put("timestamp", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ZonedDateTime.ofInstant(new Date().toInstant(), ZoneOffset.UTC)) + "+00:00");

        // ส่งเลขสถานะข้อผิดพลาด
        error.put("status", HttpStatus.UNAUTHORIZED.value());

        // ตรวจสอบว่ามี exception message ถูกตั้งค่าใน request หรือไม่
        String exceptionMessage = (String) request.getAttribute("exception");
        error.put("message", exceptionMessage != null ? exceptionMessage : "Token is required to access");

        // แสดง URI ที่ร้องขอ
        error.put("instance", request.getRequestURI());

        // ส่ง error response ในรูปแบบ JSON
        response.getOutputStream().println(new ObjectMapper().writeValueAsString(error));
    }
}
