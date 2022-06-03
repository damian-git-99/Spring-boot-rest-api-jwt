package rest.api.example.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import rest.api.example.auth.exceptions.InvalidJwtTokenException;
import rest.api.example.auth.jwt.JWTService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final ObjectMapper mapper = new ObjectMapper();

    public JWTAuthorizationFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request
            , HttpServletResponse response
            , FilterChain chain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/1.0/auth/")) chain.doFilter(request, response);

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            if (jwtService.validateToken(authorizationHeader)) {
                Claims claims = jwtService.getClaims(authorizationHeader);
                List<String> list = (List<String>) claims.get("authorities", List.class);
                Collection<? extends GrantedAuthority> authorities = list.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(token);
            }
            chain.doFilter(request, response);
        } catch (InvalidJwtTokenException ex) {
            SecurityContextHolder.clearContext();
            Map<String, Object> errors = new HashMap<>();
            errors.put("message", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(response.getWriter(), errors);
        }
    }


}
