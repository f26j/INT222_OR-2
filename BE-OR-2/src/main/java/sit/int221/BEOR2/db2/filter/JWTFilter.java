//package sit.int221.BEOR2.db2.filter;
//
//import org.springframework.web.filter.OncePerRequestFilter;
//import sit.int221.BEOR2.db2.services.JWTService;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import io.jsonwebtoken.Claims;
//
//public class JWTFilter extends OncePerRequestFilter {
//
//    private final JWTService jwtService;
//
//    public JWTFilter(JWTService jwtService) {
//        this.jwtService = jwtService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String authorizationHeader = request.getHeader("Authorization");
//
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            String token = authorizationHeader.substring(7);
//            try {
//                Claims claims = jwtService.validateToken(token);
//                if (claims != null) {
//                    request.setAttribute("claims", claims);
//                }
//            } catch (Exception e) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
//                return;
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}